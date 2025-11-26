package org.example.retoconjuntojavafxhibernate.user;

import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.utils.DataProvider;
import org.hibernate.Session;

public class UserService {


    public User deleteGameFromUser(User user, Pelicula game) {
        try(Session s = DataProvider.getSessionFactory().openSession()) {
            s.beginTransaction();
            // Recargar datos desde la BD
            User currentUser = s.find(User.class, user.getId());
            Pelicula gameToDelete = s.find(Pelicula.class, game.getId());

            // Buscar y eliminar el juego de la colección y la bbdd
            currentUser.getGames().remove(gameToDelete);
            currentUser.getGames().removeIf(g -> g.getId().equals(game.getId()));
            s.remove(gameToDelete);

            s.getTransaction().commit();

            return currentUser;
        }
    }

    public User createNewGame(Pelicula newGame, User actualUser) {
        try(Session s = DataProvider.getSessionFactory().openSession()) {
            actualUser.addGame(newGame);
            s.beginTransaction();
            s.merge(actualUser);
            s.getTransaction().commit();
            return s.find(User.class, actualUser.getId());
        }

    }
}
