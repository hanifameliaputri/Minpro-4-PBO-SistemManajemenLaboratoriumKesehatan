/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 *
 * @author User
 */

@Entity
@Table(name = "petugas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipe_petugas", discriminatorType = DiscriminatorType.STRING)
public abstract class Petugas implements Identitas {
 
    @Id
    @Column(name = "id_petugas")
    private String id;
 
    private String nama;
 
    private int umur;
 
    @Column(name = "jenis_kelamin")
    private String jenisKelamin;
 
    protected Petugas() {
        // dibutuhkan JPA/Hibernate
    }
 
    public Petugas(String id, String nama, int umur, String jenisKelamin) {
        this.id = id;
        setNama(nama);
        setUmur(umur);
        setJenisKelamin(jenisKelamin);
    }
 
    public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public String getNama() {
        return nama;
    }
 
    public void setNama(String nama) {
        if (nama != null && !nama.trim().isEmpty()) {
            this.nama = nama;
        } else {
            System.out.println(">> ERROR: Nama tidak boleh kosong!");
        }
    }
 
    public int getUmur() {
        return umur;
    }
 
    public void setUmur(int umur) {
        if (umur > 0) {
            this.umur = umur;
        } else {
            System.out.println(">> ERROR: Umur harus lebih dari 0!");
        }
    }
 
    public String getJenisKelamin() {
        return jenisKelamin;
    }
 
    public void setJenisKelamin(String jenisKelamin) {
        if (jenisKelamin != null && !jenisKelamin.trim().isEmpty()) {
            this.jenisKelamin = jenisKelamin;
        } else {
            System.out.println(">> ERROR: Jenis kelamin tidak boleh kosong!");
        }
    }
 
   protected String tampilkanInfoDasar() {
        return "ID: " + id + " | Nama: " + nama;
    }
 
  
   @Override
    public abstract void tampilkanInfo();
 
    public void tampilkanInfo(boolean detail) {
        tampilkanInfo();   // versi Dokter/Analis yang jalan (polymorphism)
        if (detail) {
            System.out.println("Umur: " + getUmur() + " | Jenis Kelamin: " + getJenisKelamin());
        }
    }
}