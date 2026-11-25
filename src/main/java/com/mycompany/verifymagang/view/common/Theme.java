package com.mycompany.verifymagang.view.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Palet warna, font, dan ukuran yang dipakai seluruh antarmuka.
 *
 * <p>Semua halaman mengambil nilai dari sini sehingga tema konsisten. Ubah satu
 * nilai di kelas ini untuk mengubah tampilan seluruh aplikasi.</p>
 */
public final class Theme {

    private Theme() {
    }

    /** Ukuran radius sudut kartu. */
    public static final int CARD_RADIUS = 12;

    /** Tinggi baris tabel. */
    public static final int ROW_HEIGHT = 44;

    /** Tinggi header tabel. */
    public static final int TABLE_HEADER_HEIGHT = 42;

    /** Lebar sidebar. */
    public static final int SIDEBAR_WIDTH = 236;

    /** Tinggi topbar. */
    public static final int TOPBAR_HEIGHT = 72;

    /** Padding standar isi halaman. */
    public static final int PAGE_PADDING = 24;

    // ================================================================
    // Font
    // ================================================================

    private static final String FAMILY = pickFamily();

    public static final Font FONT_TITLE = new Font(FAMILY, Font.BOLD, 22);
    public static final Font FONT_SECTION = new Font(FAMILY, Font.BOLD, 16);
    public static final Font FONT_METRIC = new Font(FAMILY, Font.BOLD, 26);
    public static final Font FONT_BODY = new Font(FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font(FAMILY, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(FAMILY, Font.PLAIN, 12);
    public static final Font FONT_LABEL = new Font(FAMILY, Font.BOLD, 12);
    public static final Font FONT_TABLE_HEAD = new Font(FAMILY, Font.BOLD, 12);
    public static final Font FONT_BRAND = new Font(FAMILY, Font.BOLD, 18);
    public static final Font FONT_LOGIN_TITLE = new Font(FAMILY, Font.BOLD, 28);

    private static String pickFamily() {
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidate : new String[]{"Segoe UI", "Inter", "Roboto", "Noto Sans", "SansSerif"}) {
            if (available.contains(candidate)) {
                return candidate;
            }
        }
        return Font.SANS_SERIF;
    }

    // ================================================================
    // Warna
    // ================================================================

    /** Kelas warna tema. */
    public static final class Colors {

        private Colors() {
        }

        // Brand
        public static final Color NAVY = new Color(0x0F, 0x1E, 0x3D);
        public static final Color NAVY_DEEP = new Color(0x0A, 0x15, 0x2B);
        public static final Color NAVY_HOVER = new Color(0x1B, 0x2C, 0x4F);

        public static final Color PRIMARY = new Color(0x25, 0x63, 0xEB);
        public static final Color PRIMARY_HOVER = new Color(0x1D, 0x4E, 0xD8);
        public static final Color PRIMARY_PRESSED = new Color(0x1E, 0x40, 0xAF);
        public static final Color PRIMARY_SOFT = new Color(0xEF, 0xF4, 0xFF);

        // Latar
        public static final Color BACKGROUND = new Color(0xF5, 0xF7, 0xFA);
        public static final Color CARD = Color.WHITE;
        public static final Color SURFACE_HOVER = new Color(0xF1, 0xF5, 0xF9);
        public static final Color SURFACE_PRESSED = new Color(0xE6, 0xEC, 0xF4);
        public static final Color TABLE_HEADER_BG = new Color(0xF8, 0xFA, 0xFC);
        public static final Color TABLE_ROW_ALT = new Color(0xFC, 0xFD, 0xFE);

        // Teks
        public static final Color TEXT = new Color(0x0F, 0x1E, 0x3D);
        public static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);
        public static final Color TEXT_FAINT = new Color(0x94, 0xA3, 0xB8);
        public static final Color TEXT_ON_DARK = new Color(0xE8, 0xEE, 0xF7);
        public static final Color TEXT_ON_DARK_MUTED = new Color(0x94, 0xA8, 0xC6);

        // Garis
        public static final Color BORDER = new Color(0xE4, 0xEA, 0xF2);
        public static final Color BORDER_STRONG = new Color(0xCD, 0xD7, 0xE5);

        // Status
        public static final Color SUCCESS = new Color(0x15, 0x80, 0x3D);
        public static final Color SUCCESS_SOFT = new Color(0xEC, 0xFD, 0xF5);
        public static final Color WARNING = new Color(0xB4, 0x53, 0x09);
        public static final Color WARNING_SOFT = new Color(0xFF, 0xFB, 0xEB);
        public static final Color DANGER = new Color(0xB9, 0x1C, 0x1C);
        public static final Color DANGER_SOFT = new Color(0xFE, 0xF2, 0xF2);
        public static final Color INFO = new Color(0x0E, 0x74, 0x90);
        public static final Color INFO_SOFT = new Color(0xEC, 0xFE, 0xFF);
    }
}
