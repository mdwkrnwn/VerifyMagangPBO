package com.mycompany.verifymagang.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.view.LoginFrameHolder;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Titik masuk aplikasi.
 *
 * <p>Bertanggung jawab menyiapkan Look &amp; Feel (FlatLaf Light), memeriksa
 * koneksi database, lalu membuka form login.</p>
 */
public final class App {

    private App() {
        // kelas utilitas
    }

    /**
     * Menjalankan aplikasi.
     *
     * @param args argumen baris perintah (tidak dipakai)
     */
    public static void main(String[] args) {
        setupLookAndFeel();
        cekKoneksiDatabase();

        SwingUtilities.invokeLater(() -> {
            LoginFrameHolder.showLogin();
        });
    }

    private static void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.arc", 8);
            UIManager.put("Button.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 8);
            UIManager.put("ScrollBar.trackArc", 8);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("TableHeader.height", 40);
            UIManager.put("Table.rowHeight", 40);
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        } catch (RuntimeException ex) {
            System.err.println("[App] Gagal memasang FlatLaf: " + ex.getMessage());
        }
    }

    /**
     * Memeriksa koneksi database saat start-up. Aplikasi tetap dibuka agar
     * pengguna bisa memperbaiki {@code db.properties}, tetapi pesan kesalahan
     * dicetak ke konsol.
     */
    private static void cekKoneksiDatabase() {
        String error = DatabaseConfig.testConnectionMessage();
        if (error != null) {
            System.err.println("[App] Koneksi database gagal: " + error);
            System.err.println("[App] Periksa src/main/resources/db.properties atau jalankan database/schema SQL.");
        } else {
            System.out.println("[App] Terhubung ke " + DatabaseConfig.getUrl());
        }
    }
}
