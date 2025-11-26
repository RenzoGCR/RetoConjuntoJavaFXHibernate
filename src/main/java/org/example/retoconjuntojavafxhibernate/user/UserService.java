package org.example.retoconjuntojavafxhibernate.user;

import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.utils.DataProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserService {


    public User addPeliculaOrCopia(User actor, Pelicula pelicula) {
        Transaction transaction = null;
        try (Session session = DataProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Refrescamos el usuario para asegurarnos de tener la última versión
            User persistentUser = session.find(User.class, actor.getId());

            if (persistentUser.isAdmin()) {
                // --- LÓGICA DE ADMIN: CREAR PELÍCULA ---
                // El admin está insertando una nueva película en el catálogo global.
                // Asumimos que 'pelicula' es un objeto transient (nuevo, sin ID).
                session.persist(pelicula);
                System.out.println("Admin ha añadido una nueva película al catálogo: " + pelicula.getTitulo());

            } else {
                // --- LÓGICA DE USUARIO: CREAR COPIA (ALQUILAR) ---
                // El usuario quiere una copia de una película YA EXISTENTE.

                // 1. Verificamos si ya tiene una copia (regla de negocio 1:1)
                if (persistentUser.getCopiaAsignada() != null) {
                    throw new RuntimeException("El usuario ya tiene una película asignada. Devuélvela antes de coger otra.");
                }

                // 2. Buscamos la película existente en la BD para asociarla
                Pelicula persistentPelicula = session.find(Pelicula.class, pelicula.getId());
                if (persistentPelicula == null) {
                    throw new RuntimeException("La película seleccionada no existe en la base de datos.");
                }

                // 3. Creamos la nueva copia
                CopiaPelicula nuevaCopia = new CopiaPelicula();
                nuevaCopia.setPelicula(persistentPelicula); // Relación con la película
                nuevaCopia.setUsuario(persistentUser);      // Relación con el usuario (Dueño)
                nuevaCopia.setEstado("En posesión");        // Ejemplo de estado
                nuevaCopia.setSoporte("DVD");               // Ejemplo por defecto

                // 4. Guardamos la copia (Esto inserta en tabla copia_pelicula)
                session.persist(nuevaCopia);

                // Actualizamos la referencia en el usuario para el retorno
                persistentUser.setCopiaAsignada(nuevaCopia);
                System.out.println("Usuario ha adquirido una copia de: " + persistentPelicula.getTitulo());
            }

            transaction.commit();

            // Retornamos el usuario actualizado (útil para refrescar la UI)
            session.refresh(persistentUser);
            return persistentUser;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
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
                // --- LÓGICA DE ADMIN: BORRAR PELÍCULA ---
                // Borra la película de la tabla 'peliculas'.
                // IMPORTANTE: Si Pelicula tiene @OneToMany(cascade=ALL) hacia copias,
                // esto borrará también todas las copias de esa película en el mundo.

                Pelicula pToDelete = session.find(Pelicula.class, pelicula.getId());
                if (pToDelete != null) {
                    session.remove(pToDelete);
                    System.out.println("Admin ha eliminado la película y todas sus copias.");
                }

            } else {
                // --- LÓGICA DE USUARIO: BORRAR COPIA (DEVOLVER) ---
                // Solo elimina la copia asignada a ESTE usuario.

                CopiaPelicula copia = persistentUser.getCopiaAsignada();

                // Verificamos que la copia que tiene es de la película que dice querer borrar
                // (O simplemente borramos lo que tenga asignado, depende de tu UI).
                if (copia != null) {
                    // Rompemos la relación en el objeto usuario (buena práctica)
                    persistentUser.setCopiaAsignada(null);

                    // Borramos la fila de la tabla copia_pelicula
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
}
