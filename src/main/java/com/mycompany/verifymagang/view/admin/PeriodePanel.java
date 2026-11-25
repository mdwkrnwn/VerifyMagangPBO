package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.PeriodeMagang;
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
import javax.swing.JTextField;
import java.util.List;

/**
 * Halaman pengelolaan periode magang.
 */
public class PeriodePanel extends TablePage<PeriodeMagang> {

    private final PeriodeMagangService service = new PeriodeMagangService();

    /** Membuat halaman periode magang. */
    public PeriodePanel() {
        super("Periode Magang", "Kelola periode pelaksanaan magang",
                "Nama Periode", "Tahun Akademik", "Semester", "Mulai", "Selesai", "Status");

        setColumnWidth(0, 220);
        setColumnWidth(1, 150);
        setColumnWidth(2, 110);
        setColumnWidth(3, 120);
        setColumnWidth(4, 120);
        setColumnWidth(5, 110);
        boldColumn(0);
        statusColumn(5);

        JButton tambah = tombolTambah(this::dialogTambah);
        JButton ubah = tombolUbah(this::dialogUbah);
        JButton aktifkan = tombolAksi("Aktifkan",
                Icons.get(Icons.I.CHECK, 15, Theme.Colors.TEXT), this::aktifkanPeriode);
        aktifkan.setEnabled(false);
        bindToSelection(aktifkan);
        JButton hapus = tombolHapus(this::hapusData);

        addToolbarRight(tambah, ubah, aktifkan, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<PeriodeMagang> muatData() throws Exception {
        String keyword = kataKunci();
        List<PeriodeMagang> semua = service.daftarSemua();
        if (keyword.isEmpty()) {
            return semua;
        }
        String k = keyword.toLowerCase();
        return semua.stream()
                .filter(p -> (p.getNama() != null && p.getNama().toLowerCase().contains(k))
                || (p.getTahunAkademik() != null && p.getTahunAkademik().toLowerCase().contains(k)))
                .toList();
    }

    @Override
    protected Object[] baris(PeriodeMagang p) {
        return new Object[]{p.getNama(), p.getTahunAkademik(), p.getSemester(),
            DateField.format(p.getTanggalMulai()), DateField.format(p.getTanggalSelesai()),
            p.isAktif() ? "AKTIF" : "TUTUP"};
    }

    @Override
    protected void onRowDoubleClick() {
        dialogUbah();
    }

    private void dialogTambah() {
        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tambah Periode", "Tentukan rentang waktu pelaksanaan magang.");

        JTextField nama = dialog.addField("Nama Periode", Ui.textField(20));
        JTextField tahun = dialog.addField("Tahun Akademik", Ui.textField(20));
        tahun.setToolTipText("Contoh: 2025/2026");
        JComboBox<String> semester = dialog.addField("Semester",
                Ui.comboBox(new String[]{"Ganjil", "Genap", "Antara"}));
        DateField mulai = dialog.addField("Tanggal Mulai", new DateField());
        DateField selesai = dialog.addField("Tanggal Selesai", new DateField());

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(nama.getText())) {
            Messages.peringatan(this, "Nama periode wajib diisi.");
            return;
        }
        if (selesai.getLocalDate().isBefore(mulai.getLocalDate())) {
            Messages.peringatan(this, "Tanggal selesai tidak boleh sebelum tanggal mulai.");
            return;
        }

        PeriodeMagang p = new PeriodeMagang();
        p.setNama(nama.getText().trim());
        p.setTahunAkademik(tahun.getText().trim());
        p.setSemester(String.valueOf(semester.getSelectedItem()));
        p.setTanggalMulai(mulai.getLocalDate());
        p.setTanggalSelesai(selesai.getLocalDate());
        p.setAktif(false);

        Async.run(this,
                () -> service.tambahPeriode(p),
                id -> {
                    Messages.sukses(this, "Periode berhasil ditambahkan.");
                    muatUlang();
                });
    }

    private void dialogUbah() {
        PeriodeMagang selected = terpilih();
        if (selected == null) {
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ubah Periode", "Perbarui data periode magang.");

        JTextField nama = dialog.addField("Nama Periode", Ui.textField(20));
        nama.setText(selected.getNama());
        JTextField tahun = dialog.addField("Tahun Akademik", Ui.textField(20));
        tahun.setText(selected.getTahunAkademik());
        JComboBox<String> semester = dialog.addField("Semester",
                Ui.comboBox(new String[]{"Ganjil", "Genap", "Antara"}));
        semester.setSelectedItem(selected.getSemester());
        DateField mulai = dialog.addField("Tanggal Mulai",
                new DateField(selected.getTanggalMulai()));
        DateField selesai = dialog.addField("Tanggal Selesai",
                new DateField(selected.getTanggalSelesai()));

        if (!dialog.showDialog()) {
            return;
        }
        if (kosong(nama.getText())) {
            Messages.peringatan(this, "Nama periode wajib diisi.");
            return;
        }
        if (selesai.getLocalDate().isBefore(mulai.getLocalDate())) {
            Messages.peringatan(this, "Tanggal selesai tidak boleh sebelum tanggal mulai.");
            return;
        }

        selected.setNama(nama.getText().trim());
        selected.setTahunAkademik(tahun.getText().trim());
        selected.setSemester(String.valueOf(semester.getSelectedItem()));
        selected.setTanggalMulai(mulai.getLocalDate());
        selected.setTanggalSelesai(selesai.getLocalDate());

        Async.run(this,
                () -> service.ubahPeriode(selected),
                ok -> {
                    Messages.sukses(this, "Periode diperbarui.");
                    muatUlang();
                });
    }

    private void aktifkanPeriode() {
        PeriodeMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasi(this,
                "Aktifkan periode \"" + selected.getNama() + "\"?\n"
                + "Periode aktif sebelumnya akan dinonaktifkan.")) {
            return;
        }
        Async.run(this,
                () -> service.aktifkan(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Periode \"" + selected.getNama() + "\" diaktifkan.");
                    muatUlang();
                });
    }

    private void hapusData() {
        PeriodeMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this, "periode " + selected.getNama())) {
            return;
        }
        Async.run(this,
                () -> service.hapusPeriode(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Periode dihapus.");
                    muatUlang();
                });
    }

    private static boolean kosong(String teks) {
        return teks == null || teks.isBlank();
    }
}
