package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.PeriodeMagang;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO untuk tabel {@code periode_magang}.
 */
public class PeriodeMagangDAO {

    private static final String COLUMNS =
            "id, nama, tahun_akademik, semester, tanggal_mulai, tanggal_selesai, aktif";

    /**
     * Menyimpan periode magang baru.
     *
     * @param periode data yang akan disimpan
     * @return id periode yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(PeriodeMagang periode) throws SQLException {
        String sql = "INSERT INTO periode_magang (nama, tahun_akademik, semester, tanggal_mulai, "
                + "tanggal_selesai, aktif) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, periode.getNama());
            ps.setString(2, periode.getTahunAkademik());
            ps.setString(3, periode.getSemester());
            setDate(ps, 4, periode.getTanggalMulai());
            setDate(ps, 5, periode.getTanggalSelesai());
            ps.setBoolean(6, periode.isAktif());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    periode.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan periode magang: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data periode magang.
     *
     * @param periode data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(PeriodeMagang periode) throws SQLException {
        String sql = "UPDATE periode_magang SET nama = ?, tahun_akademik = ?, semester = ?, "
                + "tanggal_mulai = ?, tanggal_selesai = ?, aktif = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, periode.getNama());
            ps.setString(2, periode.getTahunAkademik());
            ps.setString(3, periode.getSemester());
            setDate(ps, 4, periode.getTanggalMulai());
            setDate(ps, 5, periode.getTanggalSelesai());
            ps.setBoolean(6, periode.isAktif());
            ps.setInt(7, periode.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus periode magang berdasarkan id.
     *
     * @param id id periode
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM periode_magang WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari periode berdasarkan id.
     *
     * @param id id periode
     * @return periode bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PeriodeMagang> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM periode_magang WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh periode magang.
     *
     * @return daftar periode
     * @throws SQLException jika query gagal
     */
    public List<PeriodeMagang> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM periode_magang ORDER BY tanggal_mulai DESC";
        List<PeriodeMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    /**
     * Mengambil periode yang sedang aktif.
     *
     * @return periode aktif bila ada
     * @throws SQLException jika query gagal
     */
    public Optional<PeriodeMagang> findAktif() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM periode_magang WHERE aktif = TRUE "
                + "ORDER BY tanggal_mulai DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        }
    }

    /**
     * Menetapkan satu periode sebagai satu-satunya periode aktif.
     *
     * @param id id periode yang akan diaktifkan
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean aktifkan(int id) throws SQLException {
        String nonaktif = "UPDATE periode_magang SET aktif = FALSE";
        String aktif = "UPDATE periode_magang SET aktif = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(nonaktif);
                    PreparedStatement ps2 = conn.prepareStatement(aktif)) {

                ps1.executeUpdate();
                ps2.setInt(1, id);
                boolean ok = ps2.executeUpdate() > 0;
                conn.commit();
                return ok;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private void setDate(PreparedStatement ps, int index, java.time.LocalDate value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private PeriodeMagang mapRow(ResultSet rs) throws SQLException {
        PeriodeMagang p = new PeriodeMagang();
        p.setId(rs.getInt("id"));
        p.setNama(rs.getString("nama"));
        p.setTahunAkademik(rs.getString("tahun_akademik"));
        p.setSemester(rs.getString("semester"));

        Date mulai = rs.getDate("tanggal_mulai");
        if (mulai != null) {
            p.setTanggalMulai(mulai.toLocalDate());
        }
        Date selesai = rs.getDate("tanggal_selesai");
        if (selesai != null) {
            p.setTanggalSelesai(selesai.toLocalDate());
        }

        p.setAktif(rs.getBoolean("aktif"));
        return p;
    }
}
