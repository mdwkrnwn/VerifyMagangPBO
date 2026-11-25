package com.mycompany.verifymagang.view.common;

/**
 * Kontrak untuk halaman yang perlu memuat ulang data setiap kali ditampilkan.
 *
 * <p>Dengan kontrak ini, halaman tidak perlu tahu kapan dirinya aktif; frame
 * utama yang memanggil {@link #onShow()} saat navigasi.</p>
 */
public interface Refreshable {

    /** Dipanggil setiap kali halaman ditampilkan. */
    void onShow();
}
