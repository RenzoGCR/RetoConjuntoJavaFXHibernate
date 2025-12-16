package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.session.SimpleSessionService;
import org.example.retoconjuntojavafxhibernate.user.User;
import org.example.retoconjuntojavafxhibernate.user.UserService;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador principal de la aplicación (main-view.fxml).
 * Gestiona la vista principal, mostrando el catálogo de películas y las copias del usuario.
 * Adapta la interfaz dependiendo de si el usuario es administrador o no.
 */
public class MainController implements Initializable {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button cerrarSesion;
    @FXML
    private Menu menuAdmin;
    @FXML
    private MenuItem itemAñadir;
    @FXML
    private Button salir;
    @FXML
    private TableView<CopiaPelicula> table;
    @FXML
    private Button btnEliminar; // Botón nuevo

    private User currentUser;
    private SimpleSessionService sessionService = new SimpleSessionService();
    private UserService userService = new UserService();

    @FXML
    private TableColumn<CopiaPelicula, String> descripcion;
    @FXML
    private TableColumn<CopiaPelicula, String> director;
    @FXML
    private TableColumn<CopiaPelicula, String> genero;
    @FXML
    private TableColumn<CopiaPelicula, String> titulo;
    @FXML
    private TableColumn<CopiaPelicula, Integer> año;
    @FXML
    private Button btnAlquilar;
    @FXML
    private TableView<Pelicula> tablaCatalogo;
    @FXML
    private TableColumn<Pelicula, Integer> colCatAnio;
    @FXML
    private TableColumn<Pelicula, String> colCatTitulo;
    @FXML
    private TableColumn<Pelicula, String> colCatGenero;


    /**
     * Cierra la sesión del usuario actual y vuelve a la pantalla de login.
     * @param actionEvent El evento que disparó esta acción.
     */
    @FXML
    public void cerrarSesion(ActionEvent actionEvent) {
        sessionService.logout();
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/login-view.fxml");
    }

    /**
     * Cierra la aplicación.
     * @param actionEvent El evento que disparó esta acción.
     */
    @FXML
    public void salir(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Inicializa el controlador. Carga el usuario de la sesión, determina si es
     * administrador y configura la vista correspondientemente.
     * @param url La ubicación utilizada para resolver rutas relativas.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User basicUser = sessionService.getActive();

        if (basicUser == null) {
            JavaFXUtil.setScene("login-view.fxml");
            return;
        }

        currentUser = userService.getUserWithDependencies(basicUser.getId());
        sessionService.login(currentUser);

        tablaCatalogo.setRowFactory(tv -> {
            TableRow<Pelicula> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Pelicula clickedPelicula = row.getItem();
                    verDetallePelicula(clickedPelicula);
                }
            });
            return row;
        });

        if (currentUser.isAdmin()) {
            configurarVistaAdmin();
        } else {
            configurarVistaUsuario();
        }
    }

    private void verDetallePelicula(Pelicula pelicula) {
        sessionService.setObject("pelicula_seleccionada", pelicula);
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/detail-view.fxml");
    }

    private void configurarVistaAdmin() {
        menuAdmin.setVisible(true);
        btnEliminar.setVisible(true); // Hacer visible el botón de eliminar
        configurarTablaCatalogo();
        cargarCatalogo();
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Vista de Admin: No gestiona copias personales"));
        btnAlquilar.setVisible(false);
    }

    private void configurarVistaUsuario() {
        menuAdmin.setVisible(false);
        btnEliminar.setVisible(false); // Asegurarse de que el botón esté oculto
        configurarTablaCopias();
        cargarDatosUsuario();
        configurarTablaCatalogo();
        cargarCatalogo();
    }

    private void configurarTablaCatalogo() {
        colCatTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
        colCatGenero.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGenero()));
        colCatAnio.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAño()).asObject());
    }

    private void cargarCatalogo() {
        var listaPeliculas = userService.findAllPeliculas();
        tablaCatalogo.setItems(FXCollections.observableArrayList(listaPeliculas));
    }

    /**
     * Maneja el evento de eliminar una película del catálogo.
     * Solo debe ser accesible por un administrador.
     * @param actionEvent El evento que disparó esta acción.
     */
    @FXML
    public void eliminarPelicula(ActionEvent actionEvent) {
        Pelicula seleccionada = tablaCatalogo.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una película del catálogo para eliminar.");
            return;
        }

        // Pedir confirmación antes de borrar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que quieres eliminar la película '" + seleccionada.getTitulo() + "'?");
        confirmacion.setContentText("Esta acción es irreversible y eliminará la película y todas sus copias asociadas.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Llamar al servicio para eliminar la película
                userService.removePeliculaOrCopia(currentUser, seleccionada);
                // Refrescar la tabla del catálogo para reflejar la eliminación
                tablaCatalogo.getItems().remove(seleccionada);
                mostrarAlerta("Éxito", "La película ha sido eliminada.");
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar la película: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void alquilarPelicula(ActionEvent actionEvent) {
        Pelicula seleccionada = tablaCatalogo.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una película del catálogo primero.");
            return;
        }

        try {
            userService.addPeliculaOrCopia(currentUser, seleccionada);
            mostrarAlerta("Éxito", "Película alquilada en la base de datos. Por favor, pulse 'Refrescar' para ver los cambios.");

        } catch (Exception e) {
            mostrarAlerta("Error de escritura", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void refrescarVistaUsuario(ActionEvent actionEvent) {
        try {
            System.out.println("Refrescando vista de usuario...");
            currentUser = userService.getUserWithDependencies(currentUser.getId());
            sessionService.login(currentUser);
            cargarDatosUsuario();
            System.out.println("Vista refrescada.");
        } catch (Exception e) {
            mostrarAlerta("Error al refrescar la vista", e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
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

    @FXML
    public void añadirPelicula(ActionEvent actionEvent) {
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/newFilmForm-view.fxml");
    }
}
