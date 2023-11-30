package dev.tomas.tiendafunkos.notifications.mapper;

import dev.tomas.tiendafunkos.funko.models.Funko;
import dev.tomas.tiendafunkos.notifications.dto.FunkoNotificationDto;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificationDto toProductNotificationDto(Funko funko) {
        return new FunkoNotificationDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getStock(),
                funko.getImagen(),
                funko.getFechaCreated().toString(),
                funko.getFechaUpdated().toString(),
                funko.getCategoria().getTipoCategoria(),
                funko.getIsDeleted()
        );
    }
}
