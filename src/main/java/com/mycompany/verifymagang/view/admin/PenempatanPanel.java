package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.Dosen;
import com.mycompany.verifymagang.model.PendaftaranMagang;
import com.mycompany.verifymagang.model.PenempatanMagang;
import com.mycompany.verifymagang.service.DosenService;
import com.mycompany.verifymagang.service.PendaftaranService;
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
import java.util.List;

/**
 * Halaman pengelolaan penempatan magang.
 *
 * <p>Penempatan dibuat dari pendaftaran berstatus DISETUJUI, lalu admin memilih
 * dosen pembimbing dan rentang tanggal magang.</p>
 */
public class PenempatanPanel extends TablePage<PenempatanMagang> {

    private final PenempatanService service = new PenempatanService();
    private final PendaftaranService pendaftaranService = new PendaftaranService();
    private final DosenService dosenService = new DosenService();

    /** Membuat halaman penempatan. */
    public PenempatanPanel() {
        super("Penempatan Magang", "Tempatkan mahasiswa dan tentukan dosen pembimbing",
                "Mahasiswa", "Dosen Pembimbing", "Mitra", "Mulai", "Selesai", "Status");

        setColumnWidth(0, 190);
        setColumnWidth(1, 190);
        setColumnWidth(2, 190);
        setColumnWidth(3, 110);
        setColumnWidth(4, 110);
        setColumnWidth(5, 100);
        boldColumn(0);
        statusColumn(5);

        JButton tambah = tombolTambah(this::dialogTambah);
        JButton gantiDosen = tombolAksi("Ganti Dosen",
                Icons.get(Icons.I.DOSEN, 15, Theme.Colors.TEXT), this::gantiDosen);
        gantiDosen.setEnabled(false);
        bindToSelection(gantiDosen);

        JButton selesai = tombolAksi("Selesaikan",
                Icons.get(Icons.I.CHECK, 15, Theme.Colors.SUCCESS), this::selesaikan);
        selesai.setEnabled(false);
        bindToSelection(selesai);

        JButton batal = tombolAksi("Batalkan",
                Icons.get(Icons.I.CLOSE, 15, Theme.Colors.DANGER), this::batalkan);
        batal.setEnabled(false);
        bindToSelection(batal);

        addToolbarRight(tambah, gantiDosen, selesai, batal, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<PenempatanMagang> muatData() throws Exception {
        List<PenempatanMagang> data = service.daftarSemua();
        String keyword = kataKunci().toLowerCase();
        if (keyword.isEmpty()) {
            return data;
        }
        return data.stream()
                .filter(p -> mengandung(p.getNamaMahasiswa(), keyword)
                || mengandung(p.getNamaDosen(), keyword)
                || mengandung(p.getNamaMitra(), keyword))
                .toList();
    }

    private static boolean mengandung(String nilai, String kunci) {
        return nilai != null && nilai.toLowerCase().contains(kunci);
    }

    @Override
    protected Object[] baris(PenempatanMagang p) {
        return new Object[]{p.getNamaMahasiswa(), p.getNamaDosen(), p.getNamaMitra(),
            DateField.format(p.getTanggalMulai()), DateField.format(p.getTanggalSelesai()),
            p.getStatus()};
    }

    private void dialogTambah() {
        List<PendaftaranMagang> siap;
        List<Dosen> dosen;
        try {
            siap = pendaftaranService.daftarSiapDitempatkan();
            dosen = dosenService.daftarSemua();
        } catch (Exception ex) {
            Messages.error(this, Messages.pesanDari(ex));
            return;
        }

        if (siap.isEmpty()) {
            Messages.info(this, "Tidak ada pendaftaran disetujui yang belum ditempatkan.");
            return;
        }
        if (dosen.isEmpty()) {
            Messages.peringatan(this, "Belum ada data dosen. Tambahkan dosen terlebih dahulu.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Buat Penempatan", "Pilih pendaftaran yang sudah disetujui.");

        JComboBox<PendaftaranMagang> cbPendaftaran = dialog.addField("Pendaftaran",
                Ui.comboBox(siap, p -> p.getNamaMahasiswa() + " - " + p.getJudulLowongan()));
        JComboBox<Dosen> cbDosen = dialog.addField("Dosen Pembimbing",
                Ui.comboBox(dosen, Dosen::getNama));
        DateField mulai = dialog.addField("Tanggal Mulai", new DateField());
        DateField selesai = dialog.addField("Tanggal Selesai", new DateField());

        if (!dialog.showDialog()) {
            return;
        }

        PendaftaranMagang pendaftaran = (PendaftaranMagang) cbPendaftaran.getSelectedItem();
        Dosen pembimbing = (Dosen) cbDosen.getSelectedItem();
        if (pendaftaran == null || pembimbing == null) {
            Messages.peringatan(this, "Pendaftaran dan dosen pembimbing wajib dipilih.");
            return;
        }
        if (selesai.getLocalDate().isBefore(mulai.getLocalDate())) {
            Messages.peringatan(this, "Tanggal selesai tidak boleh sebelum tanggal mulai.");
            return;
        }

        Async.run(this,
                () -> service.buatPenempatan(pendaftaran.getId(), pembimbing.getId(),
                        mulai.getLocalDate(), selesai.getLocalDate()),
                id -> {
                    Messages.sukses(this, "Penempatan berhasil dibuat.");
                    muatUlang();
                });
    }

    private void gantiDosen() {
        PenempatanMagang selected = terpilih();
        if (selected == null) {
            return;
        }

        List<Dosen> dosen;
        try {
            dosen = dosenService.daftarSemua();
        } catch (Exception ex) {
            Messages.error(this, Messages.pesanDari(ex));
            return;
        }
        if (dosen.isEmpty()) {
            Messages.peringatan(this, "Belum ada data dosen.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ganti Dosen Pembimbing", selected.getNamaMahasiswa());

        JComboBox<Dosen> cbDosen = dialog.addField("Dosen Pembimbing Baru",
                Ui.comboBox(dosen, Dosen::getNama));
        for (int i = 0; i < cbDosen.getItemCount(); i++) {
            if (cbDosen.getItemAt(i).getId() == selected.getDosenId()) {
                cbDosen.setSelectedIndex(i);
                break;
            }
        }

        if (!dialog.showDialog()) {
            return;
        }
        Dosen baru = (Dosen) cbDosen.getSelectedItem();
        if (baru == null) {
            return;
        }

        Async.run(this,
                () -> service.gantiDosen(selected.getId(), baru.getId()),
                ok -> {
                    Messages.sukses(this, "Dosen pembimbing diganti menjadi " + baru.getNama() + ".");
                    muatUlang();
                });
    }

    private void selesaikan() {
        PenempatanMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!selected.isAktif()) {
            Messages.peringatan(this, "Hanya penempatan AKTIF yang dapat diselesaikan.");
            return;
        }
        if (!Messages.konfirmasi(this,
                "Tandai penempatan " + selected.getNamaMahasiswa() + " sebagai SELESAI?")) {
            return;
        }
        Async.run(this,
                () -> service.selesaikan(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Penempatan ditandai selesai.");
                    muatUlang();
                });
    }

    private void batalkan() {
        PenempatanMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasi(this,
                "Batalkan penempatan " + selected.getNamaMahasiswa() + "?")) {
            return;
        }
        Async.run(this,
                () -> service.batalkan(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Penempatan dibatalkan.");
                    muatUlang();
                });
    }
}
