# Panduan Integrasi GUI → Service

Panduan singkat menghubungkan komponen NetBeans GUI Builder ke lapisan
`service`. Semua contoh ditaruh di dalam *event handler* hasil klik-kanan
komponen → **Events** → **Action**.

Pola dasarnya selalu sama:

```java
private final PendaftaranService pendaftaranService = new PendaftaranService();

private void btnMuatActionPerformed(java.awt.event.ActionEvent evt) {
    try {
        List<PendaftaranMagang> data = pendaftaranService.daftarSemua();
        // isi JTable milik Anda dengan data
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    } catch (IllegalStateException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Perhatian",
                JOptionPane.WARNING_MESSAGE);
    }
}
```

Semua service melempar dua jenis exception:

| Exception | Arti | Saran penanganan |
|---|---|---|
| `SQLException` | gangguan database | dialog error, log |
| `IllegalStateException` / `IllegalArgumentException` | aturan bisnis / validasi | dialog peringatan |

---

## Login → `LoginFrame`

```java
AuthService authService = new AuthService();
AuthService.LoginResult hasil = authService.login(
        txtUsername.getText(),
        new String(txtPassword.getPassword()));

if (!hasil.isBerhasil()) {
    JOptionPane.showMessageDialog(this, hasil.getPesan());
    return;
}

SessionManager.login(hasil.getUser(), hasil.getMahasiswaId(), hasil.getDosenId());

if (hasil.isAdmin()) {
    new com.mycompany.verifymagang.view.admin.MainAdminFrame().setVisible(true);
} else {
    new com.mycompany.verifymagang.view.mahasiswa.DashboardMahasiswaFrame().setVisible(true);
}
dispose();
```

---

## Navigasi dashboard

Tombol menu admin cukup berpindah kartu panel:

```java
// asumsi CardLayout bernama cardLayout pada panel konten
cardLayout.show(panelKonten, "mahasiswa");
```

---

## `MahasiswaPanel`

| Aksi | Pemanggilan |
|---|---|
| muat tabel | `new MahasiswaService().daftarSemua()` |
| cari | `new MahasiswaService().cari(txtCari.getText())` |
| tambah | `new MahasiswaService().tambahMahasiswa(mhs, new String(txtPass.getPassword()))` |
| ubah | `new MahasiswaService().ubahMahasiswa(mhs)` |
| hapus | `new MahasiswaService().hapusMahasiswa(id)` |
| reset password | `new MahasiswaService().resetPassword(id, "passbaru")` |

Saat menambah, username akun otomatis memakai NIM. Jadi mahasiswa login dengan
NIM + password yang Anda isi.

---

## `DosenPanel`

| Aksi | Pemanggilan |
|---|---|
| muat tabel | `new DosenService().daftarSemua()` |
| cari | `new DosenService().cari(keyword)` |
| tambah | `new DosenService().tambahDosen(dosen)` |
| ubah | `new DosenService().ubahDosen(dosen)` |
| hapus | `new DosenService().hapusDosen(id)` |

---

## `MitraPanel`

| Aksi | Pemanggilan |
|---|---|
| muat tabel | `new LowonganService().daftarMitra()` |
| tambah | `new LowonganService().tambahMitra(mitra)` |
| ubah | `new LowonganService().ubahMitra(mitra)` |
| hapus | `new LowonganService().hapusMitra(id)` |

---

## `LowonganPanel`

| Aksi | Pemanggilan |
|---|---|
| muat tabel | `new LowonganService().daftarSemuaLowongan()` |
| isi combo mitra | `new LowonganService().daftarMitraAktif()` |
| isi combo periode | `new PeriodeMagangService().daftarSemua()` |
| tambah | `new LowonganService().tambahLowongan(lowongan)` |
| tutup | `new LowonganService().tutupLowongan(id)` |
| hapus | `new LowonganService().hapusLowongan(id)` |

`LowonganMagang.getNamaMitra()` sudah terisi hasil join, jadi tabel bisa langsung
menampilkan nama mitra tanpa query tambahan.

---

## `PendaftaranPanel`

| Aksi | Pemanggilan |
|---|---|
| muat tabel | `new PendaftaranService().daftarSemua()` |
| filter menunggu | `new PendaftaranService().daftarMenunggu()` |
| setujui | `new PendaftaranService().setujui(id, txtCatatan.getText())` |
| tolak | `new PendaftaranService().tolak(id, txtAlasan.getText())` |

Baris hasil `daftarSemua()` sudah membawa `getNamaMahasiswa()`, `getNim()`, dan
`getJudulLowongan()`.

---

## `PenempatanPanel`

| Aksi | Pemanggilan |
|---|---|
| muat tabel | `new PenempatanService().daftarSemua()` |
| buat penempatan | `new PenempatanService().buatPenempatan(pendaftaranId, dosenId, mulai, selesai)` |
| ganti pembimbing | `new PenempatanService().gantiDosen(penempatanId, dosenBaruId)` |
| selesaikan | `new PenempatanService().selesaikan(id)` |

`mitra_id` penempatan diambil otomatis dari lowongan asal pendaftaran, jadi Anda
tidak perlu mengisinya di form.

---

## Sisi mahasiswa

```java
int mahasiswaId = SessionManager.getMahasiswaId();
```

| Kebutuhan | Pemanggilan |
|---|---|
| daftar lowongan tersedia | `new LowonganService().daftarLowonganBuka()` |
| daftar magang | `new PendaftaranService().daftarMagang(mahasiswaId, lowonganId)` |
| riwayat pendaftaran | `new PendaftaranService().daftarByMahasiswa(mahasiswaId)` |
| penempatan aktif | `new PenempatanService().penempatanAktifMahasiswa(mahasiswaId)` |
| isi logbook | `new LogbookService().isiLogbook(logbook)` |
| logbook saya | `new LogbookService().daftarByMahasiswa(mahasiswaId)` |
| nilai saya | `new PenilaianService().daftarByMahasiswa(mahasiswaId)` |

Contoh isi logbook:

```java
PenempatanMagang penempatan = new PenempatanService()
        .penempatanAktifMahasiswa(mahasiswaId)
        .orElseThrow(() -> new IllegalStateException("Belum ada penempatan aktif."));

LogbookHarian lb = new LogbookHarian();
lb.setPenempatanId(penempatan.getId());
lb.setTanggal(LocalDate.parse(txtTanggal.getText()));
lb.setJamMulai(LocalTime.parse(txtJamMulai.getText()));
lb.setJamSelesai(LocalTime.parse(txtJamSelesai.getText()));
lb.setKegiatan(txtKegiatan.getText());
lb.setKendala(txtKendala.getText());

new LogbookService().isiLogbook(lb);
```

---

## Logout

```java
SessionManager.logout();
new com.mycompany.verifymagang.LoginFrame().setVisible(true);
dispose();
```
