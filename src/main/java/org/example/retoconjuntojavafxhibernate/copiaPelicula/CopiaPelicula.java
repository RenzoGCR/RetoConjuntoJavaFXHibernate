package org.example.retoconjuntojavafxhibernate.copiaPelicula;

import jakarta.persistence.*;
import lombok.*;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.user.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"pelicula", "usuario"}) // Excluir ambas relaciones
@Entity
@Table(name = "copias_Pelicula")
public class CopiaPelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_copia")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id_pelicula")
    private Pelicula pelicula;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id_usuario")
    private User usuario;

    @Column(name = "estado")
    private String estado;

    @Column(name = "soporte")
    private String soporte;

    @Override
    public String toString() {
        // Nunca incluir relaciones LAZY en toString()
        return "CopiaPelicula{" +
                "id=" + id +
                ", estado='" + estado + '\'' +
                ", soporte='" + soporte + '\'' +
                // Opcional: incluir el ID de la película si es útil para depurar, pero no el objeto entero
                ", pelicula_id=" + (pelicula != null ? pelicula.getId() : "null") +
                '}';
    }
}
