package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.LowonganMagang;
import com.mycompany.verifymagang.service.LowonganService;
import com.mycompany.verifymagang.service.PendaftaranService;
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
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Halaman detail satu lowongan.
 *
 * <p>Dibuka dari halaman daftar lowongan. Menampilkan seluruh informasi
 * lowongan dan menyediakan tombol untuk mengajukan pendaftaran.</p>
 */
public class DetailLowonganPanel extends Page implements Refreshable {

    private final LowonganService lowonganService = new LowonganService();
    private final PendaftaranService pendaftaranService = new PendaftaranService();

    private final JLabel judulLabel = Ui.sectionTitle("-");
    private final JLabel mitraLabel = Ui.bold("-");
    private final JLabel lokasiLabel = Ui.bold("-");
    private final JLabel kuotaLabel = Ui.bold("-");
    private final JLabel tunjanganLabel = Ui.bold("-");
    private final JLabel periodeLabel = Ui.bold("-");
    private final JLabel statusLabel = Widgets.badge("-");
    private final JTextArea deskripsiArea = new JTextArea();
    private final JTextArea syaratArea = new JTextArea();
    private final JButton daftarButton;
    private final JButton kembaliButton;

    private int lowonganId;
    private Runnable onKembali;
    private Runnable onDaftarSelesai;

    /** Membuat halaman detail lowongan. */
    public DetailLowonganPanel() {
        super("Detail Lowongan", "Informasi lengkap lowongan magang");

        kembaliButton = Ui.secondaryButton("Kembali",
                Icons.get(Icons.I.CLOSE, 15, Theme.Colors.TEXT));
        kembaliButton.addActionListener(e -> {
            if (onKembali != null) {
                onKembali.run();
            }
        });

        daftarButton = Ui.primaryButton("Daftar Sekarang",
                Icons.get(Icons.I.SEND, 16, java.awt.Color.WHITE));
        daftarButton.addActionListener(e -> ajukan());

        addAction(kembaliButton, daftarButton);

        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        JComponent header = Ui.penuhLebar(buildHeaderCard());
        JComponent detail = Ui.penuhLebar(buildDetailCard());
        root.add(header);
        root.add(Box.createVerticalStrut(16));
        root.add(detail);
        root.add(Box.createVerticalGlue());

        setBody(Ui.scroll(root));
        kosongkan();
    }

    private JComponent buildHeaderCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        judulLabel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel mitraCaption = Ui.caption("Mitra penyelenggara");
        mitraCaption.setAlignmentX(LEFT_ALIGNMENT);
        mitraLabel.setAlignmentX(LEFT_ALIGNMENT);

        left.add(judulLabel);
        left.add(Box.createVerticalStrut(8));
        left.add(mitraCaption);
        left.add(Box.createVerticalStrut(2));
        left.add(mitraLabel);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        statusLabel.setAlignmentX(RIGHT_ALIGNMENT);
        right.add(statusLabel);
        right.add(Box.createVerticalStrut(10));
        JLabel lokasi = Ui.caption("Lokasi");
        lokasi.setAlignmentX(RIGHT_ALIGNMENT);
        lokasiLabel.setAlignmentX(RIGHT_ALIGNMENT);
        right.add(lokasi);
        right.add(lokasiLabel);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JComponent buildDetailCard() {
        Ui.Card card = Ui.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel infoGrid = new JPanel(new java.awt.GridLayout(1, 3, 20, 0));
        infoGrid.setOpaque(false);
        infoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        infoGrid.setPreferredSize(new Dimension(0, 64));
        infoGrid.add(infoBlock("Kuota", kuotaLabel));
        infoGrid.add(infoBlock("Tunjangan", tunjanganLabel));
        infoGrid.add(infoBlock("Periode Pendaftaran", periodeLabel));

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        infoGrid.setAlignmentX(LEFT_ALIGNMENT);
        isi.add(infoGrid);
        isi.add(Box.createVerticalStrut(22));

        JLabel deskripsiTitle = Ui.caption("DESKRIPSI LOWONGAN");
        deskripsiTitle.setAlignmentX(LEFT_ALIGNMENT);
        isi.add(deskripsiTitle);
        isi.add(Box.createVerticalStrut(8));
        isi.add(readOnlyArea(deskripsiArea, 6));
        isi.add(Box.createVerticalStrut(20));

        JLabel syaratTitle = Ui.caption("PERSYARATAN");
        syaratTitle.setAlignmentX(LEFT_ALIGNMENT);
        isi.add(syaratTitle);
        isi.add(Box.createVerticalStrut(8));
        isi.add(readOnlyArea(syaratArea, 5));

        // Samakan alignment seluruh anak agar BoxLayout tidak menggeser komponen.
        for (java.awt.Component c : isi.getComponents()) {
            if (c instanceof JComponent jc) {
                jc.setAlignmentX(LEFT_ALIGNMENT);
            }
        }

        card.add(isi, BorderLayout.CENTER);
        return card;
    }

    private JComponent infoBlock(String label, JLabel value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = Ui.caption(label);
        l.setAlignmentX(LEFT_ALIGNMENT);
        value.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(6));
        p.add(value);
        return p;
    }

    private JComponent readOnlyArea(JTextArea area, int rows) {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(Theme.FONT_BODY);
        area.setForeground(Theme.Colors.TEXT);
        area.setBackground(Theme.Colors.CARD);
        area.setBorder(new EmptyBorder(4, 0, 4, 0));
        area.setRows(rows);
        area.setAlignmentX(LEFT_ALIGNMENT);

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(javax.swing.BorderFactory.createLineBorder(Theme.Colors.BORDER));
        sp.setPreferredSize(new Dimension(0, rows * 22 + 16));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, rows * 22 + 16));
        sp.setAlignmentX(LEFT_ALIGNMENT);
        return sp;
    }

    /**
     * Menetapkan lowongan yang ditampilkan.
     *
     * @param lowonganId id lowongan
     */
    public void setLowonganId(int lowonganId) {
        this.lowonganId = lowonganId;
        onShow();
    }

    /**
     * Menetapkan aksi tombol Kembali.
     *
     * @param action aksi
     */
    public void setOnKembali(Runnable action) {
        this.onKembali = action;
    }

    /**
     * Menetapkan aksi setelah pendaftaran berhasil dikirim.
     *
     * @param action aksi
     */
    public void setOnDaftarSelesai(Runnable action) {
        this.onDaftarSelesai = action;
    }

    private void kosongkan() {
        judulLabel.setText("Pilih lowongan dari daftar");
        mitraLabel.setText("-");
        lokasiLabel.setText("-");
        kuotaLabel.setText("-");
        tunjanganLabel.setText("-");
        periodeLabel.setText("-");
        statusLabel.setText("-");
        deskripsiArea.setText("");
        syaratArea.setText("");
        daftarButton.setEnabled(false);
    }

    @Override
    public void onShow() {
        if (lowonganId <= 0) {
            kosongkan();
            return;
        }
        Async.run(this, () -> lowonganService.detailLowongan(lowonganId), this::terapkan);
    }

    private void terapkan(Optional<LowonganMagang> hasil) {
        if (hasil.isEmpty()) {
            kosongkan();
            Messages.info(this, "Lowongan tidak ditemukan.");
            return;
        }
        LowonganMagang l = hasil.get();
        judulLabel.setText(l.getJudul());
        mitraLabel.setText(l.getNamaMitra());
        lokasiLabel.setText(l.getLokasi() == null || l.getLokasi().isBlank() ? "-" : l.getLokasi());
        kuotaLabel.setText(l.getKuota() + " orang");
        tunjanganLabel.setText(rupiah(l.getTunjangan()));
        periodeLabel.setText(DateField.format(l.getTanggalBuka())
                + " s/d " + DateField.format(l.getTanggalTutup()));
        statusLabel.setText(l.getStatus());
        statusLabel.setForeground(Widgets.warnaStatus(l.getStatus()));
        deskripsiArea.setText(l.getDeskripsi() == null || l.getDeskripsi().isBlank()
                ? "-" : l.getDeskripsi());
        syaratArea.setText(l.getPersyaratan() == null || l.getPersyaratan().isBlank()
                ? "-" : l.getPersyaratan());
        daftarButton.setEnabled(l.isBuka());
    }

    private void ajukan() {
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            Messages.error(this, "Sesi mahasiswa tidak ditemukan.");
            return;
        }
        Async.run(this,
                () -> lowonganService.detailLowongan(lowonganId),
                hasil -> {
                    if (hasil.isEmpty()) {
                        Messages.error(this, "Lowongan tidak ditemukan.");
                        return;
                    }
                    boolean lanjut = FormPendaftaranDialog.tampilkan(
                            javax.swing.SwingUtilities.getWindowAncestor(this),
                            hasil.get(), pendaftaranService);
                    if (!lanjut) {
                        return;
                    }
                    Async.run(this,
                            () -> pendaftaranService.daftarMagang(mahasiswaId, lowonganId),
                            id -> {
                                Messages.sukses(this, "Pendaftaran berhasil dikirim.\n"
                                        + "Menunggu persetujuan admin.");
                                if (onDaftarSelesai != null) {
                                    onDaftarSelesai.run();
                                }
                            });
                });
    }

    private static String rupiah(BigDecimal nilai) {
        if (nilai == null || nilai.compareTo(BigDecimal.ZERO) <= 0) {
            return "-";
        }
        return "Rp " + String.format("%,.0f", nilai);
    }
}
