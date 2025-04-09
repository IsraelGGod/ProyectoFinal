package mx.uam.libreria.prueba.servicio;

import mx.uam.libreria.prueba.entidades.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {
    List<Libro> listarTodos();
    List<Libro> buscarPorTitulo(String titulo);
    List<Libro> buscarPorAutor(String autor);
    Optional<Libro> obtenerLibroPorId(Long id);
    boolean existeLibro(Long id);
    Libro guardarLibro(Libro libro);
    Libro actualizarLibro(Libro libro);
    Optional<Libro> actualizarStock(Long id, int cantidad);
    void eliminarLibro(Long id);
}