package com.mycompany.verifymagang.view.common;

import com.mycompany.verifymagang.LoginFrame;
import com.mycompany.verifymagang.app.SessionManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Kerangka jendela utama aplikasi.
 *
 * <p>Menyusun tiga bagian tetap: sidebar navigasi di kiri, topbar di atas, dan
 * area konten yang berganti memakai {@link CardLayout}. Setiap halaman
 * didaftarkan sekali lewat {@link #addPage(String, String, String, JComponent)}
 * lalu ditampilkan lewat {@link #navigate(String)}.</p>
 *
 * <p>Kelas ini tidak mengakses database. Halaman yang perlu memuat ulang data
 * cukup mengimplementasikan {@link Refreshable}.</p>
 */
public class AppFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Sidebar sidebar;
    private final JLabel greetingLabel = new JLabel();
    private final JLabel roleLabel = new JLabel();
    private final Map<String, JComponent> pages = new LinkedHashMap<>();
    private final Map<String, String> titles = new LinkedHashMap<>();
    private String activeKey;

    /**
     * Membuat jendela utama.
     *
     * @param judul judul jendela
     * @param peran teks peran yang ditampilkan di topbar
     */
    public AppFrame(String judul, String peran) {
        super(judul);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 700));
        setSize(new Dimension(1280, 780));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.Colors.BACKGROUND);

        sidebar = new Sidebar("VerifyMagang", peran);
        sidebar.setListener(this::navigate);
        root.add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.Colors.BACKGROUND);
        main.add(buildTopbar(peran), BorderLayout.NORTH);

        content.setBackground(Theme.Colors.BACKGROUND);
        main.add(content, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);

        setContentPane(root);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SessionManager.logout();
            }
        });
    }

    private JComponent buildTopbar(String peran) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.Colors.CARD);
        bar.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, Theme.Colors.BORDER));
        bar.setPreferredSize(new Dimension(0, Theme.TOPBAR_HEIGHT));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(0, 24, 0, 0));
        greetingLabel.setFont(Theme.FONT_BODY);
        greetingLabel.setForeground(Theme.Colors.TEXT_MUTED);
        left.add(greetingLabel);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(0, 0, 0, 20));

        roleLabel.setFont(Theme.FONT_SMALL.deriveFont(java.awt.Font.BOLD));
        roleLabel.setForeground(Theme.Colors.PRIMARY);
        roleLabel.setOpaque(true);
        roleLabel.setBackground(Theme.Colors.PRIMARY_SOFT);
        roleLabel.setBorder(new EmptyBorder(5, 12, 5, 12));

        JButton logout = Ui.ghostButton("Keluar",
                Icons.get(Icons.I.LOGOUT, 16, Theme.Colors.TEXT_MUTED));
        logout.addActionListener(e -> logout());

        right.add(roleLabel);
        right.add(Box.createHorizontalStrut(4));
        right.add(logout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /**
     * Memperbarui sapaan dan nama pengguna di topbar.
     */
    public void refreshUserInfo() {
        String nama = SessionManager.isLoggedIn()
                ? SessionManager.getCurrentUser().getNamaLengkap()
                : "-";
        greetingLabel.setText("Selamat datang, " + nama);
        roleLabel.setText(SessionManager.isAdmin() ? "ADMIN" : "MAHASISWA");
    }

    /**
     * Mendaftarkan satu halaman.
     *
     * @param key       kunci navigasi
     * @param judul     judul menu di sidebar
     * @param subjudul  subjudul (tidak dipakai di sidebar, disimpan untuk info)
     * @param component komponen halaman
     */
    public void addPage(String key, String judul, String subjudul, JComponent component) {
        pages.put(key, component);
        titles.put(key, judul);
        content.add(component, key);
    }

    /**
     * Menambahkan item navigasi sidebar.
     *
     * @param key   kunci halaman
     * @param label teks menu
     * @param icon  ikon
     */
    public void addNavItem(String key, String label, javax.swing.Icon icon) {
        sidebar.addItem(key, label, icon);
    }

    /**
     * Menambahkan judul bagian pada sidebar.
     *
     * @param judul judul bagian
     */
    public void addNavSection(String judul) {
        sidebar.addSection(judul);
    }

    /**
     * Menambahkan aksi di bagian bawah sidebar.
     *
     * @param label teks tombol
     * @param icon  ikon
     * @param aksi  aksi saat diklik
     */
    public void addFooterAction(String label, javax.swing.Icon icon, Runnable aksi) {
        sidebar.addFooterAction(label, icon, aksi);
    }

    /**
     * Berpindah ke halaman tertentu.
     *
     * @param key kunci halaman
     */
    public void navigate(String key) {
        JComponent page = pages.get(key);
        if (page == null) {
            return;
        }
        activeKey = key;
        cardLayout.show(content, key);
        sidebar.setActive(key);
        if (page instanceof Refreshable refreshable) {
            refreshable.onShow();
        }
    }

    /**
     * Menampilkan halaman pertama (dipanggil setelah semua halaman didaftarkan).
     *
     * @param key kunci halaman awal
     */
    public void startAt(String key) {
        refreshUserInfo();
        navigate(key);
    }

    /** @return kunci halaman yang sedang aktif */
    public String getActiveKey() {
        return activeKey;
    }

    /** Melakukan logout: membersihkan sesi lalu membuka form login. */
    protected void logout() {
        boolean yakin = Messages.konfirmasi(this, "Keluar dari aplikasi?");
        if (!yakin) {
            return;
        }
        SessionManager.logout();
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
