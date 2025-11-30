package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.retoconjuntojavafxhibernate.session.AuthService;
import org.example.retoconjuntojavafxhibernate.session.SimpleSessionService;
import org.example.retoconjuntojavafxhibernate.user.User;
import org.example.retoconjuntojavafxhibernate.user.UserRepository;
import org.example.retoconjuntojavafxhibernate.utils.DataProvider;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de inicio de sesión (login-view.fxml).
 * Gestiona la autenticación del usuario y la navegación a la vista principal.
 */
public class LoginController implements Initializable {
    @FXML
    private TextField txtContraseña;
    @FXML
    private TextField txtCorreo;
    @FXML
    private Label info;

    private UserRepository userRepository;
    private AuthService authService;

    /**
     * Se ejecuta al inicializar el controlador después de que la vista FXML ha sido cargada.
     * Inicializa los servicios necesarios para la autenticación.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userRepository = new UserRepository(DataProvider.getSessionFactory());
        authService = new AuthService(userRepository);
    }

    /**
     * Maneja el evento de clic en el botón "Entrar".
     * Valida las credenciales del usuario y, si son correctas, establece la sesión
     * y navega a la vista principal de la aplicación.
     *
     * @param actionEvent El evento que disparó esta acción.
     */
    @FXML
    public void entrar(ActionEvent actionEvent) {
        Optional<User> user = authService.validateUser(txtCorreo.getText(), txtContraseña.getText());
        if (user.isPresent()) {
            SimpleSessionService sessionService = new SimpleSessionService();
            sessionService.login(user.get());
            sessionService.setObject("id", user.get().getId());
            JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/main-view.fxml");
        } else {
            // Opcional: Mostrar un mensaje de error al usuario
            info.setText("Correo o contraseña incorrectos.");
            info.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Maneja el evento de clic en el botón "Salir".
     * Cierra la aplicación.
     *
     * @param actionEvent El evento que disparó esta acción.
     */
    @FXML
    public void Salir(ActionEvent actionEvent) {
        System.exit(0);
    }
}
