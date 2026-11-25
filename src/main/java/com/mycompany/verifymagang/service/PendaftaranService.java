package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.LowonganDAO;
import com.mycompany.verifymagang.dao.PendaftaranDAO;
import com.mycompany.verifymagang.model.LowonganMagang;
import com.mycompany.verifymagang.model.PendaftaranMagang;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis pendaftaran magang.
 *
 * <p>Aturan utama yang dijaga di sini:</p>
 * <ul>
 *   <li>mahasiswa tidak boleh mendaftar dua kali pada lowongan yang sama;</li>
 *   <li>mahasiswa tidak boleh punya lebih dari satu pendaftaran aktif;</li>
 *   <li>pendaftaran hanya boleh untuk lowongan berstatus buka dan bersisa kuota.</li>
 * </ul>
 */
public class PendaftaranService {

    private final PendaftaranDAO pendaftaranDAO;
    private final LowonganDAO lowonganDAO;

    public PendaftaranService() {
        this(new PendaftaranDAO(), new LowonganDAO());
    }

    public PendaftaranService(PendaftaranDAO pendaftaranDAO, LowonganDAO lowonganDAO) {
        this.pendaftaranDAO = pendaftaranDAO;
        this.lowonganDAO = lowonganDAO;
    }

    /**
     * Mendaftarkan mahasiswa pada sebuah lowongan.
     *
     * @param mahasiswaId id mahasiswa
     * @param lowonganId  id lowongan
     * @return id pendaftaran yang dibuat
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila aturan bisnis dilanggar
     */
    public int daftarMagang(int mahasiswaId, int lowonganId) throws SQLException {
        Optional<LowonganMagang> lowongan = lowonganDAO.findById(lowonganId);
        if (lowongan.isEmpty()) {
            throw new IllegalStateException("Lowongan tidak ditemukan.");
        }
        if (!lowongan.get().isBuka()) {
            throw new IllegalStateException("Lowongan sudah ditutup.");
        }
        if (pendaftaranDAO.exists(mahasiswaId, lowonganId)) {
            throw new IllegalStateException("Anda sudah mendaftar pada lowongan ini.");
        }
        if (pendaftaranDAO.hasPendaftaranAktif(mahasiswaId)) {
            throw new IllegalStateException(
                    "Anda masih memiliki pendaftaran yang menunggu atau sudah disetujui.");
        }

        int terpakai = lowonganDAO.countPendaftarDisetujui(lowonganId);
        if (terpakai >= lowongan.get().getKuota()) {
            throw new IllegalStateException("Kuota lowongan sudah penuh.");
        }

        PendaftaranMagang pendaftaran = new PendaftaranMagang();
        pendaftaran.setMahasiswaId(mahasiswaId);
        pendaftaran.setLowonganId(lowonganId);
        pendaftaran.setTanggalDaftar(LocalDate.now());
        pendaftaran.setStatus(PendaftaranMagang.STATUS_MENUNGGU);
        return pendaftaranDAO.insert(pendaftaran);
    }

    /**
     * Menyetujui pendaftaran mahasiswa.
     *
     * @param pendaftaranId id pendaftaran
     * @param catatan       catatan admin (boleh {@code null})
     * @return {@code true} bila berhasil
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila kuota sudah penuh
     */
    public boolean setujui(int pendaftaranId, String catatan) throws SQLException {
        Optional<PendaftaranMagang> found = pendaftaranDAO.findById(pendaftaranId);
        if (found.isEmpty()) {
            throw new IllegalStateException("Pendaftaran tidak ditemukan.");
        }

        PendaftaranMagang pendaftaran = found.get();
        if (PendaftaranMagang.STATUS_DISETUJUI.equalsIgnoreCase(pendaftaran.getStatus())) {
            return true;
        }

        int terpakai = lowonganDAO.countPendaftarDisetujui(pendaftaran.getLowonganId());
        Optional<LowonganMagang> lowongan = lowonganDAO.findById(pendaftaran.getLowonganId());
        if (lowongan.isEmpty()) {
            throw new IllegalStateException("Lowongan terkait tidak ditemukan.");
        }
        if (terpakai >= lowongan.get().getKuota()) {
            throw new IllegalStateException("Kuota lowongan sudah penuh, tidak bisa menyetujui lagi.");
        }

        return pendaftaranDAO.updateStatus(pendaftaranId, PendaftaranMagang.STATUS_DISETUJUI, catatan);
    }

    /**
     * Menolak pendaftaran mahasiswa.
     *
     * @param pendaftaranId id pendaftaran
     * @param alasan        alasan penolakan (boleh {@code null})
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean tolak(int pendaftaranId, String alasan) throws SQLException {
        return pendaftaranDAO.updateStatus(pendaftaranId, PendaftaranMagang.STATUS_DITOLAK, alasan);
    }

    /**
     * Membatalkan pendaftaran (menghapus baris).
     *
     * @param pendaftaranId id pendaftaran
     * @return {@code true} bila berhasil
     * @throws SQLException          jika query gagal
     * @throws IllegalStateException bila pendaftaran sudah disetujui
     */
    public boolean batalkan(int pendaftaranId) throws SQLException {
        Optional<PendaftaranMagang> found = pendaftaranDAO.findById(pendaftaranId);
        if (found.isEmpty()) {
            throw new IllegalStateException("Pendaftaran tidak ditemukan.");
        }
        if (PendaftaranMagang.STATUS_DISETUJUI.equalsIgnoreCase(found.get().getStatus())) {
            throw new IllegalStateException(
                    "Pendaftaran yang sudah disetujui tidak dapat dibatalkan. Batalkan penempatan terlebih dahulu.");
        }
        return pendaftaranDAO.delete(pendaftaranId);
    }

    /**
     * Mengambil seluruh pendaftaran untuk halaman admin.
     *
     * @return daftar pendaftaran
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> daftarSemua() throws SQLException {
        return pendaftaranDAO.findAllDetail();
    }

    /**
     * Mengambil pendaftaran milik satu mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return daftar pendaftaran mahasiswa tersebut
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> daftarByMahasiswa(int mahasiswaId) throws SQLException {
        return pendaftaranDAO.findByMahasiswa(mahasiswaId);
    }

    /**
     * Mengambil pendaftaran yang masih menunggu keputusan.
     *
     * @return daftar pendaftaran berstatus menunggu
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> daftarMenunggu() throws SQLException {
        return pendaftaranDAO.findByStatus(PendaftaranMagang.STATUS_MENUNGGU);
    }

    /**
     * Mengambil pendaftaran yang sudah disetujui tetapi belum ditempatkan.
     *
     * @return daftar pendaftaran siap ditempatkan
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> daftarSiapDitempatkan() throws SQLException {
        return pendaftaranDAO.findDisetujuiBelumDitempatkan();
    }

    /**
     * Mengambil satu pendaftaran.
     *
     * @param pendaftaranId id pendaftaran
     * @return pendaftaran bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PendaftaranMagang> detail(int pendaftaranId) throws SQLException {
        return pendaftaranDAO.findById(pendaftaranId);
    }
}
