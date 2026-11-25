package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.DosenDAO;
import com.mycompany.verifymagang.dao.MahasiswaDAO;
import com.mycompany.verifymagang.dao.UserDAO;
import com.mycompany.verifymagang.model.Dosen;
import com.mycompany.verifymagang.model.Mahasiswa;
import com.mycompany.verifymagang.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Logika bisnis autentikasi dan pengelolaan akun.
 *
 * <p>Kelas ini tidak mengetahui apa pun tentang Swing. View cukup memanggil
 * {@link #login(String, String)} lalu memakai {@link LoginResult}.</p>
 */
public class AuthService {

    private final UserDAO userDAO;
    private final MahasiswaDAO mahasiswaDAO;
    private final DosenDAO dosenDAO;

    public AuthService() {
        this(new UserDAO(), new MahasiswaDAO(), new DosenDAO());
    }

    public AuthService(UserDAO userDAO, MahasiswaDAO mahasiswaDAO, DosenDAO dosenDAO) {
        this.userDAO = userDAO;
        this.mahasiswaDAO = mahasiswaDAO;
        this.dosenDAO = dosenDAO;
    }

    /**
     * Melakukan login berdasarkan username dan password.
     *
     * <p>Pemanggil tidak perlu membedakan pesan error agar tidak membocorkan
     * informasi akun mana yang salah.</p>
     *
     * @param username username
     * @param password password asli (belum di-hash)
     * @return hasil login, selalu tidak {@code null}
     * @throws SQLException jika terjadi kesalahan database
     */
    public LoginResult login(String username, String password) throws SQLException {
        if (username == null || username.isBlank()) {
            return LoginResult.gagal("Username wajib diisi.");
        }
        if (password == null || password.isEmpty()) {
            return LoginResult.gagal("Password wajib diisi.");
        }

        Optional<User> found = userDAO.findByUsername(username.trim());
        if (found.isEmpty()) {
            return LoginResult.gagal("Username atau password salah.");
        }

        User user = found.get();
        if (!user.isAktif()) {
            return LoginResult.gagal("Akun Anda tidak aktif. Hubungi admin.");
        }
        if (!PasswordUtil.verify(password, user.getPassword())) {
            return LoginResult.gagal("Username atau password salah.");
        }

        Integer mahasiswaId = null;
        Integer dosenId = null;

        if (user.isMahasiswa()) {
            Optional<Mahasiswa> mhs = mahasiswaDAO.findByUserId(user.getId());
            if (mhs.isEmpty()) {
                return LoginResult.gagal("Data mahasiswa untuk akun ini tidak ditemukan.");
            }
            mahasiswaId = mhs.get().getId();
        } else if (user.isAdmin()) {
            Optional<Dosen> dosen = dosenDAO.findByUserId(user.getId());
            if (dosen.isPresent()) {
                dosenId = dosen.get().getId();
            }
        }

        return LoginResult.sukses(user, mahasiswaId, dosenId);
    }

    /**
     * Mendaftarkan akun user baru. Password otomatis di-hash.
     *
     * @param username    username unik
     * @param plainPassword password asli
     * @param role        {@link User#ROLE_ADMIN} atau {@link User#ROLE_MAHASISWA}
     * @param namaLengkap nama lengkap
     * @param email       email (boleh {@code null})
     * @return id user yang dibuat
     * @throws SQLException           jika query gagal
     * @throws IllegalArgumentException bila username sudah dipakai atau data tidak valid
     */
    public int registrasi(String username, String plainPassword, String role,
            String namaLengkap, String email) throws SQLException {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username wajib diisi.");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }
        if (!User.ROLE_ADMIN.equals(role) && !User.ROLE_MAHASISWA.equals(role)) {
            throw new IllegalArgumentException("Role tidak dikenal: " + role);
        }
        if (userDAO.existsByUsername(username.trim())) {
            throw new IllegalArgumentException("Username sudah digunakan.");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(PasswordUtil.hash(plainPassword));
        user.setRole(role);
        user.setNamaLengkap(namaLengkap);
        user.setEmail(email);
        user.setAktif(true);
        return userDAO.insert(user);
    }

    /**
     * Mengubah password user.
     *
     * @param userId        id user
     * @param oldPlain      password lama
     * @param newPlain      password baru
     * @return {@code true} bila berhasil
     * @throws SQLException              jika query gagal
     * @throws IllegalArgumentException  bila password lama salah atau password baru terlalu pendek
     */
    public boolean ubahPassword(int userId, String oldPlain, String newPlain) throws SQLException {
        if (newPlain == null || newPlain.length() < 6) {
            throw new IllegalArgumentException("Password baru minimal 6 karakter.");
        }

        Optional<User> found = userDAO.findById(userId);
        if (found.isEmpty()) {
            throw new IllegalArgumentException("User tidak ditemukan.");
        }
        if (!PasswordUtil.verify(oldPlain, found.get().getPassword())) {
            throw new IllegalArgumentException("Password lama salah.");
        }
        return userDAO.updatePassword(userId, PasswordUtil.hash(newPlain));
    }

    /**
     * Menonaktifkan akun user.
     *
     * @param userId id user
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean nonaktifkan(int userId) throws SQLException {
        return userDAO.setAktif(userId, false);
    }

    /**
     * Mengaktifkan kembali akun user.
     *
     * @param userId id user
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean aktifkan(int userId) throws SQLException {
        return userDAO.setAktif(userId, true);
    }

    /**
     * Hasil proses login.
     */
    public static final class LoginResult {

        private final boolean berhasil;
        private final String pesan;
        private final User user;
        private final Integer mahasiswaId;
        private final Integer dosenId;

        private LoginResult(boolean berhasil, String pesan, User user,
                Integer mahasiswaId, Integer dosenId) {
            this.berhasil = berhasil;
            this.pesan = pesan;
            this.user = user;
            this.mahasiswaId = mahasiswaId;
            this.dosenId = dosenId;
        }

        static LoginResult sukses(User user, Integer mahasiswaId, Integer dosenId) {
            return new LoginResult(true, "Login berhasil.", user, mahasiswaId, dosenId);
        }

        static LoginResult gagal(String pesan) {
            return new LoginResult(false, pesan, null, null, null);
        }

        public boolean isBerhasil() {
            return berhasil;
        }

        public String getPesan() {
            return pesan;
        }

        public User getUser() {
            return user;
        }

        /** @return id baris tabel {@code mahasiswa}, {@code null} bila bukan mahasiswa. */
        public Integer getMahasiswaId() {
            return mahasiswaId;
        }

        /** @return id baris tabel {@code dosen}, {@code null} bila tidak terhubung dosen. */
        public Integer getDosenId() {
            return dosenId;
        }

        /** @return {@code true} bila user yang login berperan sebagai admin. */
        public boolean isAdmin() {
            return user != null && user.isAdmin();
        }

        /** @return {@code true} bila user yang login berperan sebagai mahasiswa. */
        public boolean isMahasiswa() {
            return user != null && user.isMahasiswa();
        }
    }
}
