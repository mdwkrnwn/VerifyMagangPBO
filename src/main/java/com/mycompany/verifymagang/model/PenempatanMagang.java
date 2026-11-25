package com.mycompany.verifymagang.model;

import java.time.LocalDate;

/**
 * Entity untuk tabel {@code penempatan}.
 *
 * <p>Dibuat setelah pendaftaran disetujui. Menentukan {@link Dosen} pembimbing
 * dan {@link Mitra} tempat magang. Menjadi induk dari {@link LogbookHarian}
 * dan {@link PenilaianMagang}.</p>
 */
public class PenempatanMagang {

    public static final String STATUS_AKTIF = "AKTIF";
    public static final String STATUS_SELESAI = "SELESAI";
    public static final String STATUS_BATAL = "BATAL";

    private int id;
    private int pendaftaranId;
    private int dosenId;
    private int mitraId;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private String status;

    /** Kolom hasil join, tidak disimpan ke database. */
    private String namaMahasiswa;
    private String namaDosen;
    private String namaMitra;

    public PenempatanMagang() {
    }

    public PenempatanMagang(int id, int pendaftaranId, int dosenId, int mitraId,
            LocalDate tanggalMulai, LocalDate tanggalSelesai, String status) {
        this.id = id;
        this.pendaftaranId = pendaftaranId;
        this.dosenId = dosenId;
        this.mitraId = mitraId;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPendaftaranId() {
        return pendaftaranId;
    }

    public void setPendaftaranId(int pendaftaranId) {
        this.pendaftaranId = pendaftaranId;
    }

    public int getDosenId() {
        return dosenId;
    }

    public void setDosenId(int dosenId) {
        this.dosenId = dosenId;
    }

    public int getMitraId() {
        return mitraId;
    }

    public void setMitraId(int mitraId) {
        this.mitraId = mitraId;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public LocalDate getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(LocalDate tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getNamaMitra() {
        return namaMitra;
    }

    public void setNamaMitra(String namaMitra) {
        this.namaMitra = namaMitra;
    }

    public boolean isAktif() {
        return STATUS_AKTIF.equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "PenempatanMagang{id=" + id + ", pendaftaranId=" + pendaftaranId
                + ", dosenId=" + dosenId + ", status=" + status + '}';
    }
}
