package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.DosenDAO;
import com.mycompany.verifymagang.model.Dosen;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis pengelolaan data dosen pembimbing.
 */
public class DosenService {

    private final DosenDAO dosenDAO;

    public DosenService() {
        this(new DosenDAO());
    }

    public DosenService(DosenDAO dosenDAO) {
        this.dosenDAO = dosenDAO;
    }

    /**
     * Menyimpan dosen baru.
     *
     * @param dosen data dosen
     * @return id dosen yang dibuat
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public int tambahDosen(Dosen dosen) throws SQLException {
        validasi(dosen);
        return dosenDAO.insert(dosen);
    }

    /**
     * Memperbarui data dosen.
     *
     * @param dosen data dosen dengan id yang sudah ada
     * @return {@code true} bila berhasil
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public boolean ubahDosen(Dosen dosen) throws SQLException {
        validasi(dosen);
        return dosenDAO.update(dosen);
    }

    /**
     * Menghapus dosen.
     *
     * @param dosenId id dosen
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean hapusDosen(int dosenId) throws SQLException {
        return dosenDAO.delete(dosenId);
    }

    /**
     * Mengambil seluruh dosen.
     *
     * @return daftar dosen
     * @throws SQLException jika query gagal
     */
    public List<Dosen> daftarSemua() throws SQLException {
        return dosenDAO.findAll();
    }

    /**
     * Mencari dosen berdasarkan nama atau NIP.
     *
     * @param keyword kata kunci
     * @return daftar dosen yang cocok
     * @throws SQLException jika query gagal
     */
    public List<Dosen> cari(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return dosenDAO.findAll();
        }
        return dosenDAO.search(keyword.trim());
    }

    /**
     * Mengambil detail satu dosen.
     *
     * @param dosenId id dosen
     * @return dosen bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Dosen> detail(int dosenId) throws SQLException {
        return dosenDAO.findById(dosenId);
    }

    private void validasi(Dosen dosen) {
        if (dosen.getNama() == null || dosen.getNama().isBlank()) {
            throw new IllegalArgumentException("Nama dosen wajib diisi.");
        }
        if (dosen.getNip() == null || dosen.getNip().isBlank()) {
            throw new IllegalArgumentException("NIP dosen wajib diisi.");
        }
    }
}
