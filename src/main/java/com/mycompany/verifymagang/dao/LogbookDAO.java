package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.LogbookHarian;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO untuk tabel {@code logbook}.
 */
public class LogbookDAO {

    private static final String COLUMNS =
            "id, penempatan_id, tanggal, jam_mulai, jam_selesai, kegiatan, kendala, "
            + "status_verifikasi, catatan_dosen";

    private static final String SELECT_DETAIL =
            "SELECT lb.id, lb.penempatan_id, lb.tanggal, lb.jam_mulai, lb.jam_selesai, lb.kegiatan, "
            + "lb.kendala, lb.status_verifikasi, lb.catatan_dosen, m.nama AS nama_mahasiswa "
            + "FROM logbook lb "
            + "JOIN penempatan pen ON pen.id = lb.penempatan_id "
            + "JOIN pendaftaran p ON p.id = pen.pendaftaran_id "
            + "JOIN mahasiswa m ON m.id = p.mahasiswa_id ";

    /**
     * Menyimpan catatan logbook baru.
     *
     * @param logbook data yang akan disimpan
     * @return id logbook yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(LogbookHarian logbook) throws SQLException {
        String sql = "INSERT INTO logbook (penempatan_id, tanggal, jam_mulai, jam_selesai, kegiatan, "
                + "kendala, status_verifikasi, catatan_dosen) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, logbook.getPenempatanId());
            setDate(ps, 2, logbook.getTanggal());
            setTime(ps, 3, logbook.getJamMulai());
            setTime(ps, 4, logbook.getJamSelesai());
            ps.setString(5, logbook.getKegiatan());
            ps.setString(6, logbook.getKendala());
            ps.setString(7, logbook.getStatusVerifikasi());
            ps.setString(8, logbook.getCatatanDosen());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    logbook.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan logbook: id tidak dihasilkan.");
    }

    /**
     * Memperbarui catatan logbook.
     *
     * @param logbook data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(LogbookHarian logbook) throws SQLException {
        String sql = "UPDATE logbook SET penempatan_id = ?, tanggal = ?, jam_mulai = ?, "
                + "jam_selesai = ?, kegiatan = ?, kendala = ?, status_verifikasi = ?, "
                + "catatan_dosen = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, logbook.getPenempatanId());
            setDate(ps, 2, logbook.getTanggal());
            setTime(ps, 3, logbook.getJamMulai());
            setTime(ps, 4, logbook.getJamSelesai());
            ps.setString(5, logbook.getKegiatan());
            ps.setString(6, logbook.getKendala());
            ps.setString(7, logbook.getStatusVerifikasi());
            ps.setString(8, logbook.getCatatanDosen());
            ps.setInt(9, logbook.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus logbook berdasarkan id.
     *
     * @param id id logbook
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM logbook WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari logbook berdasarkan id.
     *
     * @param id id logbook
     * @return logbook bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<LogbookHarian> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM logbook WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh logbook milik satu penempatan, urut tanggal.
     *
     * @param penempatanId id penempatan
     * @return daftar logbook
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> findByPenempatan(int penempatanId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM logbook WHERE penempatan_id = ? "
                + "ORDER BY tanggal DESC, id DESC";
        List<LogbookHarian> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, penempatanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengambil logbook milik seorang mahasiswa (melalui penempatan) beserta nama
     * mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return daftar logbook mahasiswa tersebut
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> findByMahasiswa(int mahasiswaId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE p.mahasiswa_id = ? ORDER BY lb.tanggal DESC, lb.id DESC";
        List<LogbookHarian> result = new ArrayList<>();

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
     * Mengambil logbook yang dibimbing oleh dosen tertentu.
     *
     * @param dosenId id dosen
     * @return daftar logbook bimbingan dosen tersebut
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> findByDosen(int dosenId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE pen.dosen_id = ? ORDER BY lb.tanggal DESC";
        List<LogbookHarian> result = new ArrayList<>();

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
     * Mengambil seluruh logbook dari semua mahasiswa (dipakai halaman admin).
     *
     * @return daftar seluruh logbook beserta nama mahasiswa
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> findAllDetail() throws SQLException {
        String sql = SELECT_DETAIL + "ORDER BY lb.tanggal DESC, lb.id DESC";
        List<LogbookHarian> result = new ArrayList<>();

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
     * Mengambil logbook berdasarkan status verifikasi.
     *
     * @param status status verifikasi
     * @return daftar logbook dengan status tersebut
     * @throws SQLException jika query gagal
     */
    public List<LogbookHarian> findByStatus(String status) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE lb.status_verifikasi = ? ORDER BY lb.tanggal DESC";
        List<LogbookHarian> result = new ArrayList<>();

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
     * Memverifikasi logbook oleh dosen pembimbing.
     *
     * @param id           id logbook
     * @param status       status verifikasi baru
     * @param catatanDosen catatan dosen (boleh {@code null})
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean verifikasi(int id, String status, String catatanDosen) throws SQLException {
        String sql = "UPDATE logbook SET status_verifikasi = ?, catatan_dosen = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, catatanDosen);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengecek apakah mahasiswa sudah mengisi logbook pada tanggal tertentu.
     *
     * @param penempatanId id penempatan
     * @param tanggal      tanggal yang dicek
     * @return {@code true} bila sudah ada catatan
     * @throws SQLException jika query gagal
     */
    public boolean existsByTanggal(int penempatanId, java.time.LocalDate tanggal) throws SQLException {
        String sql = "SELECT 1 FROM logbook WHERE penempatan_id = ? AND tanggal = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, penempatanId);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Menghitung jumlah logbook yang sudah diverifikasi untuk satu penempatan.
     *
     * @param penempatanId id penempatan
     * @return jumlah logbook terverifikasi
     * @throws SQLException jika query gagal
     */
    public int countDiverifikasi(int penempatanId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM logbook WHERE penempatan_id = ? AND status_verifikasi = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, penempatanId);
            ps.setString(2, LogbookHarian.STATUS_DIVERIFIKASI);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
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

    private void setTime(PreparedStatement ps, int index, java.time.LocalTime value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.TIME);
        } else {
            ps.setTime(index, Time.valueOf(value));
        }
    }

    private LogbookHarian mapRow(ResultSet rs) throws SQLException {
        LogbookHarian l = new LogbookHarian();
        l.setId(rs.getInt("id"));
        l.setPenempatanId(rs.getInt("penempatan_id"));

        Date tanggal = rs.getDate("tanggal");
        if (tanggal != null) {
            l.setTanggal(tanggal.toLocalDate());
        }
        Time mulai = rs.getTime("jam_mulai");
        if (mulai != null) {
            l.setJamMulai(mulai.toLocalTime());
        }
        Time selesai = rs.getTime("jam_selesai");
        if (selesai != null) {
            l.setJamSelesai(selesai.toLocalTime());
        }

        l.setKegiatan(rs.getString("kegiatan"));
        l.setKendala(rs.getString("kendala"));
        l.setStatusVerifikasi(rs.getString("status_verifikasi"));
        l.setCatatanDosen(rs.getString("catatan_dosen"));
        return l;
    }

    private LogbookHarian mapRowDetail(ResultSet rs) throws SQLException {
        LogbookHarian l = mapRow(rs);
        l.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
        return l;
    }
}
