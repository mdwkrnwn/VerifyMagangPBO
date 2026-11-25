package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.PenilaianMagang;
import com.mycompany.verifymagang.service.PenilaianService;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.Page;
import com.mycompany.verifymagang.view.common.Refreshable;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;
import com.mycompany.verifymagang.view.common.Widgets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

/**
 * Halaman nilai magang milik mahasiswa (hanya baca).
 */
public class PenilaianMahasiswaPanel extends Page implements Refreshable {

    private final PenilaianService service = new PenilaianService();

    private final JLabel nilaiAkhirLabel = new JLabel("-");
    private final JLabel hurufLabel = new JLabel("-");
    private final JLabel nilaiMitraLabel = new JLabel("-");
    private final JLabel nilaiDosenLabel = new JLabel("-");
    private final JLabel tanggalLabel = new JLabel("-");
    private final JLabel catatanLabel = new JLabel("-");
    private final JPanel riwayatPanel = new JPanel();

    /** Membuat halaman penilaian mahasiswa. */
    public PenilaianMahasiswaPanel() {
        super("Penilaian Saya", "Hasil penilaian magang dari mitra dan dosen");

        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(penuhLebar(buildScoreCard()));
        root.add(Box.createVerticalStrut(16));
        root.add(penuhLebar(buildHistoryCard()));
        root.add(Box.createVerticalGlue());

        setBody(Ui.scroll(root));
    }

    /**
     * Membungkus kartu agar mengisi lebar penuh tanpa mengubah tingginya.
     */
    private JComponent penuhLebar(JComponent isi) {
        return Ui.penuhLebar(isi);
    }

    private JComponent buildScoreCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 26, 24, 26));

        JLabel title = Ui.sectionTitle("Nilai Akhir Magang");
        card.add(title, BorderLayout.NORTH);

        nilaiAkhirLabel.setFont(Theme.FONT_TITLE.deriveFont(java.awt.Font.BOLD, 44f));
        nilaiAkhirLabel.setForeground(Theme.Colors.PRIMARY);

        hurufLabel.setFont(Theme.FONT_TITLE.deriveFont(java.awt.Font.BOLD, 20f));
        hurufLabel.setForeground(Theme.Colors.TEXT_MUTED);

        JPanel scoreRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 16, 0));
        scoreRow.setOpaque(false);
        scoreRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        scoreRow.add(nilaiAkhirLabel);
        scoreRow.add(hurufLabel);

        JPanel detailGrid = new JPanel(new GridLayout(1, 3, 18, 0));
        detailGrid.setOpaque(false);
        detailGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        detailGrid.add(infoBlock("Nilai Mitra (40%)", nilaiMitraLabel));
        detailGrid.add(infoBlock("Nilai Dosen (60%)", nilaiDosenLabel));
        detailGrid.add(infoBlock("Tanggal Penilaian", tanggalLabel));

        JPanel catatanBlock = new JPanel();
        catatanBlock.setOpaque(false);
        catatanBlock.setLayout(new BoxLayout(catatanBlock, BoxLayout.Y_AXIS));
        catatanBlock.setAlignmentX(LEFT_ALIGNMENT);
        catatanBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        JLabel catatanCaption = Ui.caption("CATATAN PENILAIAN");
        catatanCaption.setAlignmentX(LEFT_ALIGNMENT);
        catatanLabel.setFont(Theme.FONT_BODY);
        catatanLabel.setForeground(Theme.Colors.TEXT);
        catatanLabel.setAlignmentX(LEFT_ALIGNMENT);
        catatanBlock.add(catatanCaption);
        catatanBlock.add(Box.createVerticalStrut(6));
        catatanBlock.add(catatanLabel);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(10, 0, 0, 0));
        body.add(scoreRow);
        body.add(Box.createVerticalStrut(18));
        body.add(detailGrid);
        body.add(Box.createVerticalStrut(16));
        body.add(catatanBlock);

        // BoxLayout Y_AXIS hanya meregangkan komponen yang alignment-nya kiri.
        for (java.awt.Component c : body.getComponents()) {
            if (c instanceof JComponent jc) {
                jc.setAlignmentX(LEFT_ALIGNMENT);
            }
        }
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JComponent infoBlock(String label, JLabel value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = Ui.caption(label);
        l.setAlignmentX(LEFT_ALIGNMENT);
        value.setFont(Theme.FONT_BOLD);
        value.setForeground(Theme.Colors.TEXT);
        value.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(6));
        p.add(value);
        return p;
    }

    private JComponent buildHistoryCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = Ui.sectionTitle("Riwayat Penilaian");
        card.add(title, BorderLayout.NORTH);

        riwayatPanel.setOpaque(false);
        riwayatPanel.setLayout(new BoxLayout(riwayatPanel, BoxLayout.Y_AXIS));

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 0, 0, 0));
        body.add(riwayatPanel, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void onShow() {
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            return;
        }
        com.mycompany.verifymagang.view.common.Async.run(this,
                () -> service.daftarByMahasiswa(mahasiswaId),
                this::terapkan);
    }

    private void terapkan(List<PenilaianMagang> data) {
        riwayatPanel.removeAll();

        if (data.isEmpty()) {
            nilaiAkhirLabel.setText("-");
            hurufLabel.setText("Belum dinilai");
            nilaiMitraLabel.setText("-");
            nilaiDosenLabel.setText("-");
            tanggalLabel.setText("-");
            catatanLabel.setText("-");

            JLabel kosong = Ui.caption("Belum ada penilaian. "
                    + "Nilai akan muncul setelah dosen dan mitra menilai magang Anda.");
            kosong.setBorder(new EmptyBorder(10, 0, 10, 0));
            riwayatPanel.add(kosong);
        } else {
            PenilaianMagang terbaru = data.get(0);
            nilaiAkhirLabel.setText(angka(terbaru.getNilaiAkhir()));
            hurufLabel.setText("Huruf Mutu " + terbaru.getHurufMutu());
            nilaiMitraLabel.setText(angka(terbaru.getNilaiMitra()));
            nilaiDosenLabel.setText(angka(terbaru.getNilaiDosen()));
            tanggalLabel.setText(DateField.format(terbaru.getTanggalPenilaian()));
            catatanLabel.setText(terbaru.getCatatan() == null || terbaru.getCatatan().isBlank()
                    ? "-" : terbaru.getCatatan());

            for (PenilaianMagang n : data) {
                riwayatPanel.add(riwayatRow(n));
            }
        }

        riwayatPanel.revalidate();
        riwayatPanel.repaint();
    }

    private JComponent riwayatRow(PenilaianMagang n) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel tanggal = Ui.text(DateField.format(n.getTanggalPenilaian()));
        tanggal.setPreferredSize(new Dimension(120, 20));
        tanggal.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        JLabel nilai = Ui.bold("Nilai akhir " + angka(n.getNilaiAkhir())
                + "  \u2022  " + n.getHurufMutu());

        JTextArea catatan = new JTextArea(n.getCatatan() == null || n.getCatatan().isBlank()
                ? "-" : n.getCatatan());
        catatan.setEditable(false);
        catatan.setOpaque(false);
        catatan.setLineWrap(true);
        catatan.setWrapStyleWord(true);
        catatan.setFont(Theme.FONT_SMALL);
        catatan.setForeground(Theme.Colors.TEXT_MUTED);
        catatan.setBorder(null);
        catatan.setAlignmentX(LEFT_ALIGNMENT);

        JPanel tengah = new JPanel();
        tengah.setOpaque(false);
        tengah.setLayout(new BoxLayout(tengah, BoxLayout.Y_AXIS));
        nilai.setAlignmentX(LEFT_ALIGNMENT);
        tengah.add(nilai);
        tengah.add(Box.createVerticalStrut(2));
        tengah.add(catatan);

        row.add(tanggal, BorderLayout.WEST);
        row.add(tengah, BorderLayout.CENTER);
        return row;
    }

    private static String angka(BigDecimal nilai) {
        return nilai == null ? "-" : String.format("%.2f", nilai);
    }
}
