/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author NITRO V15
 */


public class DatabaseConnection {
 
    private static final String URL =
            "jdbc:mysql://localhost:3306/laboratorium_kesehatan"
            + "?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";
 
    private static Connection connection;
 
    private DatabaseConnection() {
    }
 
    /**
     * Mengembalikan koneksi aktif. Jika belum ada atau sudah tertutup,
     * koneksi baru dibuat. Error dilempar ke pemanggil (DAO/Controller)
     * agar bisa ditampilkan lewat View.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
 