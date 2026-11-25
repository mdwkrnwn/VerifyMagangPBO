package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.Mahasiswa;
import com.mycompany.verifymagang.model.User;
import com.mycompany.verifymagang.service.AuthService;
import com.mycompany.verifymagang.service.MahasiswaService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.Page;
import com.mycompany.verifymagang.view.common.Refreshable;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;
import com.mycompany.verifymagang.view.common.Widgets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Optional;

/**
 * Halaman profil mahasiswa: data diri (dapat diubah) dan ganti password.
 */
public class ProfilMahasiswaPanel extends Page implements Refreshable {

    private final MahasiswaService mahasiswaService = new MahasiswaService();
    private final AuthService authService = new AuthService();

    private final JTextField nimField = Ui.textField(20);
    private final JTextField namaField = Ui.textField(20);
    private final JTextField jurusanField = Ui.textField(20);
    private final JTextField angkatanField = Ui.textField(20);
    private final JTextField noHpField = Ui.textField(20);
    private final JTextField alamatField = Ui.textField(20);
    private final JTextField usernameField = Ui.textField(20);

    private Mahasiswa profil;

    /** Membuat halaman profil mahasiswa. */
    public ProfilMahasiswaPanel() {
        super("Profil Saya", "Kelola data diri dan keamanan akun");

        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(Ui.penuhLebar(buildProfileCard()));
        root.add(Box.createVerticalStrut(16));
        root.add(Ui.penuhLebar(buildPasswordCard()));
        root.add(Box.createVerticalGlue());

        setBody(Ui.scroll(root));

        nimField.setEditable(false);
        usernameField.setEditable(false);
        for (JTextField f : new JTextField[]{nimField, namaField, jurusanField,
            angkatanField, noHpField, alamatField, usernameField}) {
            f.setPreferredSize(new Dimension(240, 38));
        }
    }

    private JComponent buildProfileCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel title = Ui.sectionTitle("Data Diri");
        card.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 14));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(18, 0, 0, 0));
        grid.add(Ui.field("NIM (username login)", nimField));
        grid.add(Ui.field("Username", usernameField));
        grid.add(Ui.field("Nama Lengkap", namaField));
        grid.add(Ui.field("Jurusan", jurusanField));
        grid.add(Ui.field("Angkatan", angkatanField));
        grid.add(Ui.field("No. HP", noHpField));
        grid.add(Ui.field("Alamat", alamatField));
        grid.add(new JLabel());

        JButton simpan = Ui.primaryButton("Simpan Perubahan",
                Icons.get(Icons.I.CHECK, 16, java.awt.Color.WHITE));
        simpan.addActionListener(e -> simpanProfil());
        JPanel footer = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));
        footer.add(simpan);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(grid, BorderLayout.CENTER);
        body.add(footer, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildPasswordCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel title = Ui.sectionTitle("Ganti Password");
        JLabel sub = Ui.caption("Gunakan minimal 6 karakter.");
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        title.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        head.add(title);
        head.add(Box.createVerticalStrut(4));
        head.add(sub);
        card.add(head, BorderLayout.NORTH);

        JPasswordField lama = Ui.passwordField(20);
        JPasswordField baru = Ui.passwordField(20);
        JPasswordField ulang = Ui.passwordField(20);

        JPanel grid = new JPanel(new GridLayout(1, 3, 20, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(18, 0, 0, 0));
        grid.add(Ui.field("Password Lama", lama));
        grid.add(Ui.field("Password Baru", baru));
        grid.add(Ui.field("Ulangi Password Baru", ulang));

        JButton ganti = Ui.secondaryButton("Ganti Password",
                Icons.get(Icons.I.LOCK, 15, Theme.Colors.TEXT));
        ganti.addActionListener(e -> gantiPassword(lama, baru, ulang));
        JPanel footer = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));
        footer.add(ganti);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(grid, BorderLayout.CENTER);
        body.add(footer, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void onShow() {
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            return;
        }
        Async.run(this, () -> mahasiswaService.detail(mahasiswaId), this::isiForm);
    }

    private void isiForm(Optional<Mahasiswa> hasil) {
        if (hasil.isEmpty()) {
            Messages.error(this, "Data mahasiswa tidak ditemukan.");
            return;
        }
        profil = hasil.get();
        nimField.setText(profil.getNim());
        namaField.setText(profil.getNama());
        jurusanField.setText(profil.getJurusan());
        angkatanField.setText(String.valueOf(profil.getAngkatan()));
        noHpField.setText(profil.getNoHp());
        alamatField.setText(profil.getAlamat());

        User user = SessionManager.getCurrentUser();
        usernameField.setText(user == null ? "-" : user.getUsername());
    }

    private void simpanProfil() {
        if (profil == null) {
            return;
        }
        if (namaField.getText() == null || namaField.getText().isBlank()) {
            Messages.peringatan(this, "Nama lengkap wajib diisi.");
            return;
        }

        profil.setNama(namaField.getText().trim());
        profil.setJurusan(jurusanField.getText().trim());
        profil.setAngkatan(parseInt(angkatanField.getText()));
        profil.setNoHp(noHpField.getText().trim());
        profil.setAlamat(alamatField.getText().trim());

        Async.run(this,
                () -> mahasiswaService.ubahMahasiswa(profil),
                ok -> Messages.sukses(this, "Profil berhasil diperbarui."));
    }

    private void gantiPassword(JPasswordField lama, JPasswordField baru, JPasswordField ulang) {
        String lamaTeks = new String(lama.getPassword());
        String baruTeks = new String(baru.getPassword());
        String ulangTeks = new String(ulang.getPassword());

        if (lamaTeks.isEmpty() || baruTeks.isEmpty()) {
            Messages.peringatan(this, "Password lama dan baru wajib diisi.");
            return;
        }
        if (baruTeks.length() < 6) {
            Messages.peringatan(this, "Password baru minimal 6 karakter.");
            return;
        }
        if (!baruTeks.equals(ulangTeks)) {
            Messages.peringatan(this, "Ulangi password baru dengan benar.");
            return;
        }

        Async.run(this,
                () -> authService.ubahPassword(SessionManager.getUserId(), lamaTeks, baruTeks),
                ok -> {
                    Messages.sukses(this, "Password berhasil diganti.");
                    lama.setText("");
                    baru.setText("");
                    ulang.setText("");
                });
    }

    private static int parseInt(String teks) {
        try {
            return Integer.parseInt(teks == null ? "" : teks.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
