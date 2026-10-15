# Minpro-4-PBO-SistemManajemenLaboratoriumKesehatan

**Nama:** Hanif Amelia Putri  
**Kelas:** B  
**NIM:** 2509116075  

---

## 1. Deskripsi Singkat Program

Program **Sistem Manajemen Laboratorium Kesehatan** adalah program berbasis Java (console) yang digunakan untuk mengelola data pasien, petugas laboratorium, pemeriksaan, dan hasil pemeriksaan. Program ini merupakan pengembangan dari Mini Project 3. Pada Mini Project 4, data tidak lagi disimpan sementara di `ArrayList`, tetapi di database **MySQL** yang diakses menggunakan **JDBC (Prepared Statement)** dengan pola **DAO**. Sebagai **nilai tambah**, program juga menerapkan **ORM (JPA dengan Hibernate)**. JDBC dan ORM **dipakai bersamaan** dalam satu program: entity Pasien dan Pemeriksaan diakses lewat JDBC, sedangkan Petugas dan Hasil Pemeriksaan diakses lewat ORM.

Fitur utama aplikasi:

- **Pendaftaran Pemeriksaan**: mendaftarkan pasien (baru atau yang sudah terdaftar) ke suatu jenis pemeriksaan yang ditangani oleh petugas tertentu.
- **Kelola Pasien**: tambah, lihat semua, cari, ubah, dan hapus data pasien.
- **Kelola Petugas**: tambah **Analis** atau **Dokter**, lihat semua, ubah, dan hapus petugas.
- **Kelola Pemeriksaan**: tambah, lihat semua, cari, ubah, dan hapus jenis pemeriksaan beserta biayanya.
- **Kelola Hasil Pemeriksaan**: input hasil pemeriksaan, lihat semua hasil, dan lihat riwayat hasil per pasien.
- **Validasi input** agar program tidak berhenti akibat kesalahan pengetikan pengguna.
- **Dummy data** awal yang diisi lewat file database `.sql` (bukan lewat kode Java), sehingga data langsung tampil saat fitur *Lihat* dijalankan.

Seluruh data disimpan di database MySQL `laboratorium_kesehatan`. File struktur tabel dan dummy data ada di `database/laboratorium_kesehatan.sql`.

### Pembagian JDBC dan ORM

| Entity | Cara akses database | Class DAO |
|---|---|---|
| Pasien | JDBC + Prepared Statement | `PasienDAO` |
| Pemeriksaan | JDBC + Prepared Statement | `PemeriksaanDAO` |
| Petugas (Dokter dan Analis) | ORM (JPA/Hibernate) | `PetugasOrmDAO` |
| Hasil Pemeriksaan | ORM (JPA/Hibernate) | `HasilPemeriksaanOrmDAO` |

Satu alur seperti *Pendaftaran Pemeriksaan* memakai JDBC (membaca pasien dan pemeriksaan) sekaligus ORM (membaca petugas dan menyimpan hasil).


---

## 2. Penjelasan Struktur Package

Program menerapkan struktur **MVC (Model, View, Controller)** ditambah lapisan **DAO** (Data Access Object) yang menjadi penghubung antara Controller dan database.

```text
Minpro-4-PBO-SistemManajemenLaboratoriumKesehatan/
|
|-- database/
|   '-- laboratorium_kesehatan.sql        <- struktur tabel + dummy data awal
|
|-- pom.xml                               <- dependensi mysql-connector-j dan hibernate-core
|
'-- src/main/
    |-- java/
    |   |-- Main/
    |   |   '-- Main.java                 <- Titik awal program, membuat DAO dan Controller
    |   |
    |   |-- controller/                   <- [CONTROLLER]
    |   |   '-- LaboratoriumController.java   (alur menu, validasi input, pemanggil DAO)
    |   |
    |   |-- dao/                          <- [DAO] akses database
    |   |   |-- CrudDAO.java              (interface CRUD)
    |   |   |-- PasienDAO.java            (JDBC)
    |   |   |-- PemeriksaanDAO.java       (JDBC)
    |   |   |-- OrmDAO.java               (kelas dasar CRUD berbasis JPA)
    |   |   |-- PetugasOrmDAO.java        (ORM)
    |   |   '-- HasilPemeriksaanOrmDAO.java (ORM)
    |   |
    |   |-- koneksi/
    |   |   |-- DatabaseConnection.java   (koneksi JDBC)
    |   |   '-- JPAUtil.java              (EntityManager untuk ORM)
    |   |
    |   |-- model/                        <- [MODEL]
    |   |   |-- Identitas.java            (interface)
    |   |   |-- Petugas.java              (abstract class, implements Identitas, entity ORM)
    |   |   |-- Analis.java               (subclass Petugas, entity ORM)
    |   |   |-- Dokter.java               (subclass Petugas, entity ORM)
    |   |   |-- Pasien.java
    |   |   |-- Pemeriksaan.java
    |   |   '-- HasilPemeriksaan.java     (entity ORM)
    |   |
    |   '-- view/                         <- [VIEW]
    |       '-- LaboratoriumView.java     (menu, judul, pesan, dan konfirmasi pendaftaran)
    |
    '-- resources/META-INF/
        '-- persistence.xml               <- konfigurasi Hibernate (ORM)
```


| Package / Folder | Peran |
|---|---|
| `Main` | Mengecek koneksi database, membuat DAO (JDBC dan ORM), mengirimnya ke `LaboratoriumController`, lalu menjalankan program. |
| `controller` | Menerima input pengguna, memvalidasinya, memanggil DAO untuk membaca/menulis data, dan memanggil View untuk menampilkan hasil. |
| `dao` | Satu-satunya tempat akses database. `PasienDAO` dan `PemeriksaanDAO` berisi query SQL dengan Prepared Statement (JDBC); `PetugasOrmDAO` dan `HasilPemeriksaanOrmDAO` memakai JPA/Hibernate (ORM). |
| `koneksi` | `DatabaseConnection` membuka koneksi JDBC ke MySQL; `JPAUtil` menyediakan `EntityManager` untuk ORM. |
| `model` | Menyimpan struktur data, encapsulation, hierarki pewarisan (`Petugas`, `Analis`, `Dokter`), abstract class, dan interface. `Petugas`, `Analis`, `Dokter`, dan `HasilPemeriksaan` juga berisi anotasi JPA (nilai tambah). |
| `view` | Khusus menampilkan menu, judul, dan pesan ke terminal. |
| `database` | File `.sql` berisi struktur tabel dan dummy data. |
| `resources/META-INF` | `persistence.xml`, konfigurasi persistence unit Hibernate. |

---

## 3. Penjelasan Alur Program

### Alur Umum

```text
[Start] Main.main()
        |
        v
DatabaseConnection.getConnection()   -> gagal? tampilkan pesan lalu program berhenti
        |
        v
new LaboratoriumController(PasienDAO, PetugasOrmDAO, PemeriksaanDAO, HasilPemeriksaanOrmDAO)
  |-- menyimpan 4 DAO (JDBC: Pasien dan Pemeriksaan | ORM: Petugas dan Hasil)
  '-- membuat objek LaboratoriumView
        |
        v
controller.jalankanProgram(scanner)
        |
        v
+------------------------------------+
|     TAMPIL MENU UTAMA (while)      | <--------------------+
+------------------------------------+                      |
| 1. Pendaftaran Pemeriksaan         |                      |
| 2. Kelola Pasien                   |                      |
| 3. Kelola Petugas                  |                      |
| 4. Kelola Pemeriksaan              |                      |
| 5. Kelola Hasil Pemeriksaan        |                      |
| 6. Keluar                          |                      |
+------------------------------------+                      |
        |                                                   |
        |--- Pilih 1-5 -> Proses menu terkait --------------+
        |
        '--- Pilih 6   -> "Program selesai" -> [Program Berhenti]
```

1. **Program dimulai dari `Main.java`.** Method `main()` lebih dulu mengecek koneksi ke database. Jika gagal, program menampilkan pesan dan berhenti. Jika berhasil, `Main` membuat empat DAO (dua JDBC dan dua ORM), mengirimnya ke `LaboratoriumController`, lalu memanggil `jalankanProgram()`.
2. **Konstruktor Controller** menerima empat DAO lewat parameter dan membuat objek `LaboratoriumView`. Controller tidak menyimpan data sendiri; semua data dibaca dan ditulis ke database lewat DAO. Dummy data sudah ada di database karena diimpor dari file `.sql`.
3. **`jalankanProgram()`** menjalankan perulangan `while` yang terus menampilkan menu utama sampai pengguna memilih menu 6. Input menu dibaca dengan `bacaInt()` sehingga huruf atau input kosong tidak membuat program error. Pilihan di luar 1-6 menampilkan pesan `Pilihan tidak tersedia.`
4. **Setiap sub-menu** (menu 2 sampai 5) memiliki perulangan sendiri dan baru kembali ke menu utama ketika pengguna memilih opsi *Kembali*.
5. **Setiap pertanyaan input langsung menampilkan contoh format yang valid**, misalnya `Jenis Kelamin (Laki-laki/Perempuan):`, `Umur (1-120):`, dan `Status (Normal/Tidak Normal):`, sehingga pengguna tahu format yang benar sebelum mengetik.

Tampilan menu utama saat program pertama kali dijalankan:

<img height="250" alt="image" src="https://github.com/user-attachments/assets/3eeed483-5c34-462d-8a66-2bc57f5b6f42" />


### Menu 1 - Pendaftaran Pemeriksaan

```text
Pilih pasien
  |-- 1. Pasien Baru       -> input nama, umur, jenis kelamin, keluhan -> pasienDAO.tambah() (ID otomatis: P1, P2, ...)
  '-- 2. Pasien Terdaftar  -> tampil semua pasien -> input ID Pasien
                              '-- tidak ditemukan -> pesan error -> kembali ke menu utama
        |
        v
Tampil daftar pemeriksaan -> input ID Pemeriksaan
  '-- tidak ditemukan -> pesan error -> kembali ke menu utama
        |
        v
Tampil daftar petugas -> input ID Petugas
  '-- tidak ditemukan -> pesan error -> kembali ke menu utama
        |
        v
Tampil KONFIRMASI PENDAFTARAN (ID/nama pasien, pemeriksaan, biaya, petugas, status)
        |
        v
Simpan ke pasienTerdaftar, pemeriksaanTerdaftar, petugasTerdaftar
```

Data pendaftaran terakhir (hasil pembacaan dari database) disimpan pada tiga atribut Controller (`pasienTerdaftar`, `pemeriksaanTerdaftar`, `petugasTerdaftar`) dan dipakai kembali oleh menu 5 saat menginput hasil.

**Pendaftaran dengan pasien baru:**

<img height="635" alt="image" src="https://github.com/user-attachments/assets/10019c71-5444-445b-9946-008a643be269" />


Penjelasan alur pada gambar di atas:

1. Pengguna memilih menu **1. Pendaftaran Pemeriksaan**, lalu memilih **1. Pasien Baru**.
2. Program meminta data pasien: nama, umur, jenis kelamin, dan keluhan. Setiap prompt memuat contoh format, dan setiap input divalidasi, misalnya jenis kelamin hanya menerima `Laki-laki` atau `Perempuan` (huruf besar/kecil tidak dibedakan).
3. Setelah data valid, pasien disimpan dan mendapat **ID otomatis** (`P2`, karena `P1` sudah dipakai dummy data).
4. Program menampilkan daftar pemeriksaan beserta biayanya, lalu pengguna memasukkan ID pemeriksaan (`PM1`).
5. Program menampilkan daftar petugas, lalu pengguna memasukkan ID petugas (`PT2`). Daftar ini memperlihatkan hasil **overriding**: `Analis` menampilkan spesialisasi, sedangkan `Dokter` menampilkan nomor STR.

**Konfirmasi pendaftaran** setelah semua data dipilih:

<img height="200" alt="image" src="https://github.com/user-attachments/assets/3e0b6305-a361-409c-ab88-450e93848f93" />


**Pendaftaran dengan pasien yang sudah terdaftar:**

<img height="762" alt="image" src="https://github.com/user-attachments/assets/5916471f-85e3-4c11-ac15-6e47076eb03d" />


Penjelasan alur pada gambar di atas:

1. Pengguna memilih **2. Pasien Sudah Terdaftar**, lalu program menampilkan semua pasien (`P1` dan `P2`). Pasien `P2` adalah pasien yang sebelumnya didaftarkan sebagai pasien baru, sehingga terlihat bahwa data tersimpan di database.
2. Pengguna memasukkan ID pasien (`p1`). Pencarian tidak membedakan huruf besar dan kecil (`equalsIgnoreCase`), sehingga `p1` tetap ditemukan sebagai `P1`.
3. Pengguna memilih pemeriksaan (`PM1`) dan petugas (`PT1`).
4. Program menampilkan **konfirmasi pendaftaran** berisi ID dan nama pasien, jenis pemeriksaan, biaya, petugas, serta status `Terdaftar`.
5. Data pendaftaran disimpan pada `pasienTerdaftar`, `pemeriksaanTerdaftar`, dan `petugasTerdaftar` untuk dipakai pada menu **Input Hasil Pemeriksaan**.

### Menu 2 - Kelola Pasien

Menu ini berisi lima fitur: tambah pasien, lihat semua pasien, cari pasien berdasarkan ID (ditampilkan lengkap dengan umur, jenis kelamin, dan keluhan), ubah pasien, dan hapus pasien. Seluruh operasi memakai **JDBC** lewat `PasienDAO`.

<img height="217" alt="image" src="https://github.com/user-attachments/assets/62426cfe-ce5f-4cdb-a35a-ed96f7841d32" />

**Tambah pasien.** 

<img height="362" alt="image" src="https://github.com/user-attachments/assets/8f67a2b8-601c-4a31-8adb-27f4b56ea682" />


**Lihat semua pasien.**

<img height="227" alt="image" src="https://github.com/user-attachments/assets/d575566f-e358-43ab-9492-11e1bf579da4" />

**Cari pasien berdasarkan ID.**

<img height="287" alt="image" src="https://github.com/user-attachments/assets/cd6f3aaf-fc46-4b96-8bed-0616d139dfb3" />

**Ubah pasien.** Program meminta ID pasien, lalu menampilkan nilai lama di dalam tanda kurung pada setiap kolom. Menekan Enter mempertahankan nilai lama. Data diperbarui lewat `PasienDAO.ubah()` (perintah `UPDATE` dengan Prepared Statement).

<img height="493" alt="image" src="https://github.com/user-attachments/assets/893aa2dc-a48a-42f8-a50d-67e2acb2b269" />


**Hapus pasien.**

<img height="407" alt="image" src="https://github.com/user-attachments/assets/b996d0d2-b7fe-4965-812d-d258442d149b" />


**Catatan hapus pasien.** Pasien yang sudah memiliki hasil pemeriksaan tidak dapat dihapus karena ada *foreign key* di tabel `hasil_pemeriksaan`. Program menampilkan pesan penolakan dan tidak berhenti.

<img height="283" alt="image" src="https://github.com/user-attachments/assets/05eea6e9-a511-4b7f-a7c4-5ba387117300" />


### Menu 3 - Kelola Petugas

Menu ini digunakan untuk menambah **Analis**, menambah **Dokter**, melihat semua petugas, mengubah petugas, dan menghapus petugas. Seluruh operasi memakai **ORM (Hibernate)** lewat `PetugasOrmDAO`. Objek `Analis` dan `Dokter` disimpan di satu tabel `petugas` dan dibaca sebagai `List<Petugas>`.

<img height="205" alt="image" src="https://github.com/user-attachments/assets/aaaf5637-c853-4e1d-8c01-fa24f29880aa" />

**Tambah analis.**

<img height="531" alt="image" src="https://github.com/user-attachments/assets/df0b8ed2-03ec-479f-bb5f-61f53307593d" />


**Tambah dokter.**

<img height="580" alt="image" src="https://github.com/user-attachments/assets/5e50a3a0-3545-4fa1-865c-fdf77d5b1528" />


**Lihat semua petugas.** Pada tampilan ini terlihat hasil **overriding** dan **polymorphism**: `Analis` menampilkan spesialisasi, sedangkan `Dokter` menampilkan nomor STR. Baris kedua (umur dan jenis kelamin) muncul karena `tampilkanInfo(true)` dipanggil (overloading). Penjelasan lengkapnya ada di [bagian 5](#5-penerapan-polymorphism-dan-abstraction).

<img height="272" alt="image" src="https://github.com/user-attachments/assets/b37f56ad-129a-4bac-addf-8f3c6ffd65b5" />

**Ubah petugas.** Program meminta ID petugas, lalu menampilkan nilai lama pada tiap kolom (nama, umur, jenis kelamin, dan No. STR untuk Dokter atau spesialisasi untuk Analis). Menekan Enter mempertahankan nilai lama. Perubahan disimpan dengan `em.merge()` di `PetugasOrmDAO`.

<img height="597" alt="image" src="https://github.com/user-attachments/assets/d3b97c48-6cc9-429c-9666-7556bd6f4acc" />


**Hapus petugas.** Petugas dihapus berdasarkan ID.

<img height="430" alt="image" src="https://github.com/user-attachments/assets/85389fbb-c553-4ac9-bbf3-730aa6147cc3" />


### Menu 4 - Kelola Pemeriksaan

Menu ini berisi lima fitur: tambah, lihat semua, cari, ubah (nama dan biaya), dan hapus pemeriksaan. Seluruh operasi memakai **JDBC** lewat `PemeriksaanDAO`.

<img width="330" height="212" alt="image" src="https://github.com/user-attachments/assets/a5e97388-3234-4430-8bb9-53e5d6276228" />

**Tambah pemeriksaan.**

<img height="235" alt="image" src="https://github.com/user-attachments/assets/028af314-d33f-4a26-bd0b-d13f0387249e" />


**Lihat semua pemeriksaan.**

<img height="285" alt="image" src="https://github.com/user-attachments/assets/0ea4fc4d-7506-4def-a09f-6de12af46a4d" />

**Cari pemeriksaan.**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/de3e8188-d2e5-40f0-8745-632828846ea9" />

**Ubah pemeriksaan.** Pada fitur ubah, program menampilkan nilai yang sedang tersimpan di dalam tanda kurung. Pengguna cukup **menekan Enter** jika tidak ingin mengubah data tersebut, sehingga nilai lama dipertahankan.

<img width="551" height="468" alt="image" src="https://github.com/user-attachments/assets/fee5fc23-c03b-4808-8122-6a1397f49a1e" />

**Hapus pemeriksaan.**

<img width="520" height="377" alt="image" src="https://github.com/user-attachments/assets/981aed27-1654-480f-8abe-d30629280db8" />


### Menu 5 - Kelola Hasil Pemeriksaan

```text
1. Input Hasil
     |-- Belum pernah melakukan pendaftaran (menu 1)?
     |     -> tampil pesan "Silakan lakukan menu 1. Pendaftaran Pemeriksaan terlebih dahulu"
     '-- Sudah -> tampil semua pasien -> input ID Pasien -> input hasil -> input status (Normal / Tidak Normal)
           -> tambahHasil() memeriksa ID pasien, pemeriksaan, dan petugas
           -> jika valid, hasil disimpan dengan ID otomatis (H1, H2, ...)
2. Lihat Semua Hasil
3. Lihat Riwayat Hasil per Pasien (input ID Pasien)
```

Seluruh operasi pada menu ini memakai **ORM (Hibernate)** lewat `HasilPemeriksaanOrmDAO`. Jenis pemeriksaan dan petugas pada hasil diambil dari pendaftaran terakhir (menu 1), sedangkan ID pasien diinput ulang oleh pengguna. Pada tampilan hasil, program menampilkan **nama pasien** (bukan ID pasien) beserta nama jenis tes dan nama petugas yang memeriksa, sehingga lebih mudah dibaca.

<img height="233" alt="image" src="https://github.com/user-attachments/assets/e5fc2453-8778-4b71-97a1-44f4bd03e57c" />


**Input hasil pemeriksaan.**

<img height="307" alt="image" src="https://github.com/user-attachments/assets/1f3493b5-8c6d-48be-b3b2-010faebdcd5c" />



**Lihat semua hasil.**

<img height="245" alt="image" src="https://github.com/user-attachments/assets/1affbd3d-bbad-4642-829f-2c4cb24850bc" />



**Lihat riwayat hasil per pasien.**

<img height="297" alt="image" src="https://github.com/user-attachments/assets/4f0eb784-8111-48d6-b421-0e98de128c84" />



### Validasi Input

Ketika pengguna memasukkan input yang salah (misalnya huruf pada kolom umur, atau jenis kelamin yang tidak valid), program meminta input diulang dan tidak berhenti.

**Contoh pada umur:**

<img height="540" alt="image" src="https://github.com/user-attachments/assets/683747f4-23e3-4aa4-b50d-d57d3288697e" />


**Contoh pada jenis kelamin:**

<img height="285" alt="image" src="https://github.com/user-attachments/assets/33fa7de9-bbc7-4eb9-8e69-85d579648403" />



**Contoh pada nama:**

<img height="265" alt="image" src="https://github.com/user-attachments/assets/25762d37-0b2d-4395-8b50-8c70305761a8" />



### Menu 6 - Keluar

Menu ini menampilkan pesan penutup dan menghentikan perulangan program.

<img height="200" alt="image" src="https://github.com/user-attachments/assets/4cd9ff28-b650-4b05-82ce-36129abfd574" />

---
