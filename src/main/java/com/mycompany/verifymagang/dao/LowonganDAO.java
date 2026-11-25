package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.LowonganMagang;

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
 * DAO untuk tabel {@code lowongan}.
 */
public class LowonganDAO {

    private static final String COLUMNS =
            "id, mitra_id, periode_id, judul, deskripsi, persyaratan, kuota, tunjangan, "
            + "lokasi, status, tanggal_buka, tanggal_tutup";

    private static final String SELECT_WITH_MITRA =
            "SELECT l.id, l.mitra_id, l.periode_id, l.judul, l.deskripsi, l.persyaratan, l.kuota, "
            + "l.tunjangan, l.lokasi, l.status, l.tanggal_buka, l.tanggal_tutup, m.nama AS nama_mitra "
            + "FROM lowongan l JOIN mitra m ON m.id = l.mitra_id ";

    /**
     * Menyimpan lowongan baru.
     *
     * @param lowongan data yang akan disimpan
     * @return id lowongan yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(LowonganMagang lowongan) throws SQLException {
        String sql = "INSERT INTO lowongan (mitra_id, periode_id, judul, deskripsi, persyaratan, "
                + "kuota, tunjangan, lokasi, status, tanggal_buka, tanggal_tutup) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, lowongan.getMitraId());
            setNullableInt(ps, 2, lowongan.getPeriodeId());
            ps.setString(3, lowongan.getJudul());
            ps.setString(4, lowongan.getDeskripsi());
            ps.setString(5, lowongan.getPersyaratan());
            ps.setInt(6, lowongan.getKuota());
            ps.setBigDecimal(7, lowongan.getTunjangan());
            ps.setString(8, lowongan.getLokasi());
            ps.setString(9, lowongan.getStatus());
            setDate(ps, 10, lowongan.getTanggalBuka());
            setDate(ps, 11, lowongan.getTanggalTutup());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    lowongan.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan lowongan: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data lowongan.
     *
     * @param lowongan data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(LowonganMagang lowongan) throws SQLException {
        String sql = "UPDATE lowongan SET mitra_id = ?, periode_id = ?, judul = ?, deskripsi = ?, "
                + "persyaratan = ?, kuota = ?, tunjangan = ?, lokasi = ?, status = ?, "
                + "tanggal_buka = ?, tanggal_tutup = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lowongan.getMitraId());
            setNullableInt(ps, 2, lowongan.getPeriodeId());
            ps.setString(3, lowongan.getJudul());
            ps.setString(4, lowongan.getDeskripsi());
            ps.setString(5, lowongan.getPersyaratan());
            ps.setInt(6, lowongan.getKuota());
            ps.setBigDecimal(7, lowongan.getTunjangan());
            ps.setString(8, lowongan.getLokasi());
            ps.setString(9, lowongan.getStatus());
            setDate(ps, 10, lowongan.getTanggalBuka());
            setDate(ps, 11, lowongan.getTanggalTutup());
            ps.setInt(12, lowongan.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus lowongan berdasarkan id.
     *
     * @param id id lowongan
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM lowongan WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari lowongan berdasarkan id.
     *
     * @param id id lowongan
     * @return lowongan bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<LowonganMagang> findById(int id) throws SQLException {
        String sql = SELECT_WITH_MITRA + "WHERE l.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRowWithMitra(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh lowongan beserta nama mitra.
     *
     * @return daftar lowongan
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> findAll() throws SQLException {
        String sql = SELECT_WITH_MITRA + "ORDER BY l.tanggal_buka DESC, l.judul";
        List<LowonganMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowWithMitra(rs));
            }
        }
        return result;
    }

    /**
     * Mengambil lowongan yang berstatus buka beserta nama mitra.
     *
     * @return daftar lowongan buka
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> findBuka() throws SQLException {
        String sql = SELECT_WITH_MITRA + "WHERE l.status = ? ORDER BY l.tanggal_buka DESC, l.judul";
        List<LowonganMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, LowonganMagang.STATUS_BUKA);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowWithMitra(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengambil lowongan milik satu mitra.
     *
     * @param mitraId id mitra
     * @return daftar lowongan mitra tersebut
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> findByMitra(int mitraId) throws SQLException {
        String sql = SELECT_WITH_MITRA + "WHERE l.mitra_id = ? ORDER BY l.judul";
        List<LowonganMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mitraId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowWithMitra(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengambil lowongan pada satu periode.
     *
     * @param periodeId id periode magang
     * @return daftar lowongan periode tersebut
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> findByPeriode(int periodeId) throws SQLException {
        String sql = SELECT_WITH_MITRA + "WHERE l.periode_id = ? ORDER BY l.judul";
        List<LowonganMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, periodeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowWithMitra(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mencari lowongan berdasarkan judul, lokasi, atau nama mitra.
     *
     * @param keyword kata kunci pencarian
     * @return daftar lowongan yang cocok
     * @throws SQLException jika query gagal
     */
    public List<LowonganMagang> search(String keyword) throws SQLException {
        String sql = SELECT_WITH_MITRA
                + "WHERE l.judul LIKE ? OR l.lokasi LIKE ? OR m.nama LIKE ? "
                + "ORDER BY l.tanggal_buka DESC";
        List<LowonganMagang> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowWithMitra(rs));
                }
            }
        }
        return result;
    }

    /**
     * Menghitung jumlah pendaftar yang sudah disetujui untuk satu lowongan.
     * Dipakai untuk mengecek sisa kuota.
     *
     * @param lowonganId id lowongan
     * @return jumlah pendaftar disetujui
     * @throws SQLException jika query gagal
     */
    public int countPendaftarDisetujui(int lowonganId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pendaftaran WHERE lowongan_id = ? AND status = 'DISETUJUI'";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lowonganId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Mengubah status lowongan (buka/tutup).
     *
     * @param lowonganId id lowongan
     * @param status     status baru
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean updateStatus(int lowonganId, String status) throws SQLException {
        String sql = "UPDATE lowongan SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, lowonganId);
            return ps.executeUpdate() > 0;
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private void setDate(PreparedStatement ps, int index, java.time.LocalDate value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private LowonganMagang mapRow(ResultSet rs) throws SQLException {
        LowonganMagang l = new LowonganMagang();
        l.setId(rs.getInt("id"));
        l.setMitraId(rs.getInt("mitra_id"));

        int periodeId = rs.getInt("periode_id");
        l.setPeriodeId(rs.wasNull() ? null : periodeId);

        l.setJudul(rs.getString("judul"));
        l.setDeskripsi(rs.getString("deskripsi"));
        l.setPersyaratan(rs.getString("persyaratan"));
        l.setKuota(rs.getInt("kuota"));
        l.setTunjangan(rs.getBigDecimal("tunjangan"));
        l.setLokasi(rs.getString("lokasi"));
        l.setStatus(rs.getString("status"));

        Date buka = rs.getDate("tanggal_buka");
        if (buka != null) {
            l.setTanggalBuka(buka.toLocalDate());
        }
        Date tutup = rs.getDate("tanggal_tutup");
        if (tutup != null) {
            l.setTanggalTutup(tutup.toLocalDate());
        }
        return l;
    }

    private LowonganMagang mapRowWithMitra(ResultSet rs) throws SQLException {
        LowonganMagang l = mapRow(rs);
        l.setNamaMitra(rs.getString("nama_mitra"));
        return l;
    }
}
