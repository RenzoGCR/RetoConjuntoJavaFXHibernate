package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.session.SimpleSessionService;
import org.example.retoconjuntojavafxhibernate.user.User;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de detalle de una película (detail-view.fxml).
 * Muestra la información completa de una película seleccionada.
 */
public class DetailController implements Initializable {
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnEditar; // Botón nuevo
    @FXML
    private Label lblGenero;
    @FXML
    private TextArea taDescripcion;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblAño;
    @FXML
    private Label lblDirector;

    private final SimpleSessionService sessionService = new SimpleSessionService();
    private Pelicula peliculaActual;

    /**
     * Se ejecuta al inicializar el controlador.
     * Recupera la película seleccionada y el usuario de la sesión y carga los datos.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        peliculaActual = (Pelicula) sessionService.getObject("pelicula_seleccionada");
        User currentUser = sessionService.getActive();

        // El botón de editar solo es visible si el usuario es administrador
        if (currentUser != null && currentUser.isAdmin()) {
            btnEditar.setVisible(true);
        } else {
            btnEditar.setVisible(false);
        }

        cargarDatosPelicula(peliculaActual);
    }

    /**
     * Carga los datos de un objeto Pelicula en los componentes de la interfaz de usuario.
     */
    private void cargarDatosPelicula(Pelicula pelicula) {
        if (pelicula != null) {
            lblTitulo.setText(pelicula.getTitulo());
            lblGenero.setText(pelicula.getGenero());
            lblDirector.setText(pelicula.getDirector());
            lblAño.setText(String.valueOf(pelicula.getAño()));
            taDescripcion.setText(pelicula.getDescripcion());
            taDescripcion.setEditable(false);
            taDescripcion.setWrapText(true);
        }
    }

    /**
     * Navega a la vista de edición para la película actual.
     * La película actual ya está en la sesión, así que solo necesita cambiar de escena.
     */
    @FXML
    void editarPelicula(ActionEvent event) {
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/editar-view.fxml");
    }

    /**
     * Maneja el evento del botón "Volver".
     * Navega de vuelta a la vista principal de la aplicación.
     */
    @FXML
    public void volver(ActionEvent actionEvent) {
        sessionService.setObject("pelicula_seleccionada", null);
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/main-view.fxml");
    }
}
