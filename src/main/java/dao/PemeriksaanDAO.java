/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import koneksi.DatabaseConnection;
import model.Pemeriksaan;
 

/**
 *
 * @author NITRO V15
 */

public class PemeriksaanDAO implements CrudDAO<Pemeriksaan> {
 
    @Override
    public void tambah(Pemeriksaan pemeriksaan) throws SQLException {
        String sql = "INSERT INTO pemeriksaan (id_pemeriksaan, nama_pemeriksaan, biaya) "
                + "VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pemeriksaan.getIdPemeriksaan());
            ps.setString(2, pemeriksaan.getNamaPemeriksaan());
            ps.setDouble(3, pemeriksaan.getBiaya());
            ps.executeUpdate();
        }
    }
 
    @Override
    public List<Pemeriksaan> semua() throws SQLException {
        List<Pemeriksaan> daftar = new ArrayList<>();
        String sql = "SELECT * FROM pemeriksaan "
                + "ORDER BY CAST(SUBSTRING(id_pemeriksaan, 3) AS UNSIGNED)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(petakan(rs));
            }
        }
        return daftar;
    }
 
    @Override
    public Pemeriksaan cariById(String id) throws SQLException {
        String sql = "SELECT * FROM pemeriksaan WHERE id_pemeriksaan = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? petakan(rs) : null;
            }
        }
    }
 
    @Override
    public boolean ubah(Pemeriksaan pemeriksaan) throws SQLException {
        String sql = "UPDATE pemeriksaan SET nama_pemeriksaan = ?, biaya = ? "
                + "WHERE id_pemeriksaan = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pemeriksaan.getNamaPemeriksaan());
            ps.setDouble(2, pemeriksaan.getBiaya());
            ps.setString(3, pemeriksaan.getIdPemeriksaan());
            return ps.executeUpdate() > 0;
        }
    }
 
    @Override
    public boolean hapus(String id) throws SQLException {
        String sql = "DELETE FROM pemeriksaan WHERE id_pemeriksaan = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }
 
    @Override
    public String buatIdBaru() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(id_pemeriksaan, 3) AS UNSIGNED)), 0) + 1 "
                + "FROM pemeriksaan";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return "PM" + rs.getInt(1);
        }
    }
 
    private Pemeriksaan petakan(ResultSet rs) throws SQLException {
        return new Pemeriksaan(
                rs.getString("id_pemeriksaan"),
                rs.getString("nama_pemeriksaan"),
                rs.getDouble("biaya"));
    }
}