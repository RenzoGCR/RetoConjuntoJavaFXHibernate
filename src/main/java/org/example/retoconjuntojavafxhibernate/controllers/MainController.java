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

        // 2. Cargar la tabla del CATÁLOGO (Lo que querías añadir)
        // Reutilizamos los mismos métodos que usa el usuario normal
        configurarTablaCatalogo();
        cargarCatalogo();

        // 3. Dejar la tabla de COPIAS vacía
        // Al no llamar a 'cargarDatosUsuario()', la lista no se llena.
        // Opcionalmente, puedes establecer un mensaje o limpiar explícitamente:
        table.setItems(FXCollections.observableArrayList()); // Asegura que esté vacía
        table.setPlaceholder(new Label("Vista de Admin: No gestiona copias personales"));

        // OPCIONAL: Desactivar el botón de alquilar para el admin
        // (Ya que la lógica de 'alquilar' para admin en tu servicio intentaba CREAR películas nuevas,
        //  no alquilar existentes, y podría dar error si seleccionas una existente).
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
        // 1. Obtener la selección de la tabla de CATÁLOGO
        Pelicula seleccionada = tablaCatalogo.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una película del catálogo primero.");
            return;
        }

        try {
            // 2. Llamar a tu lógica existente en UserService
            User usuarioActualizado = userService.addPeliculaOrCopia(currentUser, seleccionada);

            if (usuarioActualizado != null) {
                // 3. Éxito: Actualizar usuario y refrescar tabla de copias
                currentUser = usuarioActualizado;
                sessionService.login(currentUser);

                cargarDatosUsuario(); // Refresca la tabla de arriba (Mis copias)
                mostrarAlerta("Éxito", "Has alquilado: " + seleccionada.getTitulo());
            }

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage()); // Muestra "El usuario ya tiene una película..."
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
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
