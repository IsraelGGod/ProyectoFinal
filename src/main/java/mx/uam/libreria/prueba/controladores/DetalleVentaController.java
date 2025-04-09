package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.entidades.DetalleVenta;
import mx.uam.libreria.prueba.servicio.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-venta")
public class DetalleVentaController {
    @Autowired
    private DetalleVentaService detalleVentaService;

    @GetMapping
    public List<DetalleVenta> listarDetalles() {
        return detalleVentaService.listarTodos();
    }

    @PostMapping
    public DetalleVenta guardarDetalle(@RequestBody DetalleVenta detalle) {
        return detalleVentaService.guardarDetalle(detalle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> obtenerDetallePorId(@PathVariable Long id) {
        DetalleVenta detalle = detalleVentaService.obtenerDetallePorId(id);
        return ResponseEntity.ok(detalle);
    }
    
    @DeleteMapping("/{id}")
    public void eliminarDetalle(@PathVariable Long id) {
        detalleVentaService.eliminarDetalle(id);
    }

    // Endpoint opcional: Buscar detalles por venta
    @GetMapping("/venta/{ventaId}")
    public List<DetalleVenta> obtenerDetallesPorVenta(@PathVariable Long ventaId) {
        return detalleVentaService.obtenerDetallesPorVenta(ventaId);
    }
}
