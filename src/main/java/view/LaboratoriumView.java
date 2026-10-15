/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author User
 */


 
import model.Pasien;
import model.Pemeriksaan;
import model.Petugas;
 
 
public class LaboratoriumView {
 
    public void tampilkanMenuUtama() {
        System.out.println("==================================================");
        System.out.println("   SISTEM MANAJEMEN LABORATORIUM KESEHATAN");
        System.out.println("==================================================");
        System.out.println("1. Pendaftaran Pemeriksaan");
        System.out.println("2. Kelola Pasien");
        System.out.println("3. Kelola Petugas");
        System.out.println("4. Kelola Pemeriksaan");
        System.out.println("5. Kelola Hasil Pemeriksaan");
        System.out.println("6. Keluar");
        System.out.println("==================================================");
        System.out.print("Pilih menu: ");
    }
 
    public void tampilkanSubMenuPasien() {
        System.out.println("\n--- KELOLA PASIEN ---");
        System.out.println("1. Tambah Pasien");
        System.out.println("2. Lihat Semua Pasien");
        System.out.println("3. Cari Pasien");
        System.out.println("4. Ubah Pasien");
        System.out.println("5. Hapus Pasien");
        System.out.println("6. Kembali ke Menu Utama");
        System.out.print("Pilih menu: ");
    }
 
    public void tampilkanSubMenuPetugas() {
        System.out.println("\n--- KELOLA PETUGAS ---");
        System.out.println("1. Tambah Analis");
        System.out.println("2. Tambah Dokter");
        System.out.println("3. Lihat Semua Petugas");
        System.out.println("4. Ubah Petugas");
        System.out.println("5. Hapus Petugas");
        System.out.println("6. Kembali ke Menu Utama");
        System.out.print("Pilih menu: ");
    }
 
    public void tampilkanSubMenuPemeriksaan() {
        System.out.println("\n--- KELOLA PEMERIKSAAN ---");
        System.out.println("1. Tambah Pemeriksaan");
        System.out.println("2. Lihat Semua Pemeriksaan");
        System.out.println("3. Cari Pemeriksaan");
        System.out.println("4. Ubah Pemeriksaan");
        System.out.println("5. Hapus Pemeriksaan");
        System.out.println("6. Kembali ke Menu Utama");
        System.out.print("Pilih menu: ");
    }
 
    public void tampilkanSubMenuHasil() {
        System.out.println("\n--- KELOLA HASIL PEMERIKSAAN ---");
        System.out.println("1. Input Hasil Pemeriksaan");
        System.out.println("2. Lihat Semua Hasil");
        System.out.println("3. Lihat Riwayat Hasil per Pasien");
        System.out.println("4. Kembali ke Menu Utama");
        System.out.print("Pilih menu: ");
    }
 
    // pendafatran pemeriksaan
 
    public void tampilkanPilihanPasien() {
        System.out.println("1. Pasien Baru");
        System.out.println("2. Pasien Sudah Terdaftar");
        System.out.print("Pilih: ");
    }
 
    public void tampilkanInputPasienBaru() {
        System.out.println("\n--- INPUT PASIEN BARU ---");
    }
 
    public void tampilkanPilihanPemeriksaan() {
        System.out.println("\n--- PILIH PEMERIKSAAN ---");
    }
 
    public void tampilkanPilihanPetugas() {
        System.out.println("\n--- PILIH PETUGAS ---");
    }
 
    public void tampilkanKonfirmasiPendaftaran(
            Pasien pasien,
            Pemeriksaan pemeriksaan,
            Petugas petugas) {
 
        System.out.println("ID Pasien       : " + pasien.getId());
        System.out.println("Nama Pasien     : " + pasien.getNama());
        System.out.println("Pemeriksaan     : " + pemeriksaan.getNamaPemeriksaan());
        System.out.println("Biaya           : Rp" + pemeriksaan.getBiaya());
        System.out.println("Petugas         : " + petugas.getNama());
        System.out.println("Status          : Terdaftar");
        System.out.println("------------------------------------");
        System.out.println("Pendaftaran pemeriksaan berhasil.");
    }
 
    public void tampilkanJudul(String judul) {
        System.out.println("\n=== " + judul + " ===");
    }
 
    public void tampilkanPesan(String pesan) {
        System.out.println(pesan);
    }
 
    public void tampilkanBaris(String baris) {
        System.out.println(baris);
        System.out.println("------------------------------------");
    }
}