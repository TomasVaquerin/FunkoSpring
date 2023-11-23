package dev.tomas.tiendafunkos.funko.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import dev.tomas.tiendafunkos.categoria.servicies.CategoriaService;
import dev.tomas.tiendafunkos.funko.dto.FunkoDto;
import dev.tomas.tiendafunkos.funko.exceptions.FunkoConflict;
import dev.tomas.tiendafunkos.funko.exceptions.FunkoNotFound;

import dev.tomas.tiendafunkos.funko.mapper.FunkoMapper;
import dev.tomas.tiendafunkos.funko.models.Funko;
import dev.tomas.tiendafunkos.funko.repositories.FunkoRepository;
import dev.tomas.tiendafunkos.notifications.config.WebSocketConfig;
import dev.tomas.tiendafunkos.notifications.config.WebSocketHandler;
import dev.tomas.tiendafunkos.notifications.mapper.FunkoNotificationMapper;
import dev.tomas.tiendafunkos.storage.StorageService;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {"funkos"})
@Slf4j
public class FunkoServiceImpl implements FunkoService{

    private final FunkoRepository funkoRepository;

    private final CategoriaService categoriaService;

    private final FunkoMapper funkosMaper;

    private final StorageService storageService;

    private final WebSocketConfig webSocketConfig;

    private final ObjectMapper mapper;

    private final FunkoNotificationMapper funkoNotificationMapper;

    private WebSocketHandler webSocketService;


    public FunkoServiceImpl(FunkoRepository funkoRepository,
                            CategoriaService categoriaService,
                            FunkoMapper funkosMapper, // Corrected parameter name
                            StorageService storageService,
                            WebSocketConfig webSocketConfig,
                            FunkoNotificationMapper funkoNotificationMapper) {
        this.funkoRepository = funkoRepository;
        this.categoriaService = categoriaService;
        this.funkosMaper = funkosMapper; // Corrected assignment
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketRaquetasHandler();
        mapper = new ObjectMapper();
        this.funkoNotificationMapper = funkoNotificationMapper;
    }



    @Override
    public Funko save(FunkoDto funkoDto) {
        log.info("Creando un nuevo funko: " + funkoDto);

        // Comprobamos que la categoría exista
        categoriaService.findByTipoCategoria(funkoDto.getCategoria().getTipoCategoria());

        // Comprobamos que no exista un funko con el mismo nombre
        funkoRepository.findByNombreEqualsIgnoreCase(funkoDto.getNombre()).ifPresent(f -> {
            throw new FunkoConflict(funkoDto.getNombre());
        });

        return funkoRepository.save(funkosMaper.toFunko(funkoDto));
    }

    @Override
    public Funko findById(UUID uuid) {
        log.info("Buscando funko por id: " + uuid);
        return funkoRepository.findById(uuid).orElseThrow(() -> new FunkoNotFound(uuid));
    }

    @Override
    public void deleteById(UUID uuid) {
        log.info("Borrando funko por id: " + uuid);
        Funko funkoActual = findById(uuid);
        funkoActual.setIsDeleted(true);
        funkoRepository.save(funkoActual);
    }

    @Override
    public Funko update(UUID uuid, FunkoDto funkoDto) {
        log.info("Actualizando funko: " + funkoDto);
        Funko funkoActual = findById(uuid);

        // Comprobamos que la categoría exista
        categoriaService.findByTipoCategoria(funkoDto.getCategoria().getTipoCategoria());

        // Comprobamos que no exista un funko con el mismo nombre, y si existe soy yo mismo
        funkoRepository.findByNombreEqualsIgnoreCase(funkoDto.getNombre()).ifPresent(f -> {
            throw new FunkoConflict(funkoDto.getNombre());
        });

        return funkoRepository.save(funkosMaper.toFunko(funkoDto, funkoActual));
    }

    @Override
    public Funko updateImage(UUID id, MultipartFile image) {
        log.info("Actualizando imagen de funko por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        Funko funkoActual = this.findById(id);
        // Borramos la imagen anterior si existe y no es la de por defecto
        if (funkoActual.getImagen() != null && !funkoActual.getImagen().equals(Funko.IMAGEN_PREDETERMINADA)) {
            storageService.delete(funkoActual.getImagen());
        }
        String imageStored = storageService.store(image);
        String imageUrl = imageStored; //storageService.getUrl(imageStored); // Si quiero la url completa
        // Clonamos el producto con la nueva imagen, porque inmutabilidad de los objetos
        var funkoActualizado = Funko.builder()
                .id(funkoActual.getId())
                .nombre(funkoActual.getNombre())
                .precio(funkoActual.getPrecio())
                .stock(funkoActual.getStock())
                .imagen(imageUrl)
                .categoria(funkoActual.getCategoria())
                .isDeleted(funkoActual.getIsDeleted())
                .fechaCreated(funkoActual.getFechaCreated())
                .fechaUpdated(LocalDateTime.now())
                .build();

        // Lo guardamos en el repositorio
        var funkoUpdated = funkoRepository.save(funkoActualizado);
        // Enviamos la notificación a los clientes ws
        //onChange(Notificacion.Tipo.UPDATE, funkoUpdated);
        // Devolvemos el producto actualizado
        return funkoUpdated;
    }

    @Override
    public Page<Funko> findAll(Optional<String> nombre,
                               Optional<Double> precio,
                               Optional<Integer> stock,
                               Optional<String> imagen,
                               Optional<String> categoria,
                               Optional<Boolean> isDeleted,
                               Pageable pageable
    ) {
        Specification<Funko> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + m + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specPrecio = (root, query, criteriaBuilder) ->
                precio.map(m -> criteriaBuilder.equal(root.get("precio"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specStock = (root, query, criteriaBuilder) ->
                stock.map(m -> criteriaBuilder.equal(root.get("stock"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specImagen = (root, query, criteriaBuilder) ->
                imagen.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("imagen")), "%" + m + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specCategoriaProducto = (root, query, criteriaBuilder) ->
                categoria.map(c -> {
                    Join<Funko, Categoria> categoriaJoin = root.join("categoria"); // Join con categoría
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("tipoCategoria")),"%" + c + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));


        Specification<Funko> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));


        Specification<Funko> criterio = Specification.where(specNombre)
                .and(specPrecio)
                .and(specStock)
                .and(specImagen)
                .and(specCategoriaProducto)
                .and(specIsDeleted);


        return funkoRepository.findAll(criterio, pageable);
    }
}
