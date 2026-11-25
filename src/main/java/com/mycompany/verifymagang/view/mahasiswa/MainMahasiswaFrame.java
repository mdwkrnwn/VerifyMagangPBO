package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.view.common.AppFrame;
import com.mycompany.verifymagang.view.common.Icons;

/**
 * Jendela utama mahasiswa.
 *
 * <p>Sidebar mahasiswa berisi Dashboard, Magang (daftar lowongan, detail,
 * pendaftaran saya), Logbook, Penilaian, dan Profil. Halaman detail lowongan
 * tidak diberi item sidebar sendiri; ia dibuka dari daftar lowongan.</p>
 */
public class MainMahasiswaFrame extends AppFrame {

    private final DetailLowonganPanel detailPanel = new DetailLowonganPanel();
    private final LowonganMahasiswaPanel lowonganPanel = new LowonganMahasiswaPanel();

    /** Membuat jendela utama mahasiswa. */
    public MainMahasiswaFrame() {
        super("VerifyMagang - Mahasiswa", "Portal Mahasiswa");

        addNavSection("MENU");
        addNavItem("dashboard", "Dashboard", Icons.get(Icons.I.DASHBOARD, 18, null));
        addPage("dashboard", "Dashboard", "Ringkasan", new DashboardMahasiswaPanel());

        addNavItem("lowongan", "Daftar Lowongan", Icons.get(Icons.I.LOWONGAN, 18, null));
        addPage("lowongan", "Daftar Lowongan", "Lowongan tersedia", lowonganPanel);

        addNavItem("pendaftaran", "Pendaftaran Saya", Icons.get(Icons.I.PENDAFTARAN, 18, null));
        addPage("pendaftaran", "Pendaftaran Saya", "Riwayat", new PendaftaranSayaPanel());

        addNavItem("logbook", "Logbook Saya", Icons.get(Icons.I.LOGBOOK, 18, null));
        addPage("logbook", "Logbook Saya", "Catatan harian", new LogbookMahasiswaPanel());

        addNavItem("penilaian", "Penilaian Saya", Icons.get(Icons.I.PENILAIAN, 18, null));
        addPage("penilaian", "Penilaian Saya", "Nilai magang", new PenilaianMahasiswaPanel());

        addNavSection("AKUN");
        addNavItem("profil", "Profil Saya", Icons.get(Icons.I.PROFIL, 18, null));
        addPage("profil", "Profil Saya", "Data diri", new ProfilMahasiswaPanel());

        // Halaman detail lowongan: tidak punya menu sendiri.
        addPage("detail-lowongan", "Detail Lowongan", "Detail", detailPanel);
        lowonganPanel.setOnOpenDetail(id -> {
            detailPanel.setLowonganId(id);
            navigate("detail-lowongan");
        });
        detailPanel.setOnKembali(() -> navigate("lowongan"));
        detailPanel.setOnDaftarSelesai(() -> navigate("pendaftaran"));

        startAt("dashboard");
    }
}
