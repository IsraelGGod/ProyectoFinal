package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.entidades.Venta;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody SolicitudVentaDTO solicitud) {
        Venta venta = ventaService.registrarVenta(solicitud);
        return ResponseEntity.ok(venta);
    }
}