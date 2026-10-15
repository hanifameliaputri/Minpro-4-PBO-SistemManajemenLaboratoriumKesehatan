/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.function.Function;
import koneksi.JPAUtil;
/**
 *
 * @author NITRO V15
 */

public abstract class OrmDAO<T> implements CrudDAO<T> {
 
    private final Class<T> kelas;
    private final String namaEntity;
    private final String atributId;
    private final String prefixId;
 
    protected OrmDAO(Class<T> kelas, String namaEntity, String atributId, String prefixId) {
        this.kelas = kelas;
        this.namaEntity = namaEntity;
        this.atributId = atributId;
        this.prefixId = prefixId;
    }
 
    @Override
    public void tambah(T objek) throws SQLException {
        jalankan(true, em -> {
            em.persist(objek);
            return null;
        });
    }
 
    @Override
    public List<T> semua() throws SQLException {
        String jpql = "SELECT e FROM " + namaEntity + " e "
                + "ORDER BY length(e." + atributId + "), e." + atributId;
        return jalankan(false, em -> em.createQuery(jpql, kelas).getResultList());
    }
 
    @Override
    public T cariById(String id) throws SQLException {
        return jalankan(false, em -> em.find(kelas, id));
    }
 
    @Override
    public boolean ubah(T objek) throws SQLException {
        jalankan(true, em -> em.merge(objek));
        return true;
    }
 
    @Override
    public boolean hapus(String id) throws SQLException {
        return jalankan(true, em -> {
            T data = em.find(kelas, id);
            if (data == null) {
                return false;
            }
            em.remove(data);
            return true;
        });
    }
 
    @Override
    public String buatIdBaru() throws SQLException {
        String jpql = "SELECT e." + atributId + " FROM " + namaEntity + " e "
                + "ORDER BY length(e." + atributId + ") DESC, e." + atributId + " DESC";
        List<String> idTerakhir = jalankan(false,
                em -> em.createQuery(jpql, String.class).setMaxResults(1).getResultList());
 
        int nomor = 0;
        if (!idTerakhir.isEmpty()) {
            nomor = Integer.parseInt(idTerakhir.get(0).substring(prefixId.length()));
        }
        return prefixId + (nomor + 1);
    }
 
    /**
     * Menjalankan aksi ORM dalam satu EntityManager. Untuk operasi tulis,
     * dibuka transaksi lalu commit; jika gagal, rollback. Error JPA
     * diubah menjadi SQLException agar ditangani Controller seperti versi JDBC.
     */
    protected <R> R jalankan(boolean tulis, Function<EntityManager, R> aksi) throws SQLException {
        EntityManager em = null;
        try {
            em = JPAUtil.buatEntityManager();
            if (tulis) {
                em.getTransaction().begin();
            }
            R hasil = aksi.apply(em);
            if (tulis) {
                em.getTransaction().commit();
            }
            return hasil;
        } catch (PersistenceException e) {
            throw terjemahkan(e);
        } finally {
            if (em != null) {
                if (tulis && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }
 
    /** Mencari SQLException asli di balik exception JPA/Hibernate. */
    private SQLException terjemahkan(PersistenceException e) {
        Throwable penyebab = e;
        while (penyebab != null) {
            if (penyebab instanceof SQLIntegrityConstraintViolationException integritas) {
                return integritas;
            }
            penyebab = penyebab.getCause();
        }
        Throwable asli = e;
        while (asli.getCause() != null) {
            asli = asli.getCause();
        }
        return new SQLException(asli.getMessage(), e);
    }
}
 