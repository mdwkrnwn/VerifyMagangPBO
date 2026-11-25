package com.mycompany.verifymagang.model;

/**
 * Entity untuk tabel {@code dosen}.
 *
 * <p>Dosen berperan sebagai pembimbing lapangan akademik pada proses
 * {@link PenempatanMagang}. Relasi ke {@link User} bersifat opsional
 * ({@code user_id} boleh {@code NULL}) karena tidak semua dosen perlu login.</p>
 */
public class Dosen {

    private int id;
    private Integer userId;
    private String nip;
    private String nama;
    private String bidangKeahlian;
    private String noHp;
    private String email;

    public Dosen() {
    }

    public Dosen(int id, Integer userId, String nip, String nama, String bidangKeahlian,
            String noHp, String email) {
        this.id = id;
        this.userId = userId;
        this.nip = nip;
        this.nama = nama;
        this.bidangKeahlian = bidangKeahlian;
        this.noHp = noHp;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getBidangKeahlian() {
        return bidangKeahlian;
    }

    public void setBidangKeahlian(String bidangKeahlian) {
        this.bidangKeahlian = bidangKeahlian;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Dosen{id=" + id + ", nip=" + nip + ", nama=" + nama + '}';
    }
}
