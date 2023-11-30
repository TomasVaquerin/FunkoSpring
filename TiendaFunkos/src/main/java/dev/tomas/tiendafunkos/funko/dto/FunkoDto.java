package dev.tomas.tiendafunkos.funko.dto;

import dev.tomas.tiendafunkos.categoria.models.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FunkoDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotNull
    private Double precio;

    @NotNull
    private Integer stock;

    private String imagen;

    @NotNull
    private String categoria;

    @NotNull
    private Boolean isDeleted;
}
