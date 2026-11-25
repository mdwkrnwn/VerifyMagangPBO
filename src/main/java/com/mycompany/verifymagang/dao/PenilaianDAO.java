package com.mycompany.verifymagang.dao;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.model.PenilaianMagang;

import java.math.BigDecimal;
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
 * DAO untuk tabel {@code penilaian}.
 */
public class PenilaianDAO {

    private static final String COLUMNS =
            "id, penempatan_id, dosen_id, nilai_mitra, nilai_dosen, nilai_akhir, catatan, "
            + "tanggal_penilaian";

    private static final String SELECT_DETAIL =
            "SELECT nl.id, nl.penempatan_id, nl.dosen_id, nl.nilai_mitra, nl.nilai_dosen, "
            + "nl.nilai_akhir, nl.catatan, nl.tanggal_penilaian, m.nama AS nama_mahasiswa, "
            + "d.nama AS nama_dosen "
            + "FROM penilaian nl "
            + "JOIN penempatan pen ON pen.id = nl.penempatan_id "
            + "JOIN pendaftaran p ON p.id = pen.pendaftaran_id "
            + "JOIN mahasiswa m ON m.id = p.mahasiswa_id "
            + "JOIN dosen d ON d.id = nl.dosen_id ";

    /**
     * Menyimpan penilaian baru.
     *
     * @param penilaian data yang akan disimpan
     * @return id penilaian yang baru dibuat
     * @throws SQLException jika query gagal
     */
    public int insert(PenilaianMagang penilaian) throws SQLException {
        String sql = "INSERT INTO penilaian (penempatan_id, dosen_id, nilai_mitra, nilai_dosen, "
                + "nilai_akhir, catatan, tanggal_penilaian) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, penilaian.getPenempatanId());
            ps.setInt(2, penilaian.getDosenId());
            setDecimal(ps, 3, penilaian.getNilaiMitra());
            setDecimal(ps, 4, penilaian.getNilaiDosen());
            setDecimal(ps, 5, penilaian.getNilaiAkhir());
            ps.setString(6, penilaian.getCatatan());
            setDate(ps, 7, penilaian.getTanggalPenilaian());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    penilaian.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal menyimpan penilaian: id tidak dihasilkan.");
    }

    /**
     * Memperbarui data penilaian.
     *
     * @param penilaian data dengan id yang sudah ada
     * @return {@code true} bila ada baris yang berubah
     * @throws SQLException jika query gagal
     */
    public boolean update(PenilaianMagang penilaian) throws SQLException {
        String sql = "UPDATE penilaian SET penempatan_id = ?, dosen_id = ?, nilai_mitra = ?, "
                + "nilai_dosen = ?, nilai_akhir = ?, catatan = ?, tanggal_penilaian = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, penilaian.getPenempatanId());
            ps.setInt(2, penilaian.getDosenId());
            setDecimal(ps, 3, penilaian.getNilaiMitra());
            setDecimal(ps, 4, penilaian.getNilaiDosen());
            setDecimal(ps, 5, penilaian.getNilaiAkhir());
            ps.setString(6, penilaian.getCatatan());
            setDate(ps, 7, penilaian.getTanggalPenilaian());
            ps.setInt(8, penilaian.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus penilaian berdasarkan id.
     *
     * @param id id penilaian
     * @return {@code true} bila ada baris yang terhapus
     * @throws SQLException jika query gagal
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM penilaian WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mencari penilaian berdasarkan id.
     *
     * @param id id penilaian
     * @return penilaian bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PenilaianMagang> findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM penilaian WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mencari penilaian untuk satu penempatan (relasi satu-ke-satu).
     *
     * @param penempatanId id penempatan
     * @return penilaian bila ditemukan
     * @throws SQLException jika query gagal
     */
    public Optional<PenilaianMagang> findByPenempatan(int penempatanId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM penilaian WHERE penempatan_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, penempatanId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /**
     * Mengambil seluruh penilaian beserta nama mahasiswa dan dosen.
     *
     * @return daftar penilaian
     * @throws SQLException jika query gagal
     */
    public List<PenilaianMagang> findAllDetail() throws SQLException {
        String sql = SELECT_DETAIL + "ORDER BY nl.tanggal_penilaian DESC";
        List<PenilaianMagang> result = new ArrayList<>();

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
     * Mengambil penilaian yang diberikan oleh dosen tertentu.
     *
     * @param dosenId id dosen
     * @return daftar penilaian dosen tersebut
     * @throws SQLException jika query gagal
     */
    public List<PenilaianMagang> findByDosen(int dosenId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE nl.dosen_id = ? ORDER BY nl.tanggal_penilaian DESC";
        List<PenilaianMagang> result = new ArrayList<>();

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
     * Mengambil riwayat penilaian milik seorang mahasiswa.
     *
     * @param mahasiswaId id mahasiswa
     * @return daftar penilaian mahasiswa tersebut
     * @throws SQLException jika query gagal
     */
    public List<PenilaianMagang> findByMahasiswa(int mahasiswaId) throws SQLException {
        String sql = SELECT_DETAIL + "WHERE p.mahasiswa_id = ? ORDER BY nl.tanggal_penilaian DESC";
        List<PenilaianMagang> result = new ArrayList<>();

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
     * Mengecek apakah penempatan sudah dinilai.
     *
     * @param penempatanId id penempatan
     * @return {@code true} bila sudah ada penilaian
     * @throws SQLException jika query gagal
     */
    public boolean existsByPenempatan(int penempatanId) throws SQLException {
        String sql = "SELECT 1 FROM penilaian WHERE penempatan_id = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, penempatanId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Menghitung rata-rata nilai akhir seluruh mahasiswa.
     *
     * @return rata-rata nilai akhir, atau {@code null} bila belum ada data
     * @throws SQLException jika query gagal
     */
    public BigDecimal rataRataNilaiAkhir() throws SQLException {
        String sql = "SELECT AVG(nilai_akhir) FROM penilaian WHERE nilai_akhir IS NOT NULL";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return null;
    }

    private void setDecimal(PreparedStatement ps, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setBigDecimal(index, value);
        }
    }

    private void setDate(PreparedStatement ps, int index, java.time.LocalDate value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private PenilaianMagang mapRow(ResultSet rs) throws SQLException {
        PenilaianMagang p = new PenilaianMagang();
        p.setId(rs.getInt("id"));
        p.setPenempatanId(rs.getInt("penempatan_id"));
        p.setDosenId(rs.getInt("dosen_id"));
        p.setNilaiMitra(rs.getBigDecimal("nilai_mitra"));
        p.setNilaiDosen(rs.getBigDecimal("nilai_dosen"));
        p.setNilaiAkhir(rs.getBigDecimal("nilai_akhir"));
        p.setCatatan(rs.getString("catatan"));

        Date tanggal = rs.getDate("tanggal_penilaian");
        if (tanggal != null) {
            p.setTanggalPenilaian(tanggal.toLocalDate());
        }
        return p;
    }

    private PenilaianMagang mapRowDetail(ResultSet rs) throws SQLException {
        PenilaianMagang p = mapRow(rs);
        p.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
        p.setNamaDosen(rs.getString("nama_dosen"));
        return p;
    }
}
