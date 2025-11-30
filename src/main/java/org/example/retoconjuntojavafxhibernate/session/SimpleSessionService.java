package org.example.retoconjuntojavafxhibernate.session;

import org.example.retoconjuntojavafxhibernate.user.User;
import org.example.retoconjuntojavafxhibernate.utils.SessionService;

import java.util.HashMap;

/**
 * Una implementación simple de {@link SessionService} para gestionar la sesión del usuario.
 * Utiliza variables estáticas para mantener al usuario activo y datos de sesión adicionales.
 * <p>
 * <strong>Nota:</strong> Esta implementación es básica y no es segura para entornos con múltiples hilos (multi-threading).
 * Es adecuada para una aplicación de escritorio simple.
 */
public class SimpleSessionService implements SessionService<User> {

    private static User activeUser = null;
    private static final HashMap<String, Object> data = new HashMap<>();

    /**
     * Inicia la sesión para un usuario específico.
     * @param user El usuario que inicia sesión.
     */
    public void login(User user) {
        activeUser = user;
    }

    /**
     * Actualiza los datos del usuario en sesión.
     * @param user El objeto de usuario con los datos actualizados.
     */
    public void update(User user) {
        activeUser = user;
    }

    /**
     * Comprueba si hay un usuario actualmente en sesión.
     * @return {@code true} si hay un usuario logueado, {@code false} en caso contrario.
     */
    public boolean isLoggedIn() {
        return activeUser != null;
    }

    /**
     * Cierra la sesión del usuario actual, eliminando sus datos y cualquier otro dato de sesión.
     */
    public void logout() {
        activeUser = null;
        data.clear();
    }

    /**
     * Obtiene el usuario que está actualmente en sesión.
     * @return El {@link User} activo, o {@code null} si no hay nadie logueado.
     */
    @Override
    public User getActive() {
        return activeUser;
    }

    /**
     * Almacena un objeto genérico en el mapa de datos de la sesión.
     * @param key La clave con la que se asociará el objeto.
     * @param o El objeto a almacenar.
     */
    @Override
    public void setObject(String key, Object o) {
        data.put(key, o);
    }

    /**
     * Recupera un objeto genérico del mapa de datos de la sesión.
     * @param key La clave del objeto a recuperar.
     * @return El objeto asociado a la clave, o {@code null} si no se encuentra.
     */
    @Override
    public Object getObject(String key) {
        return data.get(key);
    }
}
