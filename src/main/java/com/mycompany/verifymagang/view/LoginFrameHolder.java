package com.mycompany.verifymagang.view;

import com.mycompany.verifymagang.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Jembatan antara package {@code app} dan form login yang Anda desain di
 * NetBeans GUI Builder.
 *
 * <p>Dengan memusatkan pembuatan form di satu tempat, lokasi/penamaan form
 * login bisa berubah tanpa menyentuh {@code App}. Jika Anda memindahkan
 * {@code LoginFrame} ke package {@code view}, cukup ubah import di kelas ini.</p>
 */
public final class LoginFrameHolder {

    private LoginFrameHolder() {
        // kelas utilitas
    }

    /** Menampilkan form login. */
    public static void showLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
