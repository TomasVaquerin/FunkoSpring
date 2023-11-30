package dev.tomas.tiendafunkos.categoria.controllers;

import dev.tomas.tiendafunkos.categoria.dto.CategoriaDto;
import dev.tomas.tiendafunkos.categoria.exceptions.CategoriaConflict;
import dev.tomas.tiendafunkos.categoria.exceptions.CategoriaNotFound;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import dev.tomas.tiendafunkos.categoria.servicies.CategoriaService;

import dev.tomas.tiendafunkos.utils.pageresponse.PageResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/categorias")
@Slf4j
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping()
    public ResponseEntity<Categoria> createCategory(@Valid @RequestBody CategoriaDto categoriaCreateDto) {
        log.info("Creando categegoría: " + categoriaCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.save(categoriaCreateDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoryById(@PathVariable Long id) {
        log.info("Buscando producto por id: " + id);
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Borrando categoria por id: " + id);
        categoriaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoriaDto categoriaUpdateDto) {
        log.info("Actualizando categoria por id: " + id + " con categoria: " + categoriaUpdateDto);
        return ResponseEntity.ok(categoriaService.update(id, categoriaUpdateDto));
    }

    @GetMapping()
    public ResponseEntity<PageResponse<Categoria>> getAllCategories(
            @RequestParam(required = false) Optional<String> tipoCategoria,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (page < 0 || size < 1) {
            log.warn("No se puede buscar con valores negativos y tampoco se puede poner size a 0");
            return ResponseEntity.badRequest().build();
        }

        log.info("Buscando todos las categorias con nombre: " + tipoCategoria + " y borrados: " + isDeleted);
        // Creamos el objeto de ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(categoriaService.findAll(tipoCategoria, isDeleted, pageable), sortBy, direction));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CategoriaNotFound.class)
    public ResponseEntity<String> handleValidationExceptions(CategoriaNotFound ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CategoriaConflict.class)
    public ResponseEntity<String> handleValidationExceptions(CategoriaConflict ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}