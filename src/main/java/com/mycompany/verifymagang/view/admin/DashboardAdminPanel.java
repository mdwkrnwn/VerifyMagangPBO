package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.LogbookHarian;
import com.mycompany.verifymagang.model.PendaftaranMagang;
import com.mycompany.verifymagang.model.PenempatanMagang;
import com.mycompany.verifymagang.service.DosenService;
import com.mycompany.verifymagang.service.LogbookService;
import com.mycompany.verifymagang.service.LowonganService;
import com.mycompany.verifymagang.service.MahasiswaService;
import com.mycompany.verifymagang.service.PendaftaranService;
import com.mycompany.verifymagang.service.PenempatanService;
import com.mycompany.verifymagang.service.PenilaianService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.Page;
import com.mycompany.verifymagang.view.common.Refreshable;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;
import com.mycompany.verifymagang.view.common.Widgets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard admin: ringkasan statistik dan aktivitas terbaru.
 *
 * <p>Seluruh angka diambil dari database lewat service, bukan data statis.</p>
 */
public class DashboardAdminPanel extends Page implements Refreshable {

    private final MahasiswaService mahasiswaService = new MahasiswaService();
    private final DosenService dosenService = new DosenService();
    private final LowonganService lowonganService = new LowonganService();
    private final PendaftaranService pendaftaranService = new PendaftaranService();
    private final PenempatanService penempatanService = new PenempatanService();
    private final LogbookService logbookService = new LogbookService();
    private final PenilaianService penilaianService = new PenilaianService();

    private final Widgets.StatCard cardMahasiswa = Widgets.statCard("Total Mahasiswa", "-",
            Icons.get(Icons.I.MAHASISWA, 22, Theme.Colors.PRIMARY), Theme.Colors.PRIMARY);
    private final Widgets.StatCard cardDosen = Widgets.statCard("Total Dosen", "-",
            Icons.get(Icons.I.DOSEN, 22, Theme.Colors.INFO), Theme.Colors.INFO);
    private final Widgets.StatCard cardLowongan = Widgets.statCard("Lowongan Buka", "-",
            Icons.get(Icons.I.LOWONGAN, 22, Theme.Colors.SUCCESS), Theme.Colors.SUCCESS);
    private final Widgets.StatCard cardMenunggu = Widgets.statCard("Pendaftaran Menunggu", "-",
            Icons.get(Icons.I.PENDAFTARAN, 22, Theme.Colors.WARNING), Theme.Colors.WARNING);

    private final Widgets.StatCard cardPenempatan = Widgets.statCard("Penempatan Aktif", "-",
            Icons.get(Icons.I.PENEMPATAN, 22, Theme.Colors.PRIMARY), Theme.Colors.PRIMARY);
    private final Widgets.StatCard cardLogbook = Widgets.statCard("Logbook Menunggu", "-",
            Icons.get(Icons.I.LOGBOOK, 22, Theme.Colors.WARNING), Theme.Colors.WARNING);
    private final Widgets.StatCard cardNilai = Widgets.statCard("Rata-rata Nilai", "-",
            Icons.get(Icons.I.PENILAIAN, 22, Theme.Colors.SUCCESS), Theme.Colors.SUCCESS);
    private final Widgets.StatCard cardMitra = Widgets.statCard("Total Mitra", "-",
            Icons.get(Icons.I.MITRA, 22, Theme.Colors.INFO), Theme.Colors.INFO);

    private final DefaultTableModel aktivitasModel = Ui.tableModel(
            "Tanggal", "Mahasiswa", "Lowongan", "Status");
    private final JTable aktivitasTable = new JTable(aktivitasModel);
    private final JLabel updatedLabel = Ui.caption(" ");

    /** Membuat dashboard admin. */
    public DashboardAdminPanel() {
        super("Dashboard Admin",
                "Ringkasan data magang dan aktivitas terbaru");

        JButton refresh = Ui.secondaryButton("Muat Ulang",
                Icons.get(Icons.I.REFRESH, 15, Theme.Colors.TEXT));
        refresh.addActionListener(e -> onShow());
        addAction(refresh);

        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        root.add(buildStatGrid(new Component[]{cardMahasiswa, cardDosen, cardLowongan, cardMenunggu}));
        root.add(Box.createVerticalStrut(16));
        root.add(buildStatGrid(new Component[]{cardPenempatan, cardLogbook, cardNilai, cardMitra}));
        root.add(Box.createVerticalStrut(20));
        root.add(buildActivityCard());
        root.add(Box.createVerticalStrut(12));
        root.add(updatedLabel);
        root.add(Box.createVerticalGlue());

        JScrollPane scroll = Ui.scroll(root);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        setBody(scroll);
    }

    private JComponent buildStatGrid(Component[] cards) {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));
        grid.setPreferredSize(new Dimension(0, 108));
        for (Component c : cards) {
            grid.add(c);
        }
        return grid;
    }

    private JComponent buildActivityCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = Ui.sectionTitle("Pendaftaran Terbaru");
        JButton lihatSemua = Ui.ghostButton("Lihat semua",
                Icons.get(Icons.I.CHEVRON_DOWN, 14, Theme.Colors.PRIMARY));
        lihatSemua.setForeground(Theme.Colors.PRIMARY);
        lihatSemua.addActionListener(e -> {
            if (javax.swing.SwingUtilities.getWindowAncestor(this)
                    instanceof com.mycompany.verifymagang.view.common.AppFrame frame) {
                frame.navigate("pendaftaran");
            }
        });
        header.add(title, BorderLayout.WEST);
        header.add(lihatSemua, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        Ui.styleTable(aktivitasTable);
        aktivitasTable.setRowHeight(40);
        aktivitasTable.getColumnModel().getColumn(3).setCellRenderer(Ui.statusRenderer());
        JScrollPane sp = Ui.tableScroll(aktivitasTable);
        sp.setPreferredSize(new Dimension(0, 250));
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 0, 0, 0));
        body.add(sp, BorderLayout.CENTER);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void onShow() {
        Async.run(this, this::muatStatistik, this::terapkanStatistik);
    }

    private Object[] muatStatistik() throws Exception {
        int mahasiswa = mahasiswaService.daftarSemua().size();
        int dosen = dosenService.daftarSemua().size();
        int lowonganBuka = lowonganService.daftarLowonganBuka().size();
        int mitra = lowonganService.daftarMitra().size();

        List<PendaftaranMagang> semuaPendaftaran = pendaftaranService.daftarSemua();
        long menunggu = semuaPendaftaran.stream().filter(PendaftaranMagang::isMenunggu).count();

        List<PenempatanMagang> penempatan = penempatanService.daftarSemua();
        long aktif = penempatan.stream().filter(PenempatanMagang::isAktif).count();

        List<LogbookHarian> logbook = logbookService.daftarSemua();
        long logbookMenunggu = logbook.stream()
                .filter(l -> LogbookHarian.STATUS_MENUNGGU.equalsIgnoreCase(l.getStatusVerifikasi()))
                .count();

        BigDecimal rata = penilaianService.rataRataNilai();

        List<PendaftaranMagang> terbaru = semuaPendaftaran.stream()
                .sorted((a, b) -> {
                    if (a.getTanggalDaftar() == null) {
                        return 1;
                    }
                    if (b.getTanggalDaftar() == null) {
                        return -1;
                    }
                    return b.getTanggalDaftar().compareTo(a.getTanggalDaftar());
                })
                .limit(6)
                .toList();

        return new Object[]{mahasiswa, dosen, lowonganBuka, mitra, menunggu, aktif,
            logbookMenunggu, rata, terbaru};
    }

    @SuppressWarnings("unchecked")
    private void terapkanStatistik(Object[] data) {
        cardMahasiswa.setValue(String.valueOf(data[0]));
        cardDosen.setValue(String.valueOf(data[1]));
        cardLowongan.setValue(String.valueOf(data[2]));
        cardMitra.setValue(String.valueOf(data[3]));
        cardMenunggu.setValue(String.valueOf(data[4]));
        cardPenempatan.setValue(String.valueOf(data[5]));
        cardLogbook.setValue(String.valueOf(data[6]));

        BigDecimal rata = (BigDecimal) data[7];
        cardNilai.setValue(rata == null ? "-" : String.format("%.2f", rata));

        cardMahasiswa.setHint("Terdaftar di sistem");
        cardDosen.setHint("Pembimbing magang");
        cardLowongan.setHint("Siap didaftar mahasiswa");
        cardMitra.setHint("Mitra kerja sama");
        cardMenunggu.setHint("Perlu keputusan admin");
        cardPenempatan.setHint("Sedang berjalan");
        cardLogbook.setHint("Perlu verifikasi");
        cardNilai.setHint("Skala 0 - 100");

        aktivitasModel.setRowCount(0);
        List<PendaftaranMagang> terbaru = (List<PendaftaranMagang>) data[8];
        for (PendaftaranMagang p : terbaru) {
            aktivitasModel.addRow(new Object[]{
                DateField.format(p.getTanggalDaftar()),
                p.getNamaMahasiswa(),
                p.getJudulLowongan(),
                p.getStatus()});
        }

        updatedLabel.setText("Diperbarui oleh "
                + (SessionManager.isLoggedIn()
                        ? SessionManager.getCurrentUser().getNamaLengkap() : "-")
                + " pada " + java.time.LocalTime.now().withNano(0));
    }
}
