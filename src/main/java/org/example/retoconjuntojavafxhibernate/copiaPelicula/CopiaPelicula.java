package org.example.retoconjuntojavafxhibernate.copiaPelicula;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.retoconjuntojavafxhibernate.pelicula.Pelicula;
import org.example.retoconjuntojavafxhibernate.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "copias_Pelicula")
public class CopiaPelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_copia")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id_pelicula", unique = true)
    private Pelicula pelicula;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id_user")
    private User usuario;

    @Column(name = "estado")
    private String estado;

    @Column(name = "soporte")
    private String soporte;

}
