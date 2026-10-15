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
import model.Pasien;


/**
 * 
 * @author NITRO V15
 */
    

public class PasienDAO implements CrudDAO<Pasien> {
 
    @Override
    public void tambah(Pasien pasien) throws SQLException {
        String sql = "INSERT INTO pasien (id_pasien, nama, umur, jenis_kelamin, keluhan) "
                + "VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pasien.getId());
            ps.setString(2, pasien.getNama());
            ps.setInt(3, pasien.getUmur());
            ps.setString(4, pasien.getJenisKelamin());
            ps.setString(5, pasien.getKeluhan());
            ps.executeUpdate();
        }
    }
 
    @Override
    public List<Pasien> semua() throws SQLException {
        List<Pasien> daftar = new ArrayList<>();
        String sql = "SELECT * FROM pasien ORDER BY CAST(SUBSTRING(id_pasien, 2) AS UNSIGNED)";
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
    public Pasien cariById(String id) throws SQLException {
        String sql = "SELECT * FROM pasien WHERE id_pasien = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? petakan(rs) : null;
            }
        }
    }
 
    @Override
    public boolean ubah(Pasien pasien) throws SQLException {
        String sql = "UPDATE pasien SET nama = ?, umur = ?, jenis_kelamin = ?, keluhan = ? "
                + "WHERE id_pasien = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pasien.getNama());
            ps.setInt(2, pasien.getUmur());
            ps.setString(3, pasien.getJenisKelamin());
            ps.setString(4, pasien.getKeluhan());
            ps.setString(5, pasien.getId());
            return ps.executeUpdate() > 0;
        }
    }
 
    @Override
    public boolean hapus(String id) throws SQLException {
        String sql = "DELETE FROM pasien WHERE id_pasien = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }
 
    @Override
    public String buatIdBaru() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(id_pasien, 2) AS UNSIGNED)), 0) + 1 "
                + "FROM pasien";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return "P" + rs.getInt(1);
        }
    }
 
    private Pasien petakan(ResultSet rs) throws SQLException {
        return new Pasien(
                rs.getString("id_pasien"),
                rs.getString("nama"),
                rs.getInt("umur"),
                rs.getString("jenis_kelamin"),
                rs.getString("keluhan"));
    }
}