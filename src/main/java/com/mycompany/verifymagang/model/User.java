package com.mycompany.verifymagang.model;

import java.time.LocalDateTime;

/**
 * Entity untuk tabel {@code users}.
 *
 * <p>Kolom {@code role} hanya bernilai {@link #ROLE_ADMIN} atau
 * {@link #ROLE_MAHASISWA}.</p>
 */
public class User {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MAHASISWA = "MAHASISWA";

    private int id;
    private String username;
    private String password;
    private String role;
    private String namaLengkap;
    private String email;
    private boolean aktif;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(int id, String username, String password, String role, String namaLengkap,
            String email, boolean aktif, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.aktif = aktif;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** @return {@code true} bila user ini berperan sebagai admin. */
    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }

    /** @return {@code true} bila user ini berperan sebagai mahasiswa. */
    public boolean isMahasiswa() {
        return ROLE_MAHASISWA.equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username=" + username + ", role=" + role
                + ", namaLengkap=" + namaLengkap + ", aktif=" + aktif + '}';
    }
}
