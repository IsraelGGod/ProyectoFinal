package mx.uam.libreria.prueba.servicio;

import mx.uam.libreria.prueba.entidades.Venta;
import java.util.List;
import java.util.Optional;
import mx.uam.libreria.prueba.dto.TicketDTO;
import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.entidades.DetalleVenta;
import org.springframework.transaction.annotation.Transactional;

public interface VentaService {
    List<Venta> listarTodas();
    Optional<Venta> obtenerVentaPorId(Long id);
    Venta guardarVenta(Venta venta);
    void eliminarVenta(Long id);
    Venta procesarVenta(Long clienteId, List<DetalleVenta> detallesRequest);

    // Cambiado de ItemVentaDTO a SolicitudVentaDTO.DetalleVentaDTO
    TicketDTO generarTicketVenta(Long clienteId, List<SolicitudVentaDTO.DetalleVentaDTO> items);

    Optional<TicketDTO> obtenerTicketPorVentaId(Long id);
    boolean existeVenta(Long id);

    @Transactional
    Venta registrarVenta(SolicitudVentaDTO solicitud);
}