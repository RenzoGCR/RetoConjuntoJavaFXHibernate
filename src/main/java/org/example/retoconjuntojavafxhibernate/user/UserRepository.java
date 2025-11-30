package org.example.retoconjuntojavafxhibernate.user;

import org.example.retoconjuntojavafxhibernate.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link User}.
 * Proporciona métodos para realizar operaciones de base de datos relacionadas con los usuarios.
 * Actualmente, se centra en la búsqueda de usuarios.
 */
public class UserRepository implements Repository<User> {

    private final SessionFactory sessionFactory;

    /**
     * Constructor que inyecta la fábrica de sesiones de Hibernate.
     * @param sessionFactory La instancia de SessionFactory para crear sesiones de base de datos.
     */
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     * @param entity El usuario a guardar.
     * @return El usuario guardado.
     * @implSpec Este método no está implementado actualmente.
     */
    @Override
    public User save(User entity) {
        // TODO: Implementar la lógica para guardar un usuario.
        return null;
    }

    /**
     * Elimina un usuario de la base de datos.
     * @param entity El usuario a eliminar.
     * @return Un Optional que contiene el usuario eliminado.
     * @implSpec Este método no está implementado actualmente.
     */
    @Override
    public Optional<User> delete(User entity) {
        // TODO: Implementar la lógica para eliminar un usuario.
        return Optional.empty();
    }

    /**
     * Elimina un usuario de la base de datos por su ID.
     * @param id El ID del usuario a eliminar.
     * @return Un Optional que contiene el usuario eliminado.
     * @implSpec Este método no está implementado actualmente.
     */
    @Override
    public Optional<User> deleteById(Long id) {
        // TODO: Implementar la lógica para eliminar un usuario por ID.
        return Optional.empty();
    }

    /**
     * Busca un usuario por su ID.
     * @param id El ID del usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     * @implSpec Este método no está implementado actualmente.
     */
    @Override
    public Optional<User> findById(Long id) {
        // TODO: Implementar la lógica para buscar un usuario por ID.
        return Optional.empty();
    }

    /**
     * Devuelve una lista con todos los usuarios.
     * @return Una lista de todos los usuarios.
     * @implSpec Este método no está implementado actualmente.
     */
    @Override
    public List<User> findAll() {
        // TODO: Implementar la lógica para buscar todos los usuarios.
        return List.of();
    }

    /**
     * Cuenta el número total de usuarios.
     * @return El número total de usuarios.
     * @implSpec Este método no está implementado actualmente.
     */
    @Override
    public Long count() {
        // TODO: Implementar la lógica para contar usuarios.
        return 0L;
    }

    /**
     * Busca un usuario por su nombre de usuario (en este caso, el correo electrónico).
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return Un {@link Optional} que contiene el {@link User} si se encuentra, o un Optional vacío en caso contrario.
     */
    public Optional<User> findByNombreUsuario(String nombreUsuario) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery(
                    "from User where nombreUsuario=:nombre", User.class);
            q.setParameter("nombre", nombreUsuario);
            return Optional.ofNullable(q.uniqueResult());
        }
    }
}
