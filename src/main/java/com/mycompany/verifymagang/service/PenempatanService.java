package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.DosenDAO;
import com.mycompany.verifymagang.dao.LowonganDAO;
import com.mycompany.verifymagang.dao.MitraDAO;
import com.mycompany.verifymagang.dao.PendaftaranDAO;
import com.mycompany.verifymagang.dao.PenempatanDAO;
import com.mycompany.verifymagang.model.PendaftaranMagang;
import com.mycompany.verifymagang.model.PenempatanMagang;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis penempatan magang.
 *
 * <p>Penempatan hanya boleh dibuat dari pendaftaran yang sudah disetujui, dan
 * satu pendaftaran hanya boleh menghasilkan satu penempatan.</p>
 */
public class PenempatanService {

    private final PenempatanDAO penempatanDAO;
    private final PendaftaranDAO pendaftaranDAO;
    private final DosenDAO dosenDAO;
    private final MitraDAO mitraDAO;
    private final LowonganDAO lowonganDAO;

    public PenempatanService() {
        this(new PenempatanDAO(), new PendaftaranDAO(), new DosenDAO(), new MitraDAO(), new LowonganDAO());
    }

    public PenempatanService(PenempatanDAO penempatanDAO, PendaftaranDAO pendaftaranDAO,
            DosenDAO dosenDAO, MitraDAO mitraDAO, LowonganDAO lowonganDAO) {
        this.penempatanDAO = penempatanDAO;
        this.pendaftaranDAO = pendaftaranDAO;
        this.dosenDAO = dosenDAO;
        this.mitraDAO = mitraDAO;
        this.lowonganDAO = lowonganDAO;
    }

    /**
     * Membuat penempatan dari pendaftaran yang sudah disetujui.
     *
     * @param pendaftaranId  id pendaftaran
     * @param dosenId        id dosen pembimbing
     * @param tanggalMulai   tanggal mulai magang
     * @param tanggalSelesai tanggal selesai magang
     * @return id penempatan yang dibuat
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila aturan bisnis dilanggar
     */
    public int buatPenempatan(int pendaftaranId, int dosenId, LocalDate tanggalMulai,
            LocalDate tanggalSelesai) throws SQLException {

        Optional<PendaftaranMagang> found = pendaftaranDAO.findById(pendaftaranId);
        if (found.isEmpty()) {
            throw new IllegalStateException("Pendaftaran tidak ditemukan.");
        }

        PendaftaranMagang pendaftaran = found.get();
        if (!PendaftaranMagang.STATUS_DISETUJUI.equalsIgnoreCase(pendaftaran.getStatus())) {
            throw new IllegalStateException("Pendaftaran belum disetujui, tidak dapat ditempatkan.");
        }
        if (penempatanDAO.findByPendaftaran(pendaftaranId).isPresent()) {
            throw new IllegalStateException("Pendaftaran ini sudah memiliki penempatan.");
        }
        if (dosenDAO.findById(dosenId).isEmpty()) {
            throw new IllegalStateException("Dosen pembimbing tidak ditemukan.");
        }
        if (tanggalMulai == null || tanggalSelesai == null) {
            throw new IllegalStateException("Tanggal mulai dan selesai wajib diisi.");
        }
        if (tanggalSelesai.isBefore(tanggalMulai)) {
            throw new IllegalStateException("Tanggal selesai tidak boleh sebelum tanggal mulai.");
        }

        int mitraId = ambilMitraDariLowongan(pendaftaran.getLowonganId());

        PenempatanMagang penempatan = new PenempatanMagang();
        penempatan.setPendaftaranId(pendaftaranId);
        penempatan.setDosenId(dosenId);
        penempatan.setMitraId(mitraId);
        penempatan.setTanggalMulai(tanggalMulai);
        penempatan.setTanggalSelesai(tanggalSelesai);
        penempatan.setStatus(PenempatanMagang.STATUS_AKTIF);
        return penempatanDAO.insert(penempatan);
    }

    /**
     * Mengganti dosen pembimbing sebuah penempatan.
     *
     * @param penempatanId id penempatan
     * @param dosenBaruId  id dosen baru
     * @return {@code true} bila berhasil
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila data tidak ditemukan
     */
    public boolean gantiDosen(int penempatanId, int dosenBaruId) throws SQLException {
        Optional<PenempatanMagang> found = penempatanDAO.findById(penempatanId);
        if (found.isEmpty()) {
            throw new IllegalStateException("Penempatan tidak ditemukan.");
        }
        if (dosenDAO.findById(dosenBaruId).isEmpty()) {
            throw new IllegalStateException("Dosen pembimbing tidak ditemukan.");
        }

        PenempatanMagang penempatan = found.get();
        penempatan.setDosenId(dosenBaruId);
        return penempatanDAO.update(penempatan);
    }

    /**
     * Menandai penempatan selesai.
     *
     * @param penempatanId id penempatan
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean selesaikan(int penempatanId) throws SQLException {
        return penempatanDAO.updateStatus(penempatanId, PenempatanMagang.STATUS_SELESAI);
    }

    /**
     * Membatalkan penempatan.
     *
     * @param penempatanId id penempatan
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean batalkan(int penempatanId) throws SQLException {
        return penempatanDAO.updateStatus(penempatanId, PenempatanMagang.STATUS_BATAL);
    }

    /**
     * Mengambil seluruh penempatan untuk halaman admin.
     *
     * @return daftar penempatan
     * @throws SQLException jika query gagal
     */
    public List<PenempatanMagang> daftarSemua() throws SQLException {
        return penempatanDAO.findAllDetail();
    }

    /**
     * Mengambil penempatan bimbingan seorang dosen.
     *
     * @param dosenId id dosen
     * @return daftar penempatan
     * @throws SQLException jika query gagal
     */
    public List<PenempatanMagang> daftarByDosen(int dosenId) throws SQLException {
        return penempatanDAO.findByDosen(dosenId);
    }

    /**
     * Mengambil penempatan aktif milik mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return penempatan bila ada
     * @throws SQLException jika query gagal
     */
    public Optional<PenempatanMagang> penempatanAktifMahasiswa(int mahasiswaId) throws SQLException {
        return penempatanDAO.findAktifByMahasiswa(mahasiswaId);
    }

    /**
     * Mengambil detail satu penempatan.
     *
     * @param penempatanId id penempatan
     * @return penempatan bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PenempatanMagang> detail(int penempatanId) throws SQLException {
        return penempatanDAO.findById(penempatanId);
    }

    private int ambilMitraDariLowongan(int lowonganId) throws SQLException {
        // Mitra diambil dari lowongan asal pendaftaran agar konsisten.
        return lowonganDAO.findById(lowonganId)
                .orElseThrow(() -> new IllegalStateException("Lowongan asal pendaftaran tidak ditemukan."))
                .getMitraId();
    }
}
