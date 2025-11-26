package org.example.retoconjuntojavafxhibernate.pelicula;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.retoconjuntojavafxhibernate.copiaPelicula.CopiaPelicula;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="peliculas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pelicula implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Integer id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "genero")
    private String genero;

    @Column(name = "año")
    private Integer año;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "director")
    private String director;

    @OneToMany(
            mappedBy = "pelicula", // Nombre del campo en la clase CopiaPelicula
            cascade = CascadeType.ALL, // Las operaciones de persistencia se propagan
            fetch = FetchType.LAZY // Carga perezosa (solo se carga si se accede a ella)
    )
    private Set<CopiaPelicula> copias; // Cambiado a una colección y nombre más descriptivo

    private String image_url;

    @Override
    public String toString() {
        return "Pelicula{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", año=" + año +
                ", descripcion='" + descripcion + '\'' +
                ", director='" + director + '\'' +
                ", copias=" + copias +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
