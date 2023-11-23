package dev.tomas.tiendafunkos.funko.mapper;

import dev.tomas.tiendafunkos.funko.dto.FunkoDto;
import dev.tomas.tiendafunkos.funko.models.Funko;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FunkoMapper {

    public Funko toFunko(FunkoDto funkoDto) {
        return Funko.builder()
                .id(null)
                .nombre(funkoDto.getNombre())
                .precio(funkoDto.getPrecio())
                .stock(funkoDto.getStock())
                .imagen(funkoDto.getImagen())
                .categoria(funkoDto.getCategoria())
                .isDeleted(funkoDto.getIsDeleted() != null && funkoDto.getIsDeleted())
                .build();
    }

    public Funko toFunko(FunkoDto dto, Funko funko) {
        return Funko.builder()
                .id(funko.getId())
                .nombre(dto.getNombre() != null ? dto.getNombre() : funko.getNombre())
                .precio(dto.getPrecio() != null ? dto.getPrecio() : funko.getPrecio())
                .stock(dto.getStock() != null ? dto.getStock() : funko.getStock())
                .imagen(dto.getImagen() != null ? dto.getImagen() : funko.getImagen())
                .fechaCreated(funko.getFechaCreated())
                .fechaUpdated(LocalDateTime.now())
                .categoria(dto.getCategoria() != null ? dto.getCategoria() : funko.getCategoria())
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : funko.getIsDeleted())
                .build();
    }
}
