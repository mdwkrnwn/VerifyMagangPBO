-- =====================================================================
-- VerifyMagang - Skema Database
-- 10 tabel: users, mahasiswa, dosen, mitra, lowongan, pendaftaran,
--           penempatan, logbook, penilaian, periode_magang
-- Target  : MySQL 8 / MariaDB 10.5+
-- Engine  : InnoDB, charset utf8mb4
-- =====================================================================

DROP DATABASE IF EXISTS verify_magang;
CREATE DATABASE verify_magang
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE verify_magang;

-- ---------------------------------------------------------------------
-- 1. users
--    Menyimpan akun login. Role hanya ADMIN atau MAHASISWA.
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password      VARCHAR(255) NOT NULL COMMENT 'format: sha256$salt$hash',
    role          ENUM('ADMIN','MAHASISWA') NOT NULL,
    nama_lengkap  VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NULL,
    aktif         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 2. mahasiswa
--    Relasi 1:1 ke users.
-- ---------------------------------------------------------------------
CREATE TABLE mahasiswa (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   INT NOT NULL,
    nim       VARCHAR(20)  NOT NULL,
    nama      VARCHAR(100) NOT NULL,
    jurusan   VARCHAR(100) NULL,
    angkatan  INT NULL,
    no_hp     VARCHAR(20) NULL,
    alamat    TEXT NULL,
    CONSTRAINT uk_mahasiswa_nim     UNIQUE (nim),
    CONSTRAINT uk_mahasiswa_user    UNIQUE (user_id),
    CONSTRAINT fk_mahasiswa_user    FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 3. dosen
--    Relasi 1:1 opsional ke users (dosen belum tentu punya akun login).
-- ---------------------------------------------------------------------
CREATE TABLE dosen (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NULL,
    nip              VARCHAR(30)  NOT NULL,
    nama             VARCHAR(100) NOT NULL,
    bidang_keahlian  VARCHAR(100) NULL,
    no_hp            VARCHAR(20) NULL,
    email            VARCHAR(100) NULL,
    CONSTRAINT uk_dosen_nip  UNIQUE (nip),
    CONSTRAINT uk_dosen_user UNIQUE (user_id),
    CONSTRAINT fk_dosen_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 4. mitra
--    Instansi/perusahaan tempat magang.
-- ---------------------------------------------------------------------
CREATE TABLE mitra (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nama           VARCHAR(150) NOT NULL,
    bidang_usaha   VARCHAR(100) NULL,
    alamat         TEXT NULL,
    kota           VARCHAR(100) NULL,
    kontak_person  VARCHAR(100) NULL,
    no_hp          VARCHAR(20) NULL,
    email          VARCHAR(100) NULL,
    aktif          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 5. periode_magang
--    Rentang waktu pembukaan magang.
-- ---------------------------------------------------------------------
CREATE TABLE periode_magang (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nama             VARCHAR(100) NOT NULL,
    tahun_akademik   VARCHAR(20)  NULL,
    semester         ENUM('GANJIL','GENAP','ANTARA') NULL,
    tanggal_mulai    DATE NULL,
    tanggal_selesai  DATE NULL,
    aktif            BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_periode_tanggal CHECK (
        tanggal_selesai IS NULL OR tanggal_mulai IS NULL OR tanggal_selesai >= tanggal_mulai
    )
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 6. lowongan
--    N:1 ke mitra, N:1 ke periode_magang.
-- ---------------------------------------------------------------------
CREATE TABLE lowongan (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    mitra_id       INT NOT NULL,
    periode_id     INT NULL,
    judul          VARCHAR(150) NOT NULL,
    deskripsi      TEXT NULL,
    persyaratan    TEXT NULL,
    kuota          INT NOT NULL DEFAULT 1,
    tunjangan      DECIMAL(12,2) NULL,
    lokasi         VARCHAR(150) NULL,
    status         ENUM('BUKA','TUTUP') NOT NULL DEFAULT 'BUKA',
    tanggal_buka   DATE NULL,
    tanggal_tutup  DATE NULL,
    CONSTRAINT ck_lowongan_kuota CHECK (kuota >= 0),
    CONSTRAINT fk_lowongan_mitra FOREIGN KEY (mitra_id) REFERENCES mitra (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lowongan_periode FOREIGN KEY (periode_id) REFERENCES periode_magang (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_lowongan_status (status),
    INDEX idx_lowongan_mitra (mitra_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 7. pendaftaran
--    N:1 ke mahasiswa, N:1 ke lowongan.
--    Satu mahasiswa hanya boleh mendaftar sekali pada lowongan yang sama.
-- ---------------------------------------------------------------------
CREATE TABLE pendaftaran (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    mahasiswa_id    INT NOT NULL,
    lowongan_id     INT NOT NULL,
    tanggal_daftar  DATE NOT NULL,
    status          ENUM('MENUNGGU','DISETUJUI','DITOLAK') NOT NULL DEFAULT 'MENUNGGU',
    catatan         TEXT NULL,
    CONSTRAINT uk_pendaftaran_mhs_lowongan UNIQUE (mahasiswa_id, lowongan_id),
    CONSTRAINT fk_pendaftaran_mahasiswa FOREIGN KEY (mahasiswa_id) REFERENCES mahasiswa (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_pendaftaran_lowongan FOREIGN KEY (lowongan_id) REFERENCES lowongan (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_pendaftaran_status (status)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 8. penempatan
--    1:1 ke pendaftaran, N:1 ke dosen, N:1 ke mitra.
-- ---------------------------------------------------------------------
CREATE TABLE penempatan (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    pendaftaran_id  INT NOT NULL,
    dosen_id        INT NOT NULL,
    mitra_id        INT NOT NULL,
    tanggal_mulai   DATE NULL,
    tanggal_selesai DATE NULL,
    status          ENUM('AKTIF','SELESAI','BATAL') NOT NULL DEFAULT 'AKTIF',
    CONSTRAINT uk_penempatan_pendaftaran UNIQUE (pendaftaran_id),
    CONSTRAINT fk_penempatan_pendaftaran FOREIGN KEY (pendaftaran_id) REFERENCES pendaftaran (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_penempatan_dosen FOREIGN KEY (dosen_id) REFERENCES dosen (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_penempatan_mitra FOREIGN KEY (mitra_id) REFERENCES mitra (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_penempatan_tanggal CHECK (
        tanggal_selesai IS NULL OR tanggal_mulai IS NULL OR tanggal_selesai >= tanggal_mulai
    ),
    INDEX idx_penempatan_dosen (dosen_id),
    INDEX idx_penempatan_status (status)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 9. logbook
--    N:1 ke penempatan. Satu catatan per tanggal per penempatan.
-- ---------------------------------------------------------------------
CREATE TABLE logbook (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    penempatan_id      INT NOT NULL,
    tanggal            DATE NOT NULL,
    jam_mulai          TIME NULL,
    jam_selesai        TIME NULL,
    kegiatan           TEXT NOT NULL,
    kendala            TEXT NULL,
    status_verifikasi  ENUM('MENUNGGU','DIVERIFIKASI','REVISI') NOT NULL DEFAULT 'MENUNGGU',
    catatan_dosen      TEXT NULL,
    CONSTRAINT uk_logbook_penempatan_tanggal UNIQUE (penempatan_id, tanggal),
    CONSTRAINT fk_logbook_penempatan FOREIGN KEY (penempatan_id) REFERENCES penempatan (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_logbook_status (status_verifikasi)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 10. penilaian
--     1:1 ke penempatan, N:1 ke dosen.
-- ---------------------------------------------------------------------
CREATE TABLE penilaian (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    penempatan_id      INT NOT NULL,
    dosen_id           INT NOT NULL,
    nilai_mitra        DECIMAL(5,2) NULL,
    nilai_dosen        DECIMAL(5,2) NULL,
    nilai_akhir        DECIMAL(5,2) NULL,
    catatan            TEXT NULL,
    tanggal_penilaian  DATE NULL,
    CONSTRAINT uk_penilaian_penempatan UNIQUE (penempatan_id),
    CONSTRAINT fk_penilaian_penempatan FOREIGN KEY (penempatan_id) REFERENCES penempatan (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_penilaian_dosen FOREIGN KEY (dosen_id) REFERENCES dosen (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_penilaian_mitra CHECK (nilai_mitra IS NULL OR nilai_mitra BETWEEN 0 AND 100),
    CONSTRAINT ck_penilaian_dosen CHECK (nilai_dosen IS NULL OR nilai_dosen BETWEEN 0 AND 100)
) ENGINE = InnoDB;
