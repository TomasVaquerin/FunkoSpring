-- Inserts Categorias
INSERT INTO CATEGORIAS (tipoCategoria, isDeleted) VALUES ('SERIE', FALSE); -- 1
INSERT INTO CATEGORIAS (tipoCategoria, isDeleted) VALUES ('DISNEY', FALSE); -- 2
INSERT INTO CATEGORIAS (tipoCategoria, isDeleted) VALUES ('SUPERHEROES', FALSE); -- 3
INSERT INTO CATEGORIAS (tipoCategoria, isDeleted) VALUES ('PELICULA', FALSE); -- 4
INSERT INTO CATEGORIAS (tipoCategoria, isDeleted) VALUES ('OTROS', FALSE); -- 5

-- Inserts FUNKOS
INSERT INTO FUNKOS (id, nombre, precio, cantidad, Categoria_id, isDeleted)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'FUNKO 1', 15.99, 10, 1, true);

INSERT INTO FUNKOS (id, nombre, precio, cantidad, Categoria_id, isDeleted)
VALUES ('987e6543-e21b-12d3-a456-426614174000', 'FUNKO 2', 100.99, 100, 2, true);

INSERT INTO FUNKOS (id, nombre, precio, cantidad, Categoria_id, isDeleted)
VALUES ('d94a4c08-b1bd-4b9b-8aeb-1d6f7aa0b3f3', 'FUNKO 3', 70.99, 60, 3, true);

INSERT INTO FUNKOS (id, nombre, precio, cantidad, Categoria_id, isDeleted)
VALUES ('0c8a5a5e-3f09-4b3f-a8f7-5b3e3a9e9856', 'FUNKO 4', 125.99, 1770, 4, true);

INSERT INTO FUNKOS (id, nombre, precio, cantidad, Categoria_id, isDeleted)
VALUES ('5fd156e4-7d3e-4b1e-ba21-3d5f4410a1d3', 'FUNKO 5', 200.99, 123, 5, true);
