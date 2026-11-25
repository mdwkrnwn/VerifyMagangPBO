package com.mycompany.verifymagang.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Pusat konfigurasi koneksi database.
 *
 * <p>Konfigurasi dibaca dari {@code src/main/resources/db.properties}. Nilai dapat
 * ditimpa lewat system property (misalnya {@code -Ddb.password=rahasia}) atau
 * environment variable ({@code DB_HOST}, {@code DB_PORT}, {@code DB_NAME},
 * {@code DB_USER}, {@code DB_PASSWORD}).</p>
 *
 * <p>Kelas ini hanya bertanggung jawab membuat {@link Connection}. Seluruh query
 * SQL berada di package {@code dao}.</p>
 */
public final class DatabaseConfig {

    private static final String CONFIG_FILE = "/db.properties";

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_NAME = "verify_magang";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_PARAMS =
            "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8";

    private static final Properties CONFIG = loadConfig();

    /** URL JDBC yang sedang dipakai. Berguna untuk logging/diagnosa. */
    private static final String URL = buildUrl();

    private DatabaseConfig() {
        // kelas utilitas, tidak boleh diinstansiasi
    }

    /**
     * Membuka koneksi baru ke database.
     *
     * <p>Pemanggil wajib menutup koneksi (gunakan try-with-resources).</p>
     *
     * @return koneksi database yang sudah terbuka
     * @throws SQLException jika koneksi gagal dibuat
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, CONFIG.getProperty("db.user"), CONFIG.getProperty("db.password"));
    }

    /** @return URL JDBC yang dipakai aplikasi. */
    public static String getUrl() {
        return URL;
    }

    /** @return nama database yang dipakai aplikasi. */
    public static String getDatabaseName() {
        return CONFIG.getProperty("db.name");
    }

    /**
     * Menguji koneksi database tanpa membocorkan koneksi.
     *
     * @return {@code true} bila koneksi berhasil dibuka
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Menguji koneksi dan mengembalikan pesan kesalahan bila gagal.
     *
     * @return {@code null} bila berhasil, atau pesan kesalahan bila gagal
     */
    public static String testConnectionMessage() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return null;
            }
            return "Koneksi tidak dapat dibuka.";
        } catch (SQLException ex) {
            return ex.getMessage();
        }
    }

    private static Properties loadConfig() {
        Properties props = new Properties();

        try (InputStream in = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ex) {
            System.err.println("[DatabaseConfig] Gagal membaca " + CONFIG_FILE + ": " + ex.getMessage());
        }

        applyOverride(props, "db.host", "DB_HOST", DEFAULT_HOST);
        applyOverride(props, "db.port", "DB_PORT", DEFAULT_PORT);
        applyOverride(props, "db.name", "DB_NAME", DEFAULT_NAME);
        applyOverride(props, "db.user", "DB_USER", DEFAULT_USER);
        applyOverride(props, "db.password", "DB_PASSWORD", DEFAULT_PASSWORD);
        applyOverride(props, "db.params", "DB_PARAMS", DEFAULT_PARAMS);

        return props;
    }

    /**
     * Urutan prioritas: system property &gt; environment variable &gt; isi
     * db.properties &gt; nilai default.
     */
    private static void applyOverride(Properties props, String propertyKey, String envKey, String fallback) {
        String value = System.getProperty(propertyKey);
        if (value == null) {
            value = System.getenv(envKey);
        }
        if (value == null) {
            value = props.getProperty(propertyKey);
        }
        if (value == null) {
            value = fallback;
        }
        props.setProperty(propertyKey, value);
    }

    private static String buildUrl() {
        String params = CONFIG.getProperty("db.params", "").trim();
        StringBuilder url = new StringBuilder("jdbc:mysql://")
                .append(CONFIG.getProperty("db.host"))
                .append(':')
                .append(CONFIG.getProperty("db.port"))
                .append('/')
                .append(CONFIG.getProperty("db.name"));

        if (!params.isEmpty()) {
            url.append('?').append(params);
        }
        return url.toString();
    }
}
