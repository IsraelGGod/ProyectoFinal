package mx.uam.libreria.prueba.servicio;

import mx.uam.libreria.prueba.entidades.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> listarTodos();
    Optional<Cliente> obtenerClientePorId(Long id);
    Cliente guardarCliente(Cliente cliente);
    Cliente actualizarCliente(Cliente cliente);
    void eliminarCliente(Long id);
    
    // Nuevos métodos requeridos
    List<Cliente> buscarPorMatriculacion(boolean matriculado);
    List<Cliente> buscarPorMatricula(String matricula);
    boolean existeCliente(Long id);
}