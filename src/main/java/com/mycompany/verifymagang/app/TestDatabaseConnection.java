
package com.mycompany.verifymagang.app;

import com.mycompany.verifymagang.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class TestDatabaseConnection {

    public static void main(String[] args) {

        System.out.println("=== TEST KONEKSI DATABASE ===");

        try (Connection conn = DatabaseConfig.getConnection()) {

            if (conn != null && !conn.isClosed()) {

                DatabaseMetaData meta = conn.getMetaData();

                System.out.println("Koneksi berhasil!");
                System.out.println("Database : "
                        + conn.getCatalog());
                System.out.println("DB Server : "
                        + meta.getDatabaseProductName());
                System.out.println("Versi : "
                        + meta.getDatabaseProductVersion());

            }

        } catch (Exception e) {

            System.out.println("Koneksi gagal!");
            System.out.println("Pesan: " + e.getMessage());

            e.printStackTrace();
        }
    }
}
