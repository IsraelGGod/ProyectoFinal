package mx.uam.libreria.prueba.servicio.Impl;

import mx.uam.libreria.prueba.entidades.Cliente;
import mx.uam.libreria.prueba.repositorio.ClienteRepository;
import mx.uam.libreria.prueba.servicio.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    // Implementación de nuevos métodos
    @Override
    public List<Cliente> buscarPorMatriculacion(boolean matriculado) {
        return clienteRepository.findByMatriculado(matriculado);
    }

    @Override
    public List<Cliente> buscarPorMatricula(String matricula) {
        return clienteRepository.findByMatriculaContainingIgnoreCase(matricula);
    }

    @Override
    public boolean existeCliente(Long id) {
        return clienteRepository.existsById(id);
    }
}