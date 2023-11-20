package dev.tomas.tiendafunkos.categoria.repositories;

import dev.tomas.tiendafunkos.categoria.dto.CategoriaDto;
import dev.tomas.tiendafunkos.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {

    Optional<Categoria> findByTipoCategoria(Categoria.tipoCategoria tipoCategoria);


    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Funko p WHERE p.categoria.id = :id")
    Boolean existsProductoById(Long id);
}
