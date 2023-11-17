package dev.tomas.tiendafunkos.categoria.dto;

import dev.tomas.tiendafunkos.categoria.models.Categoria;
import lombok.Data;

@Data
public class CategoriaDto {
    private Categoria.tipoCategoria tipoCategoria;

    public enum tipoCategoria{
        SERIE, DISNEY, SUPERHEROES, PELICULA, OTROS
    }
}
