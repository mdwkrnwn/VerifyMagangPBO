package com.mycompany.verifymagang.view.mahasiswa;

import com.mycompany.verifymagang.app.SessionManager;
import com.mycompany.verifymagang.model.LowonganMagang;
import com.mycompany.verifymagang.model.PendaftaranMagang;
import com.mycompany.verifymagang.service.LowonganService;
import com.mycompany.verifymagang.service.PendaftaranService;
import com.mycompany.verifymagang.view.common.DateField;
import com.mycompany.verifymagang.view.common.Icons;
import com.mycompany.verifymagang.view.common.Messages;
import com.mycompany.verifymagang.view.common.TablePage;
import com.mycompany.verifymagang.view.common.Theme;

import javax.swing.JButton;
import java.math.BigDecimal;
import java.util.List;

/**
 * Halaman daftar lowongan magang yang sedang dibuka.
 *
 * <p>Mahasiswa dapat membuka detail lowongan lalu mengajukan pendaftaran.
 * Seluruh aturan pendaftaran (kuota, status, pendaftaran ganda) dijaga oleh
 * {@link PendaftaranService}.</p>
 */
public class LowonganMahasiswaPanel extends TablePage<LowonganMagang> {

    private final LowonganService lowonganService = new LowonganService();
    private final PendaftaranService pendaftaranService = new PendaftaranService();
    private Runnable openDetail;

    /** Membuat halaman daftar lowongan. */
    public LowonganMahasiswaPanel() {
        super("Daftar Lowongan", "Lowongan magang yang sedang dibuka",
                "Judul", "Mitra", "Lokasi", "Kuota", "Tunjangan", "Buka", "Tutup");

        setColumnWidth(0, 240);
        setColumnWidth(1, 180);
        setColumnWidth(2, 140);
        setColumnWidth(3, 70);
        setColumnWidth(4, 120);
        setColumnWidth(5, 100);
        setColumnWidth(6, 100);
        boldColumn(0);

        JButton detail = tombolAksi("Lihat Detail",
                Icons.get(Icons.I.EYE, 15, Theme.Colors.TEXT), this::bukaDetail);
        detail.setEnabled(false);
        bindToSelection(detail);

        JButton daftar = tombolTambah(this::ajukan);
        daftar.setText("Daftar");

        addToolbarRight(detail, daftar, tombolMuatUlang());
        setOnSearch(this::muatUlang);
    }

    /**
     * Menetapkan aksi pembuka halaman detail.
     *
     * @param action aksi dengan parameter id lowongan
     */
    public void setOnOpenDetail(java.util.function.IntConsumer action) {
        this.openDetail = () -> {
            LowonganMagang selected = terpilih();
            if (selected != null) {
                action.accept(selected.getId());
            }
        };
    }

    @Override
    protected List<LowonganMagang> muatData() throws Exception {
        List<LowonganMagang> semua = lowonganService.daftarLowonganBuka();
        String keyword = kataKunci().toLowerCase();
        if (keyword.isEmpty()) {
            return semua;
        }
        return semua.stream()
                .filter(l -> mengandung(l.getJudul(), keyword)
                || mengandung(l.getNamaMitra(), keyword)
                || mengandung(l.getLokasi(), keyword))
                .toList();
    }

    private static boolean mengandung(String nilai, String kunci) {
        return nilai != null && nilai.toLowerCase().contains(kunci);
    }

    @Override
    protected Object[] baris(LowonganMagang l) {
        return new Object[]{l.getJudul(), l.getNamaMitra(), l.getLokasi(), l.getKuota(),
            rupiah(l.getTunjangan()), DateField.format(l.getTanggalBuka()),
            DateField.format(l.getTanggalTutup())};
    }

    private static String rupiah(BigDecimal nilai) {
        if (nilai == null || nilai.compareTo(BigDecimal.ZERO) <= 0) {
            return "-";
        }
        return "Rp " + String.format("%,.0f", nilai);
    }

    @Override
    protected void onRowDoubleClick() {
        bukaDetail();
    }

    private void bukaDetail() {
        if (openDetail != null) {
            openDetail.run();
        }
    }

    private void ajukan() {
        LowonganMagang selected = terpilih();
        if (selected == null) {
            return;
        }
        Integer mahasiswaId = SessionManager.getMahasiswaId();
        if (mahasiswaId == null) {
            Messages.error(this, "Sesi mahasiswa tidak ditemukan.");
            return;
        }

        boolean lanjut = FormPendaftaranDialog.tampilkan(
                javax.swing.SwingUtilities.getWindowAncestor(this), selected, pendaftaranService);
        if (!lanjut) {
            return;
        }

        com.mycompany.verifymagang.view.common.Async.run(this,
                () -> pendaftaranService.daftarMagang(mahasiswaId, selected.getId()),
                id -> {
                    Messages.sukses(this, "Pendaftaran berhasil dikirim.\n"
                            + "Menunggu persetujuan admin.");
                    muatUlang();
                });
    }

    /**
     * Mengambil status pendaftaran mahasiswa untuk ditampilkan di halaman lain.
     *
     * @param mahasiswaId id mahasiswa
     * @return daftar pendaftaran
     * @throws Exception bila query gagal
     */
    static List<PendaftaranMagang> riwayat(int mahasiswaId) throws Exception {
        return new PendaftaranService().daftarByMahasiswa(mahasiswaId);
    }
}
