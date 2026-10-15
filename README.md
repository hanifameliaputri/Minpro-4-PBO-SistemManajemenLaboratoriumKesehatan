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

### Cara Menjalankan Program

1. Jalankan MySQL (misalnya lewat XAMPP).
2. Import file `database/laboratorium_kesehatan.sql` lewat phpMyAdmin (tab *Import*), atau lewat terminal: `mysql -u root < database/laboratorium_kesehatan.sql`. File ini membuat database, tabel, dan dummy data.
3. Jika user atau password MySQL berbeda dari default (`root`, password kosong), sesuaikan di `koneksi/DatabaseConnection.java` dan `src/main/resources/META-INF/persistence.xml`.
4. Jalankan `Main.Main` (NetBeans, atau `mvn compile exec:java`). Dibutuhkan JDK 21 dan koneksi internet saat build pertama untuk mengunduh dependensi Maven (MySQL Connector dan Hibernate).
5. Setelah program berjalan, menu *Lihat Semua* langsung menampilkan dummy data dari database.

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

<img height="200" alt="image" src="https://github.com/user-attachments/assets/e08d72bc-32c8-4a46-bfd7-ce88df167df1" />

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

<img width="842" height="701" alt="image" src="https://github.com/user-attachments/assets/716efbfe-6b34-484b-851c-db21cb17e02d" />


Penjelasan alur pada gambar di atas:

1. Pengguna memilih menu **1. Pendaftaran Pemeriksaan**, lalu memilih **1. Pasien Baru**.
2. Program meminta data pasien: nama, umur, jenis kelamin, dan keluhan. Setiap prompt memuat contoh format, dan setiap input divalidasi, misalnya jenis kelamin hanya menerima `Laki-laki` atau `Perempuan` (huruf besar/kecil tidak dibedakan).
3. Setelah data valid, pasien disimpan dan mendapat **ID otomatis** (`P2`, karena `P1` sudah dipakai dummy data).
4. Program menampilkan daftar pemeriksaan beserta biayanya, lalu pengguna memasukkan ID pemeriksaan (`PM1`).
5. Program menampilkan daftar petugas, lalu pengguna memasukkan ID petugas (`PT2`). Daftar ini memperlihatkan hasil **overriding**: `Analis` menampilkan spesialisasi, sedangkan `Dokter` menampilkan nomor STR.

**Konfirmasi pendaftaran** setelah semua data dipilih:

<img height="215" alt="image" src="https://github.com/user-attachments/assets/6221e7de-250e-428c-8675-93c7998378e2" />

**Pendaftaran dengan pasien yang sudah terdaftar:**

<img width="682" height="657" alt="image" src="https://github.com/user-attachments/assets/e8bf9e65-f8b2-4b0a-90e1-397efe415fdc" />


Penjelasan alur pada gambar di atas:

1. Pengguna memilih **2. Pasien Sudah Terdaftar**, lalu program menampilkan semua pasien (`P1` dan `P2`). Pasien `P2` adalah pasien yang sebelumnya didaftarkan sebagai pasien baru, sehingga terlihat bahwa data tersimpan di database.
2. Pengguna memasukkan ID pasien (`p1`). Pencarian tidak membedakan huruf besar dan kecil (`equalsIgnoreCase`), sehingga `p1` tetap ditemukan sebagai `P1`.
3. Pengguna memilih pemeriksaan (`PM1`) dan petugas (`PT1`).
4. Program menampilkan **konfirmasi pendaftaran** berisi ID dan nama pasien, jenis pemeriksaan, biaya, petugas, serta status `Terdaftar`.
5. Data pendaftaran disimpan pada `pasienTerdaftar`, `pemeriksaanTerdaftar`, dan `petugasTerdaftar` untuk dipakai pada menu **Input Hasil Pemeriksaan**.

### Menu 2 - Kelola Pasien

Menu ini berisi lima fitur: tambah pasien, lihat semua pasien, cari pasien berdasarkan ID (ditampilkan lengkap dengan umur, jenis kelamin, dan keluhan), ubah pasien, dan hapus pasien. Seluruh operasi memakai **JDBC** lewat `PasienDAO`.

<img height="205" alt="image" src="https://github.com/user-attachments/assets/8bf91a83-2c9f-47aa-8841-f9337e5d3e0c" />

**Tambah pasien.** 

<img height="202" alt="image" src="https://github.com/user-attachments/assets/2128553d-56bd-40a2-b5dd-ea49f3dd3ca8" />


**Lihat semua pasien.**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/e69c3b35-7371-4439-b58e-ac83c7d6180a" />

**Cari pasien berdasarkan ID.**

<img height="220" alt="image" src="https://github.com/user-attachments/assets/a63b2cdb-bf74-4d28-b62b-39b92a2b11d3" />

**Hapus pasien.**

<img height="203" alt="image" src="https://github.com/user-attachments/assets/0922ac9e-a677-4687-8b1b-758f83c2cd66" />


**Ubah pasien.** Program meminta ID pasien, lalu menampilkan nilai lama di dalam tanda kurung pada setiap kolom. Menekan Enter mempertahankan nilai lama. Data diperbarui lewat `PasienDAO.ubah()` (perintah `UPDATE` dengan Prepared Statement).

```text
Masukkan ID Pasien yang ingin diubah: P1
Tekan Enter saja jika tidak ingin mengubah data tersebut.
Nama Baru (Aulia Ashylla P): [Enter]
Umur Baru (19): 20
Jenis Kelamin Baru (Perempuan): [Enter]
Keluhan Baru (Demam dan batuk sejak 3 hari): [Enter]
Pasien berhasil diubah.
```

**Catatan hapus pasien.** Pasien yang sudah memiliki hasil pemeriksaan tidak dapat dihapus karena ada *foreign key* di tabel `hasil_pemeriksaan`. Program menampilkan pesan penolakan dan tidak berhenti.

### Menu 3 - Kelola Petugas

Menu ini digunakan untuk menambah **Analis**, menambah **Dokter**, melihat semua petugas, mengubah petugas, dan menghapus petugas. Seluruh operasi memakai **ORM (Hibernate)** lewat `PetugasOrmDAO`. Objek `Analis` dan `Dokter` disimpan di satu tabel `petugas` dan dibaca sebagai `List<Petugas>`.

<img height="200" alt="image" src="https://github.com/user-attachments/assets/f894cbeb-39ae-4cf6-965c-41068a27e47f" />

**Tambah analis.**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/7a3e1e03-a44c-440a-8a73-410055eb1a78" />


**Tambah dokter.**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/bf9cf0b6-4308-4981-95fe-d626e9eeed0a" />


**Lihat semua petugas.** Pada tampilan ini terlihat hasil **overriding** dan **polymorphism**: `Analis` menampilkan spesialisasi, sedangkan `Dokter` menampilkan nomor STR. Baris kedua (umur dan jenis kelamin) muncul karena `tampilkanInfo(true)` dipanggil (overloading). Penjelasan lengkapnya ada di [bagian 5](#5-penerapan-polymorphism-dan-abstraction).

<img height="252" alt="image" src="https://github.com/user-attachments/assets/21810661-f7ae-4a3f-9a03-6e699b25f546" />


**Ubah petugas.** Program meminta ID petugas, lalu menampilkan nilai lama pada tiap kolom (nama, umur, jenis kelamin, dan No. STR untuk Dokter atau spesialisasi untuk Analis). Menekan Enter mempertahankan nilai lama. Perubahan disimpan dengan `em.merge()` di `PetugasOrmDAO`.

**Hapus petugas.** Petugas dihapus berdasarkan ID dengan `em.remove()`. Petugas yang sudah tercatat pada hasil pemeriksaan tidak dapat dihapus (ditolak oleh *foreign key*) dan program menampilkan pesan penolakan.

### Menu 4 - Kelola Pemeriksaan

Menu ini berisi lima fitur: tambah, lihat semua, cari, ubah (nama dan biaya), dan hapus pemeriksaan. Seluruh operasi memakai **JDBC** lewat `PemeriksaanDAO`.

<img height="200" alt="image" src="https://github.com/user-attachments/assets/1d988ff6-93f9-4cdd-b66e-975602c74b5f" />

**Tambah pemeriksaan.**

<img height="220" alt="image" src="https://github.com/user-attachments/assets/51528dd8-c305-4305-a258-907021e290a9" />


**Lihat semua pemeriksaan.**

<img height="300" alt="image" src="https://github.com/user-attachments/assets/4df5db88-4ea8-4e59-b67d-69aafd375d37" />

**Cari pemeriksaan.**

<img height="300" alt="image" src="https://github.com/user-attachments/assets/eecdf32a-d036-4861-ac4a-fc36a4e3faff" />

**Ubah pemeriksaan.** Pada fitur ubah, program menampilkan nilai yang sedang tersimpan di dalam tanda kurung. Pengguna cukup **menekan Enter** jika tidak ingin mengubah data tersebut, sehingga nilai lama dipertahankan.

```text
Tekan Enter saja jika tidak ingin mengubah data tersebut.
Nama Pemeriksaan Baru (Tes Darah Lengkap): [Enter]
Biaya Baru (150000.0): 200000
Pemeriksaan berhasil diubah.
```

Pada contoh di atas nama tetap `Tes Darah Lengkap` (karena hanya menekan Enter) dan hanya biaya yang berubah menjadi `200000.0`. Logikanya ada pada method `bacaTeksOpsional()` dan `bacaDoubleOpsional()` di Controller. Jika angka yang dimasukkan tidak valid atau negatif, nilai lama tetap dipertahankan dan program menampilkan pesan.

<img height="221" alt="image" src="https://github.com/user-attachments/assets/702e4e32-8450-459b-82b5-a93731e29cf5" />


**Hapus pemeriksaan.**

<img height="340" alt="image" src="https://github.com/user-attachments/assets/21363670-79b8-4264-aa89-70a32d090d0c" />


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

<img height="200" alt="image" src="https://github.com/user-attachments/assets/71dece68-9187-43c3-b493-39cf59267399" />

**Input hasil pemeriksaan.**

<img width="607" height="222" alt="image" src="https://github.com/user-attachments/assets/a49501cb-4a07-4d1c-8efa-c06c79984400" />


**Lihat semua hasil.**

<img height="211" alt="image" src="https://github.com/user-attachments/assets/3afbef30-0341-42e2-bddb-af30f00d275d" />


**Lihat riwayat hasil per pasien.**

<img height="217" alt="image" src="https://github.com/user-attachments/assets/26ac35f1-7321-41fb-b887-29e7ffdd49c6" />


### Validasi Input

Ketika pengguna memasukkan input yang salah (misalnya huruf pada kolom umur, atau jenis kelamin yang tidak valid), program meminta input diulang dan tidak berhenti.

**Contoh pada umur:**

<img height="238" alt="image" src="https://github.com/user-attachments/assets/d8de3950-42b3-4a2d-aa4a-2ff7d37bacb0" />

**Contoh pada jenis kelamin:**

<img height="241" alt="image" src="https://github.com/user-attachments/assets/28799782-3320-477a-ad4c-31e5a3560001" />


**Contoh pada nama:**

<img height="240" alt="image" src="https://github.com/user-attachments/assets/f8b80c84-664b-4b59-9c21-845f43c018a5" />


### Menu 6 - Keluar

Menu ini menampilkan pesan penutup dan menghentikan perulangan program.

<img height="200" alt="image" src="https://github.com/user-attachments/assets/4cd9ff28-b650-4b05-82ce-36129abfd574" />

---
