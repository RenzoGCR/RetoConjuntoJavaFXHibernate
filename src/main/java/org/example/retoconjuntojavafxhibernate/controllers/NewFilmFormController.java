package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.user.UserService;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

public class NewFilmFormController {

    @javafx.fxml.FXML
    private TextField tfTitulo;
    @javafx.fxml.FXML
    private TextArea taDescripcion;
    @javafx.fxml.FXML
    private Button btnCancelar;
    @javafx.fxml.FXML
    private TextField tfGenero;
    @javafx.fxml.FXML
    private Button btnAgregar;
    @javafx.fxml.FXML
    private TextField tfDirector;
    @javafx.fxml.FXML
    private TextField tfAño;

    // El controlador ahora necesita su propio servicio para guardar la película
    private final UserService userService = new UserService();

    @javafx.fxml.FXML
    public void cancelar(ActionEvent actionEvent) {
        // Simplemente vuelve a la vista principal
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/main-view.fxml");
    }

    @javafx.fxml.FXML
    public void agregar(ActionEvent actionEvent) {
        String titulo = tfTitulo.getText();
        String genero = tfGenero.getText();
        String añoStr = tfAño.getText();
        String director = tfDirector.getText();
        String descripcion = taDescripcion.getText();

        if (titulo.isEmpty() || genero.isEmpty() || añoStr.isEmpty() || director.isEmpty() || descripcion.isEmpty()) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "Campos incompletos", "Por favor, completa todos los campos.");
            return;
        }

        try {
            int año = Integer.parseInt(añoStr);

            Pelicula nuevaPelicula = new Pelicula();
            nuevaPelicula.setTitulo(titulo);
            nuevaPelicula.setGenero(genero);
            nuevaPelicula.setAño(año);
            nuevaPelicula.setDirector(director);
            nuevaPelicula.setDescripcion(descripcion);

            // El propio controlador se encarga de guardar la película
            Pelicula peliculaGuardada = userService.savePelicula(nuevaPelicula);

            if (peliculaGuardada != null) {
                JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Película guardada", "La película ha sido añadida al catálogo.");
                // Y después de guardar, vuelve a la vista principal
                JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/main-view.fxml");
            } else {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "Error en la base de datos", "No se pudo guardar la película.");
            }

        } catch (NumberFormatException e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de formato", "Campo 'Año' inválido", "El año debe ser un número válido.");
        }
    }
}
