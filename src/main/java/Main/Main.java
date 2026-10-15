/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Main;

import controller.LaboratoriumController;
import dao.HasilPemeriksaanOrmDAO;
import dao.PasienDAO;
import dao.PemeriksaanDAO;
import dao.PetugasOrmDAO;
import java.sql.SQLException;
import java.util.Scanner;
import koneksi.DatabaseConnection;
import koneksi.JPAUtil;
/**
 *
 * @author User
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
 
public class Main {
    public static void main(String[] args) {
        // --- KODE UNTUK MENYEMBUNYIKAN LOG INFO & WARN HIBERNATE ---
        System.setProperty("java.util.logging.config.file", "");
        java.util.logging.LogManager.getLogManager().reset();
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.setLevel(java.util.logging.Level.SEVERE); // Hanya tampilkan pesan error tingkat SEVERE saja
        
        try {
            DatabaseConnection.getConnection();
            System.out.println("Koneksi database berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi database gagal: " + e.getMessage());
            System.out.println("Pastikan MySQL sudah berjalan dan file "
                    + "database/laboratorium_kesehatan.sql sudah di-import.");
            return;
        }
 
        // JDBC + DAO : Pasien dan Pemeriksaan
        // ORM (JPA/Hibernate) : Petugas dan Hasil Pemeriksaan (nilai tambah)
        LaboratoriumController controller = new LaboratoriumController(
                new PasienDAO(),
                new PetugasOrmDAO(),
                new PemeriksaanDAO(),
                new HasilPemeriksaanOrmDAO());
 
        try (Scanner scanner = new Scanner(System.in)) {
            controller.jalankanProgram(scanner);
        } finally {
            JPAUtil.tutup();
        }
    }
}