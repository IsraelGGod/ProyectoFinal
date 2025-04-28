package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.entidades.Cliente;
import mx.uam.libreria.prueba.servicio.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(
            @RequestParam(required = false) Boolean matriculado) {

        List<Cliente> clientes = matriculado != null ?
                clienteService.buscarPorMatriculacion(matriculado) :
                clienteService.listarTodos();

        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.guardarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        return clienteService.obtenerClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {

        if (!clienteService.existeCliente(id)) {
            return ResponseEntity.notFound().build();
        }

        cliente.setId(id);
        Cliente clienteActualizado = clienteService.actualizarCliente(cliente);
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        if (!clienteService.existeCliente(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorMatricula(
            @RequestParam String matricula) {
        return ResponseEntity.ok(
                clienteService.buscarPorMatricula(matricula.toUpperCase()));
    }
}