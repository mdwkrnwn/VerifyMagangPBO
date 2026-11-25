package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.Mitra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO untuk tabel {@code mitra}.
 */
public class MitraDAO {

    private static final String COLUMNS =
            "id, nama, bidang_usaha, alamat, kota, kontak_person, no_hp, email, aktif";

    /**
     * Menyimpan data mitra baru.
     *
     * @param mitra data yang akan disimpan
     * @return id mitra yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(Mitra mitra) throws SQLException {
        String sql = "INSERT INTO mitra (nama, bidang_usaha, alamat, kota, kontak_person, no_hp, "
                + "email, aktif) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, mitra.getNama());
            ps.setString(2, mitra.getBidangUsaha());
            ps.setString(3, mitra.getAlamat());
            ps.setString(4, mitra.getKota());
            ps.setString(5, mitra.getKontakPerson());
            ps.setString(6, mitra.getNoHp());
            ps.setString(7, mitra.getEmail());
            ps.setBoolean(8, mitra.isAktif());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    mitra.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan mitra: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data mitra.
     *
     * @param mitra data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(Mitra mitra) throws SQLException {
        String sql = "UPDATE mitra SET nama = ?, bidang_usaha = ?, alamat = ?, kota = ?, "
                + "kontak_person = ?, no_hp = ?, email = ?, aktif = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mitra.getNama());
            ps.setString(2, mitra.getBidangUsaha());
            ps.setString(3, mitra.getAlamat());
            ps.setString(4, mitra.getKota());
            ps.setString(5, mitra.getKontakPerson());
            ps.setString(6, mitra.getNoHp());
            ps.setString(7, mitra.getEmail());
            ps.setBoolean(8, mitra.isAktif());
            ps.setInt(9, mitra.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus mitra berdasarkan id.
     *
     * @param id id mitra
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mitra WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari mitra berdasarkan id.
     *
     * @param id id mitra
     * @return mitra bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<Mitra> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mitra WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh mitra.
     *
     * @return daftar mitra
     * @throws SQLException jika query gagal
     */
    public List<Mitra> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mitra ORDER BY nama";
        List<Mitra> result = new ArrayList<>();

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
     * Mengambil mitra yang berstatus aktif.
     *
     * @return daftar mitra aktif
     * @throws SQLException jika query gagal
     */
    public List<Mitra> findAktif() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mitra WHERE aktif = TRUE ORDER BY nama";
        List<Mitra> result = new ArrayList<>();

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
     * Mencari mitra berdasarkan nama (pencarian sebagian).
     *
     * @param keyword kata kunci pencarian
     * @return daftar mitra yang cocok
     * @throws SQLException jika query gagal
     */
    public List<Mitra> search(String keyword) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM mitra WHERE nama LIKE ? OR kota LIKE ? ORDER BY nama";
        List<Mitra> result = new ArrayList<>();

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

    private Mitra mapRow(ResultSet rs) throws SQLException {
        Mitra m = new Mitra();
        m.setId(rs.getInt("id"));
        m.setNama(rs.getString("nama"));
        m.setBidangUsaha(rs.getString("bidang_usaha"));
        m.setAlamat(rs.getString("alamat"));
        m.setKota(rs.getString("kota"));
        m.setKontakPerson(rs.getString("kontak_person"));
        m.setNoHp(rs.getString("no_hp"));
        m.setEmail(rs.getString("email"));
        m.setAktif(rs.getBoolean("aktif"));
        return m;
    }
}
