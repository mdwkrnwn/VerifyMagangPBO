package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO untuk tabel {@code users}.
 */
public class UserDAO {

    private static final String COLUMNS =
            "id, username, password, role, nama_lengkap, email, aktif, created_at";

    /**
     * Menyimpan user baru.
     *
     * @param user data user yang akan disimpan
     * @return id user yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, nama_lengkap, email, aktif) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getNamaLengkap());
            ps.setString(5, user.getEmail());
            ps.setBoolean(6, user.isAktif());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    user.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan user: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data user berdasarkan id.
     *
     * @param user data user dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, nama_lengkap = ?, "
                + "email = ?, aktif = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getNamaLengkap());
            ps.setString(5, user.getEmail());
            ps.setBoolean(6, user.isAktif());
            ps.setInt(7, user.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus user berdasarkan id.
     *
     * @param id id user
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari user berdasarkan id.
     *
     * @param id id user
     * @return user bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mencari user berdasarkan username (dipakai proses login).
     *
     * @param username username yang dicari
     * @return user bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh user.
     *
     * @return daftar user, kosong bila tidak ada data
     * @throws SQLException jika query gagal
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users ORDER BY id";
        List<User> result = new ArrayList<>();

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
     * Mengambil user berdasarkan role.
     *
     * @param role {@link User#ROLE_ADMIN} atau {@link User#ROLE_MAHASISWA}
     * @return daftar user dengan role tersebut
     * @throws SQLException jika query gagal
     */
    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE role = ? ORDER BY id";
        List<User> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    /**
     * Mengubah password user.
     *
     * @param userId       id user
     * @param passwordHash password yang sudah di-hash
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean updatePassword(int userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengubah status aktif user.
     *
     * @param userId id user
     * @param aktif  status baru
     * @return {@code true} bila berhasil
     * @throws SQLException jika query gagal
     */
    public boolean setAktif(int userId, boolean aktif) throws SQLException {
        String sql = "UPDATE users SET aktif = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, aktif);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengecek apakah username sudah dipakai.
     *
     * @param username username yang dicek
     * @return {@code true} bila sudah ada
     * @throws SQLException jika query gagal
     */
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setNamaLengkap(rs.getString("nama_lengkap"));
        user.setEmail(rs.getString("email"));
        user.setAktif(rs.getBoolean("aktif"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            user.setCreatedAt(created.toLocalDateTime());
        }
        return user;
    }
}
