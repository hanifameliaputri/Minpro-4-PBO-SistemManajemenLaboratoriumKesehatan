/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.SQLException;
import java.util.List;
import model.HasilPemeriksaan;
/**
 *
 * @author NITRO V15
 */
public class HasilPemeriksaanOrmDAO extends OrmDAO<HasilPemeriksaan> {
 
    public HasilPemeriksaanOrmDAO() {
        super(HasilPemeriksaan.class, "HasilPemeriksaan", "idHasil", "H");
    }
 
    public List<HasilPemeriksaan> cariByPasien(String idPasien) throws SQLException {
        String jpql = "SELECT h FROM HasilPemeriksaan h WHERE h.idPasien = :idPasien "
                + "ORDER BY length(h.idHasil), h.idHasil";
        return jalankan(false, em -> em.createQuery(jpql, HasilPemeriksaan.class)
                .setParameter("idPasien", idPasien)
                .getResultList());
    }
}
 