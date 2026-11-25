package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.PendaftaranMagang;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO untuk tabel {@code pendaftaran}.
 */
public class PendaftaranDAO {

    private static final String COLUMNS =
            "id, mahasiswa_id, lowongan_id, tanggal_daftar, status, catatan";

    private static final String SELECT_DETAIL =
            "SELECT p.id, p.mahasiswa_id, p.lowongan_id, p.tanggal_daftar, p.status, p.catatan, "
            + "m.nama AS nama_mahasiswa, m.nim AS nim, l.judul AS judul_lowongan "
            + "FROM pendaftaran p "
            + "JOIN mahasiswa m ON m.id = p.mahasiswa_id "
            + "JOIN lowongan l ON l.id = p.lowongan_id ";

    /**
     * Menyimpan pendaftaran baru.
     *
     * @param pendaftaran data yang akan disimpan
     * @return id pendaftaran yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(PendaftaranMagang pendaftaran) throws SQLException {
        String sql = "INSERT INTO pendaftaran (mahasiswa_id, lowongan_id, tanggal_daftar, status, catatan) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pendaftaran.getMahasiswaId());
            ps.setInt(2, pendaftaran.getLowonganId());
            setDate(ps, 3, pendaftaran.getTanggalDaftar());
            ps.setString(4, pendaftaran.getStatus());
            ps.setString(5, pendaftaran.getCatatan());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    pendaftaran.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan pendaftaran: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data pendaftaran.
     *
     * @param pendaftaran data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(PendaftaranMagang pendaftaran) throws SQLException {
        String sql = "UPDATE pendaftaran SET mahasiswa_id = ?, lowongan_id = ?, tanggal_daftar = ?, "
                + "status = ?, catatan = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pendaftaran.getMahasiswaId());
            ps.setInt(2, pendaftaran.getLowonganId());
            setDate(ps, 3, pendaftaran.getTanggalDaftar());
            ps.setString(4, pendaftaran.getStatus());
            ps.setString(5, pendaftaran.getCatatan());
            ps.setInt(6, pendaftaran.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus pendaftaran berdasarkan id.
     *
     * @param id id pendaftaran
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pendaftaran WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari pendaftaran berdasarkan id.
     *
     * @param id id pendaftaran
     * @return pendaftaran bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PendaftaranMagang> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM pendaftaran WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh pendaftaran beserta nama mahasiswa dan judul lowongan.
     *
     * @return daftar pendaftaran
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> findAllDetail() throws SQLException {
        String sql = SELECT_DETAIL + "ORDER BY p.tanggal_daftar DESC, p.id DESC";
        List<PendaftaranMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowDetail(rs));
            }
        }
        return result;
    }

    /**
     * Mengambil pendaftaran milik satu mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return daftar pendaftaran mahasiswa tersebut
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> findByMahasiswa(int mahasiswaId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE p.mahasiswa_id = ? ORDER BY p.tanggal_daftar DESC";
        List<PendaftaranMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mahasiswaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowDetail(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengambil pendaftaran untuk satu lowongan.
     *
     * @param lowonganId id lowongan
     * @return daftar pendaftaran lowongan tersebut
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> findByLowongan(int lowonganId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE p.lowongan_id = ? ORDER BY p.tanggal_daftar";
        List<PendaftaranMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lowonganId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowDetail(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengambil pendaftaran berdasarkan status.
     *
     * @param status status pendaftaran
     * @return daftar pendaftaran dengan status tersebut
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> findByStatus(String status) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE p.status = ? ORDER BY p.tanggal_daftar";
        List<PendaftaranMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowDetail(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengambil pendaftaran yang sudah disetujui tetapi belum memiliki
     * penempatan. Dipakai admin saat membuat penempatan baru.
     *
     * @return daftar pendaftaran siap ditempatkan
     * @throws SQLException jika query gagal
     */
    public List<PendaftaranMagang> findDisetujuiBelumDitempatkan() throws SQLException {
        String sql = SELECT_DETAIL
                + "LEFT JOIN penempatan pen ON pen.pendaftaran_id = p.id "
                + "WHERE p.status = ? AND pen.id IS NULL "
                + "ORDER BY p.tanggal_daftar";
        List<PendaftaranMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PendaftaranMagang.STATUS_DISETUJUI);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowDetail(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengubah status pendaftaran beserta catatannya.
     *
     * @param id      id pendaftaran
     * @param status  status baru
     * @param catatan catatan admin (boleh {@code null})
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean updateStatus(int id, String status, String catatan) throws SQLException {
        String sql = "UPDATE pendaftaran SET status = ?, catatan = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, catatan);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengecek apakah mahasiswa sudah pernah mendaftar pada lowongan tertentu.
     *
     * @param mahasiswaId id mahasiswa
     * @param lowonganId  id lowongan
     * @return {@code true} bila sudah ada pendaftaran
     * @throws SQLException jika query gagal
     */
    public boolean exists(int mahasiswaId, int lowonganId) throws SQLException {
        String sql = "SELECT 1 FROM pendaftaran WHERE mahasiswa_id = ? AND lowongan_id = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mahasiswaId);
            ps.setInt(2, lowonganId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Mengecek apakah mahasiswa masih punya pendaftaran yang menunggu atau sudah
     * disetujui. Dipakai untuk mencegah pendaftaran ganda di lowongan berbeda.
     *
     * @param mahasiswaId id mahasiswa
     * @return {@code true} bila masih ada pendaftaran aktif
     * @throws SQLException jika query gagal
     */
    public boolean hasPendaftaranAktif(int mahasiswaId) throws SQLException {
        String sql = "SELECT 1 FROM pendaftaran WHERE mahasiswa_id = ? "
                + "AND status IN ('MENUNGGU', 'DISETUJUI') LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mahasiswaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void setDate(PreparedStatement ps, int index, java.time.LocalDate value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private PendaftaranMagang mapRow(ResultSet rs) throws SQLException {
        PendaftaranMagang p = new PendaftaranMagang();
        p.setId(rs.getInt("id"));
        p.setMahasiswaId(rs.getInt("mahasiswa_id"));
        p.setLowonganId(rs.getInt("lowongan_id"));

        Date daftar = rs.getDate("tanggal_daftar");
        if (daftar != null) {
            p.setTanggalDaftar(daftar.toLocalDate());
        }
        p.setStatus(rs.getString("status"));
        p.setCatatan(rs.getString("catatan"));
        return p;
    }

    private PendaftaranMagang mapRowDetail(ResultSet rs) throws SQLException {
        PendaftaranMagang p = mapRow(rs);
        p.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
        p.setNim(rs.getString("nim"));
        p.setJudulLowongan(rs.getString("judul_lowongan"));
        return p;
    }
}
