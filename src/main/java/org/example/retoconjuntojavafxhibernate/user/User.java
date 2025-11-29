package org.example.retoconjuntojavafxhibernate.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;

import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "copiaAsignada") // Excluir la relación
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

    private boolean isAdmin;

    @OneToOne(
            mappedBy = "usuario", // Nombre del campo en la clase CopiaPelicula que apunta a este User
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY // Recomendado para rendimiento
    )
    private CopiaPelicula copiaAsignada;

    @Override
    public String toString() {
        // Nunca incluir relaciones LAZY en toString()
        return "User{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
