package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.session.SimpleSessionService;
import org.example.retoconjuntojavafxhibernate.user.User;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @javafx.fxml.FXML
    private MenuBar menuBar;
    @javafx.fxml.FXML
    private Button cerrarSesion;
    @javafx.fxml.FXML
    private Menu menuAdmin;
    @javafx.fxml.FXML
    private MenuItem itemAñadir;
    @javafx.fxml.FXML
    private Button salir;
    @javafx.fxml.FXML
    private TableView<CopiaPelicula> table;
    private User currentUser;
    private SimpleSessionService sessionService = new SimpleSessionService();
    @javafx.fxml.FXML
    private TableColumn<CopiaPelicula, String> descripcion;
    @javafx.fxml.FXML
    private TableColumn<CopiaPelicula, String> director;
    @javafx.fxml.FXML
    private TableColumn<CopiaPelicula, String> genero;
    @javafx.fxml.FXML
    private TableColumn<CopiaPelicula, String> titulo;
    @javafx.fxml.FXML
    private TableColumn<CopiaPelicula, Integer> año;


    @javafx.fxml.FXML
    public void cerrarSesion(ActionEvent actionEvent) {
        // Limpiar sesión
        sessionService.logout();
        // Volver a la pantalla de login
        JavaFXUtil.setScene("/org/example/GestorVideojuegosHibernateJavaFX/login-view.fxml"); // Ajusta la ruta si es necesario
    }

    @javafx.fxml.FXML
    public void salir(ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = sessionService.getActive();

        if (currentUser == null) {
            System.out.println("Error: No hay usuario en sesión. Redirigiendo al login...");
            JavaFXUtil.setScene("login-view.fxml");
            return;
        }

        if (currentUser.isAdmin()) {
            configurarVistaAdmin();
        } else {
            configurarVistaUsuario();
        }
    }
    private void configurarVistaAdmin() {
        // Si es Admin, mostramos el menú de administración
        menuAdmin.setVisible(true);
        // Opcional: Podrías cargar todas las películas en la tabla o dejarla vacía
        table.setPlaceholder(new Label("Vista de Administrador"));
    }
    private void configurarVistaUsuario() {
        // Si NO es Admin, ocultamos el menú
        menuAdmin.setVisible(false);

        // Configuramos las columnas de la tabla para mostrar la CopiaPelicula
        configurarTablaCopias();

        // Cargamos la copia asignada al usuario
        cargarDatosUsuario();
    }
    private void configurarTablaCopias() {
        // 1. TÍTULO
        titulo.setCellValueFactory(cellData -> {
            Pelicula p = cellData.getValue().getPelicula();
            if (p != null) return new SimpleStringProperty(p.getTitulo());
            return new SimpleStringProperty("Sin Info");
        });

        // 2. GÉNERO
        genero.setCellValueFactory(cellData -> {
            Pelicula p = cellData.getValue().getPelicula();
            if (p != null) return new SimpleStringProperty(p.getGenero());
            return new SimpleStringProperty("");
        });

        // 3. DIRECTOR
        director.setCellValueFactory(cellData -> {
            Pelicula p = cellData.getValue().getPelicula();
            if (p != null) return new SimpleStringProperty(p.getDirector());
            return new SimpleStringProperty("");
        });

        // 4. DESCRIPCIÓN
        descripcion.setCellValueFactory(cellData -> {
            Pelicula p = cellData.getValue().getPelicula();
            if (p != null) return new SimpleStringProperty(p.getDescripcion());
            return new SimpleStringProperty("");
        });

        // 5. AÑO (Manejamos Integer)
        año.setCellValueFactory(cellData -> {
            Pelicula p = cellData.getValue().getPelicula();
            if (p != null && p.getAño() != null) return new SimpleIntegerProperty(p.getAño()).asObject();
            return null;
        });
    }
    private void cargarDatosUsuario() {
        ObservableList<CopiaPelicula> copias = FXCollections.observableArrayList();

        // Como la relación es 1 a 1, obtenemos la única copia.
        // Verificamos si tiene una asignada para evitar NullPointerException
        if (currentUser.getCopiaAsignada() != null) {
            copias.add(currentUser.getCopiaAsignada());
        }

        table.setItems(copias);
    }
}
