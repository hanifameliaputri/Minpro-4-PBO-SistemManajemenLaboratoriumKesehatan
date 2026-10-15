/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author NITRO V15
 */
public class JPAUtil {
 
    private static EntityManagerFactory factory;
 
    private JPAUtil() {
    }
 
    public static synchronized EntityManagerFactory getFactory() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("laboratoriumPU");
        }
        return factory;
    }
 
    public static EntityManager buatEntityManager() {
        return getFactory().createEntityManager();
    }
 
    /** Dipanggil saat program selesai. */
    public static synchronized void tutup() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
        factory = null;
    }
}
