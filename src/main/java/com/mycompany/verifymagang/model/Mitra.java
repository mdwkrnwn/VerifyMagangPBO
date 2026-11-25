package com.mycompany.verifymagang.model;

/**
 * Entity untuk tabel {@code mitra}.
 *
 * <p>Mitra adalah instansi/perusahaan tempat mahasiswa melaksanakan magang.
 * Satu mitra dapat memiliki banyak {@link LowonganMagang}.</p>
 */
public class Mitra {

    private int id;
    private String nama;
    private String bidangUsaha;
    private String alamat;
    private String kota;
    private String kontakPerson;
    private String noHp;
    private String email;
    private boolean aktif;

    public Mitra() {
    }

    public Mitra(int id, String nama, String bidangUsaha, String alamat, String kota,
            String kontakPerson, String noHp, String email, boolean aktif) {
        this.id = id;
        this.nama = nama;
        this.bidangUsaha = bidangUsaha;
        this.alamat = alamat;
        this.kota = kota;
        this.kontakPerson = kontakPerson;
        this.noHp = noHp;
        this.email = email;
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

    public String getBidangUsaha() {
        return bidangUsaha;
    }

    public void setBidangUsaha(String bidangUsaha) {
        this.bidangUsaha = bidangUsaha;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getKontakPerson() {
        return kontakPerson;
    }

    public void setKontakPerson(String kontakPerson) {
        this.kontakPerson = kontakPerson;
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

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    @Override
    public String toString() {
        return "Mitra{id=" + id + ", nama=" + nama + ", kota=" + kota + ", aktif=" + aktif + '}';
    }
}
