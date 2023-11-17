package dev.tomas.tiendafunkos.categoria.servicies;

import dev.tomas.tiendafunkos.categoria.dto.CategoriaDto;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoriaService {

    Categoria save(CategoriaDto categoriaDto);

    Categoria findById(Long id);

    void deleteById(Long id);

    Categoria findByTipo(String tipoCategoria);

    Categoria update(Long id, CategoriaDto categoriaDto);


    Page<Categoria> findAll(Optional<Boolean> tipoCategoria, Pageable pageable);

}