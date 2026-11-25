package com.mycompany.verifymagang.view.common;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Kerangka dasar satu halaman.
 *
 * <p>Menyediakan judul, subjudul, area aksi di kanan atas, dan area isi. Semua
 * halaman admin maupun mahasiswa memakai kerangka ini agar jarak, tipografi,
 * dan posisi tombol selalu sama.</p>
 */
public class Page extends JPanel {

    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JPanel actionBar;
    private final JPanel body;
    private final JPanel headerText;

    /**
     * Membuat halaman.
     *
     * @param judul    judul halaman
     * @param subjudul keterangan singkat di bawah judul
     */
    public Page(String judul, String subjudul) {
        setLayout(new BorderLayout());
        setBackground(Theme.Colors.BACKGROUND);
        setBorder(new EmptyBorder(Theme.PAGE_PADDING, Theme.PAGE_PADDING,
                Theme.PAGE_PADDING, Theme.PAGE_PADDING));

        headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(judul);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.Colors.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLabel = new JLabel(subjudul == null ? "" : subjudul);
        subtitleLabel.setFont(Theme.FONT_SMALL);
        subtitleLabel.setForeground(Theme.Colors.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerText.add(titleLabel);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(subtitleLabel);

        actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBar.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));
        header.add(headerText, BorderLayout.WEST);
        header.add(actionBar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        add(body, BorderLayout.CENTER);
    }

    /**
     * Menetapkan area isi halaman.
     *
     * @param component komponen isi
     */
    public void setBody(JComponent component) {
        body.removeAll();
        body.add(component, BorderLayout.CENTER);
        body.revalidate();
        body.repaint();
    }

    /**
     * Menambahkan tombol/aksi di kanan atas judul.
     *
     * @param components komponen aksi
     */
    public void addAction(Component... components) {
        for (Component c : components) {
            actionBar.add(c);
        }
        actionBar.revalidate();
    }

    /**
     * Mengosongkan seluruh area aksi.
     */
    public void clearActions() {
        actionBar.removeAll();
        actionBar.revalidate();
        actionBar.repaint();
    }

    /**
     * Mengubah judul halaman saat runtime.
     *
     * @param judul    judul baru
     * @param subjudul subjudul baru
     */
    public void setHeading(String judul, String subjudul) {
        titleLabel.setText(judul);
        subtitleLabel.setText(subjudul == null ? "" : subjudul);
    }

    /**
     * Menambahkan label kecil di atas judul (mis. breadcrumb).
     *
     * @param teks teks breadcrumb
     */
    public void setBreadcrumb(String teks) {
        JLabel crumb = new JLabel(teks);
        crumb.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        crumb.setForeground(Theme.Colors.PRIMARY);
        crumb.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerText.add(crumb, 0);
        headerText.add(Box.createVerticalStrut(6), 1);
        headerText.revalidate();
    }

    /**
     * Membuat kartu putih berisi tabel yang sudah digulir.
     *
     * @param table tabel
     * @param extra komponen tambahan di atas tabel (boleh {@code null})
     * @return panel kartu
     */
    public static JPanel tableCard(javax.swing.JTable table, JComponent extra) {
        JPanel card = Ui.card();
        card.setLayout(new BorderLayout());
        if (extra != null) {
            card.add(extra, BorderLayout.NORTH);
        }
        card.add(Ui.tableScroll(table), BorderLayout.CENTER);
        return card;
    }

    /**
     * Membuat baris toolbar putih (pencarian dan tombol) di atas tabel.
     *
     * @param kiri  komponen sisi kiri
     * @param kanan komponen sisi kanan
     * @return panel toolbar
     */
    public static JPanel toolbar(Component kiri, Component kanan) {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(Theme.Colors.CARD);
        bar.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        if (kiri != null) {
            left.add(kiri);
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        if (kanan != null) {
            right.add(kanan);
        }

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /**
     * Kolom pencarian dengan ikon.
     *
     * @param lebar lebar kolom
     * @return kolom pencarian
     */
    public static javax.swing.JTextField searchField(int lebar) {
        javax.swing.JTextField f = Ui.textField(1);
        f.setPreferredSize(new Dimension(lebar, 36));
        f.setMinimumSize(new Dimension(120, 36));
        f.putClientProperty("JTextField.placeholderText", "Cari data...");
        f.setToolTipText("Cari data");
        return f;
    }
}
