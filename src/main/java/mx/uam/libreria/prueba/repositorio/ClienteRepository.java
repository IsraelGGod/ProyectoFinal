package mx.uam.libreria.prueba.repositorio;

import mx.uam.libreria.prueba.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Método existente
    Optional<Cliente> findByEmail(String email);
    
    // Nuevos métodos requeridos
    List<Cliente> findByMatriculado(boolean matriculado);
    
    List<Cliente> findByMatriculaContainingIgnoreCase(String matricula);
    
    boolean existsByMatricula(String matricula);
    
    boolean existsByEmail(String email);
    
    // Método alternativo para búsqueda flexible de matrícula
    List<Cliente> findByMatriculaStartingWithIgnoreCase(String prefijoMatricula);
}