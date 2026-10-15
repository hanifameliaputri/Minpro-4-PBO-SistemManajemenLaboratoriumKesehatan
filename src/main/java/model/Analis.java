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
@DiscriminatorValue("Analis")
public class Analis extends Petugas {
 
    @Column(name = "spesialisasi")
    private String spesialisasiBidang;
 
    protected Analis() {
        // dibutuhkan JPA/Hibernate
    }
 
    public Analis(String id, String nama, int umur, String jenisKelamin, String spesialisasiBidang) {
        super(id, nama, umur, jenisKelamin);          // mengisi atribut milik Petugas
        setSpesialisasiBidang(spesialisasiBidang);   // mengisi atribut milik Analis
    }
 
    public String getSpesialisasiBidang() {
        return spesialisasiBidang;
    }
 
    public void setSpesialisasiBidang(String spesialisasiBidang) {
        if (spesialisasiBidang != null && !spesialisasiBidang.trim().isEmpty()) {
            this.spesialisasiBidang = spesialisasiBidang;
        } else {
            System.out.println(">> ERROR: Spesialisasi tidak boleh kosong!");
        }
    }
   
 
    @Override
    public void tampilkanInfo() {
    System.out.println(
        tampilkanInfoDasar()
        + " | Peran: Analis"
        + " | Spesialisasi/Bidang: " + spesialisasiBidang
    );
    }
}