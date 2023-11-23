package dev.tomas.tiendafunkos.notifications.models;

public record Notificacion<T>(
        String entity,
        Tipo type,
        T data,
        String createdAt
) {

    public enum Tipo {CREATE, UPDATE, DELETE}

}