package org.example.retoconjuntojavafxhibernate.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="usuarios")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "contraseña")
    private String contraseña;

    @OneToOne(
            mappedBy = "usuario", // Nombre del campo en la clase CopiaPelicula que apunta a este User
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY // Recomendado para rendimiento
    )
    private CopiaPelicula copiaAsignada;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                // Omitir contraseña y CopiaPelicula para evitar referencias circulares
                '}';
    }
}
