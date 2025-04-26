package mx.uam.libreria.prueba.servicio.Impl;

import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.dto.TicketDTO;
import mx.uam.libreria.prueba.entidades.*;
import mx.uam.libreria.prueba.repositorio.*;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private LibroRepository libroRepository; // Suponiendo que tienes un LibroRepository

    @Autowired
    private ClienteRepository clienteRepository; // Suponiendo que tienes un ClienteRepository

    @Override
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    @Override
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta guardarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Venta procesarVenta(Long clienteId, List<DetalleVenta> detallesRequest) {
        Venta venta = new Venta();
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
        venta.setCliente(cliente);
        venta.setFecha(LocalDate.now());
        venta.setDetalles(detallesRequest);

        for (DetalleVenta detalle : detallesRequest) {
            detalle.setVenta(venta);
        }

        return ventaRepository.save(venta);
    }

    @Override
    public TicketDTO generarTicketVenta(Long clienteId, List<SolicitudVentaDTO.DetalleVentaDTO> items) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        List<TicketDTO.ItemTicketDTO> itemsTicket = new ArrayList<>();
        double subtotal = 0.0;

        for (SolicitudVentaDTO.DetalleVentaDTO item : items) {
            Libro libro = libroRepository.findById(item.getLibroId())
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + item.getLibroId()));

            TicketDTO.ItemTicketDTO itemTicket = new TicketDTO.ItemTicketDTO();
            itemTicket.setTituloLibro(libro.getTitulo());
            itemTicket.setAutorLibro(libro.getAutor());
            itemTicket.setPrecioUnitario(libro.getPrecio());
            itemTicket.setCantidad(item.getCantidad());
            itemTicket.setSubtotal(libro.getPrecio() * item.getCantidad());

            subtotal += itemTicket.getSubtotal();
            itemsTicket.add(itemTicket);
        }

        double descuento = 0; // Lo puedes calcular si quieres
        double total = subtotal - descuento;

        TicketDTO ticket = new TicketDTO();
        ticket.setNumeroTicket(UUID.randomUUID().toString());
        ticket.setFecha(LocalDateTime.now());
        ticket.setClienteNombre(cliente.getNombre());
        ticket.setMatriculado(cliente.isMatriculado());
        ticket.setDescuentoAplicado(descuento);
        ticket.setItems(itemsTicket);
        ticket.setSubtotal(subtotal);
        ticket.setDescuento(descuento);
        ticket.setTotal(total);
        ticket.setMetodoPago("Efectivo"); // Puedes cambiar esto
        ticket.setCodigoBarras(UUID.randomUUID().toString());

        return ticket;
    }

    @Override
    public Optional<TicketDTO> obtenerTicketPorVentaId(Long id) {
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isPresent()) {
            Venta venta = ventaOptional.get();

            TicketDTO ticket = new TicketDTO();
            ticket.setNumeroTicket(venta.getId().toString());
            ticket.setFecha(venta.getFecha().atStartOfDay());
            ticket.setClienteNombre(venta.getCliente().getNombre());
            ticket.setMatriculado(venta.getCliente().isMatriculado());

            List<TicketDTO.ItemTicketDTO> items = new ArrayList<>();
            double subtotal = 0.0;
            for (DetalleVenta detalle : venta.getDetalles()) {
                Libro libro = detalle.getLibro();

                TicketDTO.ItemTicketDTO item = new TicketDTO.ItemTicketDTO();
                item.setTituloLibro(libro.getTitulo());
                item.setAutorLibro(libro.getAutor());
                item.setPrecioUnitario(libro.getPrecio());
                item.setCantidad(detalle.getCantidad());
                item.setSubtotal(libro.getPrecio() * detalle.getCantidad());

                subtotal += item.getSubtotal();
                items.add(item);
            }

            double descuento = venta.getDescuento();
            double total = subtotal - descuento;

            ticket.setItems(items);
            ticket.setSubtotal(subtotal);
            ticket.setDescuento(descuento);
            ticket.setTotal(total);
            ticket.setMetodoPago("Efectivo");
            ticket.setCodigoBarras(UUID.randomUUID().toString());

            return Optional.of(ticket);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean existeVenta(Long id) {
        return ventaRepository.existsById(id);
    }

    @Override
    @Transactional
    public Venta registrarVenta(SolicitudVentaDTO solicitud) {
        Cliente cliente = clienteRepository.findById(solicitud.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + solicitud.getClienteId()));

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDate.now());
        venta.setDescuento(solicitud.getDescuento());

        List<DetalleVenta> detalles = new ArrayList<>();

        for (SolicitudVentaDTO.DetalleVentaDTO detalleDTO : solicitud.getDetalles()) {
            Libro libro = libroRepository.findById(detalleDTO.getLibroId())
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + detalleDTO.getLibroId()));

            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setLibro(libro);
            detalleVenta.setCantidad(detalleDTO.getCantidad());
            detalleVenta.setVenta(venta);

            detalles.add(detalleVenta);
        }

        venta.setDetalles(detalles);

        return ventaRepository.save(venta);
    }

    @Override
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }
}
