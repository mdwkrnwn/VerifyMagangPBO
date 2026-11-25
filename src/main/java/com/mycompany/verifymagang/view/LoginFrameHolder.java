package com.mycompany.verifymagang.view;

import com.mycompany.verifymagang.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Jembatan antara package {@code app} dan form login.
 *
 * <p>Memusatkan pembuatan form login di satu tempat sehingga {@code App} tidak
 * bergantung langsung pada kelas tampilan mana pun.</p>
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
