package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.model.LowonganMagang;
import com.mycompany.verifymagang.service.PendaftaranService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.FormDialog;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;
import com.mycompany.verifymagang.view.common.Widgets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.math.BigDecimal;

/**
 * Dialog konfirmasi pengajuan pendaftaran magang.
 *
 * <p>Menampilkan ringkasan lowongan yang dipilih lalu meminta persetujuan
 * mahasiswa sebelum data dikirim ke service.</p>
 */
public final class FormPendaftaranDialog {

    private FormPendaftaranDialog() {
    }

    /**
     * Menampilkan dialog pendaftaran.
     *
     * @param owner     jendela induk
     * @param lowongan  lowongan yang akan didaftar
     * @param service   service pendaftaran (untuk memeriksa sisa kuota)
     * @return {@code true} bila mahasiswa menekan tombol kirim
     */
    public static boolean tampilkan(Window owner, LowonganMagang lowongan,
            PendaftaranService service) {

        int sisa;
        try {
            sisa = service == null ? lowongan.getKuota()
                    : new com.mycompany.verifymagang.service.LowonganService()
                            .sisaKuota(lowongan.getId());
        } catch (Exception ex) {
            Messages.error(owner, Messages.pesanDari(ex));
            return false;
        }

        FormDialog dialog = new FormDialog(owner, "Form Pendaftaran Magang",
                "Periksa kembali data lowongan sebelum mengirim pendaftaran.");

        JPanel ringkasan = new JPanel();
        ringkasan.setOpaque(false);
        ringkasan.setLayout(new BoxLayout(ringkasan, BoxLayout.Y_AXIS));
        ringkasan.add(Widgets.infoRow("Judul Lowongan", lowongan.getJudul()));
        ringkasan.add(Widgets.infoRow("Mitra", lowongan.getNamaMitra()));
        ringkasan.add(Widgets.infoRow("Lokasi", lowongan.getLokasi()));
        ringkasan.add(Widgets.infoRow("Kuota", lowongan.getKuota() + " orang"));
        ringkasan.add(Widgets.infoRow("Sisa Kuota", sisa + " orang"));
        ringkasan.add(Widgets.infoRow("Tunjangan", rupiah(lowongan.getTunjangan())));
        ringkasan.add(Widgets.infoRow("Periode Pendaftaran",
                DateField.format(lowongan.getTanggalBuka())
                + " s/d " + DateField.format(lowongan.getTanggalTutup())));
        dialog.addField("Ringkasan", ringkasan);

        JScrollPane syarat = dialog.addField("Persyaratan", Ui.textArea(4, 20));
        Ui.areaOf(syarat).setText(lowongan.getPersyaratan() == null
                ? "-" : lowongan.getPersyaratan());
        Ui.areaOf(syarat).setEditable(false);

        JLabel note = Ui.caption("Dengan menekan Simpan, pendaftaran akan dikirim dan menunggu "
                + "persetujuan admin.");
        dialog.addField(" ", note);

        return dialog.showDialog();
    }

    private static String rupiah(BigDecimal nilai) {
        if (nilai == null || nilai.compareTo(BigDecimal.ZERO) <= 0) {
            return "-";
        }
        return "Rp " + String.format("%,.0f", nilai);
    }
}
