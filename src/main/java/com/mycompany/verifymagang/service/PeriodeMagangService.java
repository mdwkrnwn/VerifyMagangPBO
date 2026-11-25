package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.PeriodeMagangDAO;
import com.mycompany.verifymagang.model.PeriodeMagang;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis pengelolaan periode magang.
 */
public class PeriodeMagangService {

    private final PeriodeMagangDAO periodeDAO;

    public PeriodeMagangService() {
        this(new PeriodeMagangDAO());
    }

    public PeriodeMagangService(PeriodeMagangDAO periodeDAO) {
        this.periodeDAO = periodeDAO;
    }

    /**
     * Menyimpan periode magang baru.
     *
     * @param periode data periode
     * @return id periode yang dibuat
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public int tambahPeriode(PeriodeMagang periode) throws SQLException {
        validasi(periode);
        return periodeDAO.insert(periode);
    }

    /**
     * Memperbarui data periode magang.
     *
     * @param periode data periode dengan id yang sudah ada
     * @return {@code true} bila berhasil
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public boolean ubahPeriode(PeriodeMagang periode) throws SQLException {
        validasi(periode);
        return periodeDAO.update(periode);
    }

    /**
     * Menghapus periode magang.
     *
     * @param periodeId id periode
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean hapusPeriode(int periodeId) throws SQLException {
        return periodeDAO.delete(periodeId);
    }

    /**
     * Mengambil seluruh periode magang.
     *
     * @return daftar periode
     * @throws SQLException jika query gagal
     */
    public List<PeriodeMagang> daftarSemua() throws SQLException {
        return periodeDAO.findAll();
    }

    /**
     * Mengambil periode yang sedang aktif.
     *
     * @return periode aktif bila ada
     * @throws SQLException jika query gagal
     */
    public Optional<PeriodeMagang> periodeAktif() throws SQLException {
        return periodeDAO.findAktif();
    }

    /**
     * Menjadikan satu periode sebagai satu-satunya periode aktif.
     *
     * @param periodeId id periode
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean aktifkan(int periodeId) throws SQLException {
        return periodeDAO.aktifkan(periodeId);
    }

    /**
     * Mengambil detail satu periode.
     *
     * @param periodeId id periode
     * @return periode bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PeriodeMagang> detail(int periodeId) throws SQLException {
        return periodeDAO.findById(periodeId);
    }

    private void validasi(PeriodeMagang periode) {
        if (periode.getNama() == null || periode.getNama().isBlank()) {
            throw new IllegalArgumentException("Nama periode wajib diisi.");
        }
        if (periode.getTanggalMulai() != null && periode.getTanggalSelesai() != null
                && periode.getTanggalSelesai().isBefore(periode.getTanggalMulai())) {
            throw new IllegalArgumentException("Tanggal selesai tidak boleh sebelum tanggal mulai.");
        }
    }
}
