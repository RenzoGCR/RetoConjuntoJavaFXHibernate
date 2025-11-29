package org.example.retoconjuntojavafxhibernate.user;

import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.utils.DataProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserService {


    public void addPeliculaOrCopia(User actor, Pelicula pelicula) {
        Transaction transaction = null;
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User persistentUser = session.find(User.class, actor.getId());

            if (persistentUser.isAdmin()) {
                session.persist(pelicula);
                System.out.println("Admin ha añadido una nueva película al catálogo: " + pelicula.getTitulo());

            } else {
                if (persistentUser.getCopiaAsignada() != null) {
                    throw new RuntimeException("El usuario ya tiene una película asignada. Devuélvela antes de coger otra.");
                }

                Pelicula persistentPelicula = session.find(Pelicula.class, pelicula.getId());
                if (persistentPelicula == null) {
                    throw new RuntimeException("La película seleccionada no existe en la base de datos.");
                }

                CopiaPelicula nuevaCopia = new CopiaPelicula();
                nuevaCopia.setEstado("En posesión");
                nuevaCopia.setSoporte("DVD");

                nuevaCopia.setPelicula(persistentPelicula);
                nuevaCopia.setUsuario(persistentUser);
                persistentUser.setCopiaAsignada(nuevaCopia);

                session.merge(persistentUser);
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al procesar el alquiler: " + e.getMessage(), e);
        }
    }

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

    public User removePeliculaOrCopia(User actor, Pelicula pelicula) {
        Transaction transaction = null;
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User persistentUser = session.find(User.class, actor.getId());

            if (persistentUser.isAdmin()) {
                Pelicula pToDelete = session.find(Pelicula.class, pelicula.getId());
                if (pToDelete != null) {
                    session.remove(pToDelete);
                    System.out.println("Admin ha eliminado la película y todas sus copias.");
                }

            } else {
                CopiaPelicula copia = persistentUser.getCopiaAsignada();

                if (copia != null) {
                    persistentUser.setCopiaAsignada(null);
                    session.remove(copia);
                    System.out.println("Usuario ha devuelto su copia.");
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

    public List<Pelicula> findAllPeliculas() {
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            return session.createQuery("FROM Pelicula", Pelicula.class).list();
        }
    }

    public User getUserWithDependencies(Integer userId) {
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            // Consulta mejorada para cargar explícitamente todas las relaciones necesarias.
            // Esto crea un objeto "listo para la vista" sin proxies perezosos que puedan fallar.
            Query<User> q = session.createQuery(
                    "SELECT u FROM User u " +
                            "LEFT JOIN FETCH u.copiaAsignada c " +
                            "LEFT JOIN FETCH c.pelicula " +
                            "LEFT JOIN FETCH c.usuario " + // Añadido para asegurar que la relación inversa también se cargue
                            "WHERE u.id = :id", User.class);
            q.setParameter("id", userId);
            return q.uniqueResult();
        }
    }

}
