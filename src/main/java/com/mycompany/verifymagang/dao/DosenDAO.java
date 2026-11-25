package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.Dosen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO untuk tabel {@code dosen}.
 */
public class DosenDAO {

    private static final String COLUMNS =
            "id, user_id, nip, nama, bidang_keahlian, no_hp, email";

    /**
     * Menyimpan data dosen baru.
     *
     * @param dosen data yang akan disimpan
     * @return id dosen yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(Dosen dosen) throws SQLException {
        String sql = "INSERT INTO dosen (user_id, nip, nama, bidang_keahlian, no_hp, email) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setUserId(ps, 1, dosen.getUserId());
            ps.setString(2, dosen.getNip());
            ps.setString(3, dosen.getNama());
            ps.setString(4, dosen.getBidangKeahlian());
            ps.setString(5, dosen.getNoHp());
            ps.setString(6, dosen.getEmail());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    dosen.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan dosen: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data dosen.
     *
     * @param dosen data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(Dosen dosen) throws SQLException {
        String sql = "UPDATE dosen SET user_id = ?, nip = ?, nama = ?, bidang_keahlian = ?, "
                + "no_hp = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setUserId(ps, 1, dosen.getUserId());
            ps.setString(2, dosen.getNip());
            ps.setString(3, dosen.getNama());
            ps.setString(4, dosen.getBidangKeahlian());
            ps.setString(5, dosen.getNoHp());
            ps.setString(6, dosen.getEmail());
            ps.setInt(7, dosen.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus dosen berdasarkan id.
     *
     * @param id id dosen
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM dosen WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari dosen berdasarkan id.
     *
     * @param id id dosen
     * @return dosen bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Dosen> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM dosen WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mencari dosen berdasarkan id user.
     *
     * @param userId id pada tabel users
     * @return dosen bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Dosen> findByUserId(int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM dosen WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh dosen.
     *
     * @return daftar dosen
     * @throws SQLException jika query gagal
     */
    public List<Dosen> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM dosen ORDER BY nama";
        List<Dosen> result = new ArrayList<>();

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
     * Mencari dosen berdasarkan nama atau NIP (pencarian sebagian).
     *
     * @param keyword kata kunci pencarian
     * @return daftar dosen yang cocok
     * @throws SQLException jika query gagal
     */
    public List<Dosen> search(String keyword) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM dosen WHERE nama LIKE ? OR nip LIKE ? ORDER BY nama";
        List<Dosen> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    private void setUserId(PreparedStatement ps, int index, Integer userId) throws SQLException {
        if (userId == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, userId);
        }
    }

    private Dosen mapRow(ResultSet rs) throws SQLException {
        Dosen d = new Dosen();
        d.setId(rs.getInt("id"));

        int userId = rs.getInt("user_id");
        d.setUserId(rs.wasNull() ? null : userId);

        d.setNip(rs.getString("nip"));
        d.setNama(rs.getString("nama"));
        d.setBidangKeahlian(rs.getString("bidang_keahlian"));
        d.setNoHp(rs.getString("no_hp"));
        d.setEmail(rs.getString("email"));
        return d;
    }
}
