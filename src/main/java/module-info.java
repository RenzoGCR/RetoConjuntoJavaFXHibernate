module org.example.retoconjuntojavafxhibernate {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens org.example.retoconjuntojavafxhibernate to javafx.fxml;
    exports org.example.retoconjuntojavafxhibernate;
    exports org.example.retoconjuntojavafxhibernate.controllers;
    opens org.example.retoconjuntojavafxhibernate.controllers;

    opens org.example.retoconjuntojavafxhibernate.user;
    exports org.example.retoconjuntojavafxhibernate.pelicula;
    opens org.example.retoconjuntojavafxhibernate.pelicula;
    exports org.example.retoconjuntojavafxhibernate.utils;
    opens org.example.retoconjuntojavafxhibernate.utils;


}