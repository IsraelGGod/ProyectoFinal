package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.entidades.Libro;
import mx.uam.libreria.prueba.servicio.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public ResponseEntity<List<Libro>> listarLibros(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor) {
        
        List<Libro> libros;
        if (titulo != null) {
            libros = libroService.buscarPorTitulo(titulo);
        } else if (autor != null) {
            libros = libroService.buscarPorAutor(autor);
        } else {
            libros = libroService.listarTodos();
        }
        return ResponseEntity.ok(libros);
    }

    @PostMapping
    public ResponseEntity<?> crearLibro(
            @Valid @RequestBody Libro libro,
            BindingResult result) {
        
        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(obtenerErroresValidacion(result));
        }
        
        try {
            Libro nuevoLibro = libroService.guardarLibro(libro);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body("Error: Ya existe un libro con ese título");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerLibroPorId(@PathVariable Long id) {
        return libroService.obtenerLibroPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLibro(
            @PathVariable Long id,
            @Valid @RequestBody Libro libro,
            BindingResult result) {
        
        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(obtenerErroresValidacion(result));
        }
        
        if (!libroService.existeLibro(id)) {
            return ResponseEntity.notFound().build();
        }
        
        libro.setId(id);
        Libro libroActualizado = libroService.actualizarLibro(libro);
        return ResponseEntity.ok(libroActualizado);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Libro> actualizarStock(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        
        return libroService.actualizarStock(id, cantidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        if (!libroService.existeLibro(id)) {
            return ResponseEntity.notFound().build();
        }
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para procesar errores de validación
    private Map<String, String> obtenerErroresValidacion(BindingResult result) {
        return result.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existente, nuevo) -> existente));
    }
}