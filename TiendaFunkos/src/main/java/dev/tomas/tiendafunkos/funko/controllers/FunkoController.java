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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    public ResponseEntity<Funko> findById(@PathVariable String id) {
        log.info("Buscando producto por id: " + id);
        return ResponseEntity.ok(funkoService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Borrando funko por id: " + id);
        funkoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funko> update(@PathVariable String id, @Valid @RequestBody FunkoDto funkoDto) {
        log.info("Actualizando funko por id: " + id + " con producto: " + funkoDto);
        return ResponseEntity.ok(funkoService.update(id, funkoDto));
    }


    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Funko> nuevoFunko(
            @PathVariable String id,
            @RequestPart("file") MultipartFile file) {

        // Lista de extensiones de archivos permitidos.
        List<String> permittedContentTypes = List.of("image/png", "image/jpg", "image/jpeg", "image/gif");

        log.info("Actualizando imagen de producto por id: " + id);

        try {
            String contentType = file.getContentType();
            log.info("Tipo de contenido: " + contentType);

            if (!file.isEmpty() && contentType != null && !contentType.isEmpty() && permittedContentTypes.contains(contentType.toLowerCase())) {
                // Actualizamos el producto
                return ResponseEntity.ok(funkoService.updateImage(id,file));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el producto válida o esta está vacía");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede saber el tipo de la imagen");
        }
    }

    @GetMapping()
    public ResponseEntity<PageResponse<Funko>> findAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<Double> precio,
            @RequestParam(required = false) Optional<Integer> stock,
            @RequestParam(required = false) Optional<String> imagen,
            @RequestParam(required = false) Optional<String> categoria,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
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
