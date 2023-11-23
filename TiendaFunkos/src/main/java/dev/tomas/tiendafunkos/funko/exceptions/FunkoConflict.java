package dev.tomas.tiendafunkos.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class FunkoConflict extends FunkoException {

    public FunkoConflict(String message) {
        super(message);
    }
}
