package org.example.retoconjuntojavafxhibernate.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.util.ResourceBundle;

/**
 * Controlador principal de la aplicación (main-view.fxml).
 * Gestiona la vista principal, mostrando el catálogo de películas y las copias del usuario.
 * Adapta la interfaz dependiendo de si el usuario es administrador o no.
 */
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


    /**
     * Cierra la sesión del usuario actual y vuelve a la pantalla de login.
     * @param actionEvent El evento que disparó esta acción.
     */
    @javafx.fxml.FXML
    public void cerrarSesion(ActionEvent actionEvent) {
        sessionService.logout();
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/login-view.fxml");
    }

    /**
     * Cierra la aplicación.
     * @param actionEvent El evento que disparó esta acción.
     */
    @javafx.fxml.FXML
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

        // Configurar el listener de doble clic para la tabla de catálogo
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

    /**
     * Guarda la película seleccionada en la sesión y navega a la vista de detalle.
     * @param pelicula La película para mostrar en detalle.
     */
    private void verDetallePelicula(Pelicula pelicula) {
        // Guardar la película en el servicio de sesión para que el siguiente controlador pueda acceder a ella
        sessionService.setObject("pelicula_seleccionada", pelicula);
        // Cambiar a la escena de detalle
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/detail-view.fxml");
    }

    /**
     * Configura la interfaz para un usuario administrador.
     * Muestra el menú de administración y oculta los controles de usuario.
     */
    private void configurarVistaAdmin() {
        menuAdmin.setVisible(true);
        configurarTablaCatalogo();
        cargarCatalogo();
        table.setItems(FXCollections.observableArrayList());
        table.setPlaceholder(new Label("Vista de Admin: No gestiona copias personales"));
        btnAlquilar.setVisible(false);
    }

    /**
     * Configura la interfaz para un usuario estándar.
     * Oculta el menú de administración y muestra las copias del usuario.
     */
    private void configurarVistaUsuario() {
        menuAdmin.setVisible(false);
        configurarTablaCopias();
        cargarDatosUsuario();
        configurarTablaCatalogo();
        cargarCatalogo();
    }

    /**
     * Configura las columnas de la tabla del catálogo de películas.
     */
    private void configurarTablaCatalogo() {
        colCatTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
        colCatGenero.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGenero()));
        colCatAnio.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAño()).asObject());
    }

    /**
     * Carga todas las películas de la base de datos en la tabla del catálogo.
     */
    private void cargarCatalogo() {
        var listaPeliculas = userService.findAllPeliculas();
        tablaCatalogo.setItems(FXCollections.observableArrayList(listaPeliculas));
    }

    /**
     * Maneja el evento de alquilar una película.
     * Crea una copia de la película seleccionada y la asigna al usuario actual.
     * @param actionEvent El evento que disparó esta acción.
     */
    @javafx.fxml.FXML
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

    /**
     * Recarga los datos del usuario actual desde la base de datos y refresca la tabla de copias.
     * @param actionEvent El evento que disparó esta acción.
     */
    @javafx.fxml.FXML
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


    /**
     * Muestra una ventana de alerta simple.
     * @param titulo El título de la ventana de alerta.
     * @param contenido El mensaje a mostrar en la alerta.
     */
    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Configura las columnas de la tabla de copias del usuario.
     */
    private void configurarTablaCopias() {
        titulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getTitulo()));
        genero.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getGenero()));
        director.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getDirector()));
        descripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getDescripcion()));
        año.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPelicula().getAño()).asObject());
    }

    /**
     * Carga la copia de la película asignada al usuario actual en la tabla de copias.
     */
    private void cargarDatosUsuario() {
        ObservableList<CopiaPelicula> copias = FXCollections.observableArrayList();
        if (currentUser != null && currentUser.getCopiaAsignada() != null) {
            copias.add(currentUser.getCopiaAsignada());
        }
        table.setItems(copias);
    }

    /**
     * Navega a la vista del formulario para añadir una nueva película.
     * @param actionEvent El evento que disparó esta acción.
     */
    @javafx.fxml.FXML
    public void añadirPelicula(ActionEvent actionEvent) {
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/newFilmForm-view.fxml");
    }
}
