package com.mycompany.verifymagang.model;

/**
 * Entity untuk tabel {@code mahasiswa}.
 *
 * <p>Berelasi satu-ke-satu dengan {@link User} melalui kolom {@code user_id}.</p>
 */
public class Mahasiswa {

    private int id;
    private int userId;
    private String nim;
    private String nama;
    private String jurusan;
    private int angkatan;
    private String noHp;
    private String alamat;

    public Mahasiswa() {
    }

    public Mahasiswa(int id, int userId, String nim, String nama, String jurusan,
            int angkatan, String noHp, String alamat) {
        this.id = id;
        this.userId = userId;
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
        this.angkatan = angkatan;
        this.noHp = noHp;
        this.alamat = alamat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public int getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(int angkatan) {
        this.angkatan = angkatan;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    @Override
    public String toString() {
        return "Mahasiswa{id=" + id + ", nim=" + nim + ", nama=" + nama + ", jurusan=" + jurusan + '}';
    }
}
