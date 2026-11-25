package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.PenempatanDAO;
import com.mycompany.verifymagang.dao.PenilaianDAO;
import com.mycompany.verifymagang.model.PenempatanMagang;
import com.mycompany.verifymagang.model.PenilaianMagang;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis penilaian magang.
 */
public class PenilaianService {

    private static final BigDecimal NILAI_MIN = BigDecimal.ZERO;
    private static final BigDecimal NILAI_MAKS = new BigDecimal("100");

    private final PenilaianDAO penilaianDAO;
    private final PenempatanDAO penempatanDAO;

    public PenilaianService() {
        this(new PenilaianDAO(), new PenempatanDAO());
    }

    public PenilaianService(PenilaianDAO penilaianDAO, PenempatanDAO penempatanDAO) {
        this.penilaianDAO = penilaianDAO;
        this.penempatanDAO = penempatanDAO;
    }

    /**
     * Menyimpan penilaian baru. Nilai akhir dihitung otomatis.
     *
     * @param penilaian data penilaian
     * @return id penilaian yang dibuat
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila aturan bisnis dilanggar
     */
    public int simpanPenilaian(PenilaianMagang penilaian) throws SQLException {
        Optional<PenempatanMagang> penempatan = penempatanDAO.findById(penilaian.getPenempatanId());
        if (penempatan.isEmpty()) {
            throw new IllegalStateException("Penempatan tidak ditemukan.");
        }
        if (penilaianDAO.existsByPenempatan(penilaian.getPenempatanId())) {
            throw new IllegalStateException(
                    "Penempatan ini sudah dinilai. Gunakan fitur ubah penilaian.");
        }

        validasiNilai(penilaian);

        penilaian.setDosenId(penempatan.get().getDosenId());
        if (penilaian.getTanggalPenilaian() == null) {
            penilaian.setTanggalPenilaian(LocalDate.now());
        }
        penilaian.hitungNilaiAkhir();
        return penilaianDAO.insert(penilaian);
    }

    /**
     * Memperbarui penilaian yang sudah ada.
     *
     * @param penilaian data penilaian dengan id yang sudah ada
     * @return {@code true} bila berhasil
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila data tidak valid
     */
    public boolean ubahPenilaian(PenilaianMagang penilaian) throws SQLException {
        validasiNilai(penilaian);
        penilaian.hitungNilaiAkhir();
        return penilaianDAO.update(penilaian);
    }

    /**
     * Menghapus penilaian.
     *
     * @param penilaianId id penilaian
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean hapusPenilaian(int penilaianId) throws SQLException {
        return penilaianDAO.delete(penilaianId);
    }

    /**
     * Mengambil penilaian untuk satu penempatan.
     *
     * @param penempatanId id penempatan
     * @return penilaian bila sudah ada
     * @throws SQLException jika query gagal
     */
    public Optional<PenilaianMagang> penilaianByPenempatan(int penempatanId) throws SQLException {
        return penilaianDAO.findByPenempatan(penempatanId);
    }

    /**
     * Mengambil riwayat penilaian milik mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return daftar penilaian
     * @throws SQLException jika query gagal
     */
    public List<PenilaianMagang> daftarByMahasiswa(int mahasiswaId) throws SQLException {
        return penilaianDAO.findByMahasiswa(mahasiswaId);
    }

    /**
     * Mengambil seluruh penilaian untuk halaman admin.
     *
     * @return daftar penilaian
     * @throws SQLException jika query gagal
     */
    public List<PenilaianMagang> daftarSemua() throws SQLException {
        return penilaianDAO.findAllDetail();
    }

    /**
     * Mengambil penilaian yang diberikan dosen tertentu.
     *
     * @param dosenId id dosen
     * @return daftar penilaian
     * @throws SQLException jika query gagal
     */
    public List<PenilaianMagang> daftarByDosen(int dosenId) throws SQLException {
        return penilaianDAO.findByDosen(dosenId);
    }

    /**
     * Menghitung rata-rata nilai akhir seluruh mahasiswa.
     *
     * @return rata-rata, atau {@code null} bila belum ada penilaian
     * @throws SQLException jika query gagal
     */
    public BigDecimal rataRataNilai() throws SQLException {
        return penilaianDAO.rataRataNilaiAkhir();
    }

    private void validasiNilai(PenilaianMagang penilaian) {
        if (penilaian.getPenempatanId() <= 0) {
            throw new IllegalStateException("Penempatan wajib dipilih.");
        }
        if (penilaian.getNilaiMitra() == null) {
            throw new IllegalStateException("Nilai dari mitra wajib diisi.");
        }
        if (penilaian.getNilaiDosen() == null) {
            throw new IllegalStateException("Nilai dari dosen wajib diisi.");
        }
        cekRentang(penilaian.getNilaiMitra(), "Nilai mitra");
        cekRentang(penilaian.getNilaiDosen(), "Nilai dosen");
    }

    private void cekRentang(BigDecimal nilai, String label) {
        if (nilai.compareTo(NILAI_MIN) < 0 || nilai.compareTo(NILAI_MAKS) > 0) {
            throw new IllegalStateException(label + " harus berada pada rentang 0 sampai 100.");
        }
    }
}
