package com.mycompany.verifymagang;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.config.DatabaseConfig;
import com.mycompany.verifymagang.service.AuthService;
import com.mycompany.verifymagang.view.admin.MainAdminFrame;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;
import com.mycompany.verifymagang.view.mahasiswa.MainMahasiswaFrame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Form login aplikasi.
 *
 * <p>Dibangun sepenuhnya dengan kode agar tampilan konsisten dengan halaman
 * lain. Proses autentikasi dijalankan lewat {@link AuthService} di luar thread
 * UI sehingga jendela tidak pernah membeku.</p>
 */
public class LoginFrame extends JFrame {

    private final JTextField usernameField = Ui.textField(24);
    private final JPasswordField passwordField = Ui.passwordField(24);
    private final JButton loginButton;
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel dbStatusLabel = new JLabel(" ");

    /** Membuat form login. */
    public LoginFrame() {
        super("VerifyMagang - Masuk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 600));
        setSize(new Dimension(1020, 640));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.Colors.CARD);

        root.add(buildBrandPanel(), BorderLayout.WEST);
        root.add(buildFormPanel(), BorderLayout.CENTER);
        setContentPane(root);

        loginButton = (JButton) findLoginButton();
        getRootPane().setDefaultButton(loginButton);
        getRootPane().registerKeyboardAction(e -> attemptLogin(),
                javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        checkDatabase();
    }

    private JButton loginButtonRef;

    private JButton findLoginButton() {
        return loginButtonRef;
    }

    // ------------------------------------------------------------- tampilan

    private JComponent buildBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                try {
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.Colors.NAVY);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    // aksen lingkaran lembut
                    g2.setColor(new Color(0x25, 0x63, 0xEB, 40));
                    g2.fillOval(-120, getHeight() - 300, 380, 380);
                    g2.setColor(new Color(0x25, 0x63, 0xEB, 26));
                    g2.fillOval(getWidth() - 150, -110, 300, 300);
                } finally {
                    g2.dispose();
                }
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(430, 0));
        panel.setMinimumSize(new Dimension(360, 0));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(0, 44, 0, 44));

        JPanel logoRow = new JPanel();
        logoRow.setOpaque(false);
        logoRow.setLayout(new BoxLayout(logoRow, BoxLayout.X_AXIS));
        logoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                try {
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.Colors.PRIMARY);
                    g2.fill(new java.awt.geom.RoundRectangle2D.Double(
                            0, 0, getWidth(), getHeight(), 12, 12));
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new java.awt.BasicStroke(2.4f,
                            java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                    java.awt.geom.Path2D tick = new java.awt.geom.Path2D.Double();
                    tick.moveTo(getWidth() * 0.28, getHeight() * 0.52);
                    tick.lineTo(getWidth() * 0.44, getHeight() * 0.68);
                    tick.lineTo(getWidth() * 0.73, getHeight() * 0.34);
                    g2.draw(tick);
                } finally {
                    g2.dispose();
                }
            }
        };
        logo.setOpaque(false);
        logo.setPreferredSize(new Dimension(44, 44));
        logo.setMaximumSize(new Dimension(44, 44));

        JLabel brand = new JLabel("VerifyMagang");
        brand.setFont(Theme.FONT_BRAND.deriveFont(Font.BOLD, 20f));
        brand.setForeground(Color.WHITE);

        logoRow.add(logo);
        logoRow.add(Box.createHorizontalStrut(12));
        logoRow.add(brand);
        logoRow.add(Box.createHorizontalGlue());

        JLabel headline = new JLabel("<html>Sistem Verifikasi<br>Magang Mahasiswa</html>");
        headline.setFont(Theme.FONT_LOGIN_TITLE);
        headline.setForeground(Color.WHITE);
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("<html>Kelola pendaftaran, penempatan,<br>"
                + "logbook harian, dan penilaian magang<br>dalam satu aplikasi desktop.</html>");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.Colors.TEXT_ON_DARK_MUTED);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel features = new JPanel();
        features.setOpaque(false);
        features.setLayout(new BoxLayout(features, BoxLayout.Y_AXIS));
        features.setAlignmentX(Component.LEFT_ALIGNMENT);
        features.add(featureRow("Pendaftaran magang terpusat"));
        features.add(Box.createVerticalStrut(10));
        features.add(featureRow("Logbook harian dengan verifikasi dosen"));
        features.add(Box.createVerticalStrut(10));
        features.add(featureRow("Penilaian otomatis 40% mitra / 60% dosen"));

        inner.add(logoRow);
        inner.add(Box.createVerticalStrut(56));
        inner.add(headline);
        inner.add(Box.createVerticalStrut(16));
        inner.add(desc);
        inner.add(Box.createVerticalStrut(40));
        inner.add(features);

        panel.add(inner);
        return panel;
    }

    private JComponent featureRow(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.Colors.TEXT_ON_DARK_MUTED);
        l.setIcon(Icons.get(Icons.I.CHECK, 15, new Color(0x4A, 0xDE, 0x80)));
        l.setIconTextGap(10);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JComponent buildFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.Colors.CARD);

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(0, 56, 0, 56));

        JLabel title = new JLabel("Masuk ke akun Anda");
        title.setFont(Theme.FONT_LOGIN_TITLE.deriveFont(Font.BOLD, 24f));
        title.setForeground(Theme.Colors.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Gunakan akun yang diberikan administrator.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.Colors.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField.putClientProperty("JTextField.leadingIcon",
                Icons.get(Icons.I.USER, 16, Theme.Colors.TEXT_FAINT));
        passwordField.putClientProperty("JTextField.leadingIcon",
                Icons.get(Icons.I.LOCK, 16, Theme.Colors.TEXT_FAINT));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        Ui.FlatButton btn = Ui.primaryButton("Masuk",
                Icons.get(Icons.I.LOGOUT, 16, Color.WHITE));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(320, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> attemptLogin());
        loginButtonRef = btn;

        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.Colors.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        dbStatusLabel.setFont(Theme.FONT_SMALL);
        dbStatusLabel.setForeground(Theme.Colors.TEXT_FAINT);
        dbStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(Ui.field("Username", usernameField));
        card.add(Box.createVerticalStrut(16));
        card.add(Ui.field("Password", passwordField));
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(btn);
        card.add(Box.createVerticalStrut(22));
        card.add(hintBox());
        card.add(Box.createVerticalStrut(18));
        card.add(dbStatusLabel);

        wrapper.add(card);

        JPanel host = new JPanel(new BorderLayout());
        host.setBackground(Theme.Colors.CARD);
        host.add(wrapper, BorderLayout.CENTER);
        return host;
    }

    private JComponent hintBox() {
        JPanel box = new JPanel(new BorderLayout(10, 0));
        box.setBackground(Theme.Colors.PRIMARY_SOFT);
        box.setBorder(new EmptyBorder(12, 14, 12, 14));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JLabel icon = new JLabel(Icons.get(Icons.I.INFO, 16, Theme.Colors.PRIMARY));
        box.add(icon, BorderLayout.WEST);

        JLabel text = new JLabel("<html><b>Akun demo</b><br>"
                + "Admin: admin / admin123 &nbsp;&middot;&nbsp; Mahasiswa: mhs01 / mhs123</html>");
        text.setFont(Theme.FONT_SMALL);
        text.setForeground(Theme.Colors.TEXT_MUTED);
        box.add(text, BorderLayout.CENTER);
        return box;
    }

    // --------------------------------------------------------------- logika

    private void checkDatabase() {
        Async.run(this, () -> {
            String error = DatabaseConfig.testConnectionMessage();
            return error == null ? "" : error;
        }, error -> {
            if (error.isBlank()) {
                dbStatusLabel.setText("Terhubung ke " + DatabaseConfig.getDatabaseName());
                dbStatusLabel.setForeground(Theme.Colors.SUCCESS);
            } else {
                dbStatusLabel.setText("Database belum terhubung: " + error);
                dbStatusLabel.setForeground(Theme.Colors.DANGER);
            }
        }, error -> dbStatusLabel.setText("Tidak dapat memeriksa database."));
    }

    private void attemptLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Username wajib diisi.");
            usernameField.requestFocusInWindow();
            return;
        }
        if (password.isEmpty()) {
            showError("Password wajib diisi.");
            passwordField.requestFocusInWindow();
            return;
        }

        setBusy(true);
        statusLabel.setText(" ");

        Async.run(this,
                () -> new AuthService().login(username, password),
                result -> {
                    setBusy(false);
                    if (!result.isBerhasil()) {
                        showError(result.getPesan());
                        passwordField.setText("");
                        passwordField.requestFocusInWindow();
                        return;
                    }
                    SessionManager.login(result.getUser(),
                            result.getMahasiswaId(), result.getDosenId());
                    openDashboard(result);
                },
                error -> {
                    setBusy(false);
                    showError(Messages.pesanDari(error));
                });
    }

    private void openDashboard(AuthService.LoginResult result) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (result.isAdmin()) {
                new MainAdminFrame().setVisible(true);
            } else {
                new MainMahasiswaFrame().setVisible(true);
            }
        });
    }

    private void setBusy(boolean busy) {
        loginButton.setEnabled(!busy);
        loginButton.setText(busy ? "Memproses..." : "Masuk");
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                : Cursor.getDefaultCursor());
    }

    private void showError(String pesan) {
        statusLabel.setText(pesan);
    }

    /**
     * Titik masuk alternatif bila form dijalankan langsung dari NetBeans.
     *
     * @param args argumen baris perintah (tidak dipakai)
     */
    public static void main(String[] args) {
        com.mycompany.verifymagang.app.App.main(args);
    }

    /** Menampilkan form login (dipakai setelah logout). */
    public void tampilkan() {
        SwingUtilities.invokeLater(() -> {
            setLocationRelativeTo(null);
            setVisible(true);
        });
    }
}
