/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
 
import model.Petugas;
/**
 *
 * @author NITRO V15
 */
public class PetugasOrmDAO extends OrmDAO<Petugas> {
 
    public PetugasOrmDAO() {
        super(Petugas.class, "Petugas", "id", "PT");
    }
}
 
