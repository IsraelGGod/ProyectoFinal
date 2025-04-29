package mx.uam.libreria.prueba.controladores;

import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.entidades.Venta;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.entidades.Venta;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//importaci´n para hacer el get
import java.util.List;


@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Permite llamadas desde  frontend
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

    // Método GET: obtener todas las ventas
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        List<Venta> ventas = ventaService.obtenerTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }
}