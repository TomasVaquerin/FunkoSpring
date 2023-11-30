package dev.tomas.tiendafunkos.funko.mapper;

import dev.tomas.tiendafunkos.categoria.models.Categoria;
import dev.tomas.tiendafunkos.funko.dto.FunkoDto;
import dev.tomas.tiendafunkos.funko.models.Funko;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FunkoMapper {

    public Funko toFunko(FunkoDto funkoDto, Categoria categoria) {
        return Funko.builder()
                .nombre(funkoDto.getNombre())
                .precio(funkoDto.getPrecio())
                .stock(funkoDto.getStock())
                .imagen(funkoDto.getImagen())
                .categoria(categoria)
                .isDeleted(funkoDto.getIsDeleted() != null && funkoDto.getIsDeleted())
                .build();
    }

    public Funko toFunko(FunkoDto dto, Funko funko, Categoria categoria){
        return Funko.builder()
                .id(funko.getId())
                .nombre(dto.getNombre() != null ? dto.getNombre() : funko.getNombre())
                .precio(dto.getPrecio() != null ? dto.getPrecio() : funko.getPrecio())
                .stock(dto.getStock() != null ? dto.getStock() : funko.getStock())
                .imagen(dto.getImagen() != null ? dto.getImagen() : funko.getImagen())
                .fechaCreated(funko.getFechaCreated())
                .fechaUpdated(LocalDateTime.now())
                .categoria(categoria)
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : funko.getIsDeleted())
                .build();
    }
}
