package mx.uam.libreria.prueba.repositorio;

import mx.uam.libreria.prueba.entidades.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    // Búsqueda por título exacto (case insensitive)
    Optional<Libro> findByTituloIgnoreCase(String titulo);
    
    // Búsqueda por fragmento de título
    List<Libro> findByTituloContainingIgnoreCase(String fragmentoTitulo);
    
    // Búsqueda por autor
    List<Libro> findByAutorContainingIgnoreCase(String autor);
    
    // Búsqueda por rango de precios
    List<Libro> findByPrecioBetween(double precioMin, double precioMax);
    
    // Búsqueda por stock disponible
    List<Libro> findByStockGreaterThan(int cantidad);
    
    // Actualización directa de stock (evita SELECT + UPDATE)
    @Modifying
    @Query("UPDATE Libro l SET l.stock = l.stock + :cantidad WHERE l.id = :id")
    int actualizarStock(@Param("id") Long id, @Param("cantidad") int cantidad);
    
    // Búsqueda combinada título + autor
    List<Libro> findByTituloContainingAndAutorContainingAllIgnoreCase(
        String titulo, String autor);
    
    // Verifica existencia por título y autor (para evitar duplicados)
    boolean existsByTituloIgnoreCaseAndAutorIgnoreCase(String titulo, String autor);
}