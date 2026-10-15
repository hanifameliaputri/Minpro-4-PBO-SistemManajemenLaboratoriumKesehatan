## 7. Penerapan Nilai Tambah

### A. ORM (JPA dengan Hibernate)

**Nilai tambah yang diterapkan: ORM (Object Relational Mapping) memakai JPA dengan Hibernate.** ORM memetakan class Java ke tabel database, sehingga operasi CRUD dilakukan lewat object tanpa menulis SQL manual. ORM dipakai untuk entity **Petugas** (beserta `Dokter` dan `Analis`) dan **Hasil Pemeriksaan**, berjalan bersamaan dengan JDBC untuk Pasien dan Pemeriksaan.

**Letak penerapan:**

| Lokasi | Penerapan |
|---|---|
| `pom.xml` | Dependensi `hibernate-core`. |
| `src/main/resources/META-INF/persistence.xml` | Konfigurasi persistence unit `laboratoriumPU`: koneksi, dialect MySQL, dan daftar entity. `hibernate.hbm2ddl.auto = none` sehingga tabel dan dummy data tetap berasal dari file `.sql`. `show_sql` dimatikan agar console bersih. |
| `model/Petugas.java`, `Dokter.java`, `Analis.java`, `HasilPemeriksaan.java` | Anotasi JPA: `@Entity`, `@Table`, `@Id`, `@Column`, serta `@Inheritance` dan `@DiscriminatorValue` untuk hierarki `Petugas`. |
| `koneksi/JPAUtil.java` | Membuat `EntityManagerFactory` sekali dan menyediakan `EntityManager`. |
| `dao/OrmDAO.java` | CRUD generik memakai `EntityManager` dan JPQL. |
| `dao/PetugasOrmDAO.java`, `dao/HasilPemeriksaanOrmDAO.java` | DAO ORM untuk Petugas dan Hasil. |
| `Main/Main.java` | Membuat DAO JDBC dan DAO ORM, lalu mengirim keduanya ke Controller. |

**Pemetaan entity.** Contoh `HasilPemeriksaan.java`:

```java
@Entity
@Table(name = "hasil_pemeriksaan")
public class HasilPemeriksaan {
    @Id
    @Column(name = "id_hasil")
    private String idHasil;

    @Column(name = "id_pasien")
    private String idPasien;
    ...
}
```

Pewarisan `Petugas` - `Dokter`/`Analis` dipetakan ke satu tabel `petugas`, dibedakan oleh kolom `tipe_petugas`:

```java
@Entity
@Table(name = "petugas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipe_petugas", discriminatorType = DiscriminatorType.STRING)
public abstract class Petugas implements Identitas { ... }

@Entity
@DiscriminatorValue("Dokter")
public class Dokter extends Petugas { ... }
```

Hasilnya, query pada `Petugas` otomatis mengembalikan object `Dokter` atau `Analis` sesuai kolom `tipe_petugas`.

**CRUD dengan ORM.** Logika CRUD ada di `OrmDAO<T>`, sedangkan `PetugasOrmDAO` dan `HasilPemeriksaanOrmDAO` hanya mengisi nama entity dan atribut ID:

| Operasi | Method di `OrmDAO` | Yang dipakai |
|---|---|---|
| Create | `tambah()` | `em.persist()` |
| Read | `semua()`, `cariById()` | JPQL `SELECT e FROM Petugas e`, `em.find()` |
| Update | `ubah()` | `em.merge()` |
| Delete | `hapus()` | `em.remove()` |

`HasilPemeriksaanOrmDAO` juga memiliki `cariByPasien()` yang memakai JPQL berparameter untuk riwayat hasil per pasien. Operasi tulis dibungkus transaksi (`begin`, `commit`, dan `rollback` jika gagal).

**Log Hibernate.** Pada `Main.java`, log level INFO dan WARN dari Hibernate disembunyikan sehingga console hanya menampilkan menu program dan error penting.

**Hubungan dengan JDBC.** JDBC dan ORM berjalan bersamaan di program yang sama. Keduanya mengikuti interface `CrudDAO`, sehingga Controller memperlakukan semuanya dengan cara yang sama.

### B. Interface `Identitas` (tambahan dari Mini Project sebelumnya)

Selain ORM, program tetap mempertahankan **interface** `Identitas` yang diterapkan pada Mini Project sebelumnya.

**Letak penerapan:**

| File | Keterangan |
|---|---|
| `model/Identitas.java` | Deklarasi interface `Identitas` |
| `model/Petugas.java` | `Petugas` melakukan `implements Identitas` |
| `model/Petugas.java` | Method `tampilkanInfo()` dari interface dideklarasikan ulang sebagai abstract |
| `model/Analis.java` dan `model/Dokter.java` | Mengisi (mengimplementasikan) `tampilkanInfo()` |

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

## 8. Informasi Tambahan

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
| `bacaTeksOpsional()`, `bacaDoubleOpsional()`, `bacaNamaOpsional()`, `bacaJenisKelaminOpsional()`, dan `bacaIntOpsional()` | Dipakai pada fitur ubah; Enter kosong mempertahankan nilai lama |

Jika input salah, program meminta pengguna memasukkan ulang tanpa berhenti.

### Dummy Data

Dummy data awal diisi lewat file `database/laboratorium_kesehatan.sql` (bagian `INSERT INTO`), bukan lewat kode Java, sehingga menu *Lihat* langsung menampilkan isi:

| Jenis Data | ID | Isi |
|---|---|---|
| Pasien | `P1` | Aulia Ashylla P, 19 tahun, Perempuan, keluhan demam dan batuk sejak 3 hari |
| Pasien | `P2` | Budi Santoso, 34 tahun, Laki-laki, keluhan sering lemas dan sakit kepala |
| Analis | `PT1` | Asti Putri, 29 tahun, spesialisasi Hematologi |
| Dokter | `PT2` | dr. Hanif Amelia Putri, 25 tahun, STR-123456789 |
| Pemeriksaan | `PM1` | Tes Darah Lengkap - Rp150.000 |
| Pemeriksaan | `PM2` | Tes Urine - Rp100.000 |
| Pemeriksaan | `PM3` | Tes Gula Darah - Rp75.000 |
| Hasil Pemeriksaan | `H1` | Pasien `P1`, `PM1`, petugas `PT1`, "Hemoglobin 13.5 g/dL, Leukosit normal", status Normal |
| Hasil Pemeriksaan | `H2` | Pasien `P2`, `PM3`, petugas `PT1`, "Gula darah sewaktu 210 mg/dL", status Tidak Normal |

### Konsep PBO yang Diterapkan

| Konsep | Penerapan |
|---|---|
| Class & Object | `Pasien`, `Petugas`, `Analis`, `Dokter`, `Pemeriksaan`, dan `HasilPemeriksaan` |
| Constructor | Membuat object dan mengisi data awal |
| Access Modifier | Atribut `private`, method `public`, dan `tampilkanInfoDasar()` `protected` |
| Encapsulation | Data diakses melalui getter dan setter |
| Inheritance | `Analis` dan `Dokter` mewarisi `Petugas` |
| Abstraction | `Petugas` adalah abstract class dengan abstract method `tampilkanInfo()` |
| Polymorphism (overriding) | `tampilkanInfo()` diisi berbeda oleh `Analis` dan `Dokter`; dipanggil lewat `List<Petugas>` |
| Polymorphism (overloading) | `tampilkanInfo()` dan `tampilkanInfo(boolean)`, serta `bacaInt(Scanner)` dan `bacaInt(Scanner, int, int)` |
| Interface | `Identitas` diimplementasikan oleh `Petugas`; `CrudDAO` diimplementasikan semua DAO |
| MVC + DAO | Program dibagi menjadi package `Main`, `controller`, `model`, `view`, dan `dao` |
| JDBC + Prepared Statement | Akses data Pasien dan Pemeriksaan di `PasienDAO` dan `PemeriksaanDAO` |
| DAO | Pemisahan kode akses database dari Controller (package `dao`) |
| ORM / Hibernate (nilai tambah) | Akses data Petugas dan Hasil di `PetugasOrmDAO` dan `HasilPemeriksaanOrmDAO` |
| Database MySQL | Menyimpan data secara permanen; dummy data dari file `.sql` |
| Validasi | Memeriksa input pengguna |

---

## 9. Kesimpulan

Program Sistem Manajemen Laboratorium Kesehatan merupakan pengembangan dari Mini Project 3. Pada Mini Project 4, data dipindahkan dari `ArrayList` ke database MySQL. Akses data dilakukan lewat pola **DAO**, dengan **JDBC (Prepared Statement)** untuk entity Pasien dan Pemeriksaan serta **ORM (JPA/Hibernate)** sebagai nilai tambah untuk entity Petugas dan Hasil Pemeriksaan. Dummy data awal diisi lewat file `database/laboratorium_kesehatan.sql`, bukan lewat kode.

Konsep PBO dari Mini Project sebelumnya tetap dipertahankan: `Petugas` sebagai abstract class dengan abstract method `tampilkanInfo()` yang diisi berbeda oleh `Analis` dan `Dokter`, interface `Identitas`, serta struktur MVC. Interface `CrudDAO` membuat semua DAO (JDBC maupun ORM) memiliki bentuk yang seragam, sehingga Controller tidak perlu mengetahui apakah data diakses dengan JDBC atau ORM.

Program juga telah diperbaiki sesuai catatan di spreadsheet: prompt input menampilkan contoh format, fitur ubah mendukung Enter untuk mempertahankan data, dan hasil pemeriksaan menampilkan nama pasien.
