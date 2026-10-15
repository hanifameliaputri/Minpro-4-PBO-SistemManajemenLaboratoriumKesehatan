/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
/**
 *
 * @author User
 */

@Entity
@DiscriminatorValue("Dokter")
public class Dokter extends Petugas {
 
    @Column(name = "nomor_str")
    private String nomorSTR;
 
    protected Dokter() {
        // dibutuhkan JPA/Hibernate
    }
 
    public Dokter(String id, String nama, int umur, String jenisKelamin, String nomorSTR) {
        super(id, nama, umur, jenisKelamin);
        setNomorSTR(nomorSTR);
    }
 
    public String getNomorSTR() {
        return nomorSTR;
    }
 
    public void setNomorSTR(String nomorSTR) {
        if (nomorSTR != null && !nomorSTR.trim().isEmpty()) {
            this.nomorSTR = nomorSTR;
        } else {
            System.out.println(">> ERROR: Nomor STR tidak boleh kosong!");
        }
    }
 
 
 
   @Override
    public void tampilkanInfo() {
        System.out.println(
            tampilkanInfoDasar()
            + " | Peran: Dokter"
            + " | No. STR: " + nomorSTR
        );
    }
}
 