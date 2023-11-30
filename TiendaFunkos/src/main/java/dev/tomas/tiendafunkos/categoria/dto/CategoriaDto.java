package dev.tomas.tiendafunkos.categoria.dto;

import dev.tomas.tiendafunkos.categoria.models.Categoria;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CategoriaDto {
    @NotNull
    private final String tipoCategoria;
    private final Boolean isDeleted;
}
