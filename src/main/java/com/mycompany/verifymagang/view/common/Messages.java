package com.mycompany.verifymagang.view.common;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Penampil pesan standar (sukses, error, konfirmasi).
 *
 * <p>Memusatkan seluruh dialog di satu tempat agar teks dan gaya konsisten.</p>
 */
public final class Messages {

    private Messages() {
    }

    /**
     * Menampilkan pesan informasi.
     *
     * @param parent komponen induk
     * @param pesan  isi pesan
     */
    public static void info(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Menampilkan pesan sukses.
     *
     * @param parent komponen induk
     * @param pesan  isi pesan
     */
    public static void sukses(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Menampilkan pesan kesalahan.
     *
     * @param parent komponen induk
     * @param pesan  isi pesan
     */
    public static void error(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan, "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Menampilkan pesan peringatan.
     *
     * @param parent komponen induk
     * @param pesan  isi pesan
     */
    public static void peringatan(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Menampilkan dialog konfirmasi Ya/Tidak.
     *
     * @param parent komponen induk
     * @param pesan  pertanyaan
     * @return {@code true} bila pengguna memilih Ya
     */
    public static boolean konfirmasi(Component parent, String pesan) {
        int jawab = JOptionPane.showConfirmDialog(parent, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return jawab == JOptionPane.YES_OPTION;
    }

    /**
     * Menampilkan dialog konfirmasi penghapusan.
     *
     * @param parent komponen induk
     * @param objek  nama objek yang akan dihapus
     * @return {@code true} bila pengguna menyetujui
     */
    public static boolean konfirmasiHapus(Component parent, String objek) {
        return konfirmasi(parent, "Hapus " + objek + "?\nTindakan ini tidak dapat dibatalkan.");
    }

    /**
     * Meminta input teks satu baris.
     *
     * @param parent  komponen induk
     * @param judul   judul dialog
     * @param pesan   pertanyaan
     * @return teks yang diisi, atau {@code null} bila dibatalkan
     */
    public static String input(Component parent, String judul, String pesan) {
        return JOptionPane.showInputDialog(parent, pesan, judul, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Mengubah exception menjadi pesan yang ramah pengguna.
     *
     * @param ex exception
     * @return teks pesan
     */
    public static String pesanDari(Throwable ex) {
        if (ex == null) {
            return "Terjadi kesalahan yang tidak diketahui.";
        }
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            return ex.getClass().getSimpleName();
        }
        return msg;
    }
}
