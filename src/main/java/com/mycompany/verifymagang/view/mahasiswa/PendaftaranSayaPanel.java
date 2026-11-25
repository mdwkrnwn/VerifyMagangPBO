package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.PendaftaranMagang;
import com.mycompany.verifymagang.service.PendaftaranService;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.TablePage;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.util.List;

/**
 * Halaman riwayat pendaftaran magang milik mahasiswa yang sedang login.
 */
public class PendaftaranSayaPanel extends TablePage<PendaftaranMagang> {

    private static final String SEMUA = "Semua Status";

    private final PendaftaranService service = new PendaftaranService();
    private final JComboBox<String> filterStatus;

    /** Membuat halaman pendaftaran saya. */
    public PendaftaranSayaPanel() {
        super("Pendaftaran Saya", "Riwayat pendaftaran magang Anda",
                "Tanggal Daftar", "Lowongan", "Status", "Catatan Admin");

        setColumnWidth(0, 130);
        setColumnWidth(1, 320);
        setColumnWidth(2, 120);
        setColumnWidth(3, 280);
        boldColumn(1);
        statusColumn(2);

        filterStatus = Ui.comboBox(new String[]{SEMUA,
            PendaftaranMagang.STATUS_MENUNGGU,
            PendaftaranMagang.STATUS_DISETUJUI,
            PendaftaranMagang.STATUS_DITOLAK});
        filterStatus.setPreferredSize(new java.awt.Dimension(160, 36));
        filterStatus.addActionListener(e -> muatUlang());
        addToolbarLeft(filterStatus);

        JButton batal = tombolAksi("Batalkan",
                Icons.get(Icons.I.CLOSE, 15, Theme.Colors.DANGER), this::batalkan);
        batal.setEnabled(false);
        bindToSelection(batal);

        addToolbarRight(batal, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<PendaftaranMagang> muatData() throws Exception {
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            return List.of();
        }
        List<PendaftaranMagang> data = service.daftarByMahasiswa(mahasiswaId);
        String status = (String) filterStatus.getSelectedItem();
        if (status != null && !SEMUA.equals(status)) {
            data = data.stream()
                    .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                    .toList();
        }
        String keyword = kataKunci().toLowerCase();
        if (keyword.isEmpty()) {
            return data;
        }
        return data.stream()
                .filter(p -> p.getJudulLowongan() != null
                && p.getJudulLowongan().toLowerCase().contains(keyword))
                .toList();
    }

    @Override
    protected Object[] baris(PendaftaranMagang p) {
        return new Object[]{DateField.format(p.getTanggalDaftar()), p.getJudulLowongan(),
            p.getStatus(),
            p.getCatatan() == null || p.getCatatan().isBlank() ? "-" : p.getCatatan()};
    }

    private void batalkan() {
        PendaftaranMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!selected.isMenunggu()) {
            Messages.peringatan(this, "Hanya pendaftaran berstatus MENUNGGU yang dapat dibatalkan.");
            return;
        }
        if (!Messages.konfirmasi(this,
                "Batalkan pendaftaran pada lowongan \"" + selected.getJudulLowongan() + "\"?")) {
            return;
        }
        com.mycompany.verifymagang.view.common.Async.run(this,
                () -> service.batalkan(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Pendaftaran dibatalkan.");
                    muatUlang();
                });
    }
}
