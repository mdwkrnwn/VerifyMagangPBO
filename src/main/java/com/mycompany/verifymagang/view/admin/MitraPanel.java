package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.Mitra;
import com.mycompany.verifymagang.service.LowonganService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.FormDialog;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.TablePage;
import com.mycompany.verifymagang.view.common.Ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

/**
 * Halaman pengelolaan data mitra (perusahaan/instansi tempat magang).
 */
public class MitraPanel extends TablePage<Mitra> {

    private final LowonganService service = new LowonganService();

    /** Membuat halaman data mitra. */
    public MitraPanel() {
        super("Data Mitra", "Kelola perusahaan atau instansi mitra magang",
                "Nama Mitra", "Bidang Usaha", "Kota", "Kontak Person", "No. HP", "Status");

        setColumnWidth(0, 220);
        setColumnWidth(1, 170);
        setColumnWidth(2, 120);
        setColumnWidth(3, 160);
        setColumnWidth(4, 130);
        setColumnWidth(5, 100);
        boldColumn(0);
        statusColumn(5);

        JButton tambah = tombolTambah(this::dialogTambah);
        JButton ubah = tombolUbah(this::dialogUbah);
        JButton hapus = tombolHapus(this::hapusData);

        addToolbarRight(tambah, ubah, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<Mitra> muatData() throws Exception {
        String keyword = kataKunci();
        List<Mitra> semua = service.daftarMitra();
        if (keyword.isEmpty()) {
            return semua;
        }
        String k = keyword.toLowerCase();
        return semua.stream()
                .filter(m -> mengandung(m.getNama(), k)
                || mengandung(m.getBidangUsaha(), k)
                || mengandung(m.getKota(), k))
                .toList();
    }

    private static boolean mengandung(String nilai, String kunci) {
        return nilai != null && nilai.toLowerCase().contains(kunci);
    }

    @Override
    protected Object[] baris(Mitra m) {
        return new Object[]{m.getNama(), m.getBidangUsaha(), m.getKota(),
            m.getKontakPerson(), m.getNoHp(), m.isAktif() ? "AKTIF" : "NONAKTIF"};
    }

    @Override
    protected void onRowDoubleClick() {
        dialogUbah();
    }

    private void dialogTambah() {
        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tambah Mitra", "Data perusahaan atau instansi mitra.");

        JTextField nama = dialog.addField("Nama Mitra", Ui.textField(20));
        JTextField bidang = dialog.addField("Bidang Usaha", Ui.textField(20));
        JTextField alamat = dialog.addField("Alamat", Ui.textField(20));
        JTextField kota = dialog.addField("Kota", Ui.textField(20));
        JTextField kontak = dialog.addField("Kontak Person", Ui.textField(20));
        JTextField noHp = dialog.addField("No. HP", Ui.textField(20));
        JTextField email = dialog.addField("Email", Ui.textField(20));
        JComboBox<String> status = dialog.addField("Status",
                Ui.comboBox(new String[]{"Aktif", "Nonaktif"}));

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(nama.getText())) {
            Messages.peringatan(this, "Nama mitra wajib diisi.");
            return;
        }

        Mitra m = new Mitra();
        m.setNama(nama.getText().trim());
        m.setBidangUsaha(bidang.getText().trim());
        m.setAlamat(alamat.getText().trim());
        m.setKota(kota.getText().trim());
        m.setKontakPerson(kontak.getText().trim());
        m.setNoHp(noHp.getText().trim());
        m.setEmail(email.getText().trim());
        m.setAktif(status.getSelectedIndex() == 0);

        Async.run(this,
                () -> service.tambahMitra(m),
                id -> {
                    Messages.sukses(this, "Mitra berhasil ditambahkan.");
                    muatUlang();
                });
    }

    private void dialogUbah() {
        Mitra selected = terpilih();
        if (selected == null) {
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ubah Data Mitra", "Perbarui data mitra.");

        JTextField nama = dialog.addField("Nama Mitra", Ui.textField(20));
        nama.setText(selected.getNama());
        JTextField bidang = dialog.addField("Bidang Usaha", Ui.textField(20));
        bidang.setText(selected.getBidangUsaha());
        JTextField alamat = dialog.addField("Alamat", Ui.textField(20));
        alamat.setText(selected.getAlamat());
        JTextField kota = dialog.addField("Kota", Ui.textField(20));
        kota.setText(selected.getKota());
        JTextField kontak = dialog.addField("Kontak Person", Ui.textField(20));
        kontak.setText(selected.getKontakPerson());
        JTextField noHp = dialog.addField("No. HP", Ui.textField(20));
        noHp.setText(selected.getNoHp());
        JTextField email = dialog.addField("Email", Ui.textField(20));
        email.setText(selected.getEmail());
        JComboBox<String> status = dialog.addField("Status",
                Ui.comboBox(new String[]{"Aktif", "Nonaktif"}));
        status.setSelectedIndex(selected.isAktif() ? 0 : 1);

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(nama.getText())) {
            Messages.peringatan(this, "Nama mitra wajib diisi.");
            return;
        }

        selected.setNama(nama.getText().trim());
        selected.setBidangUsaha(bidang.getText().trim());
        selected.setAlamat(alamat.getText().trim());
        selected.setKota(kota.getText().trim());
        selected.setKontakPerson(kontak.getText().trim());
        selected.setNoHp(noHp.getText().trim());
        selected.setEmail(email.getText().trim());
        selected.setAktif(status.getSelectedIndex() == 0);

        Async.run(this,
                () -> service.ubahMitra(selected),
                ok -> {
                    Messages.sukses(this, "Data mitra diperbarui.");
                    muatUlang();
                });
    }

    private void hapusData() {
        Mitra selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this, "mitra " + selected.getNama())) {
            return;
        }
        Async.run(this,
                () -> service.hapusMitra(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Mitra dihapus.");
                    muatUlang();
                });
    }

    private static boolean kosong(String teks) {
        return teks == null || teks.isBlank();
    }
}
