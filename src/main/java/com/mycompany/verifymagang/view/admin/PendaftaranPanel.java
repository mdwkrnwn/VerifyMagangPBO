package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.PendaftaranMagang;
import com.mycompany.verifymagang.service.PendaftaranService;
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
 * Halaman persetujuan pendaftaran magang.
 *
 * <p>Admin dapat menyetujui atau menolak pendaftaran berstatus MENUNGGU. Aturan
 * kuota dan status dijaga oleh {@link PendaftaranService}.</p>
 */
public class PendaftaranPanel extends TablePage<PendaftaranMagang> {

    private static final String SEMUA = "Semua Status";

    private final PendaftaranService service = new PendaftaranService();
    private final JComboBox<String> filterStatus;

    /** Membuat halaman pendaftaran magang. */
    public PendaftaranPanel() {
        super("Pendaftaran Magang", "Tinjau dan putuskan pendaftaran mahasiswa",
                "Tanggal Daftar", "NIM", "Nama Mahasiswa", "Lowongan", "Status", "Catatan");

        setColumnWidth(0, 120);
        setColumnWidth(1, 110);
        setColumnWidth(2, 190);
        setColumnWidth(3, 240);
        setColumnWidth(4, 110);
        statusColumn(4);
        boldColumn(2);

        filterStatus = Ui.comboBox(new String[]{SEMUA,
            PendaftaranMagang.STATUS_MENUNGGU,
            PendaftaranMagang.STATUS_DISETUJUI,
            PendaftaranMagang.STATUS_DITOLAK});
        filterStatus.setPreferredSize(new java.awt.Dimension(160, 36));
        filterStatus.addActionListener(e -> muatUlang());

        addToolbarLeft(filterStatus);

        JButton setujui = tombolAksi("Setujui",
                Icons.get(Icons.I.CHECK, 15, Theme.Colors.SUCCESS), this::setujui);
        setujui.setEnabled(false);
        bindToSelection(setujui);

        JButton tolak = tombolAksi("Tolak",
                Icons.get(Icons.I.CLOSE, 15, Theme.Colors.DANGER), this::tolak);
        tolak.setEnabled(false);
        bindToSelection(tolak);

        addToolbarRight(setujui, tolak, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<PendaftaranMagang> muatData() throws Exception {
        String status = (String) filterStatus.getSelectedItem();
        List<PendaftaranMagang> data;
        if (status == null || SEMUA.equals(status)) {
            data = service.daftarSemua();
        } else {
            data = service.daftarSemua().stream()
                    .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                    .toList();
        }

        String keyword = kataKunci().toLowerCase();
        if (keyword.isEmpty()) {
            return data;
        }
        return data.stream()
                .filter(p -> mengandung(p.getNamaMahasiswa(), keyword)
                || mengandung(p.getNim(), keyword)
                || mengandung(p.getJudulLowongan(), keyword))
                .toList();
    }

    private static boolean mengandung(String nilai, String kunci) {
        return nilai != null && nilai.toLowerCase().contains(kunci);
    }

    @Override
    protected Object[] baris(PendaftaranMagang p) {
        return new Object[]{DateField.format(p.getTanggalDaftar()), p.getNim(),
            p.getNamaMahasiswa(), p.getJudulLowongan(), p.getStatus(),
            p.getCatatan() == null || p.getCatatan().isBlank() ? "-" : p.getCatatan()};
    }

    @Override
    protected void onRowDoubleClick() {
        PendaftaranMagang selected = terpilih();
        if (selected != null && selected.isMenunggu()) {
            setujui();
        }
    }

    private void setujui() {
        PendaftaranMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!selected.isMenunggu()) {
            Messages.peringatan(this, "Hanya pendaftaran berstatus MENUNGGU yang dapat disetujui.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Setujui Pendaftaran",
                selected.getNamaMahasiswa() + " - " + selected.getJudulLowongan());

        JScrollPane catatan = dialog.addField("Catatan (opsional)", Ui.textArea(3, 20));
        if (!dialog.showDialog()) {
            return;
        }

        String isiCatatan = Ui.areaOf(catatan).getText().trim();
        Async.run(this,
                () -> service.setujui(selected.getId(), isiCatatan.isEmpty() ? null : isiCatatan),
                ok -> {
                    Messages.sukses(this, "Pendaftaran disetujui.");
                    muatUlang();
                });
    }

    private void tolak() {
        PendaftaranMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!selected.isMenunggu()) {
            Messages.peringatan(this, "Hanya pendaftaran berstatus MENUNGGU yang dapat ditolak.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tolak Pendaftaran",
                selected.getNamaMahasiswa() + " - " + selected.getJudulLowongan());

        JScrollPane alasan = dialog.addField("Alasan penolakan", Ui.textArea(3, 20));
        if (!dialog.showDialog()) {
            return;
        }

        String isiAlasan = Ui.areaOf(alasan).getText().trim();
        if (isiAlasan.isEmpty()) {
            Messages.peringatan(this, "Alasan penolakan wajib diisi.");
            return;
        }

        Async.run(this,
                () -> service.tolak(selected.getId(), isiAlasan),
                ok -> {
                    Messages.sukses(this, "Pendaftaran ditolak.");
                    muatUlang();
                });
    }
}
