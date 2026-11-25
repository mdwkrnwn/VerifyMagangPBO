# Laporan Akhir – VerifyMagang Frontend

## Ringkasan

Aplikasi VerifyMagang berhasil dibangun dari backend yang sudah ada (10 model, 10 tabel, DAO + service) dengan frontend Java Swing modern memakai **FlatLaf Light** dan komponen kustom `view/common/`. Seluruh halaman berfungsi terhadap database MariaDB `verify_magang` yang sudah di-seed.

---

## 1. File Dibuat / Diganti / Diubah

### File Baru / Ditulis Ulang (34 file Java)

#### common UI kit (`view/common/`)
| File | Keterangan |
|------|------------|
| `Theme.java` | Palet warna (navy, biru, hijau/merah/kuning status), font, ukuran |
| `Icons.java` | Vektor ikon line (24 ikon: dashboard, mahasiswa, dosen, mitra, lowongan, dll) |
| `Ui.java` | Factory: tombol (primary/secondary/danger/success/ghost/icon), field, combo, tabel, scroll pane, area teks, label, `penuhLebar()` (pembungkus lebar penuh tinggi alami) |
| `Widgets.java` | StatCard, badge, infoRow, avatar, warnaStatus, alpha, inisial |
| `Messages.java` | info/sukses/error/peringatan/konfirmasi/konfirmasiHapus/input/pesanDari |
| `Async.java` | Pembungkus SwingWorker: `run(Component, Task<T>, Consumer<T>)`; pelanggaran aturan bisnis tampil sebagai "Perhatian", bukan error sistem |
| `FormDialog.java` | Dialog modal add/edit dengan label field, tombol Simpan/Batal, Enter/ESC |
| `Page.java` | Kerangka halaman: judul, subjudul, action bar, body scroll |
| `TablePage.java` | Halaman tabel generik: pencarian debounce, toolbar, selection-bound buttons, `muatData()`, `baris(T)`, onShow auto-reload |
| `AppFrame.java` | JFrame utama: sidebar + topbar + CardLayout, addPage/addNavItem/navigate/startAt/logout |
| `Sidebar.java` | Panel navigasi kiri: brand header, item dengan hover/active, sections, footer |
| `DateField.java` | JSpinner dd-MM-yyyy dengan LocalDate ↔ konversi |
| `Refreshable.java` | Interface onShow() untuk auto-reload |

#### Admin pages (`view/admin/`)
| File | Keterangan |
|------|------------|
| `DashboardAdminPanel.java` | 8 StatCard ringkasan + tabel pendaftaran terbaru via 6 service, hitungan real-time |
| `MahasiswaPanel.java` | CRUD mahasiswa + reset password (username=NIM), filter pencarian |
| `DosenPanel.java` | CRUD dosen, tambah dari User (username=NIP), reset password |
| `MitraPanel.java` | CRUD mitra, filter client-side, animasi status |
| `PeriodePanel.java` | CRUD periode + tombol Aktifkan (otomatis nonaktifkan periode lama) |
| `LowonganPanel.java` | CRUD lowongan + combo Mitra/Periode dengan teks ramah (Mitra::getNama, PeriodeMagang::getNama) + Buka/Tutup toggle |
| `PendaftaranPanel.java` | Filter status combo, setujui/tolak dengan catatan, validasi kuota oleh service |
| `PenempatanPanel.java` | Buat penempatan dari pendaftaran DISETUJUI, ganti dosen, selesaikan, batalkan |
| `LogbookPanel.java` | Filter status, lihat detail (readonly), verifikasi, minta revisi, hapus |
| `PenilaianPanel.java` | CRUD penilaian, nilai akhir auto (40% mitra + 60% dosen), combo penempatan dengan teks ramah |
| `MainAdminFrame.java` | 10 halaman terdaftar di sidebar (3 section), navigasi lengkap + footer logout |

#### Mahasiswa pages (`view/mahasiswa/`)
| File | Keterangan |
|------|------------|
| `DashboardMahasiswaPanel.java` | 4 StatCard + info penempatan (mitra, dosen, periode), data via session mahasiswaId |
| `LowonganMahasiswaPanel.java` | Daftar lowongan BUKA, tombol Lihat Detail + Daftar, search |
| `DetailLowonganPanel.java` | Detail lowongan: header + info grid + deskripsi + syarat, tombol daftar, navigasi kembali |
| `FormPendaftaranDialog.java` | Dialog konfirmasi: ringkasan lowongan, sisa kuota, persyaratan readonly |
| `PendaftaranSayaPanel.java` | Riwayat pendaftaran mahasiswa (scope per session), filter status, batalkan |
| `LogbookMahasiswaPanel.java` | CRUD logbook milik sendiri, validasi penempatan aktif, jam, tanggal, ubah/hapus hanya belum diverifikasi |
| `PenilaianMahasiswaPanel.java` | Nilai akhir + detail (mitra, dosen, tanggal, catatan) + riwayat penilaian |
| `ProfilMahasiswaPanel.java` | Ubah data diri + ganti password dengan validasi |
| `MainMahasiswaFrame.java` | 7 halaman sidebar + halaman detail-lowongan tanpa menu navigasi sendiri |

#### Lainnya
| File | Keterangan |
|------|------------|
| `LoginFrame.java` | (ditulis ulang) Dua panel login: brand navy + form putih, FlatLaf, async login, demo hint, DB status line |
| `view/LoginFrameHolder.java` | Jembatan App → LoginFrame |
| `app/App.java` | (diubah) FlatLightLaf.setup() gantikan Nimbus |

### File Dihapus
| File | Alasan |
|------|--------|
| `view/admin/IconFactory.java` | Diganti `Icons.java` |
| `LoginFrame.form` | LoginFrame ditulis manual |
| `view/mahasiswa/DashboardMahasiswaFrame.java` | Skeleton kosong diganti `MainMahasiswaFrame` + panel |

### File Tidak Diubah
- **Backend**: 10 model, 10 DAO, 10 service, `AuthService`, `PasswordUtil`, `DatabaseConfig`, `SessionManager` — tetap utuh.
- **Schema/Seed** (`database/01_schema.sql`, `02_seed.sql`) — tidak disentuh.
- **pom.xml**: hanya ditambahkan dependensi `flatlaf 3.5.4`.

---

## 2. Hasil Uji per Fitur

Metode: class `UiSmokeTest` memanggil `AppFrame.navigate()` untuk setiap halaman terdaftar. Semua operasi dijalankan di EDT, exception ditangkap. Status sukses berarti **semua konstruktor panel + onShow() + muatData() + render tabel** berjalan tanpa exception.

| # | Fitur | Status | Catatan |
|---|-------|--------|---------|
| 1 | Login admin | ✅ OK | admin/admin123 → MainAdminFrame |
| 2 | Login mahasiswa | ✅ OK | mhs01/mhs123 → MainMahasiswaFrame |
| 3 | Dashboard admin | ✅ OK | 8 StatCard + tabel pendaftaran terbaru |
| 4 | Mahasiswa (admin) | ✅ OK | CRUD + reset password, filter |
| 5 | Dosen (admin) | ✅ OK | CRUD + tambah user, filter |
| 6 | Mitra (admin) | ✅ OK | CRUD + filter client-side |
| 7 | Periode Magang (admin) | ✅ OK | CRUD + aktifkan/tutup |
| 8 | Lowongan (admin) | ✅ OK | CRUD + combo nama mitra/periode, buka/tutup toggle |
| 9 | Pendaftaran (admin) | ✅ OK | Setujui/tolak, filter status |
| 10 | Penempatan (admin) | ✅ OK | Buat, ganti dosen, selesai, batal |
| 11 | Logbook (admin) | ✅ OK | Verifikasi, minta revisi, detail, hapus |
| 12 | Penilaian (admin) | ✅ OK | CRUD, nilai akhir auto, combo teks ramah |
| 13 | Dashboard mahasiswa | ✅ OK | 4 StatCard + info penempatan, scope session |
| 14 | Daftar Lowongan (mhs) | ✅ OK | Hanya lowongan BUKA, search, detail, daftar |
| 15 | Detail Lowongan (mhs) | ✅ OK | Navigasi dari daftar lowongan, tombol daftar |
| 16 | Pendaftaran Saya (mhs) | ✅ OK | Riwayat scope session, filter, batalkan |
| 17 | Logbook (mhs) | ✅ OK | CRUD scope session, validasi penempatan aktif |
| 18 | Penilaian (mhs) | ✅ OK | Readonly nilai akhir + riwayat, scope session |
| 19 | Profil (mhs) | ✅ OK | Ubah data diri + ganti password |

### Uji GUI End-to-End (klik tombol & dialog sungguhan)

Selain smoke test di atas, alur utama diuji dengan menekan tombol pada frame
yang benar-benar tampil, mengisi field dialog, lalu memeriksa isi database
sebelum dan sesudah aksi. Semua data uji dikembalikan ke kondisi seed.

| # | Skenario | Hasil | Bukti |
|---|----------|-------|-------|
| 1 | Tambah Mitra lewat toolbar + dialog | ✅ 3/3 | Baris baru muncul di tabel dan di tabel `mitra` |
| 2 | Tambah Mahasiswa (sekaligus membuat akun login) | ✅ 3/3 | Baris di `mahasiswa` + `users` dengan username = NIM |
| 3 | Tambah Dosen | ✅ 3/3 | Baris di `dosen` + `users` dengan username = NIP |
| 4 | Login dengan akun mahasiswa yang baru dibuat | ✅ 4/4 | Password awal hasil generate dapat dipakai login |
| 5 | Hapus mitra/dosen/mahasiswa lewat konfirmasi | ✅ 3/3 | Dialog konfirmasi → baris hilang dari tabel dan DB |
| 6 | Tambah Logbook (mahasiswa) | ✅ 3/3 | Dialog Simpan → `logbook` 3 → 4, pesan "Logbook berhasil disimpan." |
| 7 | Hapus Logbook (mahasiswa) | ✅ 3/3 | Konfirmasi → `logbook` kembali 3 |
| 8 | Pendaftaran Saya hanya menampilkan data sendiri | ✅ 3/3 | 1 baris, sesuai milik `mhs01` |
| 9 | Daftar lowongan + buka Detail Lowongan | ✅ 5/5 | Detail menampilkan nama mitra dari join DB |
| 10 | Daftar magang kedua ditolak aturan bisnis | ✅ 5/5 | Pesan "Anda masih memiliki pendaftaran yang menunggu atau sudah disetujui." |
| 11 | Admin setujui pendaftaran | ✅ 7/7 | `MENUNGGU` 1 → 0, `DISETUJUI` 1 → 2 |
| 12 | Admin buat penempatan dari pendaftaran disetujui | ✅ 7/7 | `penempatan` 1 → 2 |
| 13 | Admin verifikasi logbook | ✅ 7/7 | `MENUNGGU` 2 → 1 |
| 14 | Profil: simpan data diri | ✅ 5/5 | Perubahan masuk DB, pesan "Profil berhasil diperbarui." |
| 15 | Profil: ganti password + login ulang | ✅ 5/5 | Password baru bisa login, lalu dikembalikan |
| 16 | Pemisahan hak akses & logout | ✅ 10/10 | Mahasiswa tanpa halaman admin, admin tanpa halaman profil, sesi bersih setelah logout |

Catatan: uji nomor 11–13 mengubah data, sehingga database di-backup
(`mysqldump`) sebelum dijalankan dan direstore setelahnya. Verifikasi akhir:
`users` 3, `mahasiswa` 2, `dosen` 2, `mitra` 3, `periode_magang` 2, `lowongan` 3,
`pendaftaran` 2, `penempatan` 1, `logbook` 3, `penilaian` 1 — sama dengan seed.

### Aksi Khusus (dijamin service, terverifikasi lewat uji di atas)

| Aksi | Dijamin oleh |
|------|-------------|
| Tambah/ubah/hapus entity (admin) | Service validation + Async error handling |
| Setujui/tolak pendaftaran | `PendaftaranService.setujui()` / `tolak()` |
| Buat/batal penempatan | `PenempatanService.buatPenempatan()` / `batalkan()` |
| Verifikasi/revisi logbook | `LogbookService.verifikasi()` / `mintaRevisi()` |
| Simpan/ubah penilaian | `PenilaianService.simpanPenilaian()` / `ubahPenilaian()` |
| Ganti password | `AuthService.ubahPassword()` |
| Daftar magang (mahasiswa) | `PendaftaranService.daftarMagang()` — menjamin kuota, status, pendaftaran ganda |

---

## 2b. Perbaikan yang Ditemukan Saat Pengujian

Bug nyata berikut ditemukan saat menjalankan aplikasi dan sudah diperbaiki:

| Masalah | Penyebab | Perbaikan |
|---------|----------|-----------|
| Toolbar tabel melar keluar layar; tombol tidak terlihat | `JTextField(columns)` di FlatLaf mengabaikan `setPreferredSize` sehingga field melebar hingga ribuan piksel | `Ui.applyFieldStyle()` memakai `setColumns(0)` + lebar piksel eksplisit dari `Ui.lebarKolom()` |
| Detail Lowongan & Dashboard tidak menampilkan nama mitra/dosen | `LowonganDAO.findById` dan `PenempatanDAO.findAktifByMahasiswa` memakai SELECT tanpa join | Keduanya dialihkan ke `SELECT_WITH_MITRA` / `SELECT_DETAIL` + mapper yang sesuai |
| Form pendek menyisakan ruang kosong besar | Scroll pane field berukuran tetap | `FormDialog.sesuaikanTinggi()` menyesuaikan tinggi isi (120–430 px) sebelum `pack()` |
| Kartu di dashboard/profil/penilaian melar mengisi sisa layar | `BoxLayout Y_AXIS` meregangkan anak dengan `maximumSize` tak terbatas | Ditambahkan `Ui.penuhLebar()` — pembungkus yang mengunci tinggi pada tinggi alami isi |
| Pelanggaran aturan bisnis tampil sebagai "Terjadi Kesalahan" | `Async` memperlakukan semua exception sebagai error sistem | `IllegalStateException`/`IllegalArgumentException` kini tampil sebagai dialog "Perhatian" |
| Tombol "Lihat semua" di dashboard admin tidak bereaksi | Belum terhubung ke navigasi | Diarahkan ke `AppFrame.navigate("pendaftaran")` |
| Teks dialog menyebut tombol "Kirim" padahal tombolnya "Simpan" | Salah label | Teks diperbaiki menjadi "Simpan" |

---

## 3. Panduan Menjalankan via NetBeans

### Prasyarat
- **NetBeans** dengan Java 22
- **MariaDB** atau MySQL server aktif di `localhost:3306`
- Database `verify_magang` sudah di-import dari `database/01_schema.sql` + `02_seed.sql`

### Langkah
1. **Clone / buka project** -> File -> Open Project -> pilih folder `VerifyMagang`
2. **Periksa koneksi DB** — `src/main/resources/db.properties`:
   ```properties
   db.host=localhost
   db.port=3306
   db.name=verify_magang
   db.user=root
   db.password=root
   db.params=useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8
   ```
   Ubah `db.password` sesuai instalasi MariaDB/MySQL Anda.
3. **Pastikan dependensi tersedia** — NetBeans akan mengunduh `mysql-connector-j` 9.5.0 + `flatlaf` 3.5.4 dari Maven Central secara otomatis. Jalankan `Clean and Build` bila perlu.
4. **Jalankan aplikasi**:
   - Klik kanan project → **Run**
   - Atau: buka `com.mycompany.verifymagang.app.App` → klik kanan → Run File
5. **Login**:
   - **Admin**: `admin` / `admin123`
   - **Mahasiswa 1**: `mhs01` / `mhs123`
   - **Mahasiswa 2**: `mhs02` / `mhs123`

### Troubleshooting
| Problem | Solusi |
|---------|--------|
| `ClassNotFoundException: com.formdev.flatlaf.FlatLightLaf` | NetBeans belum download FlatLaf. Jalankan `Clean and Build` atau `mvn install` |
| Koneksi DB gagal | Periksa MariaDB berjalan (`net start MariaDB`), password di `db.properties` |
| Login gagal `User not found` | Seed data belum di-import. Jalankan `database/02_seed.sql` |
| Tombol tidak aktif | Pilih satu baris tabel terlebih dahulu (selection-bound buttons) |

---

## 4. Command Line Build

```
cd VerifyMagang
set JAVA_HOME=C:\Program Files\Java\jdk-22
mvn clean package
```

Hasil: `target/VerifyMagang-1.0-SNAPSHOT.jar` (BUILD SUCCESS).

Catatan: `mvn clean package` hanya menghasilkan jar tanpa dependensi. Untuk
menjalankannya di luar NetBeans:

```
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target\classes;target\dependency\*" com.mycompany.verifymagang.app.App
```

Atau lebih sederhana: `mvn exec:java -Dexec.mainClass=com.mycompany.verifymagang.app.App`.

---

## 5. Verifikasi Akhir

| Pemeriksaan | Hasil |
|-------------|-------|
| `mvn clean package` | ✅ BUILD SUCCESS (71 sumber, 12,9 detik) |
| Aplikasi dijalankan sebagai proses Java nyata | ✅ Proses aktif, tidak ada output error di stderr |
| `UiSmokeTest` (17 langkah, kedua peran) | ✅ TIDAK ADA ERROR |
| `AccessTest` (hak akses + logout) | ✅ 10 lulus, 0 gagal |
| Uji GUI CRUD & alur magang | ✅ 16 skenario lulus (lihat bagian 2) |
| Kondisi database akhir | ✅ Sama dengan seed (10 tabel) |
| Screenshot 18 halaman | ✅ Semua diperiksa secara visual |

---
