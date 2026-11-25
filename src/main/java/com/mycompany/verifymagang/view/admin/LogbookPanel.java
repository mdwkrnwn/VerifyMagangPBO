package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.LogbookHarian;
import com.mycompany.verifymagang.service.LogbookService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.FormDialog;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.TablePage;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import java.util.List;

/**
 * Halaman pemantauan logbook harian seluruh mahasiswa.
 *
 * <p>Admin dapat melihat isi logbook, memberi catatan, dan memverifikasi atau
 * meminta revisi. Logbook yang sudah diverifikasi tidak dapat diubah lagi.</p>
 */
public class LogbookPanel extends TablePage<LogbookHarian> {

    private static final String SEMUA = "Semua Status";

    private final LogbookService service = new LogbookService();
    private final JComboBox<String> filterStatus;

    /** Membuat halaman logbook. */
    public LogbookPanel() {
        super("Logbook Magang", "Pantau catatan harian mahasiswa dan verifikasinya",
                "Tanggal", "Mahasiswa", "Jam", "Kegiatan", "Kendala", "Status");

        setColumnWidth(0, 100);
        setColumnWidth(1, 170);
        setColumnWidth(2, 100);
        setColumnWidth(3, 300);
        setColumnWidth(4, 180);
        setColumnWidth(5, 110);
        boldColumn(1);
        statusColumn(5);

        filterStatus = Ui.comboBox(new String[]{SEMUA,
            LogbookHarian.STATUS_MENUNGGU,
            LogbookHarian.STATUS_DIVERIFIKASI,
            LogbookHarian.STATUS_REVISI});
        filterStatus.setPreferredSize(new java.awt.Dimension(160, 36));
        filterStatus.addActionListener(e -> muatUlang());
        addToolbarLeft(filterStatus);

        JButton verifikasi = tombolAksi("Verifikasi",
                Icons.get(Icons.I.CHECK, 15, Theme.Colors.SUCCESS), this::verifikasi);
        verifikasi.setEnabled(false);
        bindToSelection(verifikasi);

        JButton revisi = tombolAksi("Minta Revisi",
                Icons.get(Icons.I.REFRESH, 15, Theme.Colors.WARNING), this::mintaRevisi);
        revisi.setEnabled(false);
        bindToSelection(revisi);

        JButton hapus = tombolHapus(this::hapusData);

        addToolbarRight(verifikasi, revisi, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<LogbookHarian> muatData() throws Exception {
        String status = (String) filterStatus.getSelectedItem();
        List<LogbookHarian> data;
        if (status == null || SEMUA.equals(status)) {
            data = service.daftarSemua();
        } else {
            data = service.daftarSemua().stream()
                    .filter(l -> status.equalsIgnoreCase(l.getStatusVerifikasi()))
                    .toList();
        }

        String keyword = kataKunci().toLowerCase();
        if (keyword.isEmpty()) {
            return data;
        }
        return data.stream()
                .filter(l -> mengandung(l.getNamaMahasiswa(), keyword)
                || mengandung(l.getKegiatan(), keyword))
                .toList();
    }

    private static boolean mengandung(String nilai, String kunci) {
        return nilai != null && nilai.toLowerCase().contains(kunci);
    }

    @Override
    protected Object[] baris(LogbookHarian l) {
        return new Object[]{DateField.format(l.getTanggal()),
            l.getNamaMahasiswa(),
            jam(l.getJamMulai(), l.getJamSelesai()),
            ringkas(l.getKegiatan()),
            ringkas(l.getKendala()),
            l.getStatusVerifikasi()};
    }

    private static String jam(java.time.LocalTime mulai, java.time.LocalTime selesai) {
        if (mulai == null && selesai == null) {
            return "-";
        }
        return (mulai == null ? "-" : mulai.toString())
                + " - " + (selesai == null ? "-" : selesai.toString());
    }

    private static String ringkas(String teks) {
        if (teks == null || teks.isBlank()) {
            return "-";
        }
        String satuBaris = teks.replaceAll("\\s+", " ").trim();
        return satuBaris.length() > 60 ? satuBaris.substring(0, 57) + "..." : satuBaris;
    }

    @Override
    protected void onRowDoubleClick() {
        detail();
    }

    private void detail() {
        LogbookHarian selected = terpilih();
        if (selected == null) {
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Detail Logbook",
                selected.getNamaMahasiswa() + " - " + DateField.format(selected.getTanggal()));

        JScrollPane kegiatan = dialog.addField("Kegiatan", Ui.textArea(5, 20));
        Ui.areaOf(kegiatan).setText(selected.getKegiatan());
        Ui.areaOf(kegiatan).setEditable(false);
        JScrollPane kendala = dialog.addField("Kendala", Ui.textArea(3, 20));
        Ui.areaOf(kendala).setText(selected.getKendala());
        Ui.areaOf(kendala).setEditable(false);
        JScrollPane catatan = dialog.addField("Catatan Dosen (opsional)", Ui.textArea(3, 20));
        Ui.areaOf(catatan).setText(selected.getCatatanDosen());

        if (!dialog.showDialog()) {
            return;
        }
        String isiCatatan = Ui.areaOf(catatan).getText().trim();
        if (isiCatatan.isEmpty()) {
            return;
        }
        Async.run(this,
                () -> service.verifikasi(selected.getId(), isiCatatan),
                ok -> {
                    Messages.sukses(this, "Catatan dan verifikasi tersimpan.");
                    muatUlang();
                });
    }

    private void verifikasi() {
        LogbookHarian selected = terpilih();
        if (selected == null) {
            return;
        }
        if (selected.isDiverifikasi()) {
            Messages.info(this, "Logbook ini sudah diverifikasi.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Verifikasi Logbook",
                selected.getNamaMahasiswa() + " - " + DateField.format(selected.getTanggal()));

        JScrollPane catatan = dialog.addField("Catatan (opsional)", Ui.textArea(3, 20));
        if (!dialog.showDialog()) {
            return;
        }

        String isi = Ui.areaOf(catatan).getText().trim();
        Async.run(this,
                () -> service.verifikasi(selected.getId(), isi.isEmpty() ? null : isi),
                ok -> {
                    Messages.sukses(this, "Logbook diverifikasi.");
                    muatUlang();
                });
    }

    private void mintaRevisi() {
        LogbookHarian selected = terpilih();
        if (selected == null) {
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Minta Revisi Logbook",
                selected.getNamaMahasiswa() + " - " + DateField.format(selected.getTanggal()));

        JScrollPane catatan = dialog.addField("Catatan revisi", Ui.textArea(3, 20));
        if (!dialog.showDialog()) {
            return;
        }

        String isi = Ui.areaOf(catatan).getText().trim();
        if (isi.isEmpty()) {
            Messages.peringatan(this, "Catatan revisi wajib diisi.");
            return;
        }
        Async.run(this,
                () -> service.mintaRevisi(selected.getId(), isi),
                ok -> {
                    Messages.sukses(this, "Permintaan revisi dikirim.");
                    muatUlang();
                });
    }

    private void hapusData() {
        LogbookHarian selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this,
                "logbook " + selected.getNamaMahasiswa()
                + " tanggal " + DateField.format(selected.getTanggal()))) {
            return;
        }
        Async.run(this,
                () -> service.hapusLogbook(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Logbook dihapus.");
                    muatUlang();
                });
    }
}
