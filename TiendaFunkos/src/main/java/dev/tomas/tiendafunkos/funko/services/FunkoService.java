package dev.tomas.tiendafunkos.funko.services;

import dev.tomas.tiendafunkos.funko.dto.FunkoDto;
import dev.tomas.tiendafunkos.funko.models.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface FunkoService {

    Funko save (FunkoDto funkoDto);
    Funko findById (String uuid);

    void deleteById (String uuid);

    Funko update (String uuid, FunkoDto funkoDto);

    Funko updateImage (String uuid, MultipartFile image);

    Page<Funko> findAll(Optional<String> nombre,
                        Optional<Double> precio,
                        Optional<Integer> stock,
                        Optional<String> imagen,
                        Optional<String> categoria,
                        Optional<Boolean> isDeleted,
                        Pageable pageable);


}
