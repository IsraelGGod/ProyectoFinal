package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.entidades.Venta;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody SolicitudVentaDTO solicitud) {
        Venta venta = ventaService.registrarVenta(solicitud);
        return ResponseEntity.ok(venta);
    }
}