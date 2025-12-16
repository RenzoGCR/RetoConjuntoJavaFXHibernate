package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.session.SimpleSessionService;
import org.example.retoconjuntojavafxhibernate.user.UserService;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de edición de una película (editar-view.fxml).
 * Permite modificar los datos de una película existente y guardarlos en la base de datos.
 */
public class EditarController implements Initializable {

    @FXML
    private TextField tfTitulo;
    @FXML
    private TextField tfGenero;
    @FXML
    private TextField tfDirector;
    @FXML
    private TextField tfAño;
    @FXML
    private TextArea taDescripcion;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private final SimpleSessionService sessionService = new SimpleSessionService();
    private final UserService userService = new UserService();
    private Pelicula peliculaAEditar;

    /**
     * Inicializa el controlador, cargando la película a editar desde la sesión.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Recuperar la película de la sesión (la que se seleccionó en MainController)
        peliculaAEditar = (Pelicula) sessionService.getObject("pelicula_seleccionada");
        if (peliculaAEditar != null) {
            cargarDatos();
        }
    }

    /**
     * Carga los datos de la película en los campos del formulario.
     */
    private void cargarDatos() {
        tfTitulo.setText(peliculaAEditar.getTitulo());
        tfGenero.setText(peliculaAEditar.getGenero());
        tfDirector.setText(peliculaAEditar.getDirector());
        tfAño.setText(String.valueOf(peliculaAEditar.getAño()));
        taDescripcion.setText(peliculaAEditar.getDescripcion());
    }

    /**
     * Maneja el evento del botón "Guardar".
     * Actualiza el objeto Pelicula con los datos del formulario y lo guarda en la base de datos.
     */
    @FXML
    void guardarCambios(ActionEvent event) {
        if (peliculaAEditar == null) return;

        try {
            // Actualizar el objeto Pelicula con los nuevos datos
            peliculaAEditar.setTitulo(tfTitulo.getText());
            peliculaAEditar.setGenero(tfGenero.getText());
            peliculaAEditar.setDirector(tfDirector.getText());
            peliculaAEditar.setAño(Integer.parseInt(tfAño.getText()));
            peliculaAEditar.setDescripcion(taDescripcion.getText());

            // Llamar al servicio para actualizar la base de datos
            userService.updatePelicula(peliculaAEditar);

            JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Película actualizada", "Los cambios se han guardado correctamente.");

            // Volver a la vista principal
            volver(null);

        } catch (NumberFormatException e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de formato", "Campo 'Año' inválido", "El año debe ser un número válido.");
        } catch (Exception e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "Error al guardar", "No se pudieron guardar los cambios en la base de datos.");
            e.printStackTrace();
        }
    }

    /**
     * Maneja el evento del botón "Cancelar".
     * Vuelve a la vista principal sin guardar cambios.
     */
    @FXML
    void volver(ActionEvent event) {
        // Limpiar la sesión y volver a la vista principal
        sessionService.setObject("pelicula_seleccionada", null);
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/main-view.fxml");
    }
}
