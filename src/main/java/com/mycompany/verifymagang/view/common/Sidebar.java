package com.mycompany.verifymagang.view.common;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sidebar navigasi di sisi kiri.
 *
 * <p>Hanya mengurus tampilan dan penyorotan item aktif. Aksi navigasi
 * diteruskan ke pemanggil lewat {@link Listener}.</p>
 */
public final class Sidebar extends JPanel {

    /** Callback saat item navigasi dipilih. */
    public interface Listener {

        /**
         * Dipanggil ketika sebuah item diklik.
         *
         * @param key kunci halaman
         */
        void onNavigate(String key);
    }

    private final Map<String, NavButton> buttons = new LinkedHashMap<>();
    private final List<NavButton> allButtons = new ArrayList<>();
    private Listener listener;
    private String activeKey;

    /**
     * Membuat sidebar.
     *
     * @param brandTitle judul aplikasi
     * @param brandSub   subjudul aplikasi
     */
    public Sidebar(String brandTitle, String brandSub) {
        setLayout(new BorderLayout());
        setBackground(Theme.Colors.NAVY);
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        setMinimumSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));

        add(buildBrand(brandTitle, brandSub), BorderLayout.NORTH);
        add(buildNavContainer(), BorderLayout.CENTER);
    }

    private JComponent buildBrand(String brandTitle, String brandSub) {
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(26, 22, 22, 22));

        JPanel logoRow = new JPanel();
        logoRow.setOpaque(false);
        logoRow.setLayout(new BoxLayout(logoRow, BoxLayout.X_AXIS));

        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.Colors.PRIMARY);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new java.awt.BasicStroke(2.2f, java.awt.BasicStroke.CAP_ROUND,
                            java.awt.BasicStroke.JOIN_ROUND));
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
        logo.setPreferredSize(new Dimension(36, 36));
        logo.setMaximumSize(new Dimension(36, 36));
        logo.setMinimumSize(new Dimension(36, 36));

        JLabel title = new JLabel(brandTitle);
        title.setFont(Theme.FONT_BRAND);
        title.setForeground(Color.WHITE);

        logoRow.add(logo);
        logoRow.add(Box.createHorizontalStrut(10));
        logoRow.add(title);
        logoRow.add(Box.createHorizontalGlue());

        JLabel sub = new JLabel(brandSub);
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.Colors.TEXT_ON_DARK_MUTED);

        brand.add(logoRow);
        brand.add(Box.createVerticalStrut(10));
        brand.add(sub);
        return brand;
    }

    private JComponent buildNavContainer() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(4, 12, 12, 12));

        JLabel sectionLabel = new JLabel("MENU UTAMA");
        sectionLabel.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        sectionLabel.setForeground(Theme.Colors.TEXT_ON_DARK_MUTED);
        sectionLabel.setBorder(new EmptyBorder(6, 10, 10, 0));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nav.add(sectionLabel);
        nav.add(Box.createVerticalStrut(2));

        wrapper.add(nav, BorderLayout.NORTH);
        navContainer = nav;
        return wrapper;
    }

    private JPanel navContainer;

    /**
     * Menambahkan item navigasi.
     *
     * @param key   kunci halaman
     * @param label teks menu
     * @param icon  ikon menu
     */
    public void addItem(String key, String label, Icon icon) {
        NavButton b = new NavButton(key, label, icon);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.put(key, b);
        allButtons.add(b);
        navContainer.add(b);
        navContainer.add(Box.createVerticalStrut(2));
    }

    /**
     * Menambahkan pemisah bagian pada navigasi.
     *
     * @param judul judul bagian
     */
    public void addSection(String judul) {
        JLabel l = new JLabel(judul);
        l.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        l.setForeground(Theme.Colors.TEXT_ON_DARK_MUTED);
        l.setBorder(new EmptyBorder(18, 10, 8, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        navContainer.add(l);
    }

    /**
     * Menambahkan area kosong yang mendorong sisa konten ke bawah.
     */
    public void addFiller() {
        navContainer.add(Box.createVerticalGlue());
    }

    /**
     * Menetapkan listener navigasi.
     *
     * @param listener listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Menyorot item aktif.
     *
     * @param key kunci halaman
     */
    public void setActive(String key) {
        this.activeKey = key;
        for (Map.Entry<String, NavButton> e : buttons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(key));
        }
        repaint();
    }

    /**
     * Menambahkan tombol aksi di bagian bawah sidebar (mis. logout).
     *
     * @param label teks
     * @param icon  ikon
     * @param aksi  aksi saat diklik
     */
    public void addFooterAction(String label, Icon icon, Runnable aksi) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 12, 18, 12));

        NavButton b = new NavButton("__footer__", label, icon);
        b.addActionListener(e -> aksi.run());
        footer.add(b, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    /** Tombol navigasi dengan latar aktif dan efek hover. */
    private final class NavButton extends JComponent {

        private final String key;
        private final String label;
        private final Icon icon;
        private boolean active;
        private boolean hover;
        private final List<java.awt.event.ActionListener> listeners = new ArrayList<>();

        NavButton(String key, String label, Icon icon) {
            this.key = key;
            this.label = label;
            this.icon = icon;
            setFont(Theme.FONT_BODY);
            setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH - 24, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setMinimumSize(new Dimension(100, 42));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    for (java.awt.event.ActionListener l : new ArrayList<>(listeners)) {
                        l.actionPerformed(new java.awt.event.ActionEvent(
                                NavButton.this, java.awt.event.ActionEvent.ACTION_PERFORMED, key));
                    }
                    if (listener != null && !"__footer__".equals(key)) {
                        listener.onNavigate(key);
                    }
                }
            });
        }

        void addActionListener(java.awt.event.ActionListener l) {
            listeners.add(l);
        }

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (active) {
                    g2.setColor(Theme.Colors.PRIMARY);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 9, 9));
                } else if (hover) {
                    g2.setColor(Theme.Colors.NAVY_HOVER);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 9, 9));
                }

                int iconSize = 18;
                int iconX = 12;
                int iconY = (getHeight() - iconSize) / 2;

                Icon colored = Icons.get(
                        iconKey(key), iconSize,
                        active ? Color.WHITE : Theme.Colors.TEXT_ON_DARK_MUTED);
                colored.paintIcon(this, g2, iconX, iconY);

                g2.setFont(Theme.FONT_BODY);
                g2.setColor(active ? Color.WHITE : Theme.Colors.TEXT_ON_DARK);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(label, iconX + iconSize + 12, textY);
            } finally {
                g2.dispose();
            }
        }

        private Icons.I iconKey(String key) {
            return switch (key) {
                case "dashboard" -> Icons.I.DASHBOARD;
                case "mahasiswa" -> Icons.I.MAHASISWA;
                case "dosen" -> Icons.I.DOSEN;
                case "mitra" -> Icons.I.MITRA;
                case "lowongan" -> Icons.I.LOWONGAN;
                case "periode" -> Icons.I.PERIODE;
                case "pendaftaran" -> Icons.I.PENDAFTARAN;
                case "penempatan" -> Icons.I.PENEMPATAN;
                case "logbook" -> Icons.I.LOGBOOK;
                case "penilaian" -> Icons.I.PENILAIAN;
                case "profil" -> Icons.I.PROFIL;
                default -> Icons.I.LOGOUT;
            };
        }
    }
}
