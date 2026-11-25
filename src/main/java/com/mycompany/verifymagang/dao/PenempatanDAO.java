package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.PenempatanMagang;

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
 * DAO untuk tabel {@code penempatan}.
 */
public class PenempatanDAO {

    private static final String COLUMNS =
            "id, pendaftaran_id, dosen_id, mitra_id, tanggal_mulai, tanggal_selesai, status";

    private static final String SELECT_DETAIL =
            "SELECT pen.id, pen.pendaftaran_id, pen.dosen_id, pen.mitra_id, pen.tanggal_mulai, "
            + "pen.tanggal_selesai, pen.status, m.nama AS nama_mahasiswa, d.nama AS nama_dosen, "
            + "mt.nama AS nama_mitra "
            + "FROM penempatan pen "
            + "JOIN pendaftaran p ON p.id = pen.pendaftaran_id "
            + "JOIN mahasiswa m ON m.id = p.mahasiswa_id "
            + "JOIN dosen d ON d.id = pen.dosen_id "
            + "JOIN mitra mt ON mt.id = pen.mitra_id ";

    /**
     * Menyimpan penempatan baru.
     *
     * @param penempatan data yang akan disimpan
     * @return id penempatan yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(PenempatanMagang penempatan) throws SQLException {
        String sql = "INSERT INTO penempatan (pendaftaran_id, dosen_id, mitra_id, tanggal_mulai, "
                + "tanggal_selesai, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, penempatan.getPendaftaranId());
            ps.setInt(2, penempatan.getDosenId());
            ps.setInt(3, penempatan.getMitraId());
            setDate(ps, 4, penempatan.getTanggalMulai());
            setDate(ps, 5, penempatan.getTanggalSelesai());
            ps.setString(6, penempatan.getStatus());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    penempatan.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan penempatan: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data penempatan.
     *
     * @param penempatan data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(PenempatanMagang penempatan) throws SQLException {
        String sql = "UPDATE penempatan SET pendaftaran_id = ?, dosen_id = ?, mitra_id = ?, "
                + "tanggal_mulai = ?, tanggal_selesai = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, penempatan.getPendaftaranId());
            ps.setInt(2, penempatan.getDosenId());
            ps.setInt(3, penempatan.getMitraId());
            setDate(ps, 4, penempatan.getTanggalMulai());
            setDate(ps, 5, penempatan.getTanggalSelesai());
            ps.setString(6, penempatan.getStatus());
            ps.setInt(7, penempatan.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus penempatan berdasarkan id.
     *
     * @param id id penempatan
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM penempatan WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari penempatan berdasarkan id.
     *
     * @param id id penempatan
     * @return penempatan bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PenempatanMagang> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM penempatan WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mencari penempatan berdasarkan id pendaftaran (relasi satu-ke-satu).
     *
     * @param pendaftaranId id pendaftaran
     * @return penempatan bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PenempatanMagang> findByPendaftaran(int pendaftaranId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM penempatan WHERE pendaftaran_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pendaftaranId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mencari penempatan aktif milik seorang mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return penempatan bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PenempatanMagang> findAktifByMahasiswa(int mahasiswaId) throws SQLException {
        String sql = "SELECT pen.id, pen.pendaftaran_id, pen.dosen_id, pen.mitra_id, "
                + "pen.tanggal_mulai, pen.tanggal_selesai, pen.status "
                + "FROM penempatan pen JOIN pendaftaran p ON p.id = pen.pendaftaran_id "
                + "WHERE p.mahasiswa_id = ? AND pen.status = ? "
                + "ORDER BY pen.tanggal_mulai DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mahasiswaId);
            ps.setString(2, PenempatanMagang.STATUS_AKTIF);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh penempatan beserta nama mahasiswa, dosen, dan mitra.
     *
     * @return daftar penempatan
     * @throws SQLException jika query gagal
     */
    public List<PenempatanMagang> findAllDetail() throws SQLException {
        String sql = SELECT_DETAIL + "ORDER BY pen.tanggal_mulai DESC";
        List<PenempatanMagang> result = new ArrayList<>();

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
     * Mengambil penempatan yang dibimbing oleh dosen tertentu.
     *
     * @param dosenId id dosen
     * @return daftar penempatan bimbingan dosen tersebut
     * @throws SQLException jika query gagal
     */
    public List<PenempatanMagang> findByDosen(int dosenId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE pen.dosen_id = ? ORDER BY pen.tanggal_mulai DESC";
        List<PenempatanMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dosenId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowDetail(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengambil penempatan pada satu mitra.
     *
     * @param mitraId id mitra
     * @return daftar penempatan mitra tersebut
     * @throws SQLException jika query gagal
     */
    public List<PenempatanMagang> findByMitra(int mitraId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE pen.mitra_id = ? ORDER BY pen.tanggal_mulai DESC";
        List<PenempatanMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mitraId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowDetail(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengubah status penempatan.
     *
     * @param id     id penempatan
     * @param status status baru
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE penempatan SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void setDate(PreparedStatement ps, int index, java.time.LocalDate value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private PenempatanMagang mapRow(ResultSet rs) throws SQLException {
        PenempatanMagang p = new PenempatanMagang();
        p.setId(rs.getInt("id"));
        p.setPendaftaranId(rs.getInt("pendaftaran_id"));
        p.setDosenId(rs.getInt("dosen_id"));
        p.setMitraId(rs.getInt("mitra_id"));

        Date mulai = rs.getDate("tanggal_mulai");
        if (mulai != null) {
            p.setTanggalMulai(mulai.toLocalDate());
        }
        Date selesai = rs.getDate("tanggal_selesai");
        if (selesai != null) {
            p.setTanggalSelesai(selesai.toLocalDate());
        }
        p.setStatus(rs.getString("status"));
        return p;
    }

    private PenempatanMagang mapRowDetail(ResultSet rs) throws SQLException {
        PenempatanMagang p = mapRow(rs);
        p.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
        p.setNamaDosen(rs.getString("nama_dosen"));
        p.setNamaMitra(rs.getString("nama_mitra"));
        return p;
    }
}
