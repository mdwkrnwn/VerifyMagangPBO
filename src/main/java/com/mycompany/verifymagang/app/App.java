package com.mycompany.verifymagang.app;

import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.view.LoginFrameHolder;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Titik masuk aplikasi.
 *
 * <p>Bertanggung jawab menyiapkan Look &amp; Feel, memeriksa koneksi database,
 * lalu membuka form login. Semua tampilan tetap dibuat oleh Anda melalui
 * NetBeans GUI Builder; kelas ini hanya mengorkestrasi.</p>
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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            // Bila Nimbus tidak tersedia, gunakan Look & Feel bawaan sistem.
            System.err.println("[App] Gagal memasang Nimbus: " + ex.getMessage());
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
