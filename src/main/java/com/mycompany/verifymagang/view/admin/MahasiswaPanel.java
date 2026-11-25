package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.Mahasiswa;
import com.mycompany.verifymagang.service.MahasiswaService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.FormDialog;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.TablePage;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;

import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.util.List;

/**
 * Halaman pengelolaan data mahasiswa (tambah, ubah, hapus, reset password).
 *
 * <p>Menambah mahasiswa otomatis membuat akun login dengan username = NIM.</p>
 */
public class MahasiswaPanel extends TablePage<Mahasiswa> {

    private final MahasiswaService service = new MahasiswaService();

    /** Membuat halaman data mahasiswa. */
    public MahasiswaPanel() {
        super("Data Mahasiswa", "Kelola data mahasiswa beserta akun login-nya",
                "NIM", "Nama", "Jurusan", "Angkatan", "No. HP", "Alamat");

        setColumnWidth(0, 120);
        setColumnWidth(1, 200);
        setColumnWidth(2, 190);
        setColumnWidth(3, 90);
        setColumnWidth(4, 140);
        boldColumn(1);

        JButton tambah = tombolTambah(this::dialogTambah);
        JButton ubah = tombolUbah(this::dialogUbah);
        JButton hapus = tombolHapus(this::hapusData);
        JButton reset = tombolAksi("Reset Password",
                Icons.get(Icons.I.LOCK, 15, Theme.Colors.TEXT), this::resetPassword);
        reset.setEnabled(false);
        bindToSelection(reset);

        addToolbarRight(tambah, ubah, reset, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<Mahasiswa> muatData() throws Exception {
        return service.cari(kataKunci());
    }

    @Override
    protected Object[] baris(Mahasiswa m) {
        return new Object[]{m.getNim(), m.getNama(), m.getJurusan(),
            m.getAngkatan(), m.getNoHp(), m.getAlamat()};
    }

    @Override
    protected void onRowDoubleClick() {
        dialogUbah();
    }

    // ------------------------------------------------------------- dialog

    private void dialogTambah() {
        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tambah Mahasiswa", "Akun login dibuat otomatis dengan username = NIM.");

        JTextField nim = dialog.addField("NIM", Ui.textField(20));
        JTextField nama = dialog.addField("Nama Lengkap", Ui.textField(20));
        JTextField jurusan = dialog.addField("Jurusan", Ui.textField(20));
        JTextField angkatan = dialog.addField("Angkatan", Ui.textField(20));
        JTextField noHp = dialog.addField("No. HP", Ui.textField(20));
        JTextField alamat = dialog.addField("Alamat", Ui.textField(20));
        JPasswordField password = dialog.addField("Password Awal", Ui.passwordField(20));

        if (!dialog.showDialog()) {
            return;
        }

        if (kosong(nim.getText()) || kosong(nama.getText())) {
            Messages.peringatan(this, "NIM dan Nama wajib diisi.");
            return;
        }
        if (password.getPassword().length < 6) {
            Messages.peringatan(this, "Password minimal 6 karakter.");
            return;
        }

        Mahasiswa m = new Mahasiswa();
        m.setNim(nim.getText().trim());
        m.setNama(nama.getText().trim());
        m.setJurusan(jurusan.getText().trim());
        m.setAngkatan(parseInt(angkatan.getText()));
        m.setNoHp(noHp.getText().trim());
        m.setAlamat(alamat.getText().trim());

        Async.run(this,
                () -> service.tambahMahasiswa(m, new String(password.getPassword())),
                id -> {
                    Messages.sukses(this, "Mahasiswa berhasil ditambahkan.");
                    muatUlang();
                });
    }

    private void dialogUbah() {
        Mahasiswa selected = terpilih();
        if (selected == null) {
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ubah Data Mahasiswa", "Perubahan tidak memengaruhi password akun.");

        JTextField nim = dialog.addField("NIM", Ui.textField(20));
        nim.setText(selected.getNim());
        JTextField nama = dialog.addField("Nama Lengkap", Ui.textField(20));
        nama.setText(selected.getNama());
        JTextField jurusan = dialog.addField("Jurusan", Ui.textField(20));
        jurusan.setText(selected.getJurusan());
        JTextField angkatan = dialog.addField("Angkatan", Ui.textField(20));
        angkatan.setText(String.valueOf(selected.getAngkatan()));
        JTextField noHp = dialog.addField("No. HP", Ui.textField(20));
        noHp.setText(selected.getNoHp());
        JTextField alamat = dialog.addField("Alamat", Ui.textField(20));
        alamat.setText(selected.getAlamat());

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(nim.getText()) || kosong(nama.getText())) {
            Messages.peringatan(this, "NIM dan Nama wajib diisi.");
            return;
        }

        selected.setNim(nim.getText().trim());
        selected.setNama(nama.getText().trim());
        selected.setJurusan(jurusan.getText().trim());
        selected.setAngkatan(parseInt(angkatan.getText()));
        selected.setNoHp(noHp.getText().trim());
        selected.setAlamat(alamat.getText().trim());

        Async.run(this,
                () -> service.ubahMahasiswa(selected),
                ok -> {
                    Messages.sukses(this, "Data mahasiswa diperbarui.");
                    muatUlang();
                });
    }

    private void hapusData() {
        Mahasiswa selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this,
                "mahasiswa " + selected.getNama() + " (" + selected.getNim() + ")")) {
            return;
        }
        Async.run(this,
                () -> service.hapusMahasiswa(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Mahasiswa dan akun login-nya dihapus.");
                    muatUlang();
                });
    }

    private void resetPassword() {
        Mahasiswa selected = terpilih();
        if (selected == null) {
            return;
        }
        String baru = Messages.input(this, "Reset Password",
                "Password baru untuk " + selected.getNama() + " (minimal 6 karakter):");
        if (baru == null) {
            return;
        }
        if (baru.length() < 6) {
            Messages.peringatan(this, "Password minimal 6 karakter.");
            return;
        }
        Async.run(this,
                () -> service.resetPassword(selected.getId(), baru),
                ok -> Messages.sukses(this, "Password berhasil direset."));
    }

    private static boolean kosong(String teks) {
        return teks == null || teks.isBlank();
    }

    private static int parseInt(String teks) {
        try {
            return Integer.parseInt(teks == null ? "" : teks.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
