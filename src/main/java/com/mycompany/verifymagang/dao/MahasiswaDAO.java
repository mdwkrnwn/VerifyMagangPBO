package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.Mahasiswa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO untuk tabel {@code mahasiswa}.
 */
public class MahasiswaDAO {

    private static final String COLUMNS =
            "id, user_id, nim, nama, jurusan, angkatan, no_hp, alamat";

    private static final String SELECT_WITH_USER =
            "SELECT m.id, m.user_id, m.nim, m.nama, m.jurusan, m.angkatan, m.no_hp, m.alamat "
            + "FROM mahasiswa m ";

    /**
     * Menyimpan data mahasiswa baru.
     *
     * @param mahasiswa data yang akan disimpan
     * @return id mahasiswa yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(Mahasiswa mahasiswa) throws SQLException {
        String sql = "INSERT INTO mahasiswa (user_id, nim, nama, jurusan, angkatan, no_hp, alamat) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, mahasiswa.getUserId());
            ps.setString(2, mahasiswa.getNim());
            ps.setString(3, mahasiswa.getNama());
            ps.setString(4, mahasiswa.getJurusan());
            ps.setInt(5, mahasiswa.getAngkatan());
            ps.setString(6, mahasiswa.getNoHp());
            ps.setString(7, mahasiswa.getAlamat());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    mahasiswa.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan mahasiswa: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data mahasiswa.
     *
     * @param mahasiswa data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(Mahasiswa mahasiswa) throws SQLException {
        String sql = "UPDATE mahasiswa SET user_id = ?, nim = ?, nama = ?, jurusan = ?, "
                + "angkatan = ?, no_hp = ?, alamat = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mahasiswa.getUserId());
            ps.setString(2, mahasiswa.getNim());
            ps.setString(3, mahasiswa.getNama());
            ps.setString(4, mahasiswa.getJurusan());
            ps.setInt(5, mahasiswa.getAngkatan());
            ps.setString(6, mahasiswa.getNoHp());
            ps.setString(7, mahasiswa.getAlamat());
            ps.setInt(8, mahasiswa.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus mahasiswa berdasarkan id.
     *
     * @param id id mahasiswa
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari mahasiswa berdasarkan id.
     *
     * @param id id mahasiswa
     * @return mahasiswa bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Mahasiswa> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mahasiswa WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mencari mahasiswa berdasarkan id user (relasi satu-ke-satu).
     *
     * @param userId id pada tabel users
     * @return mahasiswa bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Mahasiswa> findByUserId(int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mahasiswa WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mencari mahasiswa berdasarkan NIM.
     *
     * @param nim NIM mahasiswa
     * @return mahasiswa bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Mahasiswa> findByNim(String nim) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mahasiswa WHERE nim = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh mahasiswa.
     *
     * @return daftar mahasiswa
     * @throws SQLException jika query gagal
     */
    public List<Mahasiswa> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mahasiswa ORDER BY nama";
        List<Mahasiswa> result = new ArrayList<>();

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
     * Mencari mahasiswa berdasarkan nama atau NIM (pencarian sebagian).
     *
     * @param keyword kata kunci pencarian
     * @return daftar mahasiswa yang cocok
     * @throws SQLException jika query gagal
     */
    public List<Mahasiswa> search(String keyword) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mahasiswa WHERE nama LIKE ? OR nim LIKE ? "
                + "ORDER BY nama";
        List<Mahasiswa> result = new ArrayList<>();

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

    /**
     * Mengambil mahasiswa beserta username login-nya.
     *
     * @return daftar mahasiswa (kolom user terisi pada {@link Mahasiswa#getUserId()})
     * @throws SQLException jika query gagal
     */
    public List<Mahasiswa> findAllWithUser() throws SQLException {
        String sql = SELECT_WITH_USER + "ORDER BY m.nama";
        List<Mahasiswa> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    private Mahasiswa mapRow(ResultSet rs) throws SQLException {
        Mahasiswa m = new Mahasiswa();
        m.setId(rs.getInt("id"));
        m.setUserId(rs.getInt("user_id"));
        m.setNim(rs.getString("nim"));
        m.setNama(rs.getString("nama"));
        m.setJurusan(rs.getString("jurusan"));
        m.setAngkatan(rs.getInt("angkatan"));
        m.setNoHp(rs.getString("no_hp"));
        m.setAlamat(rs.getString("alamat"));
        return m;
    }
}
