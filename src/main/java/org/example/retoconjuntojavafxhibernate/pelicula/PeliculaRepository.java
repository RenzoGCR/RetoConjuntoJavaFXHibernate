package org.example.retoconjuntojavafxhibernate.pelicula;

import org.example.retoconjuntojavafxhibernate.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Pelicula}.
 * Proporciona métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * en la base de datos para las películas.
 */
public class PeliculaRepository implements Repository<Pelicula> {

    private final SessionFactory sessionFactory;

    /**
     * Constructor que inyecta la fábrica de sesiones de Hibernate.
     * @param sessionFactory La instancia de SessionFactory para crear sesiones de base de datos.
     */
    public PeliculaRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Guarda una nueva película en la base de datos.
     * (Actualmente no implementado).
     * @param entity La película a guardar.
     * @return La película guardada.
     */
    @Override
    public Pelicula save(Pelicula entity) {
        // TODO: Implementar la lógica para guardar una película.
        return null;
    }

    /**
     * Elimina una película de la base de datos.
     * @param entity La película a eliminar.
     * @return Un Optional que contiene la película eliminada si la operación fue exitosa.
     */
    @Override
    public Optional<Pelicula> delete(Pelicula entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(entity);
            session.getTransaction().commit();
            return Optional.of(entity);
        }
    }

    /**
     * Elimina una película de la base de datos por su ID.
     * @param id El ID de la película a eliminar.
     * @return Un Optional que contiene la película eliminada si se encontró y eliminó.
     */
    @Override
    public Optional<Pelicula> deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Pelicula pelicula = session.find(Pelicula.class, id);
            if (pelicula != null) {
                session.beginTransaction();
                session.remove(pelicula);
                session.getTransaction().commit();
                return Optional.of(pelicula);
            }
            return Optional.empty();
        }
    }

    /**
     * Busca una película por su ID.
     * @param id El ID de la película a buscar.
     * @return Un Optional que contiene la película si se encuentra.
     */
    @Override
    public Optional<Pelicula> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Pelicula.class, id));
        }
    }

    /**
     * Devuelve una lista con todas las películas del catálogo.
     * @return Una lista de todas las películas.
     */
    @Override
    public List<Pelicula> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Pelicula", Pelicula.class).list();
        }
    }

    /**
     * Cuenta el número total de películas en el catálogo.
     * @return El número total de películas.
     */
    @Override
    public Long count() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT count(p) from Pelicula p", Long.class).getSingleResult();
        }
    }
}
