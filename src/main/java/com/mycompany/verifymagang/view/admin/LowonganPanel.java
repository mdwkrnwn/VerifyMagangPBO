package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.LowonganMagang;
import com.mycompany.verifymagang.model.Mitra;
import com.mycompany.verifymagang.model.PeriodeMagang;
import com.mycompany.verifymagang.service.LowonganService;
import com.mycompany.verifymagang.service.PeriodeMagangService;
import com.mycompany.verifymagang.view.common.Async;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.FormDialog;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.TablePage;
import com.mycompany.verifymagang.view.common.Theme;
import com.mycompany.verifymagang.view.common.Ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Halaman pengelolaan lowongan magang.
 */
public class LowonganPanel extends TablePage<LowonganMagang> {

    private final LowonganService service = new LowonganService();
    private final PeriodeMagangService periodeService = new PeriodeMagangService();

    /** Membuat halaman data lowongan. */
    public LowonganPanel() {
        super("Data Lowongan", "Kelola lowongan magang yang ditawarkan mitra",
                "Judul Lowongan", "Mitra", "Kuota", "Tunjangan", "Lokasi",
                "Buka", "Tutup", "Status");

        setColumnWidth(0, 230);
        setColumnWidth(1, 170);
        setColumnWidth(2, 70);
        setColumnWidth(3, 120);
        setColumnWidth(4, 130);
        setColumnWidth(5, 100);
        setColumnWidth(6, 100);
        setColumnWidth(7, 90);
        boldColumn(0);
        statusColumn(7);

        JButton tambah = tombolTambah(this::dialogTambah);
        JButton ubah = tombolUbah(this::dialogUbah);
        JButton status = tombolAksi("Buka/Tutup",
                Icons.get(Icons.I.FILTER, 15, Theme.Colors.TEXT), this::ubahStatus);
        status.setEnabled(false);
        bindToSelection(status);
        JButton hapus = tombolHapus(this::hapusData);

        addToolbarRight(tambah, ubah, status, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<LowonganMagang> muatData() throws Exception {
        return service.cariLowongan(kataKunci());
    }

    @Override
    protected Object[] baris(LowonganMagang l) {
        return new Object[]{l.getJudul(), l.getNamaMitra(), l.getKuota(),
            rupiah(l.getTunjangan()), l.getLokasi(),
            DateField.format(l.getTanggalBuka()), DateField.format(l.getTanggalTutup()),
            l.getStatus()};
    }

    private static String rupiah(BigDecimal nilai) {
        if (nilai == null || nilai.compareTo(BigDecimal.ZERO) <= 0) {
            return "-";
        }
        return "Rp " + String.format("%,.0f", nilai);
    }

    @Override
    protected void onRowDoubleClick() {
        dialogUbah();
    }

    private void dialogTambah() {
        List<Mitra> mitra;
        List<PeriodeMagang> periode;
        try {
            mitra = service.daftarMitraAktif();
            periode = periodeService.daftarSemua();
        } catch (Exception ex) {
            Messages.error(this, Messages.pesanDari(ex));
            return;
        }
        if (mitra.isEmpty()) {
            Messages.peringatan(this, "Belum ada mitra aktif. Tambahkan mitra terlebih dahulu.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tambah Lowongan", "Lowongan baru otomatis berstatus BUKA.");

        JComboBox<Mitra> cbMitra = dialog.addField("Mitra",
                Ui.comboBox(mitra, Mitra::getNama));
        JComboBox<PeriodeMagang> cbPeriode = dialog.addField("Periode Magang",
                Ui.comboBox(periode, PeriodeMagang::getNama));
        JTextField judul = dialog.addField("Judul Lowongan", Ui.textField(20));
        JScrollPane deskripsi = dialog.addField("Deskripsi", Ui.textArea(3, 20));
        JScrollPane syarat = dialog.addField("Persyaratan", Ui.textArea(3, 20));
        JTextField kuota = dialog.addField("Kuota", Ui.textField(20));
        JTextField tunjangan = dialog.addField("Tunjangan (Rp)", Ui.textField(20));
        tunjangan.setToolTipText("Angka saja, contoh 1500000");
        JTextField lokasi = dialog.addField("Lokasi", Ui.textField(20));
        DateField buka = dialog.addField("Tanggal Buka", new DateField());
        DateField tutup = dialog.addField("Tanggal Tutup", new DateField());

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(judul.getText())) {
            Messages.peringatan(this, "Judul lowongan wajib diisi.");
            return;
        }

        LowonganMagang l = new LowonganMagang();
        l.setMitraId(((Mitra) cbMitra.getSelectedItem()).getId());
        l.setPeriodeId(cbPeriode.getSelectedItem() == null ? null
                : ((PeriodeMagang) cbPeriode.getSelectedItem()).getId());
        l.setJudul(judul.getText().trim());
        l.setDeskripsi(Ui.areaOf(deskripsi).getText().trim());
        l.setPersyaratan(Ui.areaOf(syarat).getText().trim());
        l.setKuota(parseInt(kuota.getText(), 1));
        l.setTunjangan(parseDecimal(tunjangan.getText()));
        l.setLokasi(lokasi.getText().trim());
        l.setTanggalBuka(buka.getLocalDate());
        l.setTanggalTutup(tutup.getLocalDate());
        l.setStatus(LowonganMagang.STATUS_BUKA);

        if (l.getKuota() <= 0) {
            Messages.peringatan(this, "Kuota harus lebih dari 0.");
            return;
        }
        if (l.getTanggalTutup().isBefore(l.getTanggalBuka())) {
            Messages.peringatan(this, "Tanggal tutup tidak boleh sebelum tanggal buka.");
            return;
        }

        Async.run(this,
                () -> service.tambahLowongan(l),
                id -> {
                    Messages.sukses(this, "Lowongan berhasil ditambahkan.");
                    muatUlang();
                });
    }

    private void dialogUbah() {
        LowonganMagang selected = terpilih();
        if (selected == null) {
            return;
        }

        List<Mitra> mitra;
        List<PeriodeMagang> periode;
        try {
            mitra = new ArrayList<>(service.daftarMitra());
            periode = periodeService.daftarSemua();
        } catch (Exception ex) {
            Messages.error(this, Messages.pesanDari(ex));
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ubah Lowongan", "Perbarui informasi lowongan.");

        JComboBox<Mitra> cbMitra = dialog.addField("Mitra",
                Ui.comboBox(mitra, Mitra::getNama));
        selectMitra(cbMitra, selected.getMitraId());
        JComboBox<PeriodeMagang> cbPeriode = dialog.addField("Periode Magang",
                Ui.comboBox(periode, PeriodeMagang::getNama));
        selectPeriode(cbPeriode, selected.getPeriodeId());
        JTextField judul = dialog.addField("Judul Lowongan", Ui.textField(20));
        judul.setText(selected.getJudul());
        JScrollPane deskripsi = dialog.addField("Deskripsi", Ui.textArea(3, 20));
        Ui.areaOf(deskripsi).setText(selected.getDeskripsi());
        JScrollPane syarat = dialog.addField("Persyaratan", Ui.textArea(3, 20));
        Ui.areaOf(syarat).setText(selected.getPersyaratan());
        JTextField kuota = dialog.addField("Kuota", Ui.textField(20));
        kuota.setText(String.valueOf(selected.getKuota()));
        JTextField tunjangan = dialog.addField("Tunjangan (Rp)", Ui.textField(20));
        tunjangan.setText(selected.getTunjangan() == null ? "0"
                : selected.getTunjangan().toPlainString());
        JTextField lokasi = dialog.addField("Lokasi", Ui.textField(20));
        lokasi.setText(selected.getLokasi());
        DateField buka = dialog.addField("Tanggal Buka",
                new DateField(selected.getTanggalBuka()));
        DateField tutup = dialog.addField("Tanggal Tutup",
                new DateField(selected.getTanggalTutup()));

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(judul.getText())) {
            Messages.peringatan(this, "Judul lowongan wajib diisi.");
            return;
        }

        selected.setMitraId(((Mitra) cbMitra.getSelectedItem()).getId());
        selected.setPeriodeId(cbPeriode.getSelectedItem() == null ? null
                : ((PeriodeMagang) cbPeriode.getSelectedItem()).getId());
        selected.setJudul(judul.getText().trim());
        selected.setDeskripsi(Ui.areaOf(deskripsi).getText().trim());
        selected.setPersyaratan(Ui.areaOf(syarat).getText().trim());
        selected.setKuota(parseInt(kuota.getText(), 1));
        selected.setTunjangan(parseDecimal(tunjangan.getText()));
        selected.setLokasi(lokasi.getText().trim());
        selected.setTanggalBuka(buka.getLocalDate());
        selected.setTanggalTutup(tutup.getLocalDate());

        if (selected.getKuota() <= 0) {
            Messages.peringatan(this, "Kuota harus lebih dari 0.");
            return;
        }
        if (selected.getTanggalTutup().isBefore(selected.getTanggalBuka())) {
            Messages.peringatan(this, "Tanggal tutup tidak boleh sebelum tanggal buka.");
            return;
        }

        Async.run(this,
                () -> service.ubahLowongan(selected),
                ok -> {
                    Messages.sukses(this, "Lowongan diperbarui.");
                    muatUlang();
                });
    }

    private void ubahStatus() {
        LowonganMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        boolean akanDitutup = selected.isBuka();
        String aksi = akanDitutup ? "Tutup" : "Buka kembali";
        if (!Messages.konfirmasi(this, aksi + " lowongan \"" + selected.getJudul() + "\"?")) {
            return;
        }
        Async.run(this,
                () -> akanDitutup
                        ? service.tutupLowongan(selected.getId())
                        : service.bukaLowongan(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Lowongan berhasil di" + aksi.toLowerCase() + ".");
                    muatUlang();
                });
    }

    private void hapusData() {
        LowonganMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this, "lowongan " + selected.getJudul())) {
            return;
        }
        Async.run(this,
                () -> service.hapusLowongan(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Lowongan dihapus.");
                    muatUlang();
                });
    }

    private static void selectMitra(JComboBox<Mitra> combo, int mitraId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == mitraId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private static void selectPeriode(JComboBox<PeriodeMagang> combo, Integer periodeId) {
        if (periodeId == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == periodeId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private static boolean kosong(String teks) {
        return teks == null || teks.isBlank();
    }

    private static int parseInt(String teks, int fallback) {
        try {
            return Integer.parseInt(teks == null ? "" : teks.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static BigDecimal parseDecimal(String teks) {
        try {
            String bersih = teks == null ? "" : teks.replaceAll("[^0-9.]", "").trim();
            if (bersih.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(bersih);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}
