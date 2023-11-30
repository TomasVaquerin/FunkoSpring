package dev.tomas.tiendafunkos.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoBadPrice extends PedidoException {
    public ProductoBadPrice(UUID id) {
        super("Producto con id " + id + " no tiene un precio válido o no coincide con su precio actual");
    }
}
