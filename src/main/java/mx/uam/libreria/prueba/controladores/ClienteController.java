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

<<<<<<< HEAD
=======
    /**
     * Lista todos los clientes o filtra por estado de matrícula.
     */
>>>>>>> origin/charly-rama
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(
            @RequestParam(required = false) Boolean matriculado) {

<<<<<<< HEAD
        List<Cliente> clientes = matriculado != null ?
                clienteService.buscarPorMatriculacion(matriculado) :
                clienteService.listarTodos();
=======
        List<Cliente> clientes = matriculado != null
                ? clienteService.buscarPorMatriculacion(matriculado)
                : clienteService.listarTodos();
>>>>>>> origin/charly-rama

        return ResponseEntity.ok(clientes);
    }

<<<<<<< HEAD
=======
    /**
     * Crea un nuevo cliente con validación de datos.
     */
>>>>>>> origin/charly-rama
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.guardarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

<<<<<<< HEAD
=======
    /**
     * Obtiene un cliente por su ID.
     */
>>>>>>> origin/charly-rama
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        return clienteService.obtenerClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

<<<<<<< HEAD
=======
    /**
     * Actualiza un cliente existente.
     */
>>>>>>> origin/charly-rama
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {

        if (!clienteService.existeCliente(id)) {
            return ResponseEntity.notFound().build();
        }

<<<<<<< HEAD
        cliente.setId(id);
        Cliente clienteActualizado = clienteService.actualizarCliente(cliente);
        return ResponseEntity.ok(clienteActualizado);
    }

=======
        try {
            cliente.setId(id);
            Cliente clienteActualizado = clienteService.actualizarCliente(cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Error: El email o matrícula ya existen");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Elimina un cliente por su ID.
     */
>>>>>>> origin/charly-rama
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        if (!clienteService.existeCliente(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

<<<<<<< HEAD
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorMatricula(
            @RequestParam String matricula) {
        return ResponseEntity.ok(
                clienteService.buscarPorMatricula(matricula.toUpperCase()));
=======
    /**
     * Busca clientes por matrícula.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorMatricula(@RequestParam String matricula) {
        List<Cliente> clientes = clienteService.buscarPorMatricula(matricula.toUpperCase());
        return ResponseEntity.ok(clientes);
    }

    /**
     * Procesa errores de validación y devuelve un mapa de campos con sus respectivos mensajes.
     */
    private Map<String, String> obtenerErroresValidacion(BindingResult result) {
        return result.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (mensajeExistente, nuevoMensaje) -> mensajeExistente
                ));
>>>>>>> origin/charly-rama
    }
}
