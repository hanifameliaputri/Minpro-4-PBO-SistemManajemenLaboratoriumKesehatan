/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */


public class Pemeriksaan {
 
    private String idPemeriksaan;
    private String namaPemeriksaan;
    private double biaya;
 
    public Pemeriksaan(String idPemeriksaan, String namaPemeriksaan, double biaya) {
        this.idPemeriksaan = idPemeriksaan;
        setNamaPemeriksaan(namaPemeriksaan);
        setBiaya(biaya);
    }
 
    public String getIdPemeriksaan() {
        return idPemeriksaan;
    }
 
    public void setIdPemeriksaan(String idPemeriksaan) {
        this.idPemeriksaan = idPemeriksaan;
    }
 
    public String getNamaPemeriksaan() {
        return namaPemeriksaan;
    }
 
    public void setNamaPemeriksaan(String namaPemeriksaan) {
        if (namaPemeriksaan != null && !namaPemeriksaan.trim().isEmpty()) {
            this.namaPemeriksaan = namaPemeriksaan;
        } else {
            System.out.println(">> ERROR: Nama pemeriksaan tidak boleh kosong!");
        }
    }
 
    public double getBiaya() {
        return biaya;
    }
 
    public void setBiaya(double biaya) {
        if (biaya >= 0) {
            this.biaya = biaya;
        } else {
            System.out.println(">> ERROR: Biaya tidak boleh negatif!");
        }
    }
}
 