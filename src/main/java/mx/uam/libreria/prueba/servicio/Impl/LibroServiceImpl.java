package mx.uam.libreria.prueba.servicio.Impl;

import mx.uam.libreria.prueba.entidades.Libro;
import mx.uam.libreria.prueba.repositorio.LibroRepository;
import mx.uam.libreria.prueba.servicio.LibroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;

    public LibroServiceImpl(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public List<Libro> listarTodos() {
        return libroRepository.findAll();
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    @Override
    public boolean existeLibro(Long id) {
        return libroRepository.existsById(id);
    }

    @Override
    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public Libro actualizarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    @Transactional
    public Optional<Libro> actualizarStock(Long id, int cantidad) {
        return libroRepository.findById(id)
                .map(libro -> {
                    libro.aumentarStock(cantidad);
                    return libroRepository.save(libro);
                });
    }

    @Override
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }

    @Transactional
    public void actualizarStockDirecto(Long id, int cantidad) {
        libroRepository.actualizarStock(id, cantidad);
    }

}