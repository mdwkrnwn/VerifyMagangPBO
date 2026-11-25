-- =====================================================================
-- VerifyMagang - Data Contoh (seed)
-- Password contoh (format sha256$salt$hash, dihitung dengan PasswordUtil):
--   admin / admin123
--   mhs01 / mhs123
--   mhs02 / mhs123
-- =====================================================================
USE verify_magang;

-- ---------------------------------------------------------------------
-- users
-- ---------------------------------------------------------------------
INSERT INTO users (id, username, password, role, nama_lengkap, email, aktif) VALUES
(1, 'admin', 'sha256$a1b2c3d4e5f60718293a4b5c6d7e8f90$efc3a8b5f9564bd0b4fb553fe28c1d8f098b0dfa48a751f432b4cf6b3f0be69e',
 'ADMIN', 'Administrator Magang', 'admin@verify.ac.id', TRUE),
(2, 'mhs01', 'sha256$0f1e2d3c4b5a69788796a5b4c3d2e1f0$397a29a4ab15e669f1a70f10deef316984c48c38aa62c91a2891a1d5ea888855',
 'MAHASISWA', 'Rizky Pratama', 'rizky@student.ac.id', TRUE),
(3, 'mhs02', 'sha256$0f1e2d3c4b5a69788796a5b4c3d2e1f0$397a29a4ab15e669f1a70f10deef316984c48c38aa62c91a2891a1d5ea888855',
 'MAHASISWA', 'Siti Nurhaliza', 'siti@student.ac.id', TRUE);

-- ---------------------------------------------------------------------
-- mahasiswa
-- ---------------------------------------------------------------------
INSERT INTO mahasiswa (id, user_id, nim, nama, jurusan, angkatan, no_hp, alamat) VALUES
(1, 2, '2021001001', 'Rizky Pratama', 'Teknik Informatika', 2021, '081234567890', 'Jl. Merdeka No. 10, Bandung'),
(2, 3, '2021001002', 'Siti Nurhaliza', 'Sistem Informasi', 2021, '081298765432', 'Jl. Sudirman No. 25, Bandung');

-- ---------------------------------------------------------------------
-- dosen
-- ---------------------------------------------------------------------
INSERT INTO dosen (id, user_id, nip, nama, bidang_keahlian, no_hp, email) VALUES
(1, NULL, '198501012010011001', 'Dr. Budi Santoso, M.Kom.', 'Rekayasa Perangkat Lunak', '081311112222', 'budi@verify.ac.id'),
(2, NULL, '199002022015011002', 'Ani Wijaya, S.Kom., M.T.', 'Basis Data', '081333334444', 'ani@verify.ac.id');

-- ---------------------------------------------------------------------
-- mitra
-- ---------------------------------------------------------------------
INSERT INTO mitra (id, nama, bidang_usaha, alamat, kota, kontak_person, no_hp, email, aktif) VALUES
(1, 'PT Digital Nusantara', 'Pengembangan Perangkat Lunak', 'Jl. Asia Afrika No. 8', 'Bandung', 'Hendra Gunawan', '0224234567', 'hrd@digitalnusantara.co.id', TRUE),
(2, 'CV Data Cipta Solusi', 'Konsultan Data & Analitik', 'Jl. Dago No. 120', 'Bandung', 'Maya Lestari', '0224238899', 'info@datacipta.co.id', TRUE),
(3, 'Bank Mitra Sejahtera', 'Perbankan', 'Jl. Braga No. 45', 'Bandung', 'Agus Salim', '0224200111', 'rekrutmen@bankmitra.co.id', TRUE);

-- ---------------------------------------------------------------------
-- periode_magang
-- ---------------------------------------------------------------------
INSERT INTO periode_magang (id, nama, tahun_akademik, semester, tanggal_mulai, tanggal_selesai, aktif) VALUES
(1, 'Magang Ganjil 2025/2026', '2025/2026', 'GANJIL', '2025-09-01', '2026-01-31', FALSE),
(2, 'Magang Genap 2025/2026', '2025/2026', 'GENAP', '2026-02-01', '2026-06-30', TRUE);

-- ---------------------------------------------------------------------
-- lowongan
-- ---------------------------------------------------------------------
INSERT INTO lowongan (id, mitra_id, periode_id, judul, deskripsi, persyaratan, kuota, tunjangan, lokasi, status, tanggal_buka, tanggal_tutup) VALUES
(1, 1, 2, 'Backend Developer Intern',
 'Mengembangkan REST API untuk produk internal perusahaan menggunakan Java Spring Boot.',
 'Mahasiswa semester 5 ke atas, menguasai Java dan SQL, mampu bekerja dalam tim.',
 3, 2500000.00, 'Bandung', 'BUKA', '2026-01-05', '2026-02-20'),
(2, 2, 2, 'Data Analyst Intern',
 'Membantu tim analitik menyusun laporan dan dashboard menggunakan SQL dan Python.',
 'Memahami SQL, dasar statistik, teliti terhadap detail.',
 2, 2000000.00, 'Bandung', 'BUKA', '2026-01-10', '2026-02-25'),
(3, 3, 2, 'IT Support Intern',
 'Mendukung operasional TI cabang dan membantu troubleshooting perangkat.',
 'Mahasiswa D3/S1 Teknik Informatika, komunikatif.',
 1, 1750000.00, 'Bandung', 'BUKA', '2026-01-12', '2026-02-28');

-- ---------------------------------------------------------------------
-- pendaftaran
-- ---------------------------------------------------------------------
INSERT INTO pendaftaran (id, mahasiswa_id, lowongan_id, tanggal_daftar, status, catatan) VALUES
(1, 1, 1, '2026-01-08', 'DISETUJUI', 'Berkas lengkap, silakan lanjut ke tahap penempatan.'),
(2, 2, 2, '2026-01-15', 'MENUNGGU', NULL);

-- ---------------------------------------------------------------------
-- penempatan
-- ---------------------------------------------------------------------
INSERT INTO penempatan (id, pendaftaran_id, dosen_id, mitra_id, tanggal_mulai, tanggal_selesai, status) VALUES
(1, 1, 1, 1, '2026-02-02', '2026-06-30', 'AKTIF');

-- ---------------------------------------------------------------------
-- logbook
-- ---------------------------------------------------------------------
INSERT INTO logbook (id, penempatan_id, tanggal, jam_mulai, jam_selesai, kegiatan, kendala, status_verifikasi, catatan_dosen) VALUES
(1, 1, '2026-02-02', '08:00:00', '17:00:00', 'Perkenalan dengan tim backend dan penyiapan lingkungan pengembangan.',
 NULL, 'DIVERIFIKASI', 'Bagus, lanjutkan dokumentasi harian.'),
(2, 1, '2026-02-03', '08:00:00', '17:00:00', 'Membaca dokumentasi API dan mempelajari arsitektur layanan.',
 'Belum paham alur autentikasi.', 'MENUNGGU', NULL),
(3, 1, '2026-02-04', '08:30:00', '16:30:00', 'Membuat endpoint pertama untuk modul profil pengguna.',
 NULL, 'MENUNGGU', NULL);

-- ---------------------------------------------------------------------
-- penilaian
-- ---------------------------------------------------------------------
INSERT INTO penilaian (id, penempatan_id, dosen_id, nilai_mitra, nilai_dosen, nilai_akhir, catatan, tanggal_penilaian) VALUES
(1, 1, 1, 88.00, 85.00, 86.20, 'Mahasiswa disiplin dan mampu menyelesaikan tugas dengan baik.', '2026-06-25');
