/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;
 
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author NITRO V15
 */
public interface CrudDAO<T> {
 
    void tambah(T objek) throws SQLException;          // CREATE
 
    List<T> semua() throws SQLException;               // READ (semua)
 
    T cariById(String id) throws SQLException;         // READ (satu data)
 
    boolean ubah(T objek) throws SQLException;         // UPDATE
 
    boolean hapus(String id) throws SQLException;      // DELETE
 
    /** Membuat ID berikutnya berdasarkan ID terbesar di database (P1, P2, ...). */
    String buatIdBaru() throws SQLException;
}
