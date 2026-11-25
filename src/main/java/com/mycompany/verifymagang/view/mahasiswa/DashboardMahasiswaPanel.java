package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.LogbookHarian;
import com.mycompany.verifymagang.model.Mahasiswa;
import com.mycompany.verifymagang.model.PendaftaranMagang;
import com.mycompany.verifymagang.model.PenempatanMagang;
import com.mycompany.verifymagang.model.PenilaianMagang;
import com.mycompany.verifymagang.service.LogbookService;
import com.mycompany.verifymagang.service.MahasiswaService;
import com.mycompany.verifymagang.service.PendaftaranService;
import com.mycompany.verifymagang.service.PenempatanService;
import com.mycompany.verifymagang.service.PenilaianService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.Icons;
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
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Dashboard mahasiswa: status magang, mitra, dosen pembimbing, dan ringkasan
 * logbook serta nilai.
 */
public class DashboardMahasiswaPanel extends Page implements Refreshable {

    private final MahasiswaService mahasiswaService = new MahasiswaService();
    private final PendaftaranService pendaftaranService = new PendaftaranService();
    private final PenempatanService penempatanService = new PenempatanService();
    private final LogbookService logbookService = new LogbookService();
    private final PenilaianService penilaianService = new PenilaianService();

    private final Widgets.StatCard cardStatus = Widgets.statCard("Status Magang", "-",
            Icons.get(Icons.I.PENEMPATAN, 22, Theme.Colors.PRIMARY), Theme.Colors.PRIMARY);
    private final Widgets.StatCard cardLogbook = Widgets.statCard("Logbook Saya", "-",
            Icons.get(Icons.I.LOGBOOK, 22, Theme.Colors.INFO), Theme.Colors.INFO);
    private final Widgets.StatCard cardVerifikasi = Widgets.statCard("Terverifikasi", "-",
            Icons.get(Icons.I.CHECK, 22, Theme.Colors.SUCCESS), Theme.Colors.SUCCESS);
    private final Widgets.StatCard cardNilai = Widgets.statCard("Nilai Akhir", "-",
            Icons.get(Icons.I.PENILAIAN, 22, Theme.Colors.WARNING), Theme.Colors.WARNING);

    private final JLabel mitraLabel = Ui.bold("-");
    private final JLabel dosenLabel = Ui.bold("-");
    private final JLabel periodeLabel = Ui.bold("-");
    private final JLabel statusPendaftaranLabel = Widgets.badge("-");
    private final JPanel detailPanel = new JPanel();
    private final JLabel infoLabel = Ui.caption(" ");

    /** Membuat dashboard mahasiswa. */
    public DashboardMahasiswaPanel() {
        super("Dashboard", "Ringkasan aktivitas magang Anda");

        JButton refresh = Ui.secondaryButton("Muat Ulang",
                Icons.get(Icons.I.REFRESH, 15, Theme.Colors.TEXT));
        refresh.addActionListener(e -> onShow());
        addAction(refresh);

        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));
        grid.setPreferredSize(new Dimension(0, 108));
        grid.add(cardStatus);
        grid.add(cardLogbook);
        grid.add(cardVerifikasi);
        grid.add(cardNilai);
        root.add(grid);
        root.add(Box.createVerticalStrut(20));
        root.add(penuhLebar(buildPenempatanCard()));
        root.add(Box.createVerticalStrut(12));
        JPanel infoWrap = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        infoWrap.setOpaque(false);
        infoWrap.add(infoLabel);
        root.add(penuhLebar(infoWrap));
        root.add(Box.createVerticalGlue());

        setBody(Ui.scroll(root));
    }

    private JComponent penuhLebar(JComponent isi) {
        return Ui.penuhLebar(isi);
    }

    private JComponent buildPenempatanCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = Ui.sectionTitle("Informasi Penempatan");
        card.add(title, BorderLayout.NORTH);

        detailPanel.setOpaque(false);
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        detailPanel.add(row("Mitra / Instansi", mitraLabel));
        detailPanel.add(row("Dosen Pembimbing", dosenLabel));
        detailPanel.add(row("Periode", periodeLabel));
        detailPanel.add(row("Status Pendaftaran", statusPendaftaranLabel));

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14, 0, 0, 0));
        body.add(detailPanel, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JComponent row(String label, JComponent value) {
        JPanel r = new JPanel(new BorderLayout(16, 0));
        r.setOpaque(false);
        r.setBorder(new EmptyBorder(8, 0, 8, 0));
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel l = Ui.caption(label);
        l.setPreferredSize(new Dimension(180, 20));
        r.add(l, BorderLayout.WEST);

        JPanel holder = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        holder.setOpaque(false);
        holder.add(value);
        r.add(holder, BorderLayout.CENTER);
        return r;
    }

    @Override
    public void onShow() {
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            infoLabel.setText("Sesi mahasiswa tidak ditemukan. Silakan login ulang.");
            return;
        }
        Async.run(this, () -> muat(mahasiswaId), this::terapkan);
    }

    private Object[] muat(int mahasiswaId) throws Exception {
        Optional<Mahasiswa> profil = mahasiswaService.detail(mahasiswaId);
        List<PendaftaranMagang> pendaftaran = pendaftaranService.daftarByMahasiswa(mahasiswaId);
        Optional<PenempatanMagang> penempatan =
                penempatanService.penempatanAktifMahasiswa(mahasiswaId);
        List<LogbookHarian> logbook = logbookService.daftarByMahasiswa(mahasiswaId);
        List<PenilaianMagang> nilai = penilaianService.daftarByMahasiswa(mahasiswaId);
        return new Object[]{profil, pendaftaran, penempatan, logbook, nilai};
    }

    @SuppressWarnings("unchecked")
    private void terapkan(Object[] data) {
        Optional<Mahasiswa> profil = (Optional<Mahasiswa>) data[0];
        List<PendaftaranMagang> pendaftaran = (List<PendaftaranMagang>) data[1];
        Optional<PenempatanMagang> penempatan = (Optional<PenempatanMagang>) data[2];
        List<LogbookHarian> logbook = (List<LogbookHarian>) data[3];
        List<PenilaianMagang> nilai = (List<PenilaianMagang>) data[4];

        if (penempatan.isPresent()) {
            PenempatanMagang p = penempatan.get();
            cardStatus.setValue(p.getStatus());
            cardStatus.setHint("Mulai " + DateField.format(p.getTanggalMulai()));
            mitraLabel.setText(p.getNamaMitra());
            dosenLabel.setText(p.getNamaDosen());
            periodeLabel.setText(DateField.format(p.getTanggalMulai())
                    + " s/d " + DateField.format(p.getTanggalSelesai()));
        } else {
            cardStatus.setValue("Belum ada");
            cardStatus.setHint("Belum ditempatkan");
            mitraLabel.setText("-");
            dosenLabel.setText("-");
            periodeLabel.setText("-");
        }

        String statusTerakhir = pendaftaran.isEmpty()
                ? "BELUM MENDAFTAR"
                : pendaftaran.get(0).getStatus();
        statusPendaftaranLabel.setText(statusTerakhir);
        statusPendaftaranLabel.setForeground(Widgets.warnaStatus(statusTerakhir));

        long terverifikasi = logbook.stream()
                .filter(LogbookHarian::isDiverifikasi).count();
        cardLogbook.setValue(String.valueOf(logbook.size()));
        cardLogbook.setHint("Total catatan harian");
        cardVerifikasi.setValue(String.valueOf(terverifikasi));
        cardVerifikasi.setHint("Disetujui dosen");

        if (nilai.isEmpty()) {
            cardNilai.setValue("-");
            cardNilai.setHint("Belum dinilai");
        } else {
            BigDecimal akhir = nilai.get(0).getNilaiAkhir();
            cardNilai.setValue(akhir == null ? "-" : String.format("%.2f", akhir));
            cardNilai.setHint("Huruf mutu " + nilai.get(0).getHurufMutu());
        }

        String nama = profil.map(Mahasiswa::getNama)
                .orElse(SessionManager.getCurrentUser().getNamaLengkap());
        infoLabel.setText("NIM " + profil.map(Mahasiswa::getNim).orElse("-")
                + "  \u2022  " + nama
                + "  \u2022  " + profil.map(m -> m.getJurusan() == null ? "-" : m.getJurusan()).orElse("-"));
    }
}
