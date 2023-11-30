package dev.tomas.tiendafunkos.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoNotStock extends PedidoException {
    public ProductoNotStock(UUID id) {
        super("Producto con id " + id + " no tiene stock suficiente");
    }
}
