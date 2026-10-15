<img width="670" height="287" alt="image" src="https://github.com/user-attachments/assets/38708095-866c-4699-ba87-0f5e771836c8" /># Minpro-4-PBO-SistemManajemenLaboratoriumKesehatan

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
<img height="703" alt="image" src="https://github.com/user-attachments/assets/d792b870-3955-4743-b337-8b1c90acc31d" />


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


## 4. Penerapan Encapsulation

**Encapsulation** merupakan konsep OOP yang membatasi akses langsung terhadap atribut objek. Pada program ini, encapsulation diterapkan dengan menggunakan atribut `private` dan menyediakan method getter serta setter untuk mengakses atau mengubah nilainya.

Contoh penerapan pada `Pasien.java`:

<img height="690" alt="image" src="https://github.com/user-attachments/assets/62a28eb7-58fd-42bb-9552-c6d32435dbdf" />


**Penjelasan:**
- Atribut id, nama, umur, jenisKelamin, dan keluhan menggunakan modifier private, sehingga tidak dapat diakses secara langsung dari luar class.
- Method getter seperti getNama() dan getId() digunakan untuk mengambil nilai atribut.
- Method setter seperti setNama(), setUmur(), setJenisKelamin(), dan setKeluhan() digunakan untuk mengubah nilai atribut.
- Setter setNama() melakukan validasi agar nama tidak kosong sebelum disimpan.
Dengan demikian, perubahan data pasien dapat dikontrol melalui method yang disediakan oleh class.
Encapsulation juga diterapkan pada class Petugas, Pemeriksaan, dan HasilPemeriksaan.

---

## 5. Penerapan Inheritance

**Inheritance** merupakan konsep pewarisan atribut dan method dari class induk ke class turunan. Pada program ini, inheritance diterapkan melalui class `Petugas`, yang menjadi class induk bagi `Analis` dan `Dokter`.

Contoh pada `Petugas.java`:

```java
public abstract class Petugas implements Identitas {

    private String id;
    private String nama;
    private int umur;
    private String jenisKelamin;

    public Petugas(
            String id,
            String nama,
            int umur,
            String jenisKelamin) {

        this.id = id;
        setNama(nama);
        setUmur(umur);
        setJenisKelamin(jenisKelamin);
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public abstract void tampilkanInfo();
}
```

Contoh pada `Analis.java`:

<img width="446" height="182" alt="image" src="https://github.com/user-attachments/assets/bdf6b2a5-02a0-450e-8ab8-bc185aedefa0" />


Contoh pada `Dokter.java`:

<img width="446" height="107" alt="image" src="https://github.com/user-attachments/assets/b57aa87b-8d85-414b-bfa0-cfc1b2a87e88" />


**Penjelasan:**

- `Petugas` menjadi class induk yang memiliki atribut umum, seperti ID, nama, umur, dan jenis kelamin.
- `Analis` mewarisi atribut dan method dari `Petugas`, kemudian memiliki informasi khusus analis.
- `Dokter` mewarisi atribut dan method dari `Petugas`, kemudian memiliki informasi khusus dokter.
- Pewarisan ditandai dengan kata kunci `extends`.

Dengan inheritance, atribut dan method umum tidak perlu ditulis ulang pada masing-masing class turunan. Hal ini membuat kode lebih terstruktur dan mengurangi pengulangan.

---

## 6. Penerapan Polymorphism dan Abstraction

### 6.1 Polymorphism

**Polymorphism** merupakan kemampuan suatu objek untuk memiliki perilaku yang berbeda meskipun menggunakan method dengan nama yang sama.

Pada program ini, polymorphism diterapkan melalui overriding method `tampilkanInfo()` pada class `Analis` dan `Dokter`.

Contoh pada `Petugas.java`:

<img height="270" alt="image" src="https://github.com/user-attachments/assets/6fdc941f-afba-4381-abbe-b4a4c7b95400" />


Contoh penerapan pada class `Analis`:

<img height="235" alt="image" src="https://github.com/user-attachments/assets/f06f6c95-e59c-4703-bddd-76e05616ccb0" />


Contoh penerapan pada class `Dokter`:

<img height="245" alt="image" src="https://github.com/user-attachments/assets/effe79bd-ae1c-43cc-b69a-878c24e30a87" />


**Penjelasan:**

Method `tampilkanInfo()` memiliki nama yang sama, tetapi menghasilkan informasi yang berbeda sesuai jenis objeknya.

- Objek `Analis` menampilkan informasi spesialisasi.
- Objek `Dokter` menampilkan nomor STR.

Program dapat menyimpan objek `Analis` dan `Dokter` dalam referensi bertipe `Petugas`. Ketika method `tampilkanInfo()` dipanggil, implementasi yang dijalankan mengikuti jenis objek sebenarnya.

### 6.2 Overloading

Selain overriding, program juga menerapkan overloading pada method `tampilkanInfo()`.

Contoh pada `Petugas.java`:

<img width="1010" height="137" alt="image" src="https://github.com/user-attachments/assets/5205b6af-4948-45e6-8eab-c84d8e9eeff4" />


**Penjelasan:**

Method `tampilkanInfo()` tanpa parameter digunakan untuk menampilkan informasi utama petugas. Sementara itu, method `tampilkanInfo(boolean detail)` dapat menampilkan informasi tambahan berupa umur dan jenis kelamin apabila parameter `detail` bernilai `true`.

Overloading dibedakan berdasarkan parameter method, sedangkan overriding dilakukan dengan mendefinisikan ulang method dari class induk pada class turunan.

### 6.3 Abstraction

**Abstraction** merupakan konsep yang menampilkan fungsi utama suatu objek tanpa mengharuskan pengguna mengetahui seluruh detail implementasinya.

Pada program ini, abstraction diterapkan menggunakan abstract class `Petugas` dan interface `Identitas`.

Contoh pada `Identitas.java`:

<img height="296" alt="image" src="https://github.com/user-attachments/assets/86bd1772-bce7-4775-badd-d5698c1ced9b" />


Contoh pada `Petugas.java`:

<img height="363" alt="image" src="https://github.com/user-attachments/assets/7a886c3b-823f-4480-9dbd-9ef0a8e27a3c" />


**Penjelasan:**

- Interface `Identitas` menetapkan kontrak bahwa class yang mengimplementasikannya harus menyediakan method `tampilkanInfo()`.
- Class abstrak `Petugas` menyediakan atribut dan perilaku umum bagi seluruh petugas.
- Method abstrak `tampilkanInfo()` harus diimplementasikan oleh class turunan.
- Class `Analis` dan `Dokter` memberikan implementasi sesuai kebutuhan masing-masing.

Dengan abstraction, struktur umum petugas dapat ditetapkan tanpa menyamakan seluruh detail informasi yang ditampilkan oleh analis dan dokter.

---

## 7. Penerapan JDBC, PreparedStatement, dan DAO

### 7.1 JDBC

**JDBC (Java Database Connectivity)** digunakan untuk menghubungkan aplikasi Java dengan database MySQL.

Pada program ini, JDBC digunakan untuk mengakses data pasien dan data pemeriksaan melalui class `PasienDAO` dan `PemeriksaanDAO`.

Koneksi database dikelola melalui `DatabaseConnection.java`.

<img height="287" alt="image" src="https://github.com/user-attachments/assets/7b366d6f-1d7a-4d9f-811a-d3fa55f27c8f" />


**Penjelasan:**

Method `getConnection()` menyediakan koneksi ke database MySQL. Koneksi tersebut kemudian digunakan oleh DAO untuk menjalankan perintah SQL.

### 7.2 PreparedStatement

`PreparedStatement` digunakan untuk menjalankan perintah SQL dengan parameter. Penggunaan parameter membantu memisahkan perintah SQL dari nilai input pengguna.

Contoh pada `PasienDAO.java`:

<img height="372" alt="image" src="https://github.com/user-attachments/assets/adce3185-e06f-4f5f-bc64-cddcd03c61f8" />

**Penjelasan:**

- Perintah `INSERT INTO` digunakan untuk menambahkan data pasien ke tabel `pasien`.
- Tanda `?` merupakan parameter yang akan diisi dengan nilai atribut pasien.
- Method `setString()` dan `setInt()` digunakan untuk mengisi parameter sesuai tipe datanya.
- `executeUpdate()` menjalankan perintah penambahan data.
- `try-with-resources` membantu menutup `PreparedStatement` setelah digunakan.

Dengan PreparedStatement, nilai input diberikan melalui parameter, bukan digabungkan langsung ke dalam teks SQL.

### 7.3 Penerapan DAO

**DAO (Data Access Object)** merupakan pola yang memisahkan proses akses database dari logika utama program.

Pada program ini, operasi database dikelola melalui class DAO yang berbeda sesuai teknologi yang digunakan.

Contoh interface `CrudDAO.java`:

<img height="352" alt="image" src="https://github.com/user-attachments/assets/ffb27333-0ead-49f0-84e5-325fa20af16c" />


**Penjelasan:**

Interface `CrudDAO<T>` mendefinisikan operasi dasar CRUD, yaitu Create, Read, Update, dan Delete, serta method untuk membuat ID baru.

Parameter generik `<T>` memungkinkan interface digunakan untuk berbagai jenis objek.

Contohnya:

- `PasienDAO` mengelola data pasien menggunakan JDBC.
- `PemeriksaanDAO` mengelola data pemeriksaan menggunakan JDBC.
- `PetugasOrmDAO` mengelola data petugas menggunakan ORM.
- `HasilPemeriksaanOrmDAO` mengelola data hasil pemeriksaan menggunakan ORM.

Dengan pola DAO, `LaboratoriumController` tidak perlu menuliskan perintah SQL untuk setiap proses. Controller cukup memanggil method DAO yang sesuai.

---

## 8. Penerapan ORM (Nilai Tambah)

**ORM (Object-Relational Mapping)** merupakan teknik yang menghubungkan objek dalam Java dengan tabel di database. Pada program ini, ORM diterapkan menggunakan JPA dan Hibernate.

ORM digunakan untuk mengelola data petugas serta hasil pemeriksaan. Sementara itu, data pasien dan pemeriksaan tetap diakses menggunakan JDBC.

### 8.1 Anotasi Entity

Contoh pada `Petugas.java`:

```java
@Entity
@Table(name = "petugas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "tipe_petugas",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Petugas implements Identitas {

    @Id
    @Column(name = "id_petugas")
    private String id;
}
```

**Penjelasan:**

- `@Entity` menandai class sebagai entity yang dikelola oleh JPA.
- `@Table(name = "petugas")` menghubungkan class dengan tabel `petugas`.
- `@Id` menandai atribut yang menjadi identitas utama entity.
- `@Column` menentukan nama kolom database yang digunakan.
- `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` menentukan strategi penyimpanan class induk dan turunannya dalam satu tabel.
- `@DiscriminatorColumn` digunakan untuk membedakan jenis entity petugas yang disimpan.

Anotasi tersebut memungkinkan Hibernate memetakan objek Java ke struktur tabel database.

### 8.2 Operasi ORM pada DAO

Contoh pada `OrmDAO.java`:

```java
@Override
public void tambah(T objek) throws SQLException {
    jalankan(true, em -> {
        em.persist(objek);
        return null;
    });
}
```

Contoh untuk membaca seluruh data:

```java
@Override
public List<T> semua() throws SQLException {

    String jpql = "SELECT e FROM " + namaEntity + " e "
            + "ORDER BY length(e." + atributId + "), e." + atributId;

    return jalankan(
            false,
            em -> em.createQuery(jpql, kelas).getResultList()
    );
}
```

**Penjelasan:**

- `EntityManager` digunakan untuk mengelola entity.
- `em.persist(objek)` menyimpan objek baru ke database.
- `em.createQuery()` menjalankan query menggunakan JPQL.
- `getResultList()` mengambil hasil query dalam bentuk daftar objek.
- Method `jalankan()` mengelola proses transaksi dan penutupan `EntityManager`.

Pada operasi penulisan, transaksi dimulai sebelum perubahan dilakukan dan di-commit setelah operasi berhasil. Jika terjadi kegagalan, transaksi yang masih aktif akan di-rollback.

### 8.3 Konfigurasi ORM

Konfigurasi ORM disimpan pada file:

```text
src/main/resources/META-INF/persistence.xml
```

File tersebut digunakan untuk mengatur persistence unit, koneksi database, dan konfigurasi provider JPA/Hibernate.

**Letak penerapan nilai tambah:**

| File | Peran |
|---|---|
| `Petugas.java` | Entity ORM dan pemetaan inheritance petugas. |
| `Analis.java` | Entity turunan untuk data analis. |
| `Dokter.java` | Entity turunan untuk data dokter. |
| `HasilPemeriksaan.java` | Entity untuk data hasil pemeriksaan. |
| `OrmDAO.java` | Operasi CRUD generik menggunakan JPA. |
| `PetugasOrmDAO.java` | Akses data petugas melalui ORM. |
| `HasilPemeriksaanOrmDAO.java` | Akses data hasil pemeriksaan melalui ORM. |
| `JPAUtil.java` | Menyediakan `EntityManager`. |
| `persistence.xml` | Konfigurasi persistence unit Hibernate. |

ORM menjadi nilai tambah karena pengelolaan data petugas dan hasil pemeriksaan dilakukan melalui objek Java tanpa menuliskan perintah SQL secara langsung pada setiap operasi CRUD.

---

## 9. Database dan Dummy Data

Database yang digunakan bernama `laboratorium_kesehatan`.

File SQL yang digunakan dalam project:

```text
database/laboratorium_kesehatan.sql
```

File tersebut berisi struktur database dan dummy data awal yang dimasukkan langsung melalui SQL, bukan melalui kode Java.

Dummy data diperlukan agar ketika program pertama kali dijalankan, fitur baca data dapat langsung menampilkan data yang tersedia.

### Cara Menggunakan File SQL

1. Jalankan MySQL melalui XAMPP atau layanan database yang digunakan.
2. Buka phpMyAdmin atau aplikasi pengelola database lainnya.
3. Import file `database/laboratorium_kesehatan.sql`.
4. Pastikan database dan tabel berhasil dibuat.
5. Periksa konfigurasi koneksi pada `DatabaseConnection.java` dan `persistence.xml`.
6. Jalankan program melalui `Main.java`.

**Catatan:** Nama tabel, kolom, tipe data, primary key, foreign key, dan dummy data di file SQL harus sesuai dengan kebutuhan kode JDBC dan pemetaan entity Hibernate.

---


## 10. Kesimpulan

Program Sistem Manajemen Laboratorium Kesehatan merupakan pengembangan Mini Project 3 yang menggabungkan konsep Pemrograman Berorientasi Objek dengan pengelolaan database MySQL.

Konsep encapsulation diterapkan melalui atribut private dan getter-setter, inheritance melalui hubungan class `Petugas`, `Analis`, dan `Dokter`, polymorphism melalui overriding dan overloading method, serta abstraction melalui abstract class dan interface.

Koneksi database dilakukan menggunakan JDBC, PreparedStatement, dan pola DAO. Sebagai nilai tambah, program menerapkan ORM menggunakan JPA/Hibernate untuk mengelola data petugas dan hasil pemeriksaan.

Dengan pembagian tanggung jawab melalui MVC dan DAO, program menjadi lebih terstruktur serta memisahkan logika program, tampilan, dan akses database.
