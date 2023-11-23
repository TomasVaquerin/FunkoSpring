package dev.tomas.tiendafunkos.funko.repositories;

import dev.tomas.tiendafunkos.funko.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface FunkoRepository extends JpaRepository<Funko, UUID>, JpaSpecificationExecutor<Funko> {

    Optional<Object> findByNombreEqualsIgnoreCase(String nombre);
}
