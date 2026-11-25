package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.LogbookHarian;
import com.mycompany.verifymagang.model.PenempatanMagang;
import com.mycompany.verifymagang.service.LogbookService;
import com.mycompany.verifymagang.service.PenempatanService;
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
import javax.swing.JTextField;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Halaman logbook harian milik mahasiswa.
 *
 * <p>Mahasiswa hanya dapat mengisi logbook jika memiliki penempatan aktif, dan
 * hanya boleh mengubah atau menghapus logbook yang belum diverifikasi dosen.</p>
 */
public class LogbookMahasiswaPanel extends TablePage<LogbookHarian> {

    private static final String SEMUA = "Semua Status";

    private final LogbookService logbookService = new LogbookService();
    private final PenempatanService penempatanService = new PenempatanService();
    private final JComboBox<String> filterStatus;

    /** Membuat halaman logbook mahasiswa. */
    public LogbookMahasiswaPanel() {
        super("Logbook Saya", "Catatan kegiatan magang harian Anda",
                "Tanggal", "Jam", "Kegiatan", "Kendala", "Status", "Catatan Dosen");

        setColumnWidth(0, 110);
        setColumnWidth(1, 130);
        setColumnWidth(2, 300);
        setColumnWidth(3, 180);
        setColumnWidth(4, 120);
        setColumnWidth(5, 220);
        statusColumn(4);

        filterStatus = Ui.comboBox(new String[]{SEMUA,
            LogbookHarian.STATUS_MENUNGGU,
            LogbookHarian.STATUS_DIVERIFIKASI,
            LogbookHarian.STATUS_REVISI});
        filterStatus.setPreferredSize(new java.awt.Dimension(160, 36));
        filterStatus.addActionListener(e -> muatUlang());
        addToolbarLeft(filterStatus);

        JButton tambah = tombolTambah(this::dialogTambah);
        tambah.setText("Tambah Logbook");
        JButton ubah = tombolUbah(this::dialogUbah);
        JButton hapus = tombolHapus(this::hapusData);

        addToolbarRight(tambah, ubah, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<LogbookHarian> muatData() throws Exception {
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            return List.of();
        }
        List<LogbookHarian> data = logbookService.daftarByMahasiswa(mahasiswaId);
        String status = (String) filterStatus.getSelectedItem();
        if (status != null && !SEMUA.equals(status)) {
            data = data.stream()
                    .filter(l -> status.equalsIgnoreCase(l.getStatusVerifikasi()))
                    .toList();
        }
        String keyword = kataKunci().toLowerCase();
        if (keyword.isEmpty()) {
            return data;
        }
        return data.stream()
                .filter(l -> (l.getKegiatan() != null
                && l.getKegiatan().toLowerCase().contains(keyword))
                || (l.getKendala() != null
                && l.getKendala().toLowerCase().contains(keyword)))
                .toList();
    }

    @Override
    protected Object[] baris(LogbookHarian l) {
        return new Object[]{DateField.format(l.getTanggal()),
            jam(l.getJamMulai(), l.getJamSelesai()),
            ringkas(l.getKegiatan(), 90),
            ringkas(l.getKendala(), 60),
            l.getStatusVerifikasi(),
            l.getCatatanDosen() == null || l.getCatatanDosen().isBlank()
                    ? "-" : l.getCatatanDosen()};
    }

    private static String jam(LocalTime mulai, LocalTime selesai) {
        if (mulai == null && selesai == null) {
            return "-";
        }
        return (mulai == null ? "-" : mulai.toString())
                + " - " + (selesai == null ? "-" : selesai.toString());
    }

    private static String ringkas(String teks, int maks) {
        if (teks == null || teks.isBlank()) {
            return "-";
        }
        String satu = teks.replaceAll("\\s+", " ").trim();
        return satu.length() > maks ? satu.substring(0, maks - 3) + "..." : satu;
    }

    @Override
    protected void onRowDoubleClick() {
        dialogUbah();
    }

    private void dialogTambah() {
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            Messages.error(this, "Sesi mahasiswa tidak ditemukan.");
            return;
        }

        Optional<PenempatanMagang> penempatan;
        try {
            penempatan = penempatanService.penempatanAktifMahasiswa(mahasiswaId);
        } catch (Exception ex) {
            Messages.error(this, Messages.pesanDari(ex));
            return;
        }
        if (penempatan.isEmpty()) {
            Messages.info(this, "Anda belum memiliki penempatan aktif.\n"
                    + "Logbook hanya dapat diisi setelah ditempatkan oleh admin.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tambah Logbook", "Catat kegiatan magang hari ini.");

        DateField tanggal = dialog.addField("Tanggal", new DateField(LocalDate.now()));
        JTextField jamMulai = dialog.addField("Jam Mulai (HH:mm)", Ui.textField(20));
        jamMulai.setText("08:00");
        JTextField jamSelesai = dialog.addField("Jam Selesai (HH:mm)", Ui.textField(20));
        jamSelesai.setText("16:00");
        JScrollPane kegiatan = dialog.addField("Uraian Kegiatan", Ui.textArea(4, 20));
        JScrollPane kendala = dialog.addField("Kendala (opsional)", Ui.textArea(2, 20));

        if (!dialog.showDialog()) {
            return;
        }

        String isiKegiatan = Ui.areaOf(kegiatan).getText().trim();
        if (isiKegiatan.isEmpty()) {
            Messages.peringatan(this, "Uraian kegiatan wajib diisi.");
            return;
        }

        LogbookHarian lb = new LogbookHarian();
        lb.setPenempatanId(penempatan.get().getId());
        lb.setTanggal(tanggal.getLocalDate());
        lb.setJamMulai(parseJam(jamMulai.getText()));
        lb.setJamSelesai(parseJam(jamSelesai.getText()));
        lb.setKegiatan(isiKegiatan);
        lb.setKendala(Ui.areaOf(kendala).getText().trim());

        Async.run(this,
                () -> logbookService.isiLogbook(lb),
                id -> {
                    Messages.sukses(this, "Logbook berhasil disimpan.");
                    muatUlang();
                });
    }

    private void dialogUbah() {
        LogbookHarian selected = terpilih();
        if (selected == null) {
            return;
        }
        if (selected.isDiverifikasi()) {
            Messages.info(this, "Logbook yang sudah diverifikasi tidak dapat diubah.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ubah Logbook", "Perubahan akan mengembalikan status ke MENUNGGU.");

        DateField tanggal = dialog.addField("Tanggal", new DateField(selected.getTanggal()));
        tanggal.setEnabled(false);
        JTextField jamMulai = dialog.addField("Jam Mulai (HH:mm)", Ui.textField(20));
        jamMulai.setText(selected.getJamMulai() == null ? "" : selected.getJamMulai().toString());
        JTextField jamSelesai = dialog.addField("Jam Selesai (HH:mm)", Ui.textField(20));
        jamSelesai.setText(selected.getJamSelesai() == null
                ? "" : selected.getJamSelesai().toString());
        JScrollPane kegiatan = dialog.addField("Uraian Kegiatan", Ui.textArea(4, 20));
        Ui.areaOf(kegiatan).setText(selected.getKegiatan());
        JScrollPane kendala = dialog.addField("Kendala (opsional)", Ui.textArea(2, 20));
        Ui.areaOf(kendala).setText(selected.getKendala());

        if (!dialog.showDialog()) {
            return;
        }

        String isiKegiatan = Ui.areaOf(kegiatan).getText().trim();
        if (isiKegiatan.isEmpty()) {
            Messages.peringatan(this, "Uraian kegiatan wajib diisi.");
            return;
        }

        selected.setJamMulai(parseJam(jamMulai.getText()));
        selected.setJamSelesai(parseJam(jamSelesai.getText()));
        selected.setKegiatan(isiKegiatan);
        selected.setKendala(Ui.areaOf(kendala).getText().trim());

        Async.run(this,
                () -> logbookService.ubahLogbook(selected),
                ok -> {
                    Messages.sukses(this, "Logbook diperbarui.");
                    muatUlang();
                });
    }

    private void hapusData() {
        LogbookHarian selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this,
                "logbook tanggal " + DateField.format(selected.getTanggal()))) {
            return;
        }
        Async.run(this,
                () -> logbookService.hapusLogbook(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Logbook dihapus.");
                    muatUlang();
                });
    }

    private static LocalTime parseJam(String teks) {
        if (teks == null || teks.isBlank()) {
            return null;
        }
        String bersih = teks.trim().replace('.', ':');
        try {
            if (bersih.length() == 5) {
                return LocalTime.parse(bersih);
            }
            if (bersih.length() == 4) {
                return LocalTime.parse(bersih.substring(0, 2) + ":" + bersih.substring(2));
            }
            return LocalTime.parse(bersih);
        } catch (java.time.format.DateTimeParseException ex) {
            return null;
        }
    }
}
