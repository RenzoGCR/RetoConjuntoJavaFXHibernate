package org.example.retoconjuntojavafxhibernate.copiaPelicula;

import jakarta.persistence.*;
import lombok.*;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.user.User;

import java.io.Serializable;

/**
 * Entidad que representa una copia física de una película.
 * Cada copia está asociada a una película del catálogo y puede ser asignada a un único usuario.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"pelicula", "usuario"}) // Excluir ambas relaciones para evitar recursión y problemas de carga perezosa.
@Entity
@Table(name = "copias_Pelicula")
public class CopiaPelicula implements Serializable {

    /**
     * Identificador único de la copia, generado automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_copia")
    private Integer id;

    /**
     * La película del catálogo a la que pertenece esta copia.
     * Se carga de forma perezosa (LAZY) para optimizar el rendimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id_pelicula")
    private Pelicula pelicula;

    /**
     * El usuario que tiene actualmente alquilada esta copia.
     * Es una relación uno a uno y se carga de forma perezosa (LAZY).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id_usuario")
    private User usuario;

    /**
     * El estado actual de la copia (p. ej., "En posesión", "Disponible").
     */
    @Column(name = "estado")
    private String estado;

    /**
     * El formato físico de la copia (p. ej., "DVD", "Blu-ray").
     */
    @Column(name = "soporte")
    private String soporte;

    /**
     * Representación en cadena de la CopiaPelicula.
     * <p>
     * <strong>Importante:</strong> No incluye las relaciones {@code pelicula} y {@code usuario}
     * para evitar {@link org.hibernate.LazyInitializationException} cuando el objeto está fuera de una sesión de Hibernate.
     *
     * @return Una cadena que representa el estado del objeto.
     */
    @Override
    public String toString() {
        return "CopiaPelicula{" +
                "id=" + id +
                ", estado='" + estado + '\'' +
                ", soporte='" + soporte + '\'' +
                ", pelicula_id=" + (pelicula != null ? pelicula.getId() : "null") +
                '}';
    }
}
