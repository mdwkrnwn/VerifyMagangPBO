package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.LogbookDAO;
import com.mycompany.verifymagang.dao.PenempatanDAO;
import com.mycompany.verifymagang.model.LogbookHarian;
import com.mycompany.verifymagang.model.PenempatanMagang;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis logbook harian magang.
 */
public class LogbookService {

    private final LogbookDAO logbookDAO;
    private final PenempatanDAO penempatanDAO;

    public LogbookService() {
        this(new LogbookDAO(), new PenempatanDAO());
    }

    public LogbookService(LogbookDAO logbookDAO, PenempatanDAO penempatanDAO) {
        this.logbookDAO = logbookDAO;
        this.penempatanDAO = penempatanDAO;
    }

    /**
     * Mengisi logbook harian baru.
     *
     * @param logbook catatan yang akan disimpan
     * @return id logbook yang dibuat
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila aturan bisnis dilanggar
     */
    public int isiLogbook(LogbookHarian logbook) throws SQLException {
        Optional<PenempatanMagang> penempatan = penempatanDAO.findById(logbook.getPenempatanId());
        if (penempatan.isEmpty()) {
            throw new IllegalStateException("Penempatan tidak ditemukan.");
        }
        if (!penempatan.get().isAktif()) {
            throw new IllegalStateException("Penempatan sudah tidak aktif, logbook tidak dapat diisi.");
        }
        if (logbook.getTanggal() == null) {
            throw new IllegalStateException("Tanggal logbook wajib diisi.");
        }
        if (logbook.getTanggal().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Tanggal logbook tidak boleh di masa depan.");
        }
        if (logbook.getKegiatan() == null || logbook.getKegiatan().isBlank()) {
            throw new IllegalStateException("Uraian kegiatan wajib diisi.");
        }
        if (logbookDAO.existsByTanggal(logbook.getPenempatanId(), logbook.getTanggal())) {
            throw new IllegalStateException("Logbook untuk tanggal tersebut sudah ada.");
        }
        if (logbook.getJamMulai() != null && logbook.getJamSelesai() != null
                && logbook.getJamSelesai().isBefore(logbook.getJamMulai())) {
            throw new IllegalStateException("Jam selesai tidak boleh sebelum jam mulai.");
        }

        logbook.setStatusVerifikasi(LogbookHarian.STATUS_MENUNGGU);
        return logbookDAO.insert(logbook);
    }

    /**
     * Mengubah logbook yang belum diverifikasi.
     *
     * @param logbook data logbook dengan id yang sudah ada
     * @return {@code true} bila berhasil
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila logbook sudah diverifikasi
     */
    public boolean ubahLogbook(LogbookHarian logbook) throws SQLException {
        Optional<LogbookHarian> existing = logbookDAO.findById(logbook.getId());
        if (existing.isEmpty()) {
            throw new IllegalStateException("Logbook tidak ditemukan.");
        }
        if (existing.get().isDiverifikasi()) {
            throw new IllegalStateException("Logbook yang sudah diverifikasi tidak dapat diubah.");
        }
        if (logbook.getKegiatan() == null || logbook.getKegiatan().isBlank()) {
            throw new IllegalStateException("Uraian kegiatan wajib diisi.");
        }
        logbook.setStatusVerifikasi(LogbookHarian.STATUS_MENUNGGU);
        return logbookDAO.update(logbook);
    }

    /**
     * Menghapus logbook yang belum diverifikasi.
     *
     * @param logbookId id logbook
     * @return {@code true} bila berhasil
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila logbook sudah diverifikasi
     */
    public boolean hapusLogbook(int logbookId) throws SQLException {
        Optional<LogbookHarian> existing = logbookDAO.findById(logbookId);
        if (existing.isEmpty()) {
            throw new IllegalStateException("Logbook tidak ditemukan.");
        }
        if (existing.get().isDiverifikasi()) {
            throw new IllegalStateException("Logbook yang sudah diverifikasi tidak dapat dihapus.");
        }
        return logbookDAO.delete(logbookId);
    }

    /**
     * Memverifikasi logbook oleh dosen pembimbing.
     *
     * @param logbookId    id logbook
     * @param catatanDosen catatan dosen (boleh {@code null})
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean verifikasi(int logbookId, String catatanDosen) throws SQLException {
        return logbookDAO.verifikasi(logbookId, LogbookHarian.STATUS_DIVERIFIKASI, catatanDosen);
    }

    /**
     * Meminta revisi logbook.
     *
     * @param logbookId    id logbook
     * @param catatanDosen catatan revisi
     * @return {@code true} bila berhasil
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila catatan revisi kosong
     */
    public boolean mintaRevisi(int logbookId, String catatanDosen) throws SQLException {
        if (catatanDosen == null || catatanDosen.isBlank()) {
            throw new IllegalStateException("Catatan revisi wajib diisi.");
        }
        return logbookDAO.verifikasi(logbookId, LogbookHarian.STATUS_REVISI, catatanDosen);
    }

    /**
     * Mengambil seluruh logbook milik satu penempatan.
     *
     * @param penempatanId id penempatan
     * @return daftar logbook
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> daftarByPenempatan(int penempatanId) throws SQLException {
        return logbookDAO.findByPenempatan(penempatanId);
    }

    /**
     * Mengambil logbook milik seorang mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return daftar logbook
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> daftarByMahasiswa(int mahasiswaId) throws SQLException {
        return logbookDAO.findByMahasiswa(mahasiswaId);
    }

    /**
     * Mengambil logbook bimbingan seorang dosen.
     *
     * @param dosenId id dosen
     * @return daftar logbook
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> daftarByDosen(int dosenId) throws SQLException {
        return logbookDAO.findByDosen(dosenId);
    }

    /**
     * Mengambil logbook yang menunggu verifikasi.
     *
     * @return daftar logbook berstatus menunggu
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> daftarMenungguVerifikasi() throws SQLException {
        return logbookDAO.findByStatus(LogbookHarian.STATUS_MENUNGGU);
    }

    /**
     * Mengambil seluruh logbook dari semua mahasiswa (halaman admin).
     *
     * @return daftar seluruh logbook
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> daftarSemua() throws SQLException {
        return logbookDAO.findAllDetail();
    }

    /**
     * Menghitung jumlah logbook terverifikasi pada satu penempatan.
     *
     * @param penempatanId id penempatan
     * @return jumlah logbook terverifikasi
     * @throws SQLException jika query gagal
     */
    public int jumlahTerverifikasi(int penempatanId) throws SQLException {
        return logbookDAO.countDiverifikasi(penempatanId);
    }
}
