package dev.tomas.tiendafunkos.categoria.models;

import dev.tomas.tiendafunkos.categoria.dto.CategoriaDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = "CATEGORIAS")
@Builder
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime fechaCreated = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime fechaUpdated = LocalDateTime.now();


    @NotNull
    private tipoCategoria tipoCategoria;

    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    public enum tipoCategoria{
        SERIE, DISNEY, SUPERHEROES, PELICULA, OTROS
    }


}
