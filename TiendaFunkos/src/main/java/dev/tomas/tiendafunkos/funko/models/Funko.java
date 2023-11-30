package dev.tomas.tiendafunkos.funko.models;

import dev.tomas.tiendafunkos.categoria.models.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
@Table(name = "FUNKOS")
public class Funko {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, name = "NOMBRE")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(columnDefinition = "double default 0.0")
    @Builder.Default
    private Double precio = 0.0;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(columnDefinition = "integer default 0", name = "CANTIDAD")
    @Builder.Default
    private Integer stock = 0;

    public static final String IMAGEN_PREDETERMINADA = "https://via.placeholder.com/150";
    @Column(columnDefinition = "TEXT default '" + IMAGEN_PREDETERMINADA + "'") // Por defecto una imagen
    @Builder.Default
    private String imagen = IMAGEN_PREDETERMINADA;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime fechaCreated = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime fechaUpdated = LocalDateTime.now();

    @Column(columnDefinition = "boolean default false", name = "ISDELETED")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

}
