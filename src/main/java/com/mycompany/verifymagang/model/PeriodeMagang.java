package com.mycompany.verifymagang.model;

import java.time.LocalDate;

/**
 * Entity untuk tabel {@code periode_magang}.
 *
 * <p>Menandai rentang waktu pembukaan magang. Satu periode dapat menaungi
 * banyak {@link LowonganMagang}.</p>
 */
public class PeriodeMagang {

    private int id;
    private String nama;
    private String tahunAkademik;
    private String semester;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private boolean aktif;

    public PeriodeMagang() {
    }

    public PeriodeMagang(int id, String nama, String tahunAkademik, String semester,
            LocalDate tanggalMulai, LocalDate tanggalSelesai, boolean aktif) {
        this.id = id;
        this.nama = nama;
        this.tahunAkademik = tahunAkademik;
        this.semester = semester;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.aktif = aktif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTahunAkademik() {
        return tahunAkademik;
    }

    public void setTahunAkademik(String tahunAkademik) {
        this.tahunAkademik = tahunAkademik;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
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

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    @Override
    public String toString() {
        return "PeriodeMagang{id=" + id + ", nama=" + nama + ", tahunAkademik=" + tahunAkademik
                + ", semester=" + semester + ", aktif=" + aktif + '}';
    }
}
