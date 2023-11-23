package dev.tomas.tiendafunkos.categoria.servicies;

import dev.tomas.tiendafunkos.categoria.dto.CategoriaDto;
import dev.tomas.tiendafunkos.categoria.exceptions.CategoriaConflict;
import dev.tomas.tiendafunkos.categoria.exceptions.CategoriaNotFound;
import dev.tomas.tiendafunkos.categoria.mappers.CategoriaMapper;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import dev.tomas.tiendafunkos.categoria.repositories.CategoriaRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoriaServiceImpl implements CategoriaService{

    private final CategoriaRepository categoriaRepository;

    private final CategoriaMapper categoriaMapper;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    public Categoria save(CategoriaDto categoriaDto) {
        log.info("Guardando categoria: ", categoriaDto);
        categoriaRepository.findByTipoCategoria(categoriaDto.getTipoCategoria()).ifPresent(c -> {
            throw new CategoriaConflict(String.valueOf(categoriaDto.getTipoCategoria()));
        });
        return categoriaRepository.save(categoriaMapper.toCategoria(categoriaDto));
    }

    @Override
    public Categoria findById(Long id) {
        log.info("Buscando categoría por id: " + id);
        return categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
    }

    @Override
    public void deleteById(Long id) {
        if (categoriaRepository.existsProductoById(id)) {
            log.warn("No se puede borrar la categoría con id: " + id + " porque tiene Funkos asociados");
            throw new CategoriaConflict("No se puede borrar la categoría con id " + id + " porque tiene Funkos asociados");
        } else {
            log.info("Borrando categoría por id: " + id);
            Categoria categoriaActual = findById(id);
            categoriaActual.setIsDeleted(true);
            categoriaRepository.save(categoriaActual);
        }
    }

    @Override
    public Categoria findByTipo(String tipoCategoria) {
        log.info("Buscando categoría por tipo: " + tipoCategoria);
        return categoriaRepository.findByTipoCategoria(Categoria.tipoCategoria.valueOf(tipoCategoria)).orElseThrow(() -> new CategoriaNotFound(tipoCategoria));
    }

    @Override
    public Categoria update(Long id, CategoriaDto categoriaDto) {
        log.info("Actualizando categoría: " + categoriaDto);
        Categoria categoriaActual = findById(id);

        categoriaRepository.findByTipoCategoria(categoriaDto.getTipoCategoria()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new CategoriaConflict("Ya existe una categoría con el tipo " + categoriaDto.getTipoCategoria());
            }
        });

        return categoriaRepository.save(categoriaMapper.toCategoria(categoriaDto, categoriaActual));
    }

    @Override
    public Page<Categoria> findAll(Optional<String> tipoCategoria,Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Obteniendo todas las categorías paginadas y ordenadas con {}", pageable);

        Specification<Categoria> specNombreCategoria = (root, query, criteriaBuilder) ->
                tipoCategoria.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("tipoCategoria")), "%" + m + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Categoria> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Categoria> criterio = Specification.where(specNombreCategoria).and(specIsDeleted);

        return categoriaRepository.findAll(criterio, pageable);
    }

    @Override
    public void findByTipoCategoria(Categoria.tipoCategoria tipoCategoria) {
        log.info("Buscando categoría por tipo: " + tipoCategoria);
        categoriaRepository.findByTipoCategoria(tipoCategoria).orElseThrow(() -> new CategoriaNotFound(String.valueOf(tipoCategoria)));
    }


}