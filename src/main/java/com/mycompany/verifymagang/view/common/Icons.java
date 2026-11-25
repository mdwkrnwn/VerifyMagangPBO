package com.mycompany.verifymagang.view.common;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Ikon vektor yang digambar langsung dengan Java2D.
 *
 * <p>Tidak memerlukan berkas gambar sehingga aplikasi tetap ringan dan ikon
 * selalu tajam pada semua skala tampilan.</p>
 */
public final class Icons {

    private Icons() {
    }

    /** Kumpulan ikon yang dipakai antarmuka. */
    public enum I {
        DASHBOARD, MAHASISWA, DOSEN, MITRA, LOWONGAN, PERIODE, PENDAFTARAN,
        PENEMPATAN, LOGBOOK, PENILAIAN, PROFIL, LOGOUT, SEARCH, PLUS, EDIT,
        DELETE, CHECK, CLOSE, REFRESH, EYE, INFO, WARNING, CHEVRON_DOWN,
        SEND, BELL, LOCK, USER, CLOCK, FILTER, DOWNLOAD, MENU
    }

    /**
     * Membuat ikon.
     *
     * @param name  nama ikon
     * @param size  ukuran piksel persegi
     * @param color warna garis
     * @return ikon siap dipakai
     */
    public static Icon get(I name, int size, Color color) {
        return new VectorIcon(name, size, color, 1.8f);
    }

    /**
     * Membuat ikon dengan ketebalan garis tertentu.
     *
     * @param name   nama ikon
     * @param size   ukuran piksel
     * @param color  warna
     * @param weight ketebalan garis
     * @return ikon
     */
    public static Icon get(I name, int size, Color color, float weight) {
        return new VectorIcon(name, size, color, weight);
    }

    /** Implementasi ikon berbasis jalur vektor pada kanvas 24x24. */
    private static final class VectorIcon implements Icon {

        private final I name;
        private final int size;
        private final Color color;
        private final float weight;

        VectorIcon(I name, int size, Color color, float weight) {
            this.name = name;
            this.size = size;
            this.color = color;
            this.weight = weight;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.translate(x, y);
                double scale = size / 24.0;
                g2.scale(scale, scale);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                draw(g2, name);
            } finally {
                g2.dispose();
            }
        }
    }

    private static void draw(Graphics2D g, I name) {
        switch (name) {
            case DASHBOARD -> {
                g.draw(new RoundRectangle2D.Double(3, 3, 7.5, 7.5, 2, 2));
                g.draw(new RoundRectangle2D.Double(13.5, 3, 7.5, 7.5, 2, 2));
                g.draw(new RoundRectangle2D.Double(3, 13.5, 7.5, 7.5, 2, 2));
                g.draw(new RoundRectangle2D.Double(13.5, 13.5, 7.5, 7.5, 2, 2));
            }
            case MAHASISWA -> {
                g.draw(new Ellipse2D.Double(8.5, 3, 7, 7));
                Path2D body = new Path2D.Double();
                body.moveTo(4.5, 20.5);
                body.curveTo(4.5, 16.5, 8, 14, 12, 14);
                body.curveTo(16, 14, 19.5, 16.5, 19.5, 20.5);
                g.draw(body);
            }
            case DOSEN -> {
                Path2D cap = new Path2D.Double();
                cap.moveTo(2.5, 8.5);
                cap.lineTo(12, 4);
                cap.lineTo(21.5, 8.5);
                cap.lineTo(12, 13);
                cap.closePath();
                g.draw(cap);
                Path2D base = new Path2D.Double();
                base.moveTo(6.5, 10.5);
                base.lineTo(6.5, 15.5);
                base.curveTo(6.5, 17.3, 9, 18.8, 12, 18.8);
                base.curveTo(15, 18.8, 17.5, 17.3, 17.5, 15.5);
                base.lineTo(17.5, 10.5);
                g.draw(base);
            }
            case MITRA -> {
                g.draw(new RoundRectangle2D.Double(3.5, 4, 10, 16.5, 1.8, 1.8));
                g.draw(new RoundRectangle2D.Double(13.5, 9, 7, 11.5, 1.8, 1.8));
                ln(g, 6.5, 8, 6.5, 8);
                ln(g, 10.5, 8, 10.5, 8);
                ln(g, 6.5, 12, 6.5, 12);
                ln(g, 10.5, 12, 10.5, 12);
                ln(g, 17, 13, 17, 13);
            }
            case LOWONGAN -> {
                g.draw(new RoundRectangle2D.Double(2.8, 7, 18.4, 13.2, 2.4, 2.4));
                Path2D handle = new Path2D.Double();
                handle.moveTo(9, 7);
                handle.lineTo(9, 5.4);
                handle.curveTo(9, 4.6, 9.6, 4, 10.4, 4);
                handle.lineTo(13.6, 4);
                handle.curveTo(14.4, 4, 15, 4.6, 15, 5.4);
                handle.lineTo(15, 7);
                g.draw(handle);
                ln(g, 2.8, 12.5, 21.2, 12.5);
            }
            case PERIODE -> {
                g.draw(new RoundRectangle2D.Double(3.2, 5, 17.6, 16, 2.2, 2.2));
                ln(g, 3.2, 9.8, 20.8, 9.8);
                ln(g, 8, 3, 8, 6.2);
                ln(g, 16, 3, 16, 6.2);
                ln(g, 7.5, 13.5, 10.5, 13.5);
                ln(g, 13.5, 13.5, 16.5, 13.5);
                ln(g, 7.5, 17.5, 10.5, 17.5);
            }
            case PENDAFTARAN -> {
                g.draw(new RoundRectangle2D.Double(4.5, 4, 15, 17, 2.2, 2.2));
                g.draw(new RoundRectangle2D.Double(9, 2.2, 6, 3.6, 1.2, 1.2));
                Path2D tick = new Path2D.Double();
                tick.moveTo(8.5, 12);
                tick.lineTo(11, 14.5);
                tick.lineTo(15.5, 9.8);
                g.draw(tick);
                ln(g, 8.5, 18, 15.5, 18);
            }
            case PENEMPATAN -> {
                Path2D pin = new Path2D.Double();
                pin.moveTo(12, 21);
                pin.curveTo(12, 21, 19, 14.6, 19, 10);
                pin.curveTo(19, 6.1, 15.9, 3, 12, 3);
                pin.curveTo(8.1, 3, 5, 6.1, 5, 10);
                pin.curveTo(5, 14.6, 12, 21, 12, 21);
                pin.closePath();
                g.draw(pin);
                g.draw(new Ellipse2D.Double(9.6, 7.6, 4.8, 4.8));
            }
            case LOGBOOK -> {
                Path2D book = new Path2D.Double();
                book.moveTo(3.5, 4.5);
                book.lineTo(9.5, 4.5);
                book.curveTo(10.6, 4.5, 11.5, 5.4, 11.5, 6.5);
                book.lineTo(11.5, 20);
                book.curveTo(10.6, 19.2, 9.6, 18.8, 8.5, 18.8);
                book.lineTo(3.5, 18.8);
                book.closePath();
                g.draw(book);
                Path2D book2 = new Path2D.Double();
                book2.moveTo(20.5, 4.5);
                book2.lineTo(14.5, 4.5);
                book2.curveTo(13.4, 4.5, 12.5, 5.4, 12.5, 6.5);
                book2.lineTo(12.5, 20);
                book2.curveTo(13.4, 19.2, 14.4, 18.8, 15.5, 18.8);
                book2.lineTo(20.5, 18.8);
                book2.closePath();
                g.draw(book2);
            }
            case PENILAIAN -> {
                Path2D star = new Path2D.Double();
                star.moveTo(12, 2.8);
                star.lineTo(14.9, 9);
                star.lineTo(21.4, 9.8);
                star.lineTo(16.6, 14.2);
                star.lineTo(17.9, 20.7);
                star.lineTo(12, 17.5);
                star.lineTo(6.1, 20.7);
                star.lineTo(7.4, 14.2);
                star.lineTo(2.6, 9.8);
                star.lineTo(9.1, 9);
                star.closePath();
                g.draw(star);
            }
            case PROFIL, USER -> {
                g.draw(new Ellipse2D.Double(8.6, 3.2, 6.8, 6.8));
                Path2D shoulders = new Path2D.Double();
                shoulders.moveTo(4.4, 20.6);
                shoulders.curveTo(4.4, 16.4, 7.9, 13.9, 12, 13.9);
                shoulders.curveTo(16.1, 13.9, 19.6, 16.4, 19.6, 20.6);
                g.draw(shoulders);
            }
            case LOGOUT -> {
                Path2D door = new Path2D.Double();
                door.moveTo(14, 3.5);
                door.lineTo(6.5, 3.5);
                door.curveTo(5.4, 3.5, 4.5, 4.4, 4.5, 5.5);
                door.lineTo(4.5, 18.5);
                door.curveTo(4.5, 19.6, 5.4, 20.5, 6.5, 20.5);
                door.lineTo(14, 20.5);
                g.draw(door);
                ln(g, 10, 12, 20.5, 12);
                Path2D arrow = new Path2D.Double();
                arrow.moveTo(17, 8.5);
                arrow.lineTo(20.5, 12);
                arrow.lineTo(17, 15.5);
                g.draw(arrow);
            }
            case SEARCH -> {
                g.draw(new Ellipse2D.Double(4, 4, 12.5, 12.5));
                ln(g, 15.4, 15.4, 20.5, 20.5);
            }
            case PLUS -> {
                ln(g, 12, 5, 12, 19);
                ln(g, 5, 12, 19, 12);
            }
            case EDIT -> {
                Path2D pencil = new Path2D.Double();
                pencil.moveTo(4, 20);
                pencil.lineTo(4.8, 15.6);
                pencil.lineTo(16.2, 4.2);
                pencil.curveTo(16.9, 3.5, 18, 3.5, 18.7, 4.2);
                pencil.lineTo(19.8, 5.3);
                pencil.curveTo(20.5, 6, 20.5, 7.1, 19.8, 7.8);
                pencil.lineTo(8.4, 19.2);
                pencil.closePath();
                g.draw(pencil);
            }
            case DELETE -> {
                ln(g, 4, 6.5, 20, 6.5);
                Path2D can = new Path2D.Double();
                can.moveTo(6.5, 6.5);
                can.lineTo(7.4, 19.6);
                can.curveTo(7.5, 20.4, 8.1, 21, 8.9, 21);
                can.lineTo(15.1, 21);
                can.curveTo(15.9, 21, 16.5, 20.4, 16.6, 19.6);
                can.lineTo(17.5, 6.5);
                g.draw(can);
                ln(g, 9.5, 6.5, 9.5, 3.6);
                ln(g, 14.5, 6.5, 14.5, 3.6);
                ln(g, 9.5, 3.6, 14.5, 3.6);
                ln(g, 10.5, 10.5, 10.7, 17);
                ln(g, 13.5, 10.5, 13.3, 17);
            }
            case CHECK -> {
                Path2D tick = new Path2D.Double();
                tick.moveTo(4.5, 12.5);
                tick.lineTo(9.5, 17.5);
                tick.lineTo(19.5, 6.5);
                g.draw(tick);
            }
            case CLOSE -> {
                ln(g, 6, 6, 18, 18);
                ln(g, 18, 6, 6, 18);
            }
            case REFRESH -> {
                g.draw(new Arc2D.Double(4, 4, 16, 16, 60, 270, Arc2D.OPEN));
                Path2D arrow = new Path2D.Double();
                arrow.moveTo(17.5, 2.5);
                arrow.lineTo(17.5, 8);
                arrow.lineTo(12, 8);
                g.draw(arrow);
            }
            case EYE -> {
                Path2D eye = new Path2D.Double();
                eye.moveTo(2.5, 12);
                eye.curveTo(5.5, 6.8, 8.6, 5.2, 12, 5.2);
                eye.curveTo(15.4, 5.2, 18.5, 6.8, 21.5, 12);
                eye.curveTo(18.5, 17.2, 15.4, 18.8, 12, 18.8);
                eye.curveTo(8.6, 18.8, 5.5, 17.2, 2.5, 12);
                eye.closePath();
                g.draw(eye);
                g.draw(new Ellipse2D.Double(9.4, 9.4, 5.2, 5.2));
            }
            case INFO -> {
                g.draw(new Ellipse2D.Double(3.2, 3.2, 17.6, 17.6));
                ln(g, 12, 10.8, 12, 16.6);
                ln(g, 12, 7.6, 12, 7.6);
            }
            case WARNING -> {
                Path2D tri = new Path2D.Double();
                tri.moveTo(12, 3.2);
                tri.lineTo(21.4, 19.6);
                tri.lineTo(2.6, 19.6);
                tri.closePath();
                g.draw(tri);
                ln(g, 12, 9, 12, 14);
                ln(g, 12, 16.8, 12, 16.8);
            }
            case CHEVRON_DOWN -> {
                Path2D chev = new Path2D.Double();
                chev.moveTo(6, 9.5);
                chev.lineTo(12, 15.5);
                chev.lineTo(18, 9.5);
                g.draw(chev);
            }
            case SEND -> {
                Path2D plane = new Path2D.Double();
                plane.moveTo(21, 3);
                plane.lineTo(10.5, 13.5);
                plane.moveTo(21, 3);
                plane.lineTo(14.2, 21);
                plane.lineTo(10.5, 13.5);
                plane.lineTo(3, 9.8);
                plane.closePath();
                g.draw(plane);
            }
            case BELL -> {
                Path2D bell = new Path2D.Double();
                bell.moveTo(6, 17);
                bell.lineTo(6, 10.2);
                bell.curveTo(6, 6.9, 8.7, 4.2, 12, 4.2);
                bell.curveTo(15.3, 4.2, 18, 6.9, 18, 10.2);
                bell.lineTo(18, 17);
                g.draw(bell);
                ln(g, 4.2, 17, 19.8, 17);
                Path2D clapper = new Path2D.Double();
                clapper.moveTo(10, 19.5);
                clapper.curveTo(10.4, 20.7, 11.1, 21.3, 12, 21.3);
                clapper.curveTo(12.9, 21.3, 13.6, 20.7, 14, 19.5);
                g.draw(clapper);
            }
            case LOCK -> {
                g.draw(new RoundRectangle2D.Double(4.5, 10.5, 15, 10, 2.2, 2.2));
                Path2D shackle = new Path2D.Double();
                shackle.moveTo(8, 10.5);
                shackle.lineTo(8, 7.5);
                shackle.curveTo(8, 5.3, 9.8, 3.5, 12, 3.5);
                shackle.curveTo(14.2, 3.5, 16, 5.3, 16, 7.5);
                shackle.lineTo(16, 10.5);
                g.draw(shackle);
            }
            case CLOCK -> {
                g.draw(new Ellipse2D.Double(3.2, 3.2, 17.6, 17.6));
                ln(g, 12, 7.5, 12, 12.4);
                ln(g, 12, 12.4, 15.6, 14.6);
            }
            case FILTER -> {
                Path2D funnel = new Path2D.Double();
                funnel.moveTo(3.5, 5);
                funnel.lineTo(20.5, 5);
                funnel.lineTo(14, 12.4);
                funnel.lineTo(14, 19.5);
                funnel.lineTo(10, 17.2);
                funnel.lineTo(10, 12.4);
                funnel.closePath();
                g.draw(funnel);
            }
            case DOWNLOAD -> {
                ln(g, 12, 3.5, 12, 15);
                Path2D arrow = new Path2D.Double();
                arrow.moveTo(7.5, 10.5);
                arrow.lineTo(12, 15);
                arrow.lineTo(16.5, 10.5);
                g.draw(arrow);
                ln(g, 4.5, 20, 19.5, 20);
            }
            case MENU -> {
                ln(g, 4, 7, 20, 7);
                ln(g, 4, 12, 20, 12);
                ln(g, 4, 17, 20, 17);
            }
        }
    }

    /**
     * Menggambar garis lurus dengan koordinat pecahan.
     *
     * @param g graphics tujuan
     * @param x1 titik awal x
     * @param y1 titik awal y
     * @param x2 titik akhir x
     * @param y2 titik akhir y
     */
    private static void ln(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new java.awt.geom.Line2D.Double(x1, y1, x2, y2));
    }
}
