package dev.tomas.tiendafunkos.notifications.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FunkoNotificationDto(
        UUID id,
        String nombre,
        Double precio,
        Integer stock,
        String imagen,
        LocalDateTime fechaCreated,
        LocalDateTime fechaUpdated,
        Boolean isDeleted,
        String categoria
) {
}