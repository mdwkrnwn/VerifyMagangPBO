package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.MahasiswaDAO;
import com.mycompany.verifymagang.dao.UserDAO;
import com.mycompany.verifymagang.model.Mahasiswa;
import com.mycompany.verifymagang.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis pengelolaan data mahasiswa beserta akun login-nya.
 */
public class MahasiswaService {

    private final MahasiswaDAO mahasiswaDAO;
    private final UserDAO userDAO;
    private final AuthService authService;

    public MahasiswaService() {
        this(new MahasiswaDAO(), new UserDAO(), new AuthService());
    }

    public MahasiswaService(MahasiswaDAO mahasiswaDAO, UserDAO userDAO, AuthService authService) {
        this.mahasiswaDAO = mahasiswaDAO;
        this.userDAO = userDAO;
        this.authService = authService;
    }

    /**
     * Mendaftarkan mahasiswa baru sekaligus membuat akun login-nya.
     *
     * @param mahasiswa     data mahasiswa
     * @param plainPassword password akun
     * @return id mahasiswa yang dibuat
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public int tambahMahasiswa(Mahasiswa mahasiswa, String plainPassword) throws SQLException {
        validasi(mahasiswa);

        if (mahasiswaDAO.findByNim(mahasiswa.getNim()).isPresent()) {
            throw new IllegalArgumentException("NIM sudah terdaftar.");
        }

        String username = mahasiswa.getNim();
        int userId = authService.registrasi(
                username,
                plainPassword,
                User.ROLE_MAHASISWA,
                mahasiswa.getNama(),
                null);

        mahasiswa.setUserId(userId);
        return mahasiswaDAO.insert(mahasiswa);
    }

    /**
     * Memperbarui data mahasiswa. Akun login tidak diubah di sini.
     *
     * @param mahasiswa data mahasiswa dengan id yang sudah ada
     * @return {@code true} bila berhasil
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public boolean ubahMahasiswa(Mahasiswa mahasiswa) throws SQLException {
        validasi(mahasiswa);
        return mahasiswaDAO.update(mahasiswa);
    }

    /**
     * Menghapus mahasiswa beserta akun login-nya.
     *
     * @param mahasiswaId id mahasiswa
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean hapusMahasiswa(int mahasiswaId) throws SQLException {
        Optional<Mahasiswa> found = mahasiswaDAO.findById(mahasiswaId);
        if (found.isEmpty()) {
            return false;
        }
        // Hapus akun user; baris mahasiswa ikut terhapus lewat ON DELETE CASCADE.
        return userDAO.delete(found.get().getUserId());
    }

    /**
     * Mengambil seluruh mahasiswa.
     *
     * @return daftar mahasiswa
     * @throws SQLException jika query gagal
     */
    public List<Mahasiswa> daftarSemua() throws SQLException {
        return mahasiswaDAO.findAll();
    }

    /**
     * Mencari mahasiswa berdasarkan nama atau NIM.
     *
     * @param keyword kata kunci
     * @return daftar mahasiswa yang cocok
     * @throws SQLException jika query gagal
     */
    public List<Mahasiswa> cari(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return mahasiswaDAO.findAll();
        }
        return mahasiswaDAO.search(keyword.trim());
    }

    /**
     * Mengambil detail satu mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return mahasiswa bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Mahasiswa> detail(int mahasiswaId) throws SQLException {
        return mahasiswaDAO.findById(mahasiswaId);
    }

    /**
     * Mengambil data mahasiswa dari id user yang login.
     *
     * @param userId id user
     * @return mahasiswa bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Mahasiswa> detailByUser(int userId) throws SQLException {
        return mahasiswaDAO.findByUserId(userId);
    }

    /**
     * Mereset password akun mahasiswa.
     *
     * @param mahasiswaId   id mahasiswa
     * @param plainPassword password baru
     * @return {@code true} bila berhasil
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila mahasiswa tidak ditemukan
     */
    public boolean resetPassword(int mahasiswaId, String plainPassword) throws SQLException {
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }
        Optional<Mahasiswa> found = mahasiswaDAO.findById(mahasiswaId);
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Mahasiswa tidak ditemukan.");
        }
        return userDAO.updatePassword(found.get().getUserId(), PasswordUtil.hash(plainPassword));
    }

    private void validasi(Mahasiswa mahasiswa) {
        if (mahasiswa.getNim() == null || mahasiswa.getNim().isBlank()) {
            throw new IllegalArgumentException("NIM wajib diisi.");
        }
        if (mahasiswa.getNama() == null || mahasiswa.getNama().isBlank()) {
            throw new IllegalArgumentException("Nama mahasiswa wajib diisi.");
        }
    }
}
