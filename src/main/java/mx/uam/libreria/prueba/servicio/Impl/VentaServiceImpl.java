package mx.uam.libreria.prueba.servicio.Impl;

import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.dto.TicketDTO;
import mx.uam.libreria.prueba.entidades.*;
import mx.uam.libreria.prueba.repositorio.*;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final LibroRepository libroRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            ClienteRepository clienteRepository,
                            LibroRepository libroRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional
    public Venta registrarVenta(SolicitudVentaDTO solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }

        Cliente cliente = clienteRepository.findById(solicitud.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + solicitud.getClienteId()));

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDate.now());
        venta.setDescuento(solicitud.getDescuento());
        venta.setTotal(0.0);

        for (SolicitudVentaDTO.DetalleVentaDTO detalleDTO : solicitud.getDetalles()) {
            Libro libro = libroRepository.findById(detalleDTO.getLibroId())
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado: ID " + detalleDTO.getLibroId()));

            if (libro.getStock() < detalleDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + libro.getTitulo());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setLibro(libro);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setSubtotal(libro.getPrecio() * detalleDTO.getCantidad());
            detalle.setVenta(venta);

            venta.getDetalles().add(detalle);
            libro.setStock(libro.getStock() - detalleDTO.getCantidad());
            libroRepository.save(libro);
        }

        double subtotal = venta.getDetalles().stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();

        venta.setTotal(subtotal * (1 - venta.getDescuento() / 100));

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    @Transactional
    public Venta guardarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

    // Métodos no implementados (se mantienen igual)
    @Override
    public Venta procesarVenta(Long clienteId, List<DetalleVenta> detallesRequest) {
        return null;
    }

    @Override
    public TicketDTO generarTicketVenta(Long clienteId, List<SolicitudVentaDTO.DetalleVentaDTO> items) {
        return null;
    }

    @Override
    public Optional<TicketDTO> obtenerTicketPorVentaId(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean existeVenta(Long id) {
        return ventaRepository.existsById(id);
    }
}