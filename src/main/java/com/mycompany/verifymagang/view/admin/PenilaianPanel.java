package com.mycompany.verifymagang.view.admin;

import com.mycompany.verifymagang.model.PenempatanMagang;
import com.mycompany.verifymagang.model.PenilaianMagang;
import com.mycompany.verifymagang.service.PenempatanService;
import com.mycompany.verifymagang.service.PenilaianService;
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
import javax.swing.JTextField;
import java.math.BigDecimal;
import java.util.List;

/**
 * Halaman pengelolaan penilaian magang.
 *
 * <p>Nilai akhir dihitung otomatis oleh {@link PenilaianMagang#hitungNilaiAkhir()}
 * dengan bobot 40% mitra dan 60% dosen.</p>
 */
public class PenilaianPanel extends TablePage<PenilaianMagang> {

    private final PenilaianService service = new PenilaianService();
    private final PenempatanService penempatanService = new PenempatanService();

    /** Membuat halaman penilaian. */
    public PenilaianPanel() {
        super("Penilaian Magang", "Kelola nilai mitra, dosen, dan nilai akhir mahasiswa",
                "Mahasiswa", "Dosen", "Nilai Mitra", "Nilai Dosen", "Nilai Akhir",
                "Huruf", "Tanggal", "Catatan");

        setColumnWidth(0, 180);
        setColumnWidth(1, 170);
        setColumnWidth(2, 95);
        setColumnWidth(3, 95);
        setColumnWidth(4, 95);
        setColumnWidth(5, 70);
        setColumnWidth(6, 100);
        boldColumn(0);
        boldColumn(4);

        JButton tambah = tombolTambah(this::dialogTambah);
        JButton ubah = tombolUbah(this::dialogUbah);
        JButton hapus = tombolHapus(this::hapusData);

        addToolbarRight(tambah, ubah, hapus, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    @Override
    protected List<PenilaianMagang> muatData() throws Exception {
        List<PenilaianMagang> data = service.daftarSemua();
        String keyword = kataKunci().toLowerCase();
        if (keyword.isEmpty()) {
            return data;
        }
        return data.stream()
                .filter(n -> mengandung(n.getNamaMahasiswa(), keyword)
                || mengandung(n.getNamaDosen(), keyword))
                .toList();
    }

    private static boolean mengandung(String nilai, String kunci) {
        return nilai != null && nilai.toLowerCase().contains(kunci);
    }

    @Override
    protected Object[] baris(PenilaianMagang n) {
        return new Object[]{n.getNamaMahasiswa(), n.getNamaDosen(),
            angka(n.getNilaiMitra()), angka(n.getNilaiDosen()), angka(n.getNilaiAkhir()),
            n.getHurufMutu(), DateField.format(n.getTanggalPenilaian()),
            n.getCatatan() == null || n.getCatatan().isBlank() ? "-" : n.getCatatan()};
    }

    private static String angka(BigDecimal nilai) {
        return nilai == null ? "-" : String.format("%.2f", nilai);
    }

    @Override
    protected void onRowDoubleClick() {
        dialogUbah();
    }

    private void dialogTambah() {
        List<PenempatanMagang> penempatan;
        try {
            penempatan = penempatanService.daftarSemua();
        } catch (Exception ex) {
            Messages.error(this, Messages.pesanDari(ex));
            return;
        }

        if (penempatan.isEmpty()) {
            Messages.info(this, "Belum ada penempatan. Buat penempatan terlebih dahulu.");
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Tambah Penilaian",
                "Nilai akhir = 40% mitra + 60% dosen, dihitung otomatis.");

        JComboBox<PenempatanMagang> cbPenempatan = dialog.addField("Penempatan",
                Ui.comboBox(penempatan, p -> p.getNamaMahasiswa() + " - " + p.getNamaMitra()));
        JTextField nilaiMitra = dialog.addField("Nilai Mitra (0-100)", Ui.textField(20));
        JTextField nilaiDosen = dialog.addField("Nilai Dosen (0-100)", Ui.textField(20));
        JScrollPane catatan = dialog.addField("Catatan", Ui.textArea(3, 20));

        if (!dialog.showDialog()) {
            return;
        }

        PenempatanMagang selected = (PenempatanMagang) cbPenempatan.getSelectedItem();
        if (selected == null) {
            Messages.peringatan(this, "Penempatan wajib dipilih.");
            return;
        }

        BigDecimal mitra = desimal(nilaiMitra.getText());
        BigDecimal dosen = desimal(nilaiDosen.getText());
        if (mitra == null || dosen == null) {
            Messages.peringatan(this, "Nilai mitra dan dosen wajib berupa angka 0-100.");
            return;
        }

        PenilaianMagang n = new PenilaianMagang();
        n.setPenempatanId(selected.getId());
        n.setNilaiMitra(mitra);
        n.setNilaiDosen(dosen);
        n.setCatatan(Ui.areaOf(catatan).getText().trim());

        Async.run(this,
                () -> service.simpanPenilaian(n),
                id -> {
                    Messages.sukses(this, "Penilaian berhasil disimpan.");
                    muatUlang();
                });
    }

    private void dialogUbah() {
        PenilaianMagang selected = terpilih();
        if (selected == null) {
            return;
        }

        FormDialog dialog = new FormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ubah Penilaian", selected.getNamaMahasiswa());

        JTextField nilaiMitra = dialog.addField("Nilai Mitra (0-100)", Ui.textField(20));
        nilaiMitra.setText(selected.getNilaiMitra() == null ? ""
                : selected.getNilaiMitra().toPlainString());
        JTextField nilaiDosen = dialog.addField("Nilai Dosen (0-100)", Ui.textField(20));
        nilaiDosen.setText(selected.getNilaiDosen() == null ? ""
                : selected.getNilaiDosen().toPlainString());
        JScrollPane catatan = dialog.addField("Catatan", Ui.textArea(3, 20));
        Ui.areaOf(catatan).setText(selected.getCatatan());

        if (!dialog.showDialog()) {
            return;
        }

        BigDecimal mitra = desimal(nilaiMitra.getText());
        BigDecimal dosen = desimal(nilaiDosen.getText());
        if (mitra == null || dosen == null) {
            Messages.peringatan(this, "Nilai mitra dan dosen wajib berupa angka 0-100.");
            return;
        }

        selected.setNilaiMitra(mitra);
        selected.setNilaiDosen(dosen);
        selected.setCatatan(Ui.areaOf(catatan).getText().trim());

        Async.run(this,
                () -> service.ubahPenilaian(selected),
                ok -> {
                    Messages.sukses(this, "Penilaian diperbarui.");
                    muatUlang();
                });
    }

    private void hapusData() {
        PenilaianMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        if (!Messages.konfirmasiHapus(this, "penilaian " + selected.getNamaMahasiswa())) {
            return;
        }
        Async.run(this,
                () -> service.hapusPenilaian(selected.getId()),
                ok -> {
                    Messages.sukses(this, "Penilaian dihapus.");
                    muatUlang();
                });
    }

    private static BigDecimal desimal(String teks) {
        try {
            String bersih = teks == null ? "" : teks.replace(',', '.').trim();
            if (bersih.isEmpty()) {
                return null;
            }
            BigDecimal nilai = new BigDecimal(bersih);
            if (nilai.compareTo(BigDecimal.ZERO) < 0
                    || nilai.compareTo(new BigDecimal("100")) > 0) {
                return null;
            }
            return nilai;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
