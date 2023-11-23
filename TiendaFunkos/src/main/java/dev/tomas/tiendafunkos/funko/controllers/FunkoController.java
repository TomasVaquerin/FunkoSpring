package dev.tomas.tiendafunkos.funko.controllers;

import dev.tomas.tiendafunkos.funko.dto.FunkoDto;
import dev.tomas.tiendafunkos.funko.models.Funko;
import dev.tomas.tiendafunkos.funko.services.FunkoService;
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
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/funkos")
public class FunkoController {

    private final FunkoService funkoService;


    public FunkoController(FunkoService funkoService) {
        this.funkoService = funkoService;
    }


    @PostMapping()
    public ResponseEntity<Funko> save(@Valid @RequestBody FunkoDto funkoDto) {
        log.info("Creando funko: " + funkoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(funkoService.save(funkoDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funko> findById(@PathVariable UUID id) {
        log.info("Buscando producto por id: " + id);
        return ResponseEntity.ok(funkoService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Borrando funko por id: " + id);
        funkoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funko> update(@PathVariable UUID id, @Valid @RequestBody FunkoDto funkoDto) {
        log.info("Actualizando funko por id: " + id + " con producto: " + funkoDto);
        return ResponseEntity.ok(funkoService.update(id, funkoDto));
    }

    @GetMapping()
    public ResponseEntity<PageResponse<Funko>> findAll(
            @RequestParam(defaultValue = "") Optional<String> nombre,
            @RequestParam(defaultValue = "0") Optional<Double> precio,
            @RequestParam(defaultValue = "0") Optional<Integer> stock,
            @RequestParam(defaultValue = "") Optional<String> imagen,
            @RequestParam(defaultValue = "") Optional<String> categoria,
            @RequestParam(defaultValue = "false") Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Buscando todos los productos con las siguientes opciones: " + nombre + " " + precio + " " + stock + " " + imagen + " " + categoria + " " + isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(funkoService.findAll(nombre, precio, stock, imagen,categoria,isDeleted,pageable), sortBy, direction));
    }


}
