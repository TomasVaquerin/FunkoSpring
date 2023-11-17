package dev.tomas.tiendafunkos.categoria.mappers;

import dev.tomas.tiendafunkos.categoria.dto.CategoriaDto;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriaMapper {

    public Categoria toCategoria(CategoriaDto categoriaDto) {
        return new Categoria(
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                categoriaDto.getTipoCategoria()
        );
    }

    public Categoria toCategoria(CategoriaDto dto, Categoria categoria) {
        return new Categoria(
                categoria.getId(),
                categoria.getFechaCreated(),
                LocalDateTime.now(),
                dto.getTipoCategoria() != null ? dto.getTipoCategoria() : categoria.getTipoCategoria()

        );
    }
}
