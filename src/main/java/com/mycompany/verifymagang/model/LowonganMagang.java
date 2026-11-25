package com.mycompany.verifymagang.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity untuk tabel {@code lowongan}.
 *
 * <p>Dibuka oleh {@link Mitra} pada suatu {@link PeriodeMagang}. Status yang
 * dipakai: {@link #STATUS_BUKA} dan {@link #STATUS_TUTUP}.</p>
 */
public class LowonganMagang {

    public static final String STATUS_BUKA = "BUKA";
    public static final String STATUS_TUTUP = "TUTUP";

    private int id;
    private int mitraId;
    private Integer periodeId;
    private String judul;
    private String deskripsi;
    private String persyaratan;
    private int kuota;
    private BigDecimal tunjangan;
    private String lokasi;
    private String status;
    private LocalDate tanggalBuka;
    private LocalDate tanggalTutup;

    /** Nama mitra hasil join, tidak disimpan ke database. */
    private String namaMitra;

    public LowonganMagang() {
    }

    public LowonganMagang(int id, int mitraId, Integer periodeId, String judul, String deskripsi,
            String persyaratan, int kuota, BigDecimal tunjangan, String lokasi, String status,
            LocalDate tanggalBuka, LocalDate tanggalTutup) {
        this.id = id;
        this.mitraId = mitraId;
        this.periodeId = periodeId;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.persyaratan = persyaratan;
        this.kuota = kuota;
        this.tunjangan = tunjangan;
        this.lokasi = lokasi;
        this.status = status;
        this.tanggalBuka = tanggalBuka;
        this.tanggalTutup = tanggalTutup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMitraId() {
        return mitraId;
    }

    public void setMitraId(int mitraId) {
        this.mitraId = mitraId;
    }

    public Integer getPeriodeId() {
        return periodeId;
    }

    public void setPeriodeId(Integer periodeId) {
        this.periodeId = periodeId;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getPersyaratan() {
        return persyaratan;
    }

    public void setPersyaratan(String persyaratan) {
        this.persyaratan = persyaratan;
    }

    public int getKuota() {
        return kuota;
    }

    public void setKuota(int kuota) {
        this.kuota = kuota;
    }

    public BigDecimal getTunjangan() {
        return tunjangan;
    }

    public void setTunjangan(BigDecimal tunjangan) {
        this.tunjangan = tunjangan;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getTanggalBuka() {
        return tanggalBuka;
    }

    public void setTanggalBuka(LocalDate tanggalBuka) {
        this.tanggalBuka = tanggalBuka;
    }

    public LocalDate getTanggalTutup() {
        return tanggalTutup;
    }

    public void setTanggalTutup(LocalDate tanggalTutup) {
        this.tanggalTutup = tanggalTutup;
    }

    public String getNamaMitra() {
        return namaMitra;
    }

    public void setNamaMitra(String namaMitra) {
        this.namaMitra = namaMitra;
    }

    /** @return {@code true} bila lowongan masih berstatus buka. */
    public boolean isBuka() {
        return STATUS_BUKA.equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "LowonganMagang{id=" + id + ", judul=" + judul + ", mitraId=" + mitraId
                + ", status=" + status + '}';
    }
}
