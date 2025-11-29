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
import org.example.retoconjuntojavafxhibernate.user.UserService;
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
    private UserService userService = new UserService();

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
    private Button btnAlquilar;
    @javafx.fxml.FXML
    private TableView<Pelicula> tablaCatalogo;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, Integer> colCatAnio;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colCatTitulo;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colCatGenero;


    @javafx.fxml.FXML
    public void cerrarSesion(ActionEvent actionEvent) {
        // Limpiar sesión
        sessionService.logout();
        // Volver a la pantalla de login
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/login-view.fxml");
    }

    @javafx.fxml.FXML
    public void salir(ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User basicUser = sessionService.getActive();


        if (basicUser == null) {
            System.out.println("Error: No hay usuario en sesión. Redirigiendo al login...");
            JavaFXUtil.setScene("login-view.fxml");
            return;
        }

        // Carga inicial del usuario con todas sus dependencias
        currentUser = userService.getUserWithDependencies(basicUser.getId());
        sessionService.login(currentUser);

        if (currentUser.isAdmin()) {
            configurarVistaAdmin();
        } else {
            configurarVistaUsuario();
        }
    }
    private void configurarVistaAdmin() {
        // 1. Mostrar herramientas de administración
        menuAdmin.setVisible(true);

        // 2. Cargar la tabla del CATÁLOGO
        configurarTablaCatalogo();
        cargarCatalogo();

        // 3. Cargar la tabla de COPIAS vacía
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Vista de Admin: No gestiona copias personales"));

        // Desactivar el botón de alquilar para el admin
        btnAlquilar.setVisible(false);
    }
    private void configurarVistaUsuario() {
        // Si NO es Admin, ocultamos el menú
        menuAdmin.setVisible(false);

        // Configuramos las columnas de la tabla para mostrar la CopiaPelicula
        configurarTablaCopias();

        // Cargamos la copia asignada al usuario
        cargarDatosUsuario();

        configurarTablaCatalogo();
        cargarCatalogo();
    }

    private void configurarTablaCatalogo() {
        // Vinculamos las columnas de la tabla de películas disponibles
        colCatTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
        colCatGenero.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGenero()));
        colCatAnio.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAño()).asObject());
    }

    private void cargarCatalogo() {
        // Usamos el nuevo metodo del servicio para traer todas las películas
        var listaPeliculas = userService.findAllPeliculas();
        tablaCatalogo.setItems(FXCollections.observableArrayList(listaPeliculas));
    }

    @javafx.fxml.FXML
    public void alquilarPelicula(ActionEvent actionEvent) {
        Pelicula seleccionada = tablaCatalogo.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una película del catálogo primero.");
            return;
        }

        try {
            // 1. Ejecutar la operación de escritura en la base de datos
            userService.addPeliculaOrCopia(currentUser, seleccionada);

            // 2. Éxito: Volver a cargar el usuario desde la BD con todas sus dependencias
            currentUser = userService.getUserWithDependencies(currentUser.getId());
            sessionService.login(currentUser); // Actualizar la sesión global

            // 3. Refrescar la tabla de copias del usuario con el objeto recién cargado
            cargarDatosUsuario();
            mostrarAlerta("Éxito", "Has alquilado: " + seleccionada.getTitulo());

        } catch (Exception e) {
            // Mostramos el mensaje de la excepción que viene del servicio
            mostrarAlerta("Error", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }

    private void configurarTablaCopias() {
        titulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getTitulo()));
        genero.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getGenero()));
        director.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getDirector()));
        descripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getDescripcion()));
        año.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPelicula().getAño()).asObject());
    }
    private void cargarDatosUsuario() {
        ObservableList<CopiaPelicula> copias = FXCollections.observableArrayList();
        if (currentUser != null && currentUser.getCopiaAsignada() != null) {
            copias.add(currentUser.getCopiaAsignada());
        }
        table.setItems(copias);
    }

    @javafx.fxml.FXML
    public void añadirPelicula(ActionEvent actionEvent) {
        // Este es el método que se debe usar en el FXML para el MenuItem
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/newFilmForm-view.fxml");
    }
}
