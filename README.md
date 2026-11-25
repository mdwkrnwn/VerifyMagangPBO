# VerifyMagang

Aplikasi verifikasi magang (PBO) berbasis **Java Swing + NetBeans GUI Builder + Maven**.

Seluruh tampilan dirancang manual melalui tab **Design** NetBeans. Repo ini hanya
menyediakan struktur kode: model, DAO, config, service, dan kerangka view.

## Struktur package

```
com.mycompany.verifymagang
├── app/         App (bootstrap), SessionManager (user yang sedang login)
├── config/      DatabaseConfig (koneksi JDBC)
├── model/       entity 10 tabel
├── dao/         operasi CRUD database
├── service/     logika bisnis + autentikasi
└── view/        kerangka form (silakan desain di NetBeans)
```

## Pemetaan tabel → class

| Tabel            | Model              | DAO                 | Service              |
|------------------|--------------------|---------------------|----------------------|
| users            | `User`             | `UserDAO`           | `AuthService`        |
| mahasiswa        | `Mahasiswa`        | `MahasiswaDAO`      | `MahasiswaService`   |
| dosen            | `Dosen`            | `DosenDAO`          | `DosenService`       |
| mitra            | `Mitra`            | `MitraDAO`          | `LowonganService`    |
| periode_magang   | `PeriodeMagang`    | `PeriodeMagangDAO`  | `PeriodeMagangService`|
| lowongan         | `LowonganMagang`   | `LowonganDAO`       | `LowonganService`    |
| pendaftaran      | `PendaftaranMagang`| `PendaftaranDAO`    | `PendaftaranService` |
| penempatan       | `PenempatanMagang` | `PenempatanDAO`     | `PenempatanService`  |
| logbook          | `LogbookHarian`    | `LogbookDAO`        | `LogbookService`     |
| penilaian        | `PenilaianMagang`  | `PenilaianDAO`      | `PenilaianService`   |

## Setup database

1. Jalankan MySQL/MariaDB.
2. Import skema lalu seed:

   ```bash
   mysql -u root -p < database/01_schema.sql
   mysql -u root -p < database/02_seed.sql
   ```

3. Sesuaikan koneksi di `src/main/resources/db.properties`:

   ```properties
   db.host=localhost
   db.port=3306
   db.name=verify_magang
   db.user=root
   db.password=
   ```

   Nilai bisa ditimpa tanpa mengubah file, lewat environment variable
   `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, atau system property
   `-Ddb.password=...`.

> **Catatan port:** MariaDB dan MySQL sama-sama memakai 3306. Pastikan hanya satu
> server yang berjalan, atau ubah `db.port` sesuai port server yang dipakai.

### Akun contoh

| Username | Password   | Role      |
|----------|------------|-----------|
| `admin`  | `admin123` | ADMIN     |
| `mhs01`  | `mhs123`   | MAHASISWA |
| `mhs02`  | `mhs123`   | MAHASISWA |

Password disimpan sebagai `sha256$salt$hash` (lihat `PasswordUtil`).

## Menghubungkan GUI ke logika

Di dalam event handler NetBeans (misalnya tombol Login), panggil service:

```java
AuthService authService = new AuthService();
AuthService.LoginResult hasil = authService.login(txtUsername.getText(),
        new String(txtPassword.getPassword()));

if (!hasil.isBerhasil()) {
    JOptionPane.showMessageDialog(this, hasil.getPesan());
    return;
}

SessionManager.login(hasil.getUser(), hasil.getMahasiswaId(), hasil.getDosenId());

if (hasil.isAdmin()) {
    new MainAdminFrame().setVisible(true);
} else {
    new DashboardMahasiswaFrame().setVisible(true);
}
this.dispose();
```

Contoh mengambil data untuk tabel:

```java
// Admin: semua pendaftaran (sudah berisi nama mahasiswa & judul lowongan)
List<PendaftaranMagang> data = new PendaftaranService().daftarSemua();
tabelPendaftaran.setModel(new DefaultTableModel(...));

// Mahasiswa: lowongan yang bisa didaftar
int mahasiswaId = SessionManager.getMahasiswaId();
List<LowonganMagang> lowongan = new LowonganService().daftarLowonganBuka();
```

Semua method service melempar `SQLException` (masalah database) atau
`IllegalStateException`/`IllegalArgumentException` (pelanggaran aturan bisnis),
jadi tangani di view dengan `try-catch` dan tampilkan `ex.getMessage()`.

## Build

```bash
mvn clean package
```

Menjalankan aplikasi: buka project di NetBeans lalu **Run Project** (F6).

## Catatan

- File `.form` dan seluruh isi desain GUI **tidak** diubah oleh struktur ini.
- `model/LogbookMingguan.java` sengaja dibiarkan (tidak dipakai, tidak ada tabelnya).
- `view/pembimbing/` tidak dipakai karena role hanya Admin dan Mahasiswa.
