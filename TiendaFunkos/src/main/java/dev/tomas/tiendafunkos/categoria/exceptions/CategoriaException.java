package dev.tomas.tiendafunkos.categoria.exceptions;

public abstract class CategoriaException extends RuntimeException {
    public CategoriaException(String message) {
        super(message);
    }
}
