package dev.tomas.tiendafunkos.funko.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import dev.tomas.tiendafunkos.categoria.servicies.CategoriaService;
import dev.tomas.tiendafunkos.funko.dto.FunkoDto;
import dev.tomas.tiendafunkos.funko.exceptions.FunkoNotFound;

import dev.tomas.tiendafunkos.funko.mapper.FunkoMapper;
import dev.tomas.tiendafunkos.funko.models.Funko;
import dev.tomas.tiendafunkos.funko.repositories.FunkoRepository;
import dev.tomas.tiendafunkos.notifications.config.WebSocketConfig;
import dev.tomas.tiendafunkos.notifications.config.WebSocketHandler;
import dev.tomas.tiendafunkos.notifications.dto.FunkoNotificationDto;
import dev.tomas.tiendafunkos.notifications.mapper.FunkoNotificationMapper;
import dev.tomas.tiendafunkos.notifications.models.Notificacion;
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
                            FunkoMapper funkosMapper,
                            StorageService storageService,
                            WebSocketConfig webSocketConfig,
                            FunkoNotificationMapper funkoNotificationMapper) {
        this.funkoRepository = funkoRepository;
        this.categoriaService = categoriaService;
        this.funkosMaper = funkosMapper;
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketFunkosHandler();
        mapper = new ObjectMapper();
        this.funkoNotificationMapper = funkoNotificationMapper;
    }


    @Override
    public Funko save(FunkoDto funkoDto) {
        log.info("Guardando funko: " + funkoDto.getNombre());
        Categoria c = categoriaService.findByTipoCategoria(funkoDto.getCategoria());
        Funko funko =  funkoRepository.save(funkosMaper.toFunko(funkoDto, c));
        onChange(Notificacion.Tipo.CREATE, funko);
        return funko;
    }

    @Override
    public Funko findById(String uuid) {
        UUID myUuid = UUID.fromString(uuid);
        log.info("Buscando funko por id: " + uuid);
        return funkoRepository.findById(myUuid).orElseThrow(() -> new FunkoNotFound(uuid));
    }

    @Override
    public void deleteById(String uuid) {
        log.info("Borrando funko por id: " + uuid);
        Funko funkoActual = findById(uuid);
        funkoActual.setIsDeleted(true);
        funkoRepository.save(funkoActual);
        onChange(Notificacion.Tipo.DELETE, funkoActual);
    }

    @Override
    public Funko update(String id, FunkoDto funkoDto) {
        log.info("Actualizando funko: " + funkoDto);
        Funko funkoActual = findById(id);

        Categoria categoria = null;

        if (funkoDto.getCategoria() != null && !funkoDto.getCategoria().isEmpty()) {
            categoria = categoriaService.findByTipoCategoria(funkoDto.getCategoria());
        } else {
            categoria = funkoActual.getCategoria();
        }

        Funko funko = funkoRepository.save(funkosMaper.toFunko(funkoDto, funkoActual, categoria));

        onChange(Notificacion.Tipo.UPDATE, funko);

        return funko;
    }

    @Override
    public Funko updateImage(String id, MultipartFile image) {
        log.info("Actualizando imagen de funko por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        Funko funkoActual = this.findById(id);
        // Borramos la imagen anterior si existe y no es la de por defecto
        if (funkoActual.getImagen() != null && !funkoActual.getImagen().equals(Funko.IMAGEN_PREDETERMINADA)) {
            storageService.delete(funkoActual.getImagen());
        }
        String imageUrl = storageService.store(image, id); //storageService.getUrl(imageStored); // Si quiero la url completa
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

        onChange(Notificacion.Tipo.UPDATE, funkoActualizado);

        // Lo guardamos en el repositorio
        return funkoRepository.save(funkoActualizado);
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


        Specification<Funko> specCategoriaFunko = (root, query, criteriaBuilder) ->
                categoria.map(c -> { Join<Funko, Categoria> categoriaJoin = root.join("categoria");
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("tipoCategoria")), "%" + c.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));


        Specification<Funko> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));


        Specification<Funko> criterio = Specification.where(specNombre)
                .and(specPrecio)
                .and(specStock)
                .and(specImagen)
                .and(specCategoriaFunko)
                .and(specIsDeleted);

        return funkoRepository.findAll(criterio, pageable);
    }

    void onChange(Notificacion.Tipo tipo, Funko data) {
        log.debug("Servicio de productos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notificacion<FunkoNotificationDto> notificacion = new Notificacion<>(
                    "FUNKOS",
                    tipo,
                    funkoNotificationMapper.toProductNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar
            // no bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}
