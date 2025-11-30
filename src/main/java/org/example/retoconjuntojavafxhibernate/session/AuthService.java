package org.example.retoconjuntojavafxhibernate.session;

import org.example.retoconjuntojavafxhibernate.user.User;
import org.example.retoconjuntojavafxhibernate.user.UserRepository;

import java.util.Optional;

/**
 * Servicio de autenticación.
 * Se encarga de validar las credenciales de un usuario contra la base de datos.
 */
public class AuthService {

    private final UserRepository userRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * @param userRepository El repositorio para acceder a los datos de los usuarios.
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Valida si un usuario existe con el correo y la contraseña proporcionados.
     *
     * @param email El correo electrónico (nombre de usuario) a validar.
     * @param password La contraseña a validar.
     * @return Un {@link Optional} que contiene el {@link User} si la validación es exitosa,
     *         o un Optional vacío si las credenciales son incorrectas o el usuario no existe.
     */
    public Optional<User> validateUser(String email, String password) {
        // Busca al usuario por su nombre de usuario (email)
        Optional<User> userOptional = userRepository.findByNombreUsuario(email);

        // Si el usuario existe, compara la contraseña
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getContraseña().equals(password)) {
                // Contraseña correcta, devuelve el usuario
                return userOptional;
            } else {
                // Contraseña incorrecta, devuelve vacío
                return Optional.empty();
            }
        }
        // El usuario no existe, devuelve vacío
        return Optional.empty();
    }
}
