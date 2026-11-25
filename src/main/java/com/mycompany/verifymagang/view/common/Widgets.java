package com.mycompany.verifymagang.view.common;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Komponen kecil yang dipakai berulang di banyak halaman: kartu ringkasan,
 * label status, dan baris informasi.
 */
public final class Widgets {

    private Widgets() {
    }

    /**
     * Kartu ringkasan statistik untuk dashboard.
     *
     * @param judul     label metrik
     * @param nilai     nilai awal
     * @param ikon      ikon metrik
     * @param warnaAksen warna ikon dan aksen
     * @return kartu statistik
     */
    public static StatCard statCard(String judul, String nilai, Icon ikon, Color warnaAksen) {
        return new StatCard(judul, nilai, ikon, warnaAksen);
    }

    /** Kartu statistik dengan angka besar dan ikon berlatar lembut. */
    public static final class StatCard extends Ui.Card {

        private final JLabel valueLabel;
        private final JLabel hintLabel;

        StatCard(String judul, String nilai, Icon ikon, Color warnaAksen) {
            super(Theme.Colors.CARD, Theme.CARD_RADIUS, Theme.Colors.BORDER);
            setLayout(new BorderLayout(14, 0));
            setBorder(new EmptyBorder(18, 20, 18, 20));

            JPanel iconHolder = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(alpha(warnaAksen, 0.10f));
                        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                    } finally {
                        g2.dispose();
                    }
                }
            };
            iconHolder.setOpaque(false);
            iconHolder.setLayout(new BorderLayout());
            iconHolder.setPreferredSize(new Dimension(46, 46));
            JLabel iconLabel = new JLabel(ikon, SwingConstants.CENTER);
            iconHolder.add(iconLabel, BorderLayout.CENTER);

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));

            JLabel titleLabel = new JLabel(judul);
            titleLabel.setFont(Theme.FONT_LABEL);
            titleLabel.setForeground(Theme.Colors.TEXT_MUTED);
            titleLabel.setAlignmentX(LEFT_ALIGNMENT);

            valueLabel = new JLabel(nilai);
            valueLabel.setFont(Theme.FONT_METRIC);
            valueLabel.setForeground(Theme.Colors.TEXT);
            valueLabel.setAlignmentX(LEFT_ALIGNMENT);

            hintLabel = new JLabel(" ");
            hintLabel.setFont(Theme.FONT_SMALL);
            hintLabel.setForeground(Theme.Colors.TEXT_FAINT);
            hintLabel.setAlignmentX(LEFT_ALIGNMENT);

            textPanel.add(titleLabel);
            textPanel.add(javax.swing.Box.createVerticalStrut(4));
            textPanel.add(valueLabel);
            textPanel.add(hintLabel);

            add(iconHolder, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        /**
         * Memperbarui nilai yang ditampilkan.
         *
         * @param nilai nilai baru
         */
        public void setValue(String nilai) {
            valueLabel.setText(nilai);
        }

        /**
         * Memperbarui teks keterangan kecil.
         *
         * @param hint teks keterangan
         */
        public void setHint(String hint) {
            hintLabel.setText(hint == null || hint.isBlank() ? " " : hint);
        }
    }

    /**
     * Label status berwarna sesuai kondisi (mis. DISETUJUI, MENUNGGU).
     *
     * @param teks teks status
     * @return label status
     */
    public static JLabel badge(String teks) {
        JLabel l = new JLabel(teks, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(alpha(getForeground(), 0.12f));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        l.setOpaque(false);
        l.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        l.setBorder(new EmptyBorder(3, 10, 3, 10));
        l.setForeground(warnaStatus(teks));
        return l;
    }

    /**
     * Menentukan warna teks berdasarkan isi status.
     *
     * @param status teks status
     * @return warna yang sesuai
     */
    public static Color warnaStatus(String status) {
        String s = status == null ? "" : status.trim().toUpperCase();
        return switch (s) {
            case "DISETUJUI", "DIVERIFIKASI", "AKTIF", "SELESAI", "BUKA" -> Theme.Colors.SUCCESS;
            case "MENUNGGU", "REVISI" -> Theme.Colors.WARNING;
            case "DITOLAK", "BATAL", "TUTUP" -> Theme.Colors.DANGER;
            default -> Theme.Colors.TEXT_MUTED;
        };
    }

    /**
     * Baris informasi label–nilai.
     *
     * @param label teks label
     * @param nilai teks nilai
     * @return panel baris
     */
    public static JPanel infoRow(String label, String nilai) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.Colors.TEXT_MUTED);
        l.setPreferredSize(new Dimension(150, 20));

        JLabel v = new JLabel(nilai == null || nilai.isBlank() ? "-" : nilai);
        v.setFont(Theme.FONT_BOLD);
        v.setForeground(Theme.Colors.TEXT);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);
        return row;
    }

    /**
     * Lingkaran inisial untuk avatar sederhana.
     *
     * @param inisial teks inisial
     * @param ukuran  diameter piksel
     * @param warna   warna latar
     * @return panel avatar
     */
    public static JPanel avatar(String inisial, int ukuran, Color warna) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(warna);
                    g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setPreferredSize(new Dimension(ukuran, ukuran));
        p.setMaximumSize(new Dimension(ukuran, ukuran));

        JLabel l = new JLabel(inisial, SwingConstants.CENTER);
        l.setFont(Theme.FONT_BOLD);
        l.setForeground(Color.WHITE);
        l.setPreferredSize(new Dimension(ukuran, ukuran));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        p.add(l);
        return p;
    }

    /**
     * Membuat warna transparan dari warna dasar.
     *
     * @param base  warna dasar
     * @param alpha kanal alfa 0..1
     * @return warna baru
     */
    public static Color alpha(Color base, float alpha) {
        int a = Math.round(Math.max(0f, Math.min(1f, alpha)) * 255);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), a);
    }

    /**
     * Mengambil inisial dari nama (maksimal dua huruf).
     *
     * @param nama nama lengkap
     * @return inisial huruf besar
     */
    public static String inisial(String nama) {
        if (nama == null || nama.isBlank()) {
            return "?";
        }
        String[] parts = nama.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
