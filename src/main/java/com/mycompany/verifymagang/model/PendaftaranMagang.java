package com.mycompany.verifymagang.model;

import java.time.LocalDate;

/**
 * Entity untuk tabel {@code pendaftaran}.
 *
 * <p>Menghubungkan {@link Mahasiswa} dengan {@link LowonganMagang}. Alur status:
 * {@link #STATUS_MENUNGGU} &rarr; {@link #STATUS_DISETUJUI} / {@link #STATUS_DITOLAK}.</p>
 */
public class PendaftaranMagang {

    public static final String STATUS_MENUNGGU = "MENUNGGU";
    public static final String STATUS_DISETUJUI = "DISETUJUI";
    public static final String STATUS_DITOLAK = "DITOLAK";

    private int id;
    private int mahasiswaId;
    private int lowonganId;
    private LocalDate tanggalDaftar;
    private String status;
    private String catatan;

    /** Kolom hasil join, tidak disimpan ke database. */
    private String namaMahasiswa;
    private String nim;
    private String judulLowongan;

    public PendaftaranMagang() {
    }

    public PendaftaranMagang(int id, int mahasiswaId, int lowonganId, LocalDate tanggalDaftar,
            String status, String catatan) {
        this.id = id;
        this.mahasiswaId = mahasiswaId;
        this.lowonganId = lowonganId;
        this.tanggalDaftar = tanggalDaftar;
        this.status = status;
        this.catatan = catatan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMahasiswaId() {
        return mahasiswaId;
    }

    public void setMahasiswaId(int mahasiswaId) {
        this.mahasiswaId = mahasiswaId;
    }

    public int getLowonganId() {
        return lowonganId;
    }

    public void setLowonganId(int lowonganId) {
        this.lowonganId = lowonganId;
    }

    public LocalDate getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(LocalDate tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getJudulLowongan() {
        return judulLowongan;
    }

    public void setJudulLowongan(String judulLowongan) {
        this.judulLowongan = judulLowongan;
    }

    public boolean isMenunggu() {
        return STATUS_MENUNGGU.equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "PendaftaranMagang{id=" + id + ", mahasiswaId=" + mahasiswaId
                + ", lowonganId=" + lowonganId + ", status=" + status + '}';
    }
}
