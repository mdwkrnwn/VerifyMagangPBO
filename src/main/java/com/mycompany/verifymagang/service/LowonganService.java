package com.mycompany.verifymagang.service;

import com.mycompany.verifymagang.dao.LowonganDAO;
import com.mycompany.verifymagang.dao.MitraDAO;
import com.mycompany.verifymagang.model.LowonganMagang;
import com.mycompany.verifymagang.model.Mitra;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Logika bisnis pengelolaan lowongan magang dan mitra.
 */
public class LowonganService {

    private final LowonganDAO lowonganDAO;
    private final MitraDAO mitraDAO;

    public LowonganService() {
        this(new LowonganDAO(), new MitraDAO());
    }

    public LowonganService(LowonganDAO lowonganDAO, MitraDAO mitraDAO) {
        this.lowonganDAO = lowonganDAO;
        this.mitraDAO = mitraDAO;
    }

    /**
     * Menyimpan lowongan baru.
     *
     * @param lowongan data lowongan
     * @return id lowongan yang dibuat
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public int tambahLowongan(LowonganMagang lowongan) throws SQLException {
        validasi(lowongan);

        if (mitraDAO.findById(lowongan.getMitraId()).isEmpty()) {
            throw new IllegalArgumentException("Mitra tidak ditemukan.");
        }
        if (lowongan.getStatus() == null) {
            lowongan.setStatus(LowonganMagang.STATUS_BUKA);
        }
        return lowonganDAO.insert(lowongan);
    }

    /**
     * Memperbarui data lowongan.
     *
     * @param lowongan data dengan id yang sudah ada
     * @return {@code true} bila berhasil
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila data tidak valid
     */
    public boolean ubahLowongan(LowonganMagang lowongan) throws SQLException {
        validasi(lowongan);
        return lowonganDAO.update(lowongan);
    }

    /**
     * Menghapus lowongan.
     *
     * @param lowonganId id lowongan
     * @return {@code true} bila berhasil
     * @throws SQLException             jika query gagal
     * @throws IllegalStateException    bila lowongan sudah punya pendaftar
     */
    public boolean hapusLowongan(int lowonganId) throws SQLException {
        int pendaftar = lowonganDAO.countPendaftarDisetujui(lowonganId);
        if (pendaftar > 0) {
            throw new IllegalStateException(
                    "Lowongan tidak dapat dihapus karena sudah memiliki " + pendaftar + " pendaftar disetujui.");
        }
        return lowonganDAO.delete(lowonganId);
    }

    /**
     * Menutup lowongan agar tidak bisa didaftar lagi.
     *
     * @param lowonganId id lowongan
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean tutupLowongan(int lowonganId) throws SQLException {
        return lowonganDAO.updateStatus(lowonganId, LowonganMagang.STATUS_TUTUP);
    }

    /**
     * Membuka kembali lowongan.
     *
     * @param lowonganId id lowongan
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean bukaLowongan(int lowonganId) throws SQLException {
        return lowonganDAO.updateStatus(lowonganId, LowonganMagang.STATUS_BUKA);
    }

    /**
     * Mengambil lowongan yang bisa dilihat mahasiswa (status buka).
     *
     * @return daftar lowongan buka
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> daftarLowonganBuka() throws SQLException {
        return lowonganDAO.findBuka();
    }

    /**
     * Mengambil seluruh lowongan untuk halaman admin.
     *
     * @return daftar seluruh lowongan
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> daftarSemuaLowongan() throws SQLException {
        return lowonganDAO.findAll();
    }

    /**
     * Mencari lowongan berdasarkan kata kunci.
     *
     * @param keyword kata kunci
     * @return daftar lowongan yang cocok
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> cariLowongan(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return lowonganDAO.findAll();
        }
        return lowonganDAO.search(keyword.trim());
    }

    /**
     * Mengambil detail satu lowongan.
     *
     * @param lowonganId id lowongan
     * @return lowongan bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<LowonganMagang> detailLowongan(int lowonganId) throws SQLException {
        return lowonganDAO.findById(lowonganId);
    }

    /**
     * Menghitung sisa kuota lowongan.
     *
     * @param lowonganId id lowongan
     * @return sisa kuota (tidak pernah negatif)
     * @throws SQLException jika query gagal
     */
    public int sisaKuota(int lowonganId) throws SQLException {
        Optional<LowonganMagang> found = lowonganDAO.findById(lowonganId);
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Lowongan tidak ditemukan.");
        }
        int kuota = found.get().getKuota();
        int terpakai = lowonganDAO.countPendaftarDisetujui(lowonganId);
        return Math.max(0, kuota - terpakai);
    }

    /**
     * Mengecek apakah lowongan masih bisa didaftar.
     *
     * @param lowonganId id lowongan
     * @return {@code true} bila status buka dan kuota masih tersedia
     * @throws SQLException jika query gagal
     */
    public boolean isBisaDidaftar(int lowonganId) throws SQLException {
        Optional<LowonganMagang> found = lowonganDAO.findById(lowonganId);
        if (found.isEmpty() || !found.get().isBuka()) {
            return false;
        }
        return sisaKuota(lowonganId) > 0;
    }

    // ---------------------------------------------------------------- mitra

    /**
     * Menyimpan mitra baru.
     *
     * @param mitra data mitra
     * @return id mitra yang dibuat
     * @throws SQLException             jika query gagal
     * @throws IllegalArgumentException bila nama mitra kosong
     */
    public int tambahMitra(Mitra mitra) throws SQLException {
        if (mitra.getNama() == null || mitra.getNama().isBlank()) {
            throw new IllegalArgumentException("Nama mitra wajib diisi.");
        }
        return mitraDAO.insert(mitra);
    }

    /**
     * Memperbarui data mitra.
     *
     * @param mitra data mitra
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean ubahMitra(Mitra mitra) throws SQLException {
        if (mitra.getNama() == null || mitra.getNama().isBlank()) {
            throw new IllegalArgumentException("Nama mitra wajib diisi.");
        }
        return mitraDAO.update(mitra);
    }

    /**
     * Menghapus mitra.
     *
     * @param mitraId id mitra
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean hapusMitra(int mitraId) throws SQLException {
        return mitraDAO.delete(mitraId);
    }

    /**
     * Mengambil seluruh mitra.
     *
     * @return daftar mitra
     * @throws SQLException jika query gagal
     */
    public List<Mitra> daftarMitra() throws SQLException {
        return mitraDAO.findAll();
    }

    /**
     * Mengambil mitra aktif saja.
     *
     * @return daftar mitra aktif
     * @throws SQLException jika query gagal
     */
    public List<Mitra> daftarMitraAktif() throws SQLException {
        return mitraDAO.findAktif();
    }

    private void validasi(LowonganMagang lowongan) {
        if (lowongan.getJudul() == null || lowongan.getJudul().isBlank()) {
            throw new IllegalArgumentException("Judul lowongan wajib diisi.");
        }
        if (lowongan.getMitraId() <= 0) {
            throw new IllegalArgumentException("Mitra wajib dipilih.");
        }
        if (lowongan.getKuota() <= 0) {
            throw new IllegalArgumentException("Kuota harus lebih dari 0.");
        }
        if (lowongan.getTanggalBuka() != null && lowongan.getTanggalTutup() != null
                && lowongan.getTanggalTutup().isBefore(lowongan.getTanggalBuka())) {
            throw new IllegalArgumentException("Tanggal tutup tidak boleh sebelum tanggal buka.");
        }
    }
}
