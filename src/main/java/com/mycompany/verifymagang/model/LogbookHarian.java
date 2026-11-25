package com.mycompany.verifymagang.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity untuk tabel {@code logbook}.
 *
 * <p>Catatan harian kegiatan magang milik satu {@link PenempatanMagang}.
 * Dosen pembimbing memverifikasi lewat status
 * {@link #STATUS_DIVERIFIKASI}.</p>
 */
public class LogbookHarian {

    public static final String STATUS_MENUNGGU = "MENUNGGU";
    public static final String STATUS_DIVERIFIKASI = "DIVERIFIKASI";
    public static final String STATUS_REVISI = "REVISI";

    private int id;
    private int penempatanId;
    private LocalDate tanggal;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String kegiatan;
    private String kendala;
    private String statusVerifikasi;
    private String catatanDosen;

    /** Kolom hasil join, tidak disimpan ke database. */
    private String namaMahasiswa;

    public LogbookHarian() {
    }

    public LogbookHarian(int id, int penempatanId, LocalDate tanggal, LocalTime jamMulai,
            LocalTime jamSelesai, String kegiatan, String kendala, String statusVerifikasi,
            String catatanDosen) {
        this.id = id;
        this.penempatanId = penempatanId;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.kegiatan = kegiatan;
        this.kendala = kendala;
        this.statusVerifikasi = statusVerifikasi;
        this.catatanDosen = catatanDosen;
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

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(LocalTime jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public String getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(String kegiatan) {
        this.kegiatan = kegiatan;
    }

    public String getKendala() {
        return kendala;
    }

    public void setKendala(String kendala) {
        this.kendala = kendala;
    }

    public String getStatusVerifikasi() {
        return statusVerifikasi;
    }

    public void setStatusVerifikasi(String statusVerifikasi) {
        this.statusVerifikasi = statusVerifikasi;
    }

    public String getCatatanDosen() {
        return catatanDosen;
    }

    public void setCatatanDosen(String catatanDosen) {
        this.catatanDosen = catatanDosen;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public boolean isDiverifikasi() {
        return STATUS_DIVERIFIKASI.equalsIgnoreCase(statusVerifikasi);
    }

    @Override
    public String toString() {
        return "LogbookHarian{id=" + id + ", penempatanId=" + penempatanId
                + ", tanggal=" + tanggal + ", status=" + statusVerifikasi + '}';
    }
}
