package org.example.retoconjuntojavafxhibernate.user;

import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.utils.DataProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Servicio de lógica de negocio para operaciones relacionadas con usuarios y películas.
 * Esta clase encapsula las interacciones con la base de datos a través de Hibernate
 * para operaciones complejas que involucran múltiples entidades.
 */
public class UserService {

    /**
     * Asigna una copia de una película a un usuario (alquiler).
     * La lógica se asegura de que un usuario solo pueda tener una copia a la vez.
     *
     * @param actor    El usuario que realiza la acción de alquiler.
     * @param pelicula La película que se desea alquilar.
     * @throws RuntimeException Si el usuario ya tiene una película asignada, si la película no existe,
     *                          o si ocurre un error durante la transacción de base de datos.
     */
    public void addPeliculaOrCopia(User actor, Pelicula pelicula) {
        Transaction transaction = null;
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User persistentUser = session.find(User.class, actor.getId());
            Pelicula persistentPelicula = session.find(Pelicula.class, pelicula.getId());

            if (persistentUser.getCopiaAsignada() != null) {
                throw new RuntimeException("El usuario ya tiene una película asignada. Devuélvela antes de coger otra.");
            }
            if (persistentPelicula == null) {
                throw new RuntimeException("La película seleccionada no existe en la base de datos.");
            }

            CopiaPelicula nuevaCopia = new CopiaPelicula();
            nuevaCopia.setEstado("En posesión");
            nuevaCopia.setSoporte("DVD");

            nuevaCopia.setPelicula(persistentPelicula);
            nuevaCopia.setUsuario(persistentUser);
            persistentUser.setCopiaAsignada(nuevaCopia);

            session.persist(nuevaCopia);
            
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar en la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Guarda una nueva película en el catálogo.
     *
     * @param pelicula El objeto Pelicula a persistir.
     * @return La película guardada con su ID asignado, o null si ocurre un error.
     */
    public Pelicula savePelicula(Pelicula pelicula) {
        Transaction transaction = null;
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(pelicula);
            transaction.commit();
            return pelicula;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina la copia de una película asignada a un usuario (devolución).
     * Si el usuario es administrador, podría tener otra lógica (actualmente no implementada aquí).
     *
     * @param actor    El usuario que realiza la acción.
     * @param pelicula La película asociada a la copia que se devuelve (actualmente no se usa directamente).
     * @return El usuario actualizado después de la operación, o null si ocurre un error.
     */
    public User removePeliculaOrCopia(User actor, Pelicula pelicula) {
        Transaction transaction = null;
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User persistentUser = session.find(User.class, actor.getId());

            if (persistentUser.isAdmin()) {
                Pelicula pToDelete = session.find(Pelicula.class, pelicula.getId());
                if (pToDelete != null) {
                    session.remove(pToDelete);
                }
            } else {
                CopiaPelicula copia = persistentUser.getCopiaAsignada();
                if (copia != null) {
                    persistentUser.setCopiaAsignada(null);
                    session.remove(copia);
                }
            }
            transaction.commit();
            return persistentUser;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene una lista de todas las películas disponibles en el catálogo.
     *
     * @return Una lista de objetos {@link Pelicula}.
     */
    public List<Pelicula> findAllPeliculas() {
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            return session.createQuery("FROM Pelicula", Pelicula.class).list();
        }
    }

    /**
     * Obtiene un usuario y carga explícitamente sus dependencias (copia asignada y la película de esa copia).
     * Este método es crucial para evitar {@link org.hibernate.LazyInitializationException} en la capa de vista,
     * ya que devuelve un objeto "completo" o "listo para la vista".
     *
     * @param userId El ID del usuario a cargar.
     * @return Un objeto {@link User} con sus relaciones asociadas inicializadas, o null si no se encuentra.
     */
    public User getUserWithDependencies(Integer userId) {
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            Query<User> q = session.createQuery(
                    "SELECT u FROM User u " +
                            "LEFT JOIN FETCH u.copiaAsignada c " +
                            "LEFT JOIN FETCH c.pelicula " +
                            "LEFT JOIN FETCH c.usuario " +
                            "WHERE u.id = :id", User.class);
            q.setParameter("id", userId);
            return q.uniqueResult();
        }
    }
}
