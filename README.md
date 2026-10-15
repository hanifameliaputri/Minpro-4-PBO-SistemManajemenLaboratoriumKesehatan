# Minpro-3-PBO-SistemManajemenLaboratoriumKesehatan

**Nama:** Hanif Amelia Putri  
**Kelas:** B  
**NIM:** 2509116075  

---

## 1. Deskripsi Singkat Program

Program **Sistem Manajemen Laboratorium Kesehatan** adalah program berbasis Java (console) yang digunakan untuk mengelola data pasien, petugas laboratorium, pemeriksaan, dan hasil pemeriksaan. Program ini merupakan pengembangan dari Mini Project 2 dengan tambahan penerapan **abstraction** (abstract class dan abstract method), **polymorphism** (overriding dan overloading), **struktur MVC**, serta **interface** sebagai nilai tambah.

Fitur utama aplikasi:

- **Pendaftaran Pemeriksaan**: mendaftarkan pasien (baru atau yang sudah terdaftar) ke suatu jenis pemeriksaan yang ditangani oleh petugas tertentu.
- **Kelola Pasien**: tambah, lihat semua, cari, dan hapus data pasien.
- **Kelola Petugas**: tambah **Analis** atau **Dokter** dan lihat semua petugas.
- **Kelola Pemeriksaan**: tambah, lihat semua, cari, ubah, dan hapus jenis pemeriksaan beserta biayanya.
- **Kelola Hasil Pemeriksaan**: input hasil pemeriksaan, lihat semua hasil, dan lihat riwayat hasil per pasien.
- **Validasi input** agar program tidak berhenti akibat kesalahan pengetikan pengguna.
- **Dummy data** awal sehingga data langsung tersedia saat program pertama kali dijalankan.

Seluruh data disimpan sementara selama program berjalan menggunakan `ArrayList`.

---

## 2. Penjelasan Struktur Package

Program menerapkan struktur **MVC (Model, View, Controller)** agar tanggung jawab setiap bagian jelas.

```text
LaboratoriumKesehatan/src/main/java/
|
|-- Main/
|   '-- Main.java                     <- Titik awal program
|
|-- controller/                       <- [CONTROLLER]
|   '-- LaboratoriumController.java   (alur menu, pengelola ArrayList, pencarian, validasi input)
|
|-- model/                            <- [MODEL]
|   |-- Identitas.java                (interface)
|   |-- Petugas.java                  (abstract class, implements Identitas)
|   |-- Analis.java                   (subclass Petugas)
|   |-- Dokter.java                   (subclass Petugas)
|   |-- Pasien.java
|   |-- Pemeriksaan.java
|   '-- HasilPemeriksaan.java
|
'-- view/                             <- [VIEW]
    '-- LaboratoriumView.java         (menu, judul, pesan, dan konfirmasi pendaftaran)
```


<img width="428" height="450" alt="image" src="https://github.com/user-attachments/assets/8248dfc7-9bb1-4bb2-88be-9e26307b4374" />


| Package | Peran |
|---|---|
| `Main` | Membuat `LaboratoriumController` dan menjalankan `jalankanProgram()`. |
| `controller` | Menerima input pengguna, memvalidasinya, mengelola `ArrayList`, dan memanggil View untuk menampilkan hasil. |
| `model` | Menyimpan struktur data, encapsulation, hierarki pewarisan (`Petugas`, `Analis`, `Dokter`), abstract class, dan interface. |
| `view` | Khusus menampilkan menu, judul, dan pesan ke terminal. |

---

## 3. Penjelasan Alur Program

### Alur Umum

```text
[Start] Main.main()
        |
        v
new LaboratoriumController()
  |-- membuat 4 ArrayList (Pasien, Petugas, Pemeriksaan, HasilPemeriksaan)
  |-- mengatur counter ID awal (nextId... = 1)
  |-- membuat objek LaboratoriumView
  '-- isiDataAwal()  -> mengisi dummy data
        |
        v
controller.jalankanProgram()
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

1. **Program dimulai dari `Main.java`.** Method `main()` membuat objek `LaboratoriumController` lalu memanggil `jalankanProgram()`.
2. **Konstruktor Controller** menyiapkan empat `ArrayList`, counter ID otomatis, objek `LaboratoriumView`, lalu memanggil `isiDataAwal()` untuk memasukkan dummy data.
3. **`jalankanProgram()`** menjalankan perulangan `while` yang terus menampilkan menu utama sampai pengguna memilih menu 6. Input menu dibaca dengan `bacaInt()` sehingga huruf atau input kosong tidak membuat program error. Pilihan di luar 1-6 menampilkan pesan `Pilihan tidak tersedia.`
4. **Setiap sub-menu** (menu 2 sampai 5) memiliki perulangan sendiri dan baru kembali ke menu utama ketika pengguna memilih opsi *Kembali*.
5. **Setiap pertanyaan input langsung menampilkan contoh format yang valid**, misalnya `Jenis Kelamin (Laki-laki/Perempuan):`, `Umur (1-120):`, dan `Status (Normal/Tidak Normal):`, sehingga pengguna tahu format yang benar sebelum mengetik.

Tampilan menu utama saat program pertama kali dijalankan:

<img height="200" alt="image" src="https://github.com/user-attachments/assets/e08d72bc-32c8-4a46-bfd7-ce88df167df1" />

### Menu 1 - Pendaftaran Pemeriksaan

```text
Pilih pasien
  |-- 1. Pasien Baru       -> input nama, umur, jenis kelamin, keluhan -> tambahPasien() (ID otomatis: P1, P2, ...)
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

Data pendaftaran terakhir disimpan pada tiga atribut Controller (`pasienTerdaftar`, `pemeriksaanTerdaftar`, `petugasTerdaftar`) dan dipakai kembali oleh menu 5 saat menginput hasil.

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

1. Pengguna memilih **2. Pasien Sudah Terdaftar**, lalu program menampilkan semua pasien (`P1` dan `P2`). Pasien `P2` adalah pasien yang sebelumnya didaftarkan sebagai pasien baru, sehingga terlihat bahwa data tersimpan di `ArrayList`.
2. Pengguna memasukkan ID pasien (`p1`). Pencarian tidak membedakan huruf besar dan kecil (`equalsIgnoreCase`), sehingga `p1` tetap ditemukan sebagai `P1`.
3. Pengguna memilih pemeriksaan (`PM1`) dan petugas (`PT1`).
4. Program menampilkan **konfirmasi pendaftaran** berisi ID dan nama pasien, jenis pemeriksaan, biaya, petugas, serta status `Terdaftar`.
5. Data pendaftaran disimpan pada `pasienTerdaftar`, `pemeriksaanTerdaftar`, dan `petugasTerdaftar` untuk dipakai pada menu **Input Hasil Pemeriksaan**.

### Menu 2 - Kelola Pasien

Menu ini berisi empat fitur: tambah pasien, lihat semua pasien, cari pasien berdasarkan ID (ditampilkan lengkap dengan umur, jenis kelamin, dan keluhan), dan hapus pasien.

<img height="205" alt="image" src="https://github.com/user-attachments/assets/8bf91a83-2c9f-47aa-8841-f9337e5d3e0c" />

**Tambah pasien.** 

<img height="202" alt="image" src="https://github.com/user-attachments/assets/2128553d-56bd-40a2-b5dd-ea49f3dd3ca8" />


**Lihat semua pasien.**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/e69c3b35-7371-4439-b58e-ac83c7d6180a" />

**Cari pasien berdasarkan ID.**

<img height="220" alt="image" src="https://github.com/user-attachments/assets/a63b2cdb-bf74-4d28-b62b-39b92a2b11d3" />

**Hapus pasien.**

<img height="203" alt="image" src="https://github.com/user-attachments/assets/0922ac9e-a677-4687-8b1b-758f83c2cd66" />


### Menu 3 - Kelola Petugas

Menu ini digunakan untuk menambah **Analis**, menambah **Dokter**, dan melihat semua petugas. Objek `Analis` dan `Dokter` disimpan dalam satu `ArrayList<Petugas>`.

<img height="200" alt="image" src="https://github.com/user-attachments/assets/f894cbeb-39ae-4cf6-965c-41068a27e47f" />

**Tambah analis.**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/7a3e1e03-a44c-440a-8a73-410055eb1a78" />


**Tambah dokter.**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/bf9cf0b6-4308-4981-95fe-d626e9eeed0a" />


**Lihat semua petugas.** Pada tampilan ini terlihat hasil **overriding** dan **polymorphism**: `Analis` menampilkan spesialisasi, sedangkan `Dokter` menampilkan nomor STR. Baris kedua (umur dan jenis kelamin) muncul karena `tampilkanInfo(true)` dipanggil (overloading). Penjelasan lengkapnya ada di [bagian 5](#5-penerapan-polymorphism-dan-abstraction).

<img height="252" alt="image" src="https://github.com/user-attachments/assets/21810661-f7ae-4a3f-9a03-6e699b25f546" />


### Menu 4 - Kelola Pemeriksaan

Menu ini berisi lima fitur: tambah, lihat semua, cari, ubah (nama dan biaya), dan hapus pemeriksaan.

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

Jenis pemeriksaan dan petugas pada hasil diambil dari pendaftaran terakhir (menu 1), sedangkan ID pasien diinput ulang oleh pengguna. Pada tampilan hasil, program menampilkan **nama pasien** (bukan ID pasien) beserta nama jenis tes dan nama petugas yang memeriksa, sehingga lebih mudah dibaca.

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

## 4. Penerapan Encapsulation dan Inheritance

### A. Encapsulation

Encapsulation diterapkan dengan **menyembunyikan atribut** di dalam class dan hanya membolehkan akses melalui method resmi (**getter** dan **setter**).

**1. Atribut bersifat `private`**

Seluruh atribut pada package `model` berstatus `private`:

| Class | Atribut `private` |
|---|---|
| `Pasien` | `id`, `nama`, `umur`, `jenisKelamin`, `keluhan` |
| `Petugas` | `id`, `nama`, `umur`, `jenisKelamin` |
| `Analis` | `spesialisasiBidang` |
| `Dokter` | `nomorSTR` |
| `Pemeriksaan` | `idPemeriksaan`, `namaPemeriksaan`, `biaya` |
| `HasilPemeriksaan` | `idHasil`, `idPasien`, `idPemeriksaan`, `idPetugas`, `hasil`, `status` |

<img height="200" alt="image" src="https://github.com/user-attachments/assets/abf510b6-16aa-44ea-ba62-d0f15ee1a935" />

Atribut tersebut tidak dapat diakses langsung dari luar class (misalnya dari Controller atau View). Controller dan View harus memakai getter, contohnya `pasien.getNama()` atau `pemeriksaan.getBiaya()`.

**2. Getter dan setter bersifat `public`**

<img height="200" alt="image" src="https://github.com/user-attachments/assets/39fc70a5-ef99-41bb-ad16-69195176263d" />

**3. Setter berfungsi sebagai validasi**

Setter tidak hanya mengisi nilai, tetapi juga menyaring data sehingga object selalu berisi data yang valid.

| Setter | Aturan validasi |
|---|---|
| `setNama()`, `setJenisKelamin()`, `setKeluhan()`, `setSpesialisasiBidang()`, `setNomorSTR()`, `setNamaPemeriksaan()`, `setHasil()`, `setStatus()` | Tidak boleh `null` atau kosong |
| `setUmur()` | Harus lebih dari 0 |
| `setBiaya()` | Tidak boleh negatif |

<img height="200" alt="image" src="https://github.com/user-attachments/assets/28c6e143-284d-4e99-8d5b-f7bde0d036bf" />

**4. Konstruktor memakai setter**

Agar validasi juga berlaku saat object dibuat, konstruktor memanggil setter, bukan mengisi atribut secara langsung:

<img height="200" alt="image" src="https://github.com/user-attachments/assets/45ccf3cc-1254-4052-a3a0-c9070cebdd71" />

**5. Access modifier `protected` dan class `final`**

- Method `tampilkanInfoDasar()` di `Petugas` bersifat `protected`, sehingga hanya bisa dipakai oleh `Petugas` dan subclass-nya (`Analis` dan `Dokter`), tidak oleh class lain.
- Daftar data di Controller dibuat `private final` (`daftarPasien`, `daftarPetugas`, `daftarPemeriksaan`, `daftarHasil`), sehingga hanya bisa dimodifikasi lewat method Controller seperti `tambahPasien()` dan `hapusPasien()`.
- Class `HasilPemeriksaan` dideklarasikan `final` agar tidak dapat diturunkan.

---

### B. Inheritance

Inheritance diterapkan pada **1 superclass** (`Petugas`) dan **2 subclass** (`Analis` dan `Dokter`).

```text
          << interface >>
             Identitas
                 ^
                 | implements
   +---------------------------+
   |   Petugas (abstract)      |   <- Superclass
   +---------------------------+
   | - id                      |
   | - nama                    |
   | - umur                    |
   | - jenisKelamin            |
   +---------------------------+
   | # tampilkanInfoDasar()    |
   | + tampilkanInfo()  {abstract}
   | + tampilkanInfo(boolean)  |
   +---------------------------+
                 ^
                 | extends
     +-----------+-----------+
     |                       |
+-----------------+   +-----------------+
|     Analis      |   |     Dokter      |   <- Subclass
+-----------------+   +-----------------+
| - spesialisasi  |   | - nomorSTR      |
|   Bidang        |   |                 |
+-----------------+   +-----------------+
```

1. **Superclass `Petugas`** menyimpan data dan perilaku yang dimiliki semua petugas: `id`, `nama`, `umur`, `jenisKelamin`, getter/setter, serta method `tampilkanInfo()`.
2. **Subclass `Analis`** mewarisi `Petugas` dan menambahkan atribut khusus `spesialisasiBidang` (contoh: Hematologi).
3. **Subclass `Dokter`** mewarisi `Petugas` dan menambahkan atribut khusus `nomorSTR` (nomor Surat Tanda Registrasi).

Pewarisan dituliskan dengan keyword `extends`:

<img height="200" alt="image" src="https://github.com/user-attachments/assets/03720126-602d-48f5-954f-f791ca6e409f" />

dan

<img height="200" alt="image" src="https://github.com/user-attachments/assets/bfb0f14c-cdca-4c02-bd08-92148dc6002a" />

Konstruktor subclass memanggil konstruktor superclass dengan `super(...)`, lalu mengisi atribut miliknya sendiri lewat setter:

<img height="200" alt="image" src="https://github.com/user-attachments/assets/a03aa779-3f7e-4204-81e5-53bf3126b198" />

**Manfaat pewarisan pada program ini:**

- Kode `id`, `nama`, `umur`, `jenisKelamin` cukup ditulis sekali di `Petugas`. `Analis` dan `Dokter` langsung memakainya. Contohnya `analis.getId()` di Controller adalah method yang diwarisi dari `Petugas`.
- Karena `Analis` dan `Dokter` adalah `Petugas`, keduanya bisa disimpan dalam satu `ArrayList<Petugas>`.

**Hubungan dengan encapsulation:** atribut `id` dan `nama` bersifat `private` di `Petugas`, sehingga subclass tidak mengaksesnya langsung. Subclass memakai `super(...)` untuk mengisi data tersebut dan method `protected` `tampilkanInfoDasar()` untuk mengambil teks ID dan nama.

> Class `Pasien` berdiri sendiri (tidak mewarisi `Petugas`) karena pasien bukan petugas dan memiliki atribut `keluhan`.

---

## 5. Penerapan Polymorphism dan Abstraction

### A. Polymorphism

**Polymorphism** adalah kemampuan satu nama method menghasilkan perilaku yang berbeda. Pada program ini polymorphism diterapkan melalui **overriding** dan **overloading**.

#### 1. Overriding

**Overriding** adalah ketika subclass menulis ulang method milik superclass dengan **nama, parameter, dan tipe kembalian yang sama** agar perilakunya sesuai kebutuhan subclass. Method `tampilkanInfo()` dideklarasikan sebagai abstract method di `Petugas`, lalu diisi (di-override) oleh `Analis` dan `Dokter`.

| Method di superclass | Class yang meng-override | Lokasi |
|---|---|---|
| `Petugas.tampilkanInfo()` (abstract) | `Analis` | `model/Analis.java` (baris 35-42) |
| `Petugas.tampilkanInfo()` (abstract) | `Dokter` | `model/Dokter.java` (baris 35-42) |

Method di superclass (`model/Petugas.java`, baris 74-75):


<img height="220" alt="image" src="https://github.com/user-attachments/assets/a75c7189-e460-475c-9896-4f221be02020" />


Versi di subclass `Analis`:

<img height="242" alt="image" src="https://github.com/user-attachments/assets/ef4695ec-fd0d-4ada-a80b-fef14896c419" />


Versi di subclass `Dokter`:

<img height="222" alt="image" src="https://github.com/user-attachments/assets/682844dc-a0bc-4eb9-8875-b0117cfd8830" />


**Cara kerja overriding (dynamic method dispatch).** Overriding dipakai ketika pengguna membuka **Kelola Petugas -> Lihat Semua Petugas** (juga saat memilih petugas pada menu Pendaftaran Pemeriksaan). Kedua fitur tersebut memanggil `tampilkanSemuaPetugas()` di `LaboratoriumController` (baris 541-550):

```java
private final ArrayList<Petugas> daftarPetugas;   // berisi Analis dan Dokter

public void tampilkanSemuaPetugas() {
    ...
    for (Petugas p : daftarPetugas) {
        p.tampilkanInfo(true);
        System.out.println("------------------------------------");
    }
}
```

```text
tampilkanSemuaPetugas()                  (Controller)
   for (Petugas p : daftarPetugas)
        p.tampilkanInfo(true)            (Petugas.java baris 77)
              |
              |-- 1. memanggil tampilkanInfo()
              |        |-- jika p adalah Analis -> Analis.tampilkanInfo()
              |        '-- jika p adalah Dokter -> Dokter.tampilkanInfo()
              |
              '-- 2. karena detail = true -> cetak "Umur ... | Jenis Kelamin ..."
```

Langkah kerjanya:

1. Variabel `p` bertipe **`Petugas`**, tetapi object sebenarnya di dalam list bisa berupa `Analis` atau `Dokter`.
2. Program memanggil `p.tampilkanInfo(true)`. Method ini berada di `Petugas` dan memanggil `tampilkanInfo()`.
3. Saat program berjalan, Java melihat **tipe object sebenarnya** (bukan tipe variabelnya) lalu menjalankan versi `tampilkanInfo()` milik class tersebut. Mekanisme ini disebut *dynamic method dispatch*.
4. Jika object adalah `Analis`, tampil `Peran: Analis` dan spesialisasi. Jika object adalah `Dokter`, tampil `Peran: Dokter` dan nomor STR.
5. Hasilnya, satu perintah yang sama menghasilkan tampilan berbeda sesuai jenis petugasnya, tanpa perlu `if` untuk mengecek jenis petugas.

Contoh output program:

```text
ID: PT1 | Nama: Asti Putri | Peran: Analis | Spesialisasi/Bidang: Hematologi
Umur: 29 | Jenis Kelamin: Laki-Laki
------------------------------------
ID: PT2 | Nama: dr. Hanif Amelia Putri | Peran: Dokter | No. STR: STR-123456789
Umur: 25 | Jenis Kelamin: Perempuan
------------------------------------
```

**Peran `tampilkanInfoDasar()`:** setiap subclass memanggil `tampilkanInfoDasar()` (method `protected` milik `Petugas`) agar tidak menulis ulang bagian `ID: ... | Nama: ...`. Subclass hanya menambahkan informasi khususnya.

**Catatan:** Anotasi `@Override` membuat compiler memeriksa bahwa method benar-benar menimpa method milik superclass. Jika nama atau parameternya salah, program tidak dapat dikompilasi.

#### 2. Overloading

**Overloading** adalah beberapa method dengan **nama yang sama tetapi parameter berbeda** dalam satu class.

| Class | Method overload | Perbedaan |
|---|---|---|
| `Petugas` | `tampilkanInfo()` dan `tampilkanInfo(boolean detail)` | Versi kedua menerima parameter `detail`; jika `true`, ditambah baris umur dan jenis kelamin |
| `Pasien` | `tampilkanInfo()` dan `tampilkanInfo(boolean detail)` | Versi kedua menampilkan data lengkap pasien termasuk keluhan |
| `LaboratoriumController` | `bacaInt(Scanner)` dan `bacaInt(Scanner, int min, int max)` | Versi kedua membatasi angka dalam rentang `min` sampai `max` |

Overloading pada `Petugas` (`model/Petugas.java`, baris 77-82):

```java
public void tampilkanInfo(boolean detail) {
    tampilkanInfo();   // versi Dokter/Analis yang jalan (polymorphism)
    if (detail) {
        System.out.println("Umur: " + getUmur() + " | Jenis Kelamin: " + getJenisKelamin());
    }
}
```

Overloading pada Controller (`bacaInt`, baris 766-793). Versi tanpa batas dipakai untuk pilihan menu, sedangkan `bacaUmur()` memakai versi dengan batas 1 sampai 120:

```java
private int bacaInt(Scanner scanner) {
    return bacaInt(scanner, Integer.MIN_VALUE, Integer.MAX_VALUE);
}

private int bacaInt(Scanner scanner, int min, int max) {
    // ... validasi angka dalam rentang min-max
}

private int bacaUmur(Scanner scanner) {
    return bacaInt(scanner, 1, 120);
}
```

> Perbedaan keduanya: **overloading** terjadi dalam satu class dengan parameter berbeda, sedangkan **overriding** terjadi antara superclass dan subclass dengan parameter yang sama.

---

### B. Abstraction

**Abstraction** adalah menyembunyikan detail dan hanya menampilkan kerangka umum. Pada program ini abstraction diterapkan dengan **abstract class** dan **abstract method** pada class `Petugas`.

**1. Abstract class `Petugas`** (`model/Petugas.java`, baris 11):

<img height="247" alt="image" src="https://github.com/user-attachments/assets/6c04664c-fb2a-4546-9e5a-4c3602deed5b" />


`Petugas` dibuat abstract karena "petugas" secara umum tidak punya peran yang jelas. Di laboratorium, petugas pasti berperan sebagai **Analis** atau **Dokter**. Karena itu object `Petugas` tidak boleh dibuat langsung. Jika dicoba `new Petugas(...)`, compiler menolaknya:

```text
error: Petugas is abstract; cannot be instantiated
```

**2. Abstract method `tampilkanInfo()`** (`model/Petugas.java`, baris 75):

<img height="220" alt="image" src="https://github.com/user-attachments/assets/6002698d-4db5-4949-b6d2-32d578d5a68d" />


Abstract method hanya memiliki **kerangka** (nama, parameter, tipe kembalian) tanpa isi. Setiap subclass **wajib** mengisinya. Alasannya, tidak ada satu cara tampil yang cocok untuk semua petugas: `Analis` menampilkan spesialisasi, sedangkan `Dokter` menampilkan nomor STR. Jika subclass lupa mengisinya, program tidak dapat dikompilasi.

**3. Abstract class boleh berisi anggota biasa.** Selain abstract method, `Petugas` tetap memiliki anggota yang sudah berisi dan dipakai bersama oleh semua subclass:

| Anggota di `Petugas` | Jenis | Fungsi |
|---|---|---|
| `id`, `nama`, `umur`, `jenisKelamin` | Atribut `private` | Data umum semua petugas |
| `getter` dan `setter` | Method biasa (sudah berisi) | Akses dan validasi data |
| `tampilkanInfoDasar()` | Method `protected` (sudah berisi) | Menghasilkan teks `ID: ... \| Nama: ...` |
| `tampilkanInfo(boolean detail)` | Method biasa (sudah berisi) | Memanggil `tampilkanInfo()` lalu menambah umur dan jenis kelamin jika `detail = true` |
| `tampilkanInfo()` | **Abstract method** (belum berisi) | Diisi oleh `Analis` dan `Dokter` |

---

## 6. Penerapan Nilai Tambah

### Interface

Nilai tambah yang diterapkan pada program ini adalah **interface**, yaitu `Identitas`.

**Letak penerapan:**

| File | Baris | Keterangan |
|---|---|---|
| `model/Identitas.java` | 19-21 | Deklarasi interface `Identitas` |
| `model/Petugas.java` | 11 | `Petugas` melakukan `implements Identitas` |
| `model/Petugas.java` | 74-75 | Method `tampilkanInfo()` dari interface dideklarasikan ulang sebagai abstract |
| `model/Analis.java` dan `model/Dokter.java` | 35-42 | Mengisi (mengimplementasikan) `tampilkanInfo()` |

**Isi interface** (`model/Identitas.java`):

<img height="220" alt="image" src="https://github.com/user-attachments/assets/ff0fd0e7-009e-4306-9d18-d49c153ece0e" />


**Cara kerjanya:**

```text
<< interface >>
   Identitas            -> kontrak: setiap Identitas harus bisa tampilkanInfo()
       ^
       | implements
Petugas (abstract)      -> menerima kontrak, tetapi belum mengisinya (abstract)
       ^
       | extends
Analis / Dokter         -> mengisi tampilkanInfo() sesuai perannya masing-masing
```

1. **Interface** adalah kontrak yang hanya berisi nama method tanpa isi. Class yang menandatangani kontrak dengan `implements` wajib memiliki method tersebut.
2. `Identitas` menetapkan satu kontrak: object yang termasuk `Identitas` harus bisa menampilkan informasi dirinya lewat `tampilkanInfo()`.
3. `Petugas` melakukan `implements Identitas`. Karena `Petugas` adalah abstract class, ia boleh tidak mengisi method tersebut dan menyerahkannya ke subclass.
4. `Analis` dan `Dokter` mengisi `tampilkanInfo()` sehingga kontrak terpenuhi. Dengan demikian `Analis` dan `Dokter` termasuk `Identitas`.

**Perbedaan interface dan abstract class pada program ini:**

| | Interface `Identitas` | Abstract class `Petugas` |
|---|---|---|
| Isi | Hanya kontrak method (tanpa atribut dan tanpa isi method) | Atribut, method biasa, dan abstract method |
| Dipakai dengan | `implements` | `extends` |
| Tujuan di program | Menetapkan bahwa object harus bisa menampilkan info | Menyimpan data dan perilaku bersama semua petugas |

---

## 7. Informasi Tambahan

### Perbaikan dari Catatan Asisten Praktikum

| Catatan asisten | Perbaikan | Letak |
|---|---|---|
| Input sebaiknya langsung diberi contoh, misalnya Jenis Kelamin (Laki-laki/Perempuan), bukan baru diberi tahu setelah salah | Setiap prompt menampilkan contoh format: `Nama (huruf saja)`, `Umur (1-120)`, `Jenis Kelamin (Laki-laki/Perempuan)`, `Status (Normal/Tidak Normal)`, `Biaya (angka, >= 0)` | `LaboratoriumController` (semua method input) |
| Pada fitur ubah, sebaiknya tekan Enter jika tidak ingin mengganti data | Fitur ubah pemeriksaan menampilkan nilai lama dan menerima Enter untuk mempertahankannya | `ubahPemeriksaanDariInput()`, `bacaTeksOpsional()`, `bacaDoubleOpsional()` |
| Saat menampilkan hasil pemeriksaan sebaiknya nama, bukan ID pasien | Hasil pemeriksaan menampilkan nama pasien, nama jenis tes, dan nama petugas | `formatHasil()` |
| README: penjelasan alur program dan output program disatukan | Output (screenshot) kini berada di dalam bagian Penjelasan Alur Program, tidak lagi dipisah | Bagian 3 README ini |

### Validasi Input

Validasi dilakukan di dua lapis: pada **method baca input di Controller** dan pada **setter di Model**.

| Method | Fungsi |
|---|---|
| `bacaInt()` | Memastikan input berupa angka bulat (`try-catch NumberFormatException`) dan berada dalam rentang tertentu |
| `bacaUmur()` | Umur harus antara 1 sampai 120 |
| `bacaDouble()` | Memastikan input berupa angka dan tidak negatif (untuk biaya) |
| `bacaNama()` | Nama tidak boleh kosong dan hanya boleh berisi huruf, spasi, dan titik |
| `bacaJenisKelamin()` | Hanya menerima `Laki-laki` atau `Perempuan` |
| `bacaStatus()` | Hanya menerima `Normal` atau `Tidak Normal` |
| `bacaTeksTidakKosong()` | Teks tidak boleh kosong |
| `bacaTeksOpsional()` dan `bacaDoubleOpsional()` | Dipakai pada fitur ubah; Enter kosong mempertahankan nilai lama |

Jika input salah, program meminta pengguna memasukkan ulang tanpa berhenti.

### Dummy Data

Saat program pertama kali dijalankan, `isiDataAwal()` mengisi data berikut agar menu *Lihat* langsung menampilkan isi:

| Jenis Data | ID | Isi |
|---|---|---|
| Pasien | `P1` | Aulia Ashylla P, 19 tahun, Perempuan, keluhan demam dan batuk sejak 3 hari |
| Analis | `PT1` | Asti Putri, 29 tahun, spesialisasi Hematologi |
| Dokter | `PT2` | dr. Hanif Amelia Putri, 25 tahun, STR-123456789 |
| Pemeriksaan | `PM1` | Tes Darah Lengkap - Rp150.000 |
| Pemeriksaan | `PM2` | Tes Urine - Rp100.000 |
| Pemeriksaan | `PM3` | Tes Gula Darah - Rp75.000 |
| Hasil Pemeriksaan | `H1` | Pasien `P1`, `PM1`, petugas `PT1`, "Hemoglobin 13.5 g/dL, Leukosit normal", status Normal |

### Konsep PBO yang Diterapkan

| Konsep | Penerapan |
|---|---|
| Class & Object | `Pasien`, `Petugas`, `Analis`, `Dokter`, `Pemeriksaan`, dan `HasilPemeriksaan` |
| Constructor | Membuat object dan mengisi data awal |
| Access Modifier | Atribut `private`, method `public`, dan `tampilkanInfoDasar()` `protected` |
| Encapsulation | Data diakses melalui getter dan setter |
| Inheritance | `Analis` dan `Dokter` mewarisi `Petugas` |
| Abstraction | `Petugas` adalah abstract class dengan abstract method `tampilkanInfo()` |
| Polymorphism (overriding) | `tampilkanInfo()` diisi berbeda oleh `Analis` dan `Dokter`; dipanggil lewat `ArrayList<Petugas>` |
| Polymorphism (overloading) | `tampilkanInfo()` dan `tampilkanInfo(boolean)`, serta `bacaInt(Scanner)` dan `bacaInt(Scanner, int, int)` |
| Interface (nilai tambah) | `Identitas` diimplementasikan oleh `Petugas` |
| MVC | Program dibagi menjadi package `Main`, `controller`, `model`, dan `view` |
| ArrayList | Menyimpan data selama program berjalan |
| Validasi | Memeriksa input pengguna |

---

## 8. Kesimpulan

Program Sistem Manajemen Laboratorium Kesehatan merupakan pengembangan dari Mini Project 2 yang menambahkan penerapan **abstraction**, **polymorphism**, **struktur MVC**, dan **interface**.

`Petugas` dijadikan abstract class dengan abstract method `tampilkanInfo()` yang diisi berbeda oleh `Analis` dan `Dokter`. Karena keduanya disimpan dalam satu `ArrayList<Petugas>`, satu perintah yang sama menghasilkan tampilan berbeda sesuai jenis petugasnya. Interface `Identitas` menjadi kontrak bahwa petugas harus bisa menampilkan informasi dirinya, dan struktur package `model`, `view`, dan `controller` membuat tanggung jawab setiap bagian program lebih jelas.

Program juga telah diperbaiki sesuai catatan di spreadsheet: prompt input menampilkan contoh format, fitur ubah mendukung Enter untuk mempertahankan data, dan hasil pemeriksaan menampilkan nama pasien.
