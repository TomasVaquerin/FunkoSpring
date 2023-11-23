package dev.tomas.tiendafunkos.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFound extends FunkoException {
    public FunkoNotFound(Long id) {
        super("Producto con id " + id + " no encontrado");
    }

    public FunkoNotFound(UUID uuid) {
        super("Producto con uuid " + uuid + " no encontrado");
    }

}
