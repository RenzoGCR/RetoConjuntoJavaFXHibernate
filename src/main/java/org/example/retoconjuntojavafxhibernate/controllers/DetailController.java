package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.session.SimpleSessionService;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de detalle de una película (detail-view.fxml).
 * Muestra la información completa de una película seleccionada.
 */
public class DetailController implements Initializable {
    @javafx.fxml.FXML
    private Button btnVolver;
    @javafx.fxml.FXML
    private Label lblGenero;
    @javafx.fxml.FXML
    private TextArea taDescripcion;
    @javafx.fxml.FXML
    private Label lblTitulo;
    @javafx.fxml.FXML
    private Label lblAño;
    @javafx.fxml.FXML
    private Label lblDirector;

    private final SimpleSessionService sessionService = new SimpleSessionService();

    /**
     * Se ejecuta al inicializar el controlador.
     * Recupera la película seleccionada del servicio de sesión y carga sus datos en la vista.
     *
     * @param url La ubicación utilizada para resolver rutas relativas.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Recuperar la película del servicio de sesión
        Pelicula pelicula = (Pelicula) sessionService.getObject("pelicula_seleccionada");
        cargarDatosPelicula(pelicula);
    }

    /**
     * Carga los datos de un objeto Pelicula en los componentes de la interfaz de usuario.
     *
     * @param pelicula El objeto Pelicula cuyos datos se van a mostrar.
     */
    private void cargarDatosPelicula(Pelicula pelicula) {
        if (pelicula != null) {
            lblTitulo.setText(pelicula.getTitulo());
            lblGenero.setText(pelicula.getGenero());
            lblDirector.setText(pelicula.getDirector());
            lblAño.setText(String.valueOf(pelicula.getAño()));
            taDescripcion.setText(pelicula.getDescripcion());

            // Configurar propiedades del TextArea
            taDescripcion.setEditable(false); // El usuario no puede editar la descripción
            taDescripcion.setWrapText(true);  // <-- ESTA LÍNEA HACE QUE EL TEXTO SE AJUSTE
        }
    }

    /**
     * Maneja el evento del botón "Volver".
     * Navega de vuelta a la vista principal de la aplicación.
     *
     * @param actionEvent El evento que disparó esta acción.
     */
    @javafx.fxml.FXML
    public void volver(ActionEvent actionEvent) {
        // Limpiar la película de la sesión para no dejar datos residuales
        sessionService.setObject("pelicula_seleccionada", null);
        // Volver a la escena principal
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/main-view.fxml");
    }
}
