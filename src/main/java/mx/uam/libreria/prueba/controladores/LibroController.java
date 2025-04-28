package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.entidades.Libro;
import mx.uam.libreria.prueba.servicio.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

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
    public ResponseEntity<Libro> crearLibro(@Valid @RequestBody Libro libro) {
        Libro nuevoLibro = libroService.guardarLibro(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerLibroPorId(@PathVariable Long id) {
        return libroService.obtenerLibroPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizarLibro(
            @PathVariable Long id,
            @Valid @RequestBody Libro libro) {

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
}