package dev.tomas.tiendafunkos.categoria.servicies;

import dev.tomas.tiendafunkos.categoria.dto.CategoriaDto;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import dev.tomas.tiendafunkos.categoria.repositories.CategoriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoriaServiceImpl implements CategoriaService{

    private final CategoriaRepository categoriaRepository;

    private final CategoriaMapper categoriaMapper;

    @Override
    public Categoria save(CategoriaDto categoriaDto) {
        return null;
    }

    @Override
    public Categoria findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Categoria findByTipo(CategoriaDto.tipoCategoria tipoCategoriaDto) {
        return null;
    }

    @Override
    public Categoria update(Long id, CategoriaDto categoriaDto) {
        return null;
    }

    @Override
    public Page<Categoria> findAll(Optional<Boolean> tipoCategoria, Pageable pageable) {
        return null;
    }
}
