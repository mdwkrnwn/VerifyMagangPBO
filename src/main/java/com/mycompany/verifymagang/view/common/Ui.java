package com.mycompany.verifymagang.view.common;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Pabrik komponen antarmuka bergaya modern.
 *
 * <p>Seluruh halaman memakai kelas ini agar tampilan konsisten. Kelas ini murni
 * presentasi: tidak ada akses database maupun logika bisnis.</p>
 */
public final class Ui {

    private Ui() {
    }

    // ================================================================
    // Panel
    // ================================================================

    /** Panel dengan latar membulat dan garis tepi opsional. */
    public static class Card extends JPanel {

        private final Color fill;
        private final int radius;
        private final Color border;

        public Card(Color fill, int radius, Color border) {
            this.fill = fill;
            this.radius = radius;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                RoundRectangle2D shape = new RoundRectangle2D.Double(
                        0.5, 0.5, getWidth() - 1.0, getHeight() - 1.0, radius, radius);
                if (fill != null) {
                    g2.setColor(fill);
                    g2.fill(shape);
                }
                if (border != null) {
                    g2.setColor(border);
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(shape);
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Kartu putih dengan garis tepi halus.
     *
     * @return panel kartu
     */
    public static Card card() {
        return new Card(Theme.Colors.CARD, Theme.CARD_RADIUS, Theme.Colors.BORDER);
    }

    /**
     * Panel berlatar penuh tanpa sudut membulat.
     *
     * @param color warna latar
     * @return panel
     */
    public static JPanel panel(Color color) {
        JPanel p = new JPanel();
        p.setBackground(color);
        return p;
    }

    /**
     * Panel transparan untuk wadah layout.
     *
     * @return panel transparan
     */
    public static JPanel clear() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        return p;
    }

    // ================================================================
    // Tombol
    // ================================================================

    /** Tombol dengan sudut membulat dan efek hover. */
    public static class FlatButton extends JButton {

        private final Color base;
        private final Color hover;
        private final Color pressed;
        private final Color fg;
        private final Color borderColor;

        FlatButton(String text, Icon icon, Color base, Color hover, Color pressed,
                Color fg, Color borderColor) {
            super(text, icon);
            this.base = base;
            this.hover = hover;
            this.pressed = pressed;
            this.fg = fg;
            this.borderColor = borderColor;

            setFont(Theme.FONT_BOLD);
            setForeground(fg);
            setIconTextGap(8);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 16, 9, 16));
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            setCursor(enabled
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    : Cursor.getDefaultCursor());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D shape = new RoundRectangle2D.Double(
                        0.5, 0.5, getWidth() - 1.0, getHeight() - 1.0, 9, 9);

                Color bg;
                if (!isEnabled()) {
                    bg = new Color(0xEE, 0xF2, 0xF6);
                } else if (getModel().isPressed()) {
                    bg = pressed;
                } else if (getModel().isRollover()) {
                    bg = hover;
                } else {
                    bg = base;
                }

                if (bg != null) {
                    g2.setColor(bg);
                    g2.fill(shape);
                }
                if (borderColor != null) {
                    g2.setColor(isEnabled() ? borderColor : Theme.Colors.BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(shape);
                }
            } finally {
                g2.dispose();
            }
            setForeground(isEnabled() ? fg : Theme.Colors.TEXT_FAINT);
            super.paintComponent(g);
        }
    }

    /**
     * Tombol utama (biru penuh).
     *
     * @param text teks tombol
     * @param icon ikon, boleh {@code null}
     * @return tombol
     */
    public static FlatButton primaryButton(String text, Icon icon) {
        return new FlatButton(text, icon, Theme.Colors.PRIMARY, Theme.Colors.PRIMARY_HOVER,
                Theme.Colors.PRIMARY_PRESSED, Color.WHITE, null);
    }

    /**
     * Tombol sekunder (putih dengan garis tepi).
     *
     * @param text teks tombol
     * @param icon ikon, boleh {@code null}
     * @return tombol
     */
    public static FlatButton secondaryButton(String text, Icon icon) {
        return new FlatButton(text, icon, Theme.Colors.CARD, Theme.Colors.SURFACE_HOVER,
                Theme.Colors.SURFACE_PRESSED, Theme.Colors.TEXT, Theme.Colors.BORDER_STRONG);
    }

    /**
     * Tombol bahaya (merah lembut).
     *
     * @param text teks tombol
     * @param icon ikon, boleh {@code null}
     * @return tombol
     */
    public static FlatButton dangerButton(String text, Icon icon) {
        return new FlatButton(text, icon, Theme.Colors.DANGER_SOFT, new Color(0xFE, 0xE2, 0xE2),
                new Color(0xFD, 0xD3, 0xD3), Theme.Colors.DANGER, new Color(0xFB, 0xC8, 0xC8));
    }

    /**
     * Tombol sukses (hijau lembut).
     *
     * @param text teks tombol
     * @param icon ikon, boleh {@code null}
     * @return tombol
     */
    public static FlatButton successButton(String text, Icon icon) {
        return new FlatButton(text, icon, Theme.Colors.SUCCESS_SOFT, new Color(0xDC, 0xFC, 0xE7),
                new Color(0xCB, 0xF9, 0xDA), Theme.Colors.SUCCESS, new Color(0xB8, 0xF2, 0xCC));
    }

    /**
     * Tombol transparan untuk aksi ringan.
     *
     * @param text teks tombol
     * @param icon ikon, boleh {@code null}
     * @return tombol
     */
    public static FlatButton ghostButton(String text, Icon icon) {
        return new FlatButton(text, icon, null, Theme.Colors.SURFACE_HOVER,
                Theme.Colors.SURFACE_PRESSED, Theme.Colors.TEXT_MUTED, null);
    }

    /**
     * Tombol ikon persegi tanpa teks.
     *
     * @param icon    ikon
     * @param tooltip keterangan tooltip
     * @return tombol ikon
     */
    public static JButton iconButton(Icon icon, String tooltip) {
        JButton b = new JButton(icon);
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(32, 32));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(4, 4, 4, 4));
        return b;
    }

    // ================================================================
    // Teks
    // ================================================================

    /**
     * Label judul halaman.
     *
     * @param text teks
     * @return label
     */
    public static JLabel pageTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TITLE);
        l.setForeground(Theme.Colors.TEXT);
        return l;
    }

    /**
     * Label subjudul.
     *
     * @param text teks
     * @return label
     */
    public static JLabel pageSubtitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.Colors.TEXT_MUTED);
        return l;
    }

    /**
     * Label judul bagian dalam kartu.
     *
     * @param text teks
     * @return label
     */
    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SECTION);
        l.setForeground(Theme.Colors.TEXT);
        return l;
    }

    /**
     * Label teks kecil (caption).
     *
     * @param text teks
     * @return label
     */
    public static JLabel caption(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.Colors.TEXT_MUTED);
        return l;
    }

    /**
     * Label teks biasa.
     *
     * @param text teks
     * @return label
     */
    public static JLabel text(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.Colors.TEXT);
        return l;
    }

    /**
     * Label teks tebal.
     *
     * @param text teks
     * @return label
     */
    public static JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BOLD);
        l.setForeground(Theme.Colors.TEXT);
        return l;
    }

    /**
     * Label dengan ikon di kiri.
     *
     * @param text teks
     * @param icon ikon
     * @param color warna teks
     * @return label
     */
    public static JLabel withIcon(String text, Icon icon, Color color) {
        JLabel l = new JLabel(text, icon, SwingConstants.LEFT);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(color);
        l.setIconTextGap(8);
        return l;
    }

    // ================================================================
    // Input
    // ================================================================

    /**
     * Kolom teks dengan gaya seragam.
     *
     * @param columns jumlah kolom
     * @return text field
     */
    public static JTextField textField(int columns) {
        JTextField f = new JTextField();
        applyFieldStyle(f, columns);
        return f;
    }

    /**
     * Kolom password dengan gaya seragam.
     *
     * @param columns jumlah kolom
     * @return password field
     */
    public static JPasswordField passwordField(int columns) {
        JPasswordField f = new JPasswordField();
        applyFieldStyle(f, columns);
        return f;
    }

    /**
     * Area teks multiline dengan gaya seragam.
     *
     * @param rows jumlah baris
     * @param cols jumlah kolom
     * @return text area dalam scroll pane
     */
    public static JScrollPane textArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(Theme.FONT_BODY);
        area.setForeground(Theme.Colors.TEXT);
        area.setBackground(Theme.Colors.CARD);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createLineBorder(Theme.Colors.BORDER_STRONG));
        sp.setPreferredSize(new Dimension(320, rows * 22 + 16));
        sp.setName("area");
        return sp;
    }

    /**
     * Mengambil komponen JTextArea dari scroll pane hasil
     * {@link #textArea(int, int)}.
     *
     * @param sp scroll pane
     * @return text area
     */
    public static JTextArea areaOf(JScrollPane sp) {
        return (JTextArea) sp.getViewport().getView();
    }

    /**
     * Combo box dengan gaya seragam.
     *
     * @param items daftar pilihan awal
     * @param <T>   tipe elemen
     * @return combo box
     */
    public static <T> JComboBox<T> comboBox(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setFont(Theme.FONT_BODY);
        c.setForeground(Theme.Colors.TEXT);
        c.setBackground(Theme.Colors.CARD);
        c.setPreferredSize(new Dimension(180, 38));
        return c;
    }

    /**
     * Combo box berisi objek model dengan teks tampilan khusus.
     *
     * <p>Dipakai agar combo menampilkan nama yang ramah pengguna, bukan
     * {@code toString()} bawaan entity.</p>
     *
     * @param items   daftar item
     * @param keTeks  fungsi pengubah objek menjadi teks
     * @param <T>     tipe elemen
     * @return combo box
     */
    public static <T> JComboBox<T> comboBox(java.util.List<T> items,
            java.util.function.Function<T, String> keTeks) {
        JComboBox<T> c = new JComboBox<>(new java.util.Vector<>(items));
        c.setFont(Theme.FONT_BODY);
        c.setForeground(Theme.Colors.TEXT);
        c.setBackground(Theme.Colors.CARD);
        c.setPreferredSize(new Dimension(180, 38));
        c.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                setFont(Theme.FONT_BODY);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                if (value == null) {
                    setText("-");
                } else {
                    @SuppressWarnings("unchecked")
                    T item = (T) value;
                    setText(keTeks.apply(item));
                }
                if (!selected) {
                    setBackground(Theme.Colors.CARD);
                    setForeground(Theme.Colors.TEXT);
                }
                return this;
            }
        });
        return c;
    }

    private static void applyFieldStyle(JTextField f, int columns) {
        f.setFont(Theme.FONT_BODY);
        f.setForeground(Theme.Colors.TEXT);
        f.setBackground(Theme.Colors.CARD);
        f.setCaretColor(Theme.Colors.TEXT);
        // Kolom disetel lewat ukuran piksel, bukan setColumns(), karena Look & Feel
        // modern mengabaikan preferred size bila jumlah kolom lebih dari nol.
        f.setColumns(0);
        f.setPreferredSize(new Dimension(lebarKolom(columns), 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.Colors.BORDER_STRONG, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private static int lebarKolom(int columns) {
        if (columns <= 0) {
            return 200;
        }
        return Math.max(160, columns * 10 + 42);
    }

    /**
     * Pasangan label + komponen input, tersusun vertikal.
     *
     * @param label     teks label
     * @param component komponen input
     * @return panel siap ditata
     */
    public static JPanel field(String label, JComponent component) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.Colors.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension pref = component.getPreferredSize();
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

        p.add(l);
        p.add(Box.createVerticalStrut(6));
        p.add(component);
        return p;
    }

    // ================================================================
    // Tabel
    // ================================================================

    /**
     * Membuat model tabel tanpa sel yang bisa diedit.
     *
     * @param columns nama kolom
     * @return model tabel
     */
    public static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Menerapkan gaya modern pada tabel.
     *
     * @param table tabel
     */
    public static void styleTable(JTable table) {
        table.setFont(Theme.FONT_BODY);
        table.setForeground(Theme.Colors.TEXT);
        table.setBackground(Theme.Colors.CARD);
        table.setRowHeight(Theme.ROW_HEIGHT);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Theme.Colors.PRIMARY_SOFT);
        table.setSelectionForeground(Theme.Colors.TEXT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFocusable(false);
        table.setRowMargin(0);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, Theme.TABLE_HEADER_HEIGHT));
        header.setDefaultRenderer(new HeaderRenderer());
        header.setBackground(Theme.Colors.TABLE_HEADER_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.Colors.BORDER));

        table.setDefaultRenderer(Object.class, new BodyRenderer());
        table.setDefaultRenderer(Number.class, new BodyRenderer());
    }

    /**
     * Membungkus komponen apa pun dalam scroll pane tanpa garis tepi.
     *
     * @param view komponen isi
     * @return scroll pane
     */
    public static JScrollPane scroll(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Theme.Colors.BACKGROUND);
        sp.setBackground(Theme.Colors.BACKGROUND);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        return sp;
    }

    /**
     * Membungkus komponen agar mengisi lebar penuh tanpa mengubah tingginya.
     *
     * <p>Dipakai pada halaman yang isinya disusun dengan {@link BoxLayout}
     * {@code Y_AXIS}: layout tersebut meregangkan anak yang tinggi maksimumnya
     * tak terbatas, sehingga kartu akan menyisakan ruang kosong besar. Pembungkus
     * ini membatasi tinggi pada tinggi alami isinya.</p>
     *
     * @param isi komponen yang akan dilebarkan
     * @return panel pembungkus
     */
    public static JComponent penuhLebar(JComponent isi) {
        JPanel wadah = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, isi.getPreferredSize().height);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, isi.getPreferredSize().height);
            }
        };
        wadah.setOpaque(false);
        wadah.add(isi, BorderLayout.CENTER);
        return wadah;
    }

    /**
     * Membungkus tabel dalam scroll pane tanpa garis tepi.
     *
     * @param table tabel
     * @return scroll pane
     */
    public static JScrollPane tableScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Theme.Colors.CARD);
        sp.setBackground(Theme.Colors.CARD);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        return sp;
    }

    /** Renderer header tabel. */
    private static final class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);
            l.setFont(Theme.FONT_TABLE_HEAD);
            l.setForeground(Theme.Colors.TEXT_MUTED);
            l.setBackground(Theme.Colors.TABLE_HEADER_BG);
            l.setBorder(new EmptyBorder(0, 14, 0, 14));
            l.setHorizontalAlignment(SwingConstants.LEFT);
            return l;
        }
    }

    /**
     * Renderer kolom status: teks diwarnai sesuai kondisi.
     *
     * @return renderer
     */
    public static DefaultTableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                    boolean focused, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                l.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
                l.setBorder(new EmptyBorder(0, 14, 0, 14));
                l.setBackground(selected ? Theme.Colors.PRIMARY_SOFT
                        : (row % 2 == 0 ? Theme.Colors.CARD : Theme.Colors.TABLE_ROW_ALT));
                l.setForeground(Widgets.warnaStatus(value == null ? "" : value.toString()));
                return l;
            }
        };
    }

    /**
     * Renderer kolom teks tebal (mis. nama utama).
     *
     * @return renderer
     */
    public static DefaultTableCellRenderer boldRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                    boolean focused, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                l.setFont(Theme.FONT_BOLD);
                l.setBorder(new EmptyBorder(0, 14, 0, 14));
                l.setBackground(selected ? Theme.Colors.PRIMARY_SOFT
                        : (row % 2 == 0 ? Theme.Colors.CARD : Theme.Colors.TABLE_ROW_ALT));
                l.setForeground(Theme.Colors.TEXT);
                return l;
            }
        };
    }

    /** Renderer isi tabel dengan baris selang-seling. */
    private static final class BodyRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column);
            l.setFont(Theme.FONT_BODY);
            l.setBorder(new EmptyBorder(0, 14, 0, 14));
            if (selected) {
                l.setBackground(Theme.Colors.PRIMARY_SOFT);
                l.setForeground(Theme.Colors.TEXT);
            } else {
                l.setBackground(row % 2 == 0 ? Theme.Colors.CARD : Theme.Colors.TABLE_ROW_ALT);
                l.setForeground(Theme.Colors.TEXT);
            }
            return l;
        }
    }

    /**
     * Menambahkan handler klik ganda pada tabel.
     *
     * @param table   tabel
     * @param handler aksi yang dijalankan
     */
    public static void onDoubleClick(JTable table, Runnable handler) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    handler.run();
                }
            }
        });
    }

    // ================================================================
    // Layout bantu
    // ================================================================

    /**
     * Panel dengan GridBagLayout dan padding tertentu.
     *
     * @param padding jarak dalam piksel
     * @return panel
     */
    public static JPanel gridPanel(int padding) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(padding, padding, padding, padding));
        return p;
    }

    /**
     * Membuat constraint GridBag standar.
     *
     * @param x      kolom
     * @param y      baris
     * @param width  lebar dalam sel
     * @param weight bobot horizontal
     * @return constraint
     */
    public static GridBagConstraints gbc(int x, int y, int width, double weight) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.weightx = weight;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 14, 14);
        return c;
    }

    /**
     * Panel baris dengan FlowLayout kiri.
     *
     * @return panel
     */
    public static JPanel rowLeft() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        return p;
    }

    /**
     * Panel baris dengan FlowLayout kanan.
     *
     * @return panel
     */
    public static JPanel rowRight() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);
        return p;
    }

    /**
     * Panel dengan BorderLayout transparan.
     *
     * @return panel
     */
    public static JPanel border() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        return p;
    }

    /**
     * Garis pemisah horizontal tipis.
     *
     * @return komponen garis
     */
    public static JComponent divider() {
        JPanel line = new JPanel();
        line.setBackground(Theme.Colors.BORDER);
        line.setPreferredSize(new Dimension(1, 1));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return line;
    }

    /**
     * Jarak vertikal.
     *
     * @param height tinggi dalam piksel
     * @return komponen jarak
     */
    public static Component vgap(int height) {
        return Box.createVerticalStrut(height);
    }

    /**
     * Jarak horizontal.
     *
     * @param width lebar dalam piksel
     * @return komponen jarak
     */
    public static Component hgap(int width) {
        return Box.createHorizontalStrut(width);
    }

    /**
     * Memaksa font tertentu pada komponen.
     *
     * @param component komponen
     * @param font      font baru
     * @param <T>       tipe komponen
     * @return komponen yang sama
     */
    public static <T extends JComponent> T font(T component, Font font) {
        component.setFont(font);
        return component;
    }
}
