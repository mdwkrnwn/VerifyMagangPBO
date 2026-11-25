package com.mycompany.verifymagang.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Entity untuk tabel {@code penilaian}.
 *
 * <p>Berelasi satu-ke-satu dengan {@link PenempatanMagang}. Nilai akhir dihitung
 * dari komponen penilaian mitra dan dosen menggunakan bobot pada
 * {@link #BOBOT_MITRA} dan {@link #BOBOT_DOSEN}.</p>
 */
public class PenilaianMagang {

    /** Bobot komponen nilai mitra (lapangan). */
    public static final BigDecimal BOBOT_MITRA = new BigDecimal("0.40");

    /** Bobot komponen nilai dosen (akademik). */
    public static final BigDecimal BOBOT_DOSEN = new BigDecimal("0.60");

    private int id;
    private int penempatanId;
    private int dosenId;

    /** Nilai dari pembimbing lapangan (mitra), skala 0-100. */
    private BigDecimal nilaiMitra;

    /** Nilai dari dosen pembimbing, skala 0-100. */
    private BigDecimal nilaiDosen;

    /** Nilai akhir hasil perhitungan, skala 0-100. */
    private BigDecimal nilaiAkhir;

    private String catatan;
    private LocalDate tanggalPenilaian;

    /** Kolom hasil join, tidak disimpan ke database. */
    private String namaMahasiswa;
    private String namaDosen;

    public PenilaianMagang() {
    }

    public PenilaianMagang(int id, int penempatanId, int dosenId, BigDecimal nilaiMitra,
            BigDecimal nilaiDosen, BigDecimal nilaiAkhir, String catatan, LocalDate tanggalPenilaian) {
        this.id = id;
        this.penempatanId = penempatanId;
        this.dosenId = dosenId;
        this.nilaiMitra = nilaiMitra;
        this.nilaiDosen = nilaiDosen;
        this.nilaiAkhir = nilaiAkhir;
        this.catatan = catatan;
        this.tanggalPenilaian = tanggalPenilaian;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPenempatanId() {
        return penempatanId;
    }

    public void setPenempatanId(int penempatanId) {
        this.penempatanId = penempatanId;
    }

    public int getDosenId() {
        return dosenId;
    }

    public void setDosenId(int dosenId) {
        this.dosenId = dosenId;
    }

    public BigDecimal getNilaiMitra() {
        return nilaiMitra;
    }

    public void setNilaiMitra(BigDecimal nilaiMitra) {
        this.nilaiMitra = nilaiMitra;
    }

    public BigDecimal getNilaiDosen() {
        return nilaiDosen;
    }

    public void setNilaiDosen(BigDecimal nilaiDosen) {
        this.nilaiDosen = nilaiDosen;
    }

    public BigDecimal getNilaiAkhir() {
        return nilaiAkhir;
    }

    public void setNilaiAkhir(BigDecimal nilaiAkhir) {
        this.nilaiAkhir = nilaiAkhir;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDate getTanggalPenilaian() {
        return tanggalPenilaian;
    }

    public void setTanggalPenilaian(LocalDate tanggalPenilaian) {
        this.tanggalPenilaian = tanggalPenilaian;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    /**
     * Menghitung nilai akhir dari nilai mitra dan dosen sesuai bobot, lalu
     * menyimpannya ke {@link #nilaiAkhir}.
     *
     * @return nilai akhir hasil perhitungan, atau {@code null} bila salah satu
     *         komponen belum diisi
     */
    public BigDecimal hitungNilaiAkhir() {
        if (nilaiMitra == null || nilaiDosen == null) {
            return null;
        }
        this.nilaiAkhir = nilaiMitra.multiply(BOBOT_MITRA)
                .add(nilaiDosen.multiply(BOBOT_DOSEN))
                .setScale(2, RoundingMode.HALF_UP);
        return this.nilaiAkhir;
    }

    /**
     * Mengonversi nilai akhir menjadi huruf mutu.
     *
     * @return huruf mutu A/B/C/D/E, atau {@code null} bila nilai akhir kosong
     */
    public String getHurufMutu() {
        if (nilaiAkhir == null) {
            return null;
        }
        double n = nilaiAkhir.doubleValue();
        if (n >= 85) {
            return "A";
        } else if (n >= 75) {
            return "B";
        } else if (n >= 65) {
            return "C";
        } else if (n >= 50) {
            return "D";
        }
        return "E";
    }

    @Override
    public String toString() {
        return "PenilaianMagang{id=" + id + ", penempatanId=" + penempatanId
                + ", nilaiAkhir=" + nilaiAkhir + '}';
    }
}
