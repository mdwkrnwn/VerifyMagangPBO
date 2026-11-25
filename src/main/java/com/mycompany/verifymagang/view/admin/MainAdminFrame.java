package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.view.common.AppFrame;
import com.mycompany.verifymagang.view.common.Icons;

/**
 * Jendela utama admin.
 *
 * <p>Menyusun sidebar (Dashboard, Data Master, Proses Magang) dan mendaftarkan
 * seluruh halaman admin. Navigasi memakai {@code CardLayout} di {@link AppFrame}.</p>
 */
public class MainAdminFrame extends AppFrame {

    /** Membuat jendela utama admin. */
    public MainAdminFrame() {
        super("VerifyMagang - Admin", "Panel Administrator");

        addNavSection("RINGKASAN");
        addNavItem("dashboard", "Dashboard", Icons.get(Icons.I.DASHBOARD, 18, null));
        addPage("dashboard", "Dashboard", "Ringkasan data",
                new DashboardAdminPanel());

        addNavSection("DATA MASTER");
        addNavItem("mahasiswa", "Mahasiswa", Icons.get(Icons.I.MAHASISWA, 18, null));
        addPage("mahasiswa", "Data Mahasiswa", "Kelola mahasiswa", new MahasiswaPanel());

        addNavItem("dosen", "Dosen", Icons.get(Icons.I.DOSEN, 18, null));
        addPage("dosen", "Data Dosen", "Kelola dosen", new DosenPanel());

        addNavItem("mitra", "Mitra", Icons.get(Icons.I.MITRA, 18, null));
        addPage("mitra", "Data Mitra", "Kelola mitra", new MitraPanel());

        addNavItem("periode", "Periode Magang", Icons.get(Icons.I.PERIODE, 18, null));
        addPage("periode", "Periode Magang", "Kelola periode", new PeriodePanel());

        addNavItem("lowongan", "Lowongan", Icons.get(Icons.I.LOWONGAN, 18, null));
        addPage("lowongan", "Data Lowongan", "Kelola lowongan", new LowonganPanel());

        addNavSection("PROSES MAGANG");
        addNavItem("pendaftaran", "Pendaftaran", Icons.get(Icons.I.PENDAFTARAN, 18, null));
        addPage("pendaftaran", "Pendaftaran Magang", "Setujui pendaftaran",
                new PendaftaranPanel());

        addNavItem("penempatan", "Penempatan", Icons.get(Icons.I.PENEMPATAN, 18, null));
        addPage("penempatan", "Penempatan Magang", "Tempatkan mahasiswa",
                new PenempatanPanel());

        addNavItem("logbook", "Logbook", Icons.get(Icons.I.LOGBOOK, 18, null));
        addPage("logbook", "Logbook Magang", "Verifikasi logbook", new LogbookPanel());

        addNavItem("penilaian", "Penilaian", Icons.get(Icons.I.PENILAIAN, 18, null));
        addPage("penilaian", "Penilaian Magang", "Kelola nilai", new PenilaianPanel());

        startAt("dashboard");
    }
}
