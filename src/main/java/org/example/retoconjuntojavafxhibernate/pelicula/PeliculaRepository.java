package org.example.retoconjuntojavafxhibernate.pelicula;

import org.example.retoconjuntojavafxhibernate.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class PeliculaRepository implements Repository<Pelicula> {

    SessionFactory sessionFactory;

    public PeliculaRepository(SessionFactory sessionFactory) {
        this.sessionFactory=sessionFactory;
    }

    @Override
    public Pelicula save(Pelicula entity) {
        return null;
    }

    @Override
    public Optional<Pelicula> delete(Pelicula entity) {
        try(Session session=sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(entity);
            session.getTransaction().commit();
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public Optional<Pelicula> deleteById(Long id) {
        try(Session session=sessionFactory.openSession()){
            Pelicula game = session.find(Pelicula.class,id);
            if(game!=null){
                session.beginTransaction();
                session.remove(game);
                session.getTransaction().commit();
            }
            return Optional.ofNullable(game);
        }
    }

    @Override
    public Optional<Pelicula> findById(Long id) {
        try(Session session=sessionFactory.openSession()){
            return Optional.ofNullable(session.find(Pelicula.class, id));
        }
    }

    @Override
    public List<Pelicula> findAll() {
        try(Session session=sessionFactory.openSession()){
            return session.createQuery("from Pelicula", Pelicula.class).list();
        }
    }

    @Override
    public Long count() {
        try(Session session=sessionFactory.openSession()){
            Long salida = session.createQuery("SELECT count(g) from Pelicula g",Long.class).getSingleResult();
            return salida;
        }
    }
}
