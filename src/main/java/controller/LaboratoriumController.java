
package controller;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import dao.CrudDAO;
import dao.HasilPemeriksaanOrmDAO;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Scanner;
import model.Analis;
import model.Dokter;
import model.HasilPemeriksaan;
import model.Pasien;
import model.Pemeriksaan;
import model.Petugas;
import view.LaboratoriumView;
 
 
/**
 *
 * @author User
 */
 
public class LaboratoriumController {
 
    /** Aksi yang boleh melempar SQLException, dibungkus oleh aman(). */
    @FunctionalInterface
    private interface AksiDb {
        void jalankan() throws SQLException;
    }
 
    // Tipe interface: Pasien & Pemeriksaan diisi DAO JDBC, Petugas & Hasil diisi DAO ORM
    private final CrudDAO<Pasien> pasienDAO;
    private final CrudDAO<Petugas> petugasDAO;
    private final CrudDAO<Pemeriksaan> pemeriksaanDAO;
    private final HasilPemeriksaanOrmDAO hasilDAO;
 
    private final LaboratoriumView view;
 
    private Pasien pasienTerdaftar;
    private Pemeriksaan pemeriksaanTerdaftar;
    private Petugas petugasTerdaftar;
 
    public LaboratoriumController(CrudDAO<Pasien> pasienDAO,
            CrudDAO<Petugas> petugasDAO,
            CrudDAO<Pemeriksaan> pemeriksaanDAO,
            HasilPemeriksaanOrmDAO hasilDAO) {
        this.pasienDAO = pasienDAO;
        this.petugasDAO = petugasDAO;
        this.pemeriksaanDAO = pemeriksaanDAO;
        this.hasilDAO = hasilDAO;
        view = new LaboratoriumView();
    }
 
    public LaboratoriumView getView() {
        return view;
    }
 
    /**
     * Menjalankan aksi database dan menangkap error SQL di satu tempat,
     * sehingga program tidak berhenti kalau query gagal.
     */
    private void aman(AksiDb aksi) {
        try {
            aksi.jalankan();
        } catch (SQLIntegrityConstraintViolationException e) {
            view.tampilkanPesan("Operasi ditolak: data masih dipakai oleh data lain "
                    + "(misalnya sudah punya hasil pemeriksaan).");
        } catch (SQLException e) {
            view.tampilkanPesan("Terjadi kesalahan database: " + e.getMessage());
        }
    }
 
    // ===================== MENU UTAMA =====================
 
    public void jalankanProgram(Scanner scanner) {
        boolean berjalan = true;
 
        while (berjalan) {
            view.tampilkanMenuUtama();
 
            int pilihan = bacaInt(scanner);
 
            switch (pilihan) {
                case 1 -> aman(() -> pendaftaranPemeriksaan(scanner));
                case 2 -> menuPasien(scanner);
                case 3 -> menuPetugas(scanner);
                case 4 -> menuPemeriksaan(scanner);
                case 5 -> menuHasil(scanner);
                case 6 -> {
                    view.tampilkanPesan("Program selesai. Terima kasih!");
                    berjalan = false;
                }
                default -> view.tampilkanPesan("Pilihan tidak tersedia.");
            }
        }
    }
 
    // ===================== PENDAFTARAN PEMERIKSAAN =====================
 
    private void pendaftaranPemeriksaan(Scanner scanner) throws SQLException {
        view.tampilkanJudul("PENDAFTARAN PEMERIKSAAN");
 
        view.tampilkanPilihanPasien();
 
        int pilihanPasien = bacaInt(scanner);
 
        Pasien pasien;
 
        switch (pilihanPasien) {
            case 1 -> {
                view.tampilkanInputPasienBaru();
                pasien = inputPasienBaru(scanner);
                view.tampilkanPesan(
                        "Pasien berhasil didaftarkan dengan ID: " + pasien.getId());
            }
            case 2 -> {
                tampilkanSemuaPasien();
                System.out.print("Masukkan ID Pasien: ");
                String idPasien = scanner.nextLine().trim();
                pasien = pasienDAO.cariById(idPasien);
                if (pasien == null) {
                    view.tampilkanPesan("Pasien tidak ditemukan.");
                    return;
                }
            }
            default -> {
                view.tampilkanPesan("Pilihan tidak tersedia.");
                return;
            }
        }
 
        view.tampilkanPilihanPemeriksaan();
        tampilkanSemuaPemeriksaan();
 
        System.out.print("Masukkan ID Pemeriksaan: ");
        String idPemeriksaan = scanner.nextLine().trim();
 
        Pemeriksaan pemeriksaan = pemeriksaanDAO.cariById(idPemeriksaan);
 
        if (pemeriksaan == null) {
            view.tampilkanPesan("Pemeriksaan tidak ditemukan.");
            return;
        }
 
        view.tampilkanPilihanPetugas();
        tampilkanSemuaPetugas();
 
        System.out.print("Masukkan ID Petugas: ");
        String idPetugas = scanner.nextLine().trim();
 
        Petugas petugas = petugasDAO.cariById(idPetugas);
 
        if (petugas == null) {
            view.tampilkanPesan("Petugas tidak ditemukan.");
            return;
        }
 
        view.tampilkanJudul("KONFIRMASI PENDAFTARAN");
        view.tampilkanKonfirmasiPendaftaran(pasien, pemeriksaan, petugas);
 
        pasienTerdaftar = pasien;
        pemeriksaanTerdaftar = pemeriksaan;
        petugasTerdaftar = petugas;
    }
 
    // ===================== MENU PASIEN =====================
 
    private void menuPasien(Scanner scanner) {
        boolean kembali = false;
 
        while (!kembali) {
            view.tampilkanSubMenuPasien();
 
            int pilihan = bacaInt(scanner);
 
            switch (pilihan) {
                case 1 -> aman(() -> tambahPasienDariInput(scanner));
                case 2 -> aman(this::tampilkanSemuaPasien);
                case 3 -> aman(() -> cariPasienDariInput(scanner));
                case 4 -> aman(() -> ubahPasienDariInput(scanner));
                case 5 -> aman(() -> hapusPasienDariInput(scanner));
                case 6 -> kembali = true;
                default -> view.tampilkanPesan("Pilihan tidak tersedia.");
            }
        }
    }
 
    private Pasien inputPasienBaru(Scanner scanner) throws SQLException {
        System.out.print("Nama (huruf saja): ");
        String nama = bacaNama(scanner);
        System.out.print("Umur (1-120): ");
        int umur = bacaUmur(scanner);
        System.out.print("Jenis Kelamin (Laki-laki/Perempuan): ");
        String jenisKelamin = bacaJenisKelamin(scanner);
        System.out.print("Keluhan: ");
        String keluhan = bacaTeksTidakKosong(scanner);
 
        Pasien pasien = new Pasien(pasienDAO.buatIdBaru(), nama, umur, jenisKelamin, keluhan);
        pasienDAO.tambah(pasien);
        return pasien;
    }
 
    private void tambahPasienDariInput(Scanner scanner) throws SQLException {
        view.tampilkanJudul("TAMBAH PASIEN");
        Pasien pasien = inputPasienBaru(scanner);
        view.tampilkanPesan("Pasien berhasil ditambahkan dengan ID: " + pasien.getId());
    }
 
    private void tampilkanSemuaPasien() throws SQLException {
        List<Pasien> daftar = pasienDAO.semua();
        if (daftar.isEmpty()) {
            view.tampilkanPesan("Belum ada data pasien.");
            return;
        }
        for (Pasien p : daftar) {
            view.tampilkanBaris(
                    "ID: " + p.getId()
                    + " | Nama: " + p.getNama()
                    + " | Umur: " + p.getUmur()
                    + " | Jenis Kelamin: " + p.getJenisKelamin());
        }
    }
 
    private void cariPasienDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Pasien: ");
        Pasien pasien = pasienDAO.cariById(scanner.nextLine().trim());
 
        if (pasien == null) {
            view.tampilkanPesan("Pasien tidak ditemukan.");
        } else {
            view.tampilkanBaris(pasien.tampilkanInfo(true));
        }
    }
 
    private void ubahPasienDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Pasien yang ingin diubah: ");
        Pasien pasien = pasienDAO.cariById(scanner.nextLine().trim());
 
        if (pasien == null) {
            view.tampilkanPesan("Pasien tidak ditemukan.");
            return;
        }
 
        view.tampilkanPesan("Tekan Enter saja jika tidak ingin mengubah data tersebut.");
 
        System.out.print("Nama Baru (" + pasien.getNama() + "): ");
        pasien.setNama(bacaNamaOpsional(scanner, pasien.getNama()));
 
        System.out.print("Umur Baru (" + pasien.getUmur() + "): ");
        pasien.setUmur(bacaIntOpsional(scanner, pasien.getUmur(), 1, 120));
 
        System.out.print("Jenis Kelamin Baru (" + pasien.getJenisKelamin() + "): ");
        pasien.setJenisKelamin(bacaJenisKelaminOpsional(scanner, pasien.getJenisKelamin()));
 
        System.out.print("Keluhan Baru (" + pasien.getKeluhan() + "): ");
        pasien.setKeluhan(bacaTeksOpsional(scanner, pasien.getKeluhan()));
 
        if (pasienDAO.ubah(pasien)) {
            view.tampilkanPesan("Pasien berhasil diubah.");
        } else {
            view.tampilkanPesan("Gagal mengubah pasien.");
        }
    }
 
    private void hapusPasienDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Pasien yang ingin dihapus: ");
        String id = scanner.nextLine().trim();
 
        if (pasienDAO.hapus(id)) {
            view.tampilkanPesan("Pasien berhasil dihapus.");
        } else {
            view.tampilkanPesan("Pasien tidak ditemukan.");
        }
    }
 
    // ===================== MENU PETUGAS =====================
 
    private void menuPetugas(Scanner scanner) {
        boolean kembali = false;
 
        while (!kembali) {
            view.tampilkanSubMenuPetugas();
 
            int pilihan = bacaInt(scanner);
 
            switch (pilihan) {
                case 1 -> aman(() -> tambahAnalisDariInput(scanner));
                case 2 -> aman(() -> tambahDokterDariInput(scanner));
                case 3 -> aman(this::tampilkanSemuaPetugas);
                case 4 -> aman(() -> ubahPetugasDariInput(scanner));
                case 5 -> aman(() -> hapusPetugasDariInput(scanner));
                case 6 -> kembali = true;
                default -> view.tampilkanPesan("Pilihan tidak tersedia.");
            }
        }
    }
 
    private void tambahAnalisDariInput(Scanner scanner) throws SQLException {
        view.tampilkanJudul("TAMBAH ANALIS");
        System.out.print("Nama (huruf saja): ");
        String nama = bacaNama(scanner);
        System.out.print("Umur (1-120): ");
        int umur = bacaUmur(scanner);
        System.out.print("Jenis Kelamin (Laki-laki/Perempuan): ");
        String jenisKelamin = bacaJenisKelamin(scanner);
        System.out.print("Spesialisasi/Bidang: ");
        String spesialisasi = bacaTeksTidakKosong(scanner);
 
        Analis analis = new Analis(petugasDAO.buatIdBaru(), nama, umur, jenisKelamin, spesialisasi);
        petugasDAO.tambah(analis);
        view.tampilkanPesan("Analis berhasil ditambahkan dengan ID: " + analis.getId());
    }
 
    private void tambahDokterDariInput(Scanner scanner) throws SQLException {
        view.tampilkanJudul("TAMBAH DOKTER");
        System.out.print("Nama (huruf saja): ");
        String nama = bacaNama(scanner);
        System.out.print("Umur (1-120): ");
        int umur = bacaUmur(scanner);
        System.out.print("Jenis Kelamin (Laki-laki/Perempuan): ");
        String jenisKelamin = bacaJenisKelamin(scanner);
        System.out.print("Nomor STR: ");
        String nomorSTR = bacaTeksTidakKosong(scanner);
 
        Dokter dokter = new Dokter(petugasDAO.buatIdBaru(), nama, umur, jenisKelamin, nomorSTR);
        petugasDAO.tambah(dokter);
        view.tampilkanPesan("Dokter berhasil ditambahkan dengan ID: " + dokter.getId());
    }
 
    private void tampilkanSemuaPetugas() throws SQLException {
        List<Petugas> daftar = petugasDAO.semua();
        if (daftar.isEmpty()) {
            view.tampilkanPesan("Belum ada data petugas.");
            return;
        }
        // polymorphism: tampilkanInfo() versi Dokter / Analis yang dijalankan
        for (Petugas p : daftar) {
            p.tampilkanInfo(true);
            System.out.println("------------------------------------");
        }
    }
 
    private void ubahPetugasDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Petugas yang ingin diubah: ");
        Petugas petugas = petugasDAO.cariById(scanner.nextLine().trim());
 
        if (petugas == null) {
            view.tampilkanPesan("Petugas tidak ditemukan.");
            return;
        }
 
        view.tampilkanPesan("Tekan Enter saja jika tidak ingin mengubah data tersebut.");
 
        System.out.print("Nama Baru (" + petugas.getNama() + "): ");
        petugas.setNama(bacaNamaOpsional(scanner, petugas.getNama()));
 
        System.out.print("Umur Baru (" + petugas.getUmur() + "): ");
        petugas.setUmur(bacaIntOpsional(scanner, petugas.getUmur(), 1, 120));
 
        System.out.print("Jenis Kelamin Baru (" + petugas.getJenisKelamin() + "): ");
        petugas.setJenisKelamin(bacaJenisKelaminOpsional(scanner, petugas.getJenisKelamin()));
 
        if (petugas instanceof Dokter dokter) {
            System.out.print("Nomor STR Baru (" + dokter.getNomorSTR() + "): ");
            dokter.setNomorSTR(bacaTeksOpsional(scanner, dokter.getNomorSTR()));
        } else if (petugas instanceof Analis analis) {
            System.out.print("Spesialisasi Baru (" + analis.getSpesialisasiBidang() + "): ");
            analis.setSpesialisasiBidang(
                    bacaTeksOpsional(scanner, analis.getSpesialisasiBidang()));
        }
 
        if (petugasDAO.ubah(petugas)) {
            view.tampilkanPesan("Petugas berhasil diubah.");
        } else {
            view.tampilkanPesan("Gagal mengubah petugas.");
        }
    }
 
    private void hapusPetugasDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Petugas yang ingin dihapus: ");
        String id = scanner.nextLine().trim();
 
        if (petugasDAO.hapus(id)) {
            view.tampilkanPesan("Petugas berhasil dihapus.");
        } else {
            view.tampilkanPesan("Petugas tidak ditemukan.");
        }
    }
 
    // ===================== MENU PEMERIKSAAN =====================
 
    private void menuPemeriksaan(Scanner scanner) {
        boolean kembali = false;
 
        while (!kembali) {
            view.tampilkanSubMenuPemeriksaan();
 
            int pilihan = bacaInt(scanner);
 
            switch (pilihan) {
                case 1 -> aman(() -> tambahPemeriksaanDariInput(scanner));
                case 2 -> aman(this::tampilkanSemuaPemeriksaan);
                case 3 -> aman(() -> cariPemeriksaanDariInput(scanner));
                case 4 -> aman(() -> ubahPemeriksaanDariInput(scanner));
                case 5 -> aman(() -> hapusPemeriksaanDariInput(scanner));
                case 6 -> kembali = true;
                default -> view.tampilkanPesan("Pilihan tidak tersedia.");
            }
        }
    }
 
    private void tambahPemeriksaanDariInput(Scanner scanner) throws SQLException {
        view.tampilkanJudul("TAMBAH PEMERIKSAAN");
        System.out.print("Nama Pemeriksaan: ");
        String nama = bacaTeksTidakKosong(scanner);
 
        System.out.print("Biaya (angka, >= 0): ");
        double biaya = bacaDouble(scanner);
 
        Pemeriksaan pemeriksaan = new Pemeriksaan(pemeriksaanDAO.buatIdBaru(), nama, biaya);
        pemeriksaanDAO.tambah(pemeriksaan);
 
        view.tampilkanPesan("Pemeriksaan berhasil ditambahkan dengan ID: "
                + pemeriksaan.getIdPemeriksaan());
    }
 
    private void tampilkanSemuaPemeriksaan() throws SQLException {
        List<Pemeriksaan> daftar = pemeriksaanDAO.semua();
        if (daftar.isEmpty()) {
            view.tampilkanPesan("Belum ada data pemeriksaan.");
            return;
        }
        for (Pemeriksaan p : daftar) {
            view.tampilkanBaris(formatPemeriksaan(p));
        }
    }
 
    private String formatPemeriksaan(Pemeriksaan p) {
        return "ID: " + p.getIdPemeriksaan()
                + " | Nama: " + p.getNamaPemeriksaan()
                + " | Biaya: Rp" + p.getBiaya();
    }
 
    private void cariPemeriksaanDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Pemeriksaan: ");
        Pemeriksaan pemeriksaan = pemeriksaanDAO.cariById(scanner.nextLine().trim());
 
        if (pemeriksaan == null) {
            view.tampilkanPesan("Pemeriksaan tidak ditemukan.");
        } else {
            view.tampilkanBaris(formatPemeriksaan(pemeriksaan));
        }
    }
 
    private void ubahPemeriksaanDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Pemeriksaan: ");
        Pemeriksaan pemeriksaan = pemeriksaanDAO.cariById(scanner.nextLine().trim());
 
        if (pemeriksaan == null) {
            view.tampilkanPesan("Pemeriksaan tidak ditemukan.");
            return;
        }
 
        view.tampilkanPesan("Tekan Enter saja jika tidak ingin mengubah data tersebut.");
 
        System.out.print("Nama Pemeriksaan Baru (" + pemeriksaan.getNamaPemeriksaan() + "): ");
        pemeriksaan.setNamaPemeriksaan(
                bacaTeksOpsional(scanner, pemeriksaan.getNamaPemeriksaan()));
 
        System.out.print("Biaya Baru (" + pemeriksaan.getBiaya() + "): ");
        pemeriksaan.setBiaya(bacaDoubleOpsional(scanner, pemeriksaan.getBiaya()));
 
        if (pemeriksaanDAO.ubah(pemeriksaan)) {
            view.tampilkanPesan("Pemeriksaan berhasil diubah.");
        } else {
            view.tampilkanPesan("Gagal mengubah pemeriksaan.");
        }
    }
 
    private void hapusPemeriksaanDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Pemeriksaan: ");
        String id = scanner.nextLine().trim();
 
        if (pemeriksaanDAO.hapus(id)) {
            view.tampilkanPesan("Pemeriksaan berhasil dihapus.");
        } else {
            view.tampilkanPesan("Pemeriksaan tidak ditemukan.");
        }
    }
 
    // ===================== MENU HASIL PEMERIKSAAN =====================
 
    private void menuHasil(Scanner scanner) {
        boolean kembali = false;
 
        while (!kembali) {
            view.tampilkanSubMenuHasil();
 
            int pilihan = bacaInt(scanner);
 
            switch (pilihan) {
                case 1 -> aman(() -> tambahHasilDariInput(scanner));
                case 2 -> aman(this::tampilkanSemuaHasil);
                case 3 -> aman(() -> riwayatHasilDariInput(scanner));
                case 4 -> kembali = true;
                default -> view.tampilkanPesan("Pilihan tidak tersedia.");
            }
        }
    }
 
    private void tambahHasilDariInput(Scanner scanner) throws SQLException {
        view.tampilkanJudul("INPUT HASIL PEMERIKSAAN");
 
        if (pemeriksaanTerdaftar == null || petugasTerdaftar == null) {
            view.tampilkanPesan("Belum ada pemeriksaan yang terdaftar. "
                    + "Silakan lakukan menu \"1. Pendaftaran Pemeriksaan\" terlebih dahulu.");
            return;
        }
 
        tampilkanSemuaPasien();
 
        System.out.print("ID Pasien: ");
        String idPasien = scanner.nextLine().trim();
 
        Pasien pasien = pasienDAO.cariById(idPasien);
        if (pasien == null) {
            view.tampilkanPesan("Pasien tidak ditemukan.");
            return;
        }
 
        System.out.print("Hasil Pemeriksaan: ");
        String hasil = bacaTeksTidakKosong(scanner);
 
        System.out.print("Status (Normal/Tidak Normal): ");
        String status = bacaStatus(scanner);
 
        HasilPemeriksaan hasilBaru = new HasilPemeriksaan(
                hasilDAO.buatIdBaru(),
                pasien.getId(),
                pemeriksaanTerdaftar.getIdPemeriksaan(),
                petugasTerdaftar.getId(),
                hasil,
                status);
        hasilDAO.tambah(hasilBaru);
 
        view.tampilkanPesan("SUKSES: Hasil berhasil ditambahkan dengan ID " + hasilBaru.getIdHasil());
    }
 
    private void tampilkanSemuaHasil() throws SQLException {
        List<HasilPemeriksaan> daftar = hasilDAO.semua();
        if (daftar.isEmpty()) {
            view.tampilkanPesan("Belum ada data hasil pemeriksaan.");
            return;
        }
        for (HasilPemeriksaan h : daftar) {
            view.tampilkanBaris(formatHasil(h));
        }
    }
 
    private void riwayatHasilDariInput(Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Pasien: ");
        String idPasien = scanner.nextLine().trim();
 
        if (pasienDAO.cariById(idPasien) == null) {
            view.tampilkanPesan("Pasien dengan ID " + idPasien + " tidak ditemukan.");
            return;
        }
 
        List<HasilPemeriksaan> riwayat = hasilDAO.cariByPasien(idPasien);
        if (riwayat.isEmpty()) {
            view.tampilkanPesan("Belum ada riwayat pemeriksaan untuk pasien ini.");
            return;
        }
        for (HasilPemeriksaan h : riwayat) {
            view.tampilkanBaris(formatHasil(h));
        }
    }
 
    private String formatHasil(HasilPemeriksaan h) throws SQLException {
        Pemeriksaan pm = pemeriksaanDAO.cariById(h.getIdPemeriksaan());
        Petugas pt = petugasDAO.cariById(h.getIdPetugas());
        Pasien ps = pasienDAO.cariById(h.getIdPasien());
        String namaTes = (pm != null) ? pm.getNamaPemeriksaan() : "-";
        String namaPetugas = (pt != null) ? pt.getNama() : "-";
        String namaPasien = (ps != null) ? ps.getNama() : "-";
        return "ID Hasil: " + h.getIdHasil()
                + " | Pasien: " + namaPasien
                + " | Jenis Tes: " + namaTes
                + " | Hasil: " + h.getHasil()
                + " | Status: " + h.getStatus()
                + " | Diperiksa oleh: " + namaPetugas;
    }
 
    // ===================== VALIDASI INPUT =====================
 
    /** Nama untuk fitur ubah: Enter kosong atau input tidak valid = nilai lama. */
    private String bacaNamaOpsional(Scanner scanner, String nilaiLama) {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return nilaiLama;
        }
        if (!input.matches("[a-zA-Z .]+")) {
            view.tampilkanPesan("Nama hanya boleh berisi huruf, nilai lama dipertahankan.");
            return nilaiLama;
        }
        return input;
    }
 
    private String bacaJenisKelaminOpsional(Scanner scanner, String nilaiLama) {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return nilaiLama;
        }
        for (String pilihan : new String[]{"Laki-laki", "Perempuan"}) {
            if (pilihan.equalsIgnoreCase(input)) {
                return pilihan;
            }
        }
        view.tampilkanPesan("Jenis kelamin harus Laki-laki atau Perempuan, nilai lama dipertahankan.");
        return nilaiLama;
    }
 
    private int bacaIntOpsional(Scanner scanner, int nilaiLama, int min, int max) {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return nilaiLama;
        }
        try {
            int nilai = Integer.parseInt(input);
            if (nilai >= min && nilai <= max) {
                return nilai;
            }
            view.tampilkanPesan("Angka harus " + min + "-" + max + ", nilai lama dipertahankan.");
        } catch (NumberFormatException e) {
            view.tampilkanPesan("Input bukan angka, nilai lama dipertahankan.");
        }
        return nilaiLama;
    }
 
 
 
    private String bacaNama(Scanner scanner) {
        while (true) {
            String nama = scanner.nextLine().trim();
 
            if (nama.isEmpty()) {
                System.out.print("Nama tidak boleh kosong. Masukkan lagi: ");
            } else if (!nama.matches("[a-zA-Z .]+")) {
                System.out.print("Nama hanya boleh berisi huruf. Masukkan lagi: ");
            } else {
                return nama;
            }
        }
    }
 
    /**
     * Membaca teks yang harus salah satu dari pilihan valid
     * (dipakai bersama oleh jenis kelamin dan status).
     */
    private String bacaPilihanTeks(Scanner scanner, String pesanError, String... pilihanValid) {
        while (true) {
            String input = scanner.nextLine().trim();
 
            for (String pilihan : pilihanValid) {
                if (pilihan.equalsIgnoreCase(input)) {
                    return pilihan;
                }
            }
 
            System.out.print(pesanError);
        }
    }
 
    private String bacaJenisKelamin(Scanner scanner) {
        return bacaPilihanTeks(scanner,
                "Jenis kelamin harus Laki-laki atau Perempuan. Masukkan lagi: ",
                "Laki-laki", "Perempuan");
    }
 
    private String bacaStatus(Scanner scanner) {
        return bacaPilihanTeks(scanner,
                "Status harus Normal atau Tidak Normal. Masukkan lagi: ",
                "Normal", "Tidak Normal");
    }
 
    /**
     * Membaca teks untuk fitur ubah data: kalau user hanya menekan
     * Enter (input kosong), nilai lama dipertahankan.
     */
    private String bacaTeksOpsional(Scanner scanner, String nilaiLama) {
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? nilaiLama : input;
    }
 
    /**
     * Versi angka desimal dari bacaTeksOpsional(): Enter kosong akan
     * mempertahankan nilai lama, begitu juga kalau inputnya tidak valid.
     */
    private double bacaDoubleOpsional(Scanner scanner, double nilaiLama) {
        String input = scanner.nextLine().trim();
 
        if (input.isEmpty()) {
            return nilaiLama;
        }
 
        try {
            double nilai = Double.parseDouble(input);
            if (nilai >= 0) {
                return nilai;
            }
            view.tampilkanPesan("Nilai tidak boleh negatif, nilai lama dipertahankan.");
            return nilaiLama;
        } catch (NumberFormatException e) {
            view.tampilkanPesan("Input bukan angka, nilai lama dipertahankan.");
            return nilaiLama;
        }
    }
 
    private String bacaTeksTidakKosong(Scanner scanner) {
        while (true) {
            String teks = scanner.nextLine().trim();
 
            if (!teks.isEmpty()) {
                return teks;
            }
 
            System.out.print("Input tidak boleh kosong. Masukkan lagi: ");
        }
    }
 
    /**
     * Overload tanpa rentang, dipakai untuk pilihan menu.
     */
    private int bacaInt(Scanner scanner) {
        return bacaInt(scanner, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
 
    /**
     * Overload dengan rentang min-max, dipakai misalnya oleh bacaUmur().
     */
    private int bacaInt(Scanner scanner, int min, int max) {
        while (true) {
            String input = scanner.nextLine().trim();
 
            try {
                int nilai = Integer.parseInt(input);
 
                if (nilai >= min && nilai <= max) {
                    return nilai;
                }
 
                System.out.print(
                        "Masukkan angka antara " + min + " sampai " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Input harus berupa angka. Masukkan lagi: ");
            }
        }
    }
 
    private int bacaUmur(Scanner scanner) {
        return bacaInt(scanner, 1, 120);
    }
 
    private double bacaDouble(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
 
            try {
                double nilai = Double.parseDouble(input);
 
                if (nilai >= 0) {
                    return nilai;
                }
                        
 
                System.out.print(
                        "Nilai tidak boleh negatif. Masukkan lagi: ");
            } catch (NumberFormatException e) {
                System.out.print(
                        "Input harus berupa angka. Masukkan lagi: ");
            }
        }
    }
}
 