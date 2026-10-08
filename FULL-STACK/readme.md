---

# Documentación del Proyecto: API RESTful con Spring Boot 3 y Spring Data JPA

Este proyecto es una API RESTful desarrollada con Java 21, Spring Boot 3, Spring Data JPA y MySQL/MariaDB, aplicando una Arquitectura por Capas con DTOs (Records), Mappers y manejo centralizado de excepciones.

---

## 🛠️ Arquitectura y Flujo de Datos

El flujo de información sigue la separación de responsabilidades:

$$\text{Cliente (Postman / Web)} \rightleftharpoons \text{Controller} \rightleftharpoons \text{Service} \rightleftharpoons \text{Repository} \rightleftharpoons \text{Database (MySQL)}$$

1. **Presentation Layer (Controller)**: Expone las rutas HTTP y valida los datos de entrada (`@Valid`).


2. **Transfer Layer (DTOs & Mapper)**: Filtra y transporta únicamente los datos necesarios mediante **Java Records** (`ProductResponseDTO`, `CategoryDTO`) para desacoplar las entidades internas del cliente final.


3. **Business Layer (Service)**: Aplica reglas de negocio, coordina las consultas entre productos y categorías, y gestiona las excepciones de entidad no encontrada.


4. **Persistence Layer (Repository & Entity)**: Mapea las tablas de MySQL mediante anotaciones JPA y ejecuta las operaciones mediante `JpaRepository`.



---

## 📌 Guía Completa de Anotaciones (`@`) y Métodos JPA

### 1. Capa de Dominio y Persistencia (JPA & Lombok)

* **`@Entity`**: Indica que la clase Java es una entidad persistente y debe mapearse a una tabla de la base de datos.


* **`@Table(name = "nombre_tabla")`**: Especifica el nombre exacto de la tabla en MySQL (ej. `products`, `categories`).


* **`@Id`**: Define el atributo como la Clave Primaria (*Primary Key*) de la entidad.


* **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Delega a MySQL la generación autoincremental de la clave primaria (`AUTO_INCREMENT`).


* **`@ManyToOne`**: Mapea la relación **Muchos a Uno** (Muchos productos pertenecen a Una categoría).


* **`@JoinColumn(name = "category_id")`**: Define el nombre de la columna en la tabla `products` que almacena la Clave Foránea (*Foreign Key*).


* **`@Data` / `@NoArgsConstructor` / `@AllArgsConstructor**` (Lombok): Generan automáticamente los getters, setters, constructores y métodos `toString()` / `equals()`.

---

### 2. Capa Controller y REST API (Spring Web)

* **`@RestController`**: Combina `@Controller` y `@ResponseBody`. Le indica a Spring que los valores devueltos por los métodos deben serializarse automáticamente a JSON.
* **`@RequestMapping("/api/v1/products")`**: Define la ruta base para todos los endpoints contenidos en esa clase.
* **`@RequestBody`**: Convierte el cuerpo JSON recibido en la petición HTTP en un objeto Java DTO.
* **`@PathVariable`**: Extrae valores directamente de la URL (ej. `/api/v1/products/{id}` -> `Long id`).
* **`@Valid`**: Activa la evaluación automática de las reglas de validación definidas dentro del DTO.



---

### 3. Capa de Validaciones y Excepciones Centralizadas

* **`@NotNull`**: Exige que el campo no sea nulo. Usado en tipos primitivos/envolventes como `Long` (`categoryId`), `Double` (`price`) o `Integer` (`quantity`). (Evita el error `UnexpectedTypeException` que ocurre si se usa `@NotBlank` en números).


* **`@NotBlank`**: Exige que una cadena de texto (`String`) no sea nula ni esté vacía o compuesta solo de espacios.


* **`@Min(0)` / `@Positive**`: Asegura que un valor numérico sea igual o mayor que cero.


* **`@RestControllerAdvice`**: Define una clase para interceptar excepciones globales de toda la aplicación.


* **`@ExceptionHandler(NombreExcepcion.class)`**: Captura una excepción específica (ej. `EntityNotFoundException` o `MethodArgumentNotValidException`) y retorna una respuesta JSON estructurada con un código de estado HTTP adecuado (`404 NOT FOUND` o `400 BAD REQUEST`).



---

## 🚀 Implementación de los Métodos REST y el CRUD

### 1. `POST`: Crear un Producto (`CREATE`)

* **Endpoint:** `POST /api/v1/products`
* **Código HTTP de Éxito:** `201 CREATED`

* **Recorrido:**
1. El cliente envía un JSON con los 5 campos requeridos (`name`, `price`, `quantity`, `description` y `categoryId`).
2. El Controller intercepta la petición con `@Valid @RequestBody`.


3. El Servicio busca la `Category` por su `categoryId` en MySQL mediante `categoryRepository.findById()`. Si no existe, lanza `EntityNotFoundException` (capturado como `404`).


4. Se crea la instancia de `Product`, se asigna la categoría y se guarda en MySQL con `productRepository.save(product)`.


5. `ProductMapper` transforma la entidad guardada a un `ProductResponseDTO` con la categoría anidada (`category: { id, name }`).



### 2. `GET`: Listar Todos los Productos (`READ All`)

* **Endpoint:** `GET /api/v1/products`
* **Código HTTP de Éxito:** `200 OK`

* **Recorrido:**
1. El Controller solicita la lista completa de productos al Servicio.


2. El Servicio ejecuta `productRepository.findAll()` trayendo los registros desde MySQL.


3. Mediante *Streams* de Java (`.stream().map(ProductMapper::toDTO).toList()`), convierte la lista de entidades a una lista de DTOs Record.



### 3. `GET`: Obtener Producto por ID (`READ One`)

* **Endpoint:** `GET /api/v1/products/{id}`
* **Código HTTP de Éxito:** `200 OK` / `404 NOT FOUND`

* **Recorrido:**
1. El Controller captura el `{id}` mediante `@PathVariable`.
2. El Servicio ejecuta `productRepository.findById(id)`.


3. Si el registro se encuentra, se mapea a DTO y se retorna; si no, el `@RestControllerAdvice` captura la excepción devolviendo el JSON de error con `404 Not Found`.





### 4. `PUT`: Actualizar Producto (`UPDATE`)

* **Endpoint:** `PUT /api/v1/products/{id}`
* **Código HTTP de Éxito:** `200 OK` / `400` / `404`

* **Recorrido:**
1. Recibe el `{id}` del producto por la URL y el JSON con los datos nuevos en el `@RequestBody`.


2. El Servicio valida la existencia tanto del `Product` como de la nueva `Category` recibida.


3. Se sobrescriben los valores del objeto existente y se persiste de nuevo mediante `productRepository.save(existingProduct)` (Spring Data JPA detecta que el ID ya existe y realiza una actualización `UPDATE` en SQL en lugar de un `INSERT`).





### 5. `DELETE`: Eliminar Producto (`DELETE`)

* **Endpoint:** `DELETE /api/v1/products/{id}`
* **Código HTTP de Éxito:** `204 NO CONTENT`

* **Recorrido:**
1. Recibe el `{id}` a eliminar por `@PathVariable`.
2. El Servicio verifica que el registro exista con `findById()`.


3. Llama a `productRepository.delete(existingProduct)` eliminando la fila de la tabla `products` en MySQL.


4. El Controller responde con `ResponseEntity.noContent().build()`, retornando la respuesta vacía con código `204` exigido por el estándar REST.





---

## 📋 Métodos Autogenerados por `JpaRepository` Utilizados

Gracias a que nuestras interfaces heredan de `JpaRepository<Product, Long>`, disponemos de estos métodos nativos sin necesidad de escribir SQL manual:

* **`save(S entity)`**: Inserta o actualiza un registro en la base de datos.


* **`findAll()`**: Ejecuta un `SELECT * FROM products` y retorna una lista de entidades.


* **`findById(ID id)`**: Ejecuta un `SELECT * FROM products WHERE id = ?` devolviendo un `Optional<Product>`.


* **`delete(T entity)`**: Ejecuta un `DELETE FROM products WHERE id = ?`.