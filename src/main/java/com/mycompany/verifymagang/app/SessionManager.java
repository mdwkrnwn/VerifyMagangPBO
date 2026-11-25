package com.mycompany.verifymagang.app;

import com.mycompany.verifymagang.model.User;

/**
 * Menyimpan informasi user yang sedang login selama aplikasi berjalan.
 *
 * <p>Dipakai bersama oleh seluruh view sehingga view tidak perlu saling
 * mengoper objek user. Karena hanya ada satu user aktif per aplikasi desktop,
 * state ini bersifat statis.</p>
 */
public final class SessionManager {

    private static User currentUser;
    private static Integer mahasiswaId;
    private static Integer dosenId;

    private SessionManager() {
        // kelas utilitas
    }

    /**
     * Menyimpan sesi user yang baru login.
     *
     * @param user        user yang login
     * @param mahasiswaId id tabel mahasiswa ({@code null} bila admin)
     * @param dosenId     id tabel dosen ({@code null} bila tidak ada)
     */
    public static void login(User user, Integer mahasiswaId, Integer dosenId) {
        currentUser = user;
        SessionManager.mahasiswaId = mahasiswaId;
        SessionManager.dosenId = dosenId;
    }

    /** Menghapus sesi (logout). */
    public static void logout() {
        currentUser = null;
        mahasiswaId = null;
        dosenId = null;
    }

    /** @return {@code true} bila ada user yang sedang login. */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /** @return user yang sedang login, atau {@code null}. */
    public static User getCurrentUser() {
        return currentUser;
    }

    /** @return id user yang sedang login, atau {@code -1} bila belum login. */
    public static int getUserId() {
        return currentUser == null ? -1 : currentUser.getId();
    }

    /** @return id tabel mahasiswa, atau {@code null}. */
    public static Integer getMahasiswaId() {
        return mahasiswaId;
    }

    /** @return id tabel dosen, atau {@code null}. */
    public static Integer getDosenId() {
        return dosenId;
    }

    /** @return {@code true} bila user yang login adalah admin. */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /** @return {@code true} bila user yang login adalah mahasiswa. */
    public static boolean isMahasiswa() {
        return currentUser != null && currentUser.isMahasiswa();
    }
}
