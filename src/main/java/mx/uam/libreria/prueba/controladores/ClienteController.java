package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.entidades.Cliente;
import mx.uam.libreria.prueba.servicio.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Obtiene todos los clientes
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(
        @RequestParam(required = false) Boolean matriculado) {
        
        List<Cliente> clientes = matriculado != null ? 
            clienteService.buscarPorMatriculacion(matriculado) : 
            clienteService.listarTodos();
            
        return ResponseEntity.ok(clientes);
    }

    /**
     * Crea un nuevo cliente con validación
     */
    @PostMapping
    public ResponseEntity<?> crearCliente(
            @Valid @RequestBody Cliente cliente,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(obtenerErroresValidacion(result));
        }

        try {
            Cliente nuevoCliente = clienteService.guardarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Error: El email o matrícula ya existen");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtiene un cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        return clienteService.obtenerClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza un cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(obtenerErroresValidacion(result));
        }

        if (!clienteService.existeCliente(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            cliente.setId(id);
            Cliente clienteActualizado = clienteService.actualizarCliente(cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Error: El email o matrícula ya existen");
        }
    }

    /**
     * Elimina un cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        if (!clienteService.existeCliente(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca clientes por matrícula
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorMatricula(
            @RequestParam String matricula) {
        return ResponseEntity.ok(
            clienteService.buscarPorMatricula(matricula.toUpperCase()));
    }

    // Método auxiliar para procesar errores de validación
    private Map<String, String> obtenerErroresValidacion(BindingResult result) {
        return result.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existente, nuevo) -> existente));
    }
}