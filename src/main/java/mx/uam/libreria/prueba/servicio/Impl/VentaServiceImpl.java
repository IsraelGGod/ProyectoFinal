package mx.uam.libreria.prueba.servicio.Impl;

import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.dto.TicketDTO;
import mx.uam.libreria.prueba.entidades.*;
import mx.uam.libreria.prueba.repositorio.*;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarTodas() {
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
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un libro");
        }
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = obtenerVentaPorId(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
        
        venta.getDetalles().forEach(detalle -> {
            Libro libro = detalle.getLibro();
            libro.aumentarStock(detalle.getCantidad());
            libroRepository.save(libro);
        });
        
        ventaRepository.delete(venta);
    }

    @Override
    @Transactional
    public Venta procesarVenta(Long clienteId, List<DetalleVenta> detalles) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un detalle de venta");
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now().toLocalDate());

        double subtotal = 0.0;

        for (DetalleVenta detalle : detalles) {
            Libro libro = detalle.getLibro();
            if (libro.getStock() < detalle.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el libro: " + libro.getTitulo());
            }
            
            libro.reducirStock(detalle.getCantidad());
            libroRepository.save(libro);
            
            detalle.setVenta(venta);
            detalleVentaRepository.save(detalle);
            
            subtotal += detalle.getSubtotal();
            venta.getDetalles().add(detalle);
        }

        double descuento = cliente.isMatriculado() ? subtotal * 0.10 : 0.0;
        venta.setDescuento(descuento);
        venta.setTotal(subtotal - descuento);

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public boolean existeVenta(Long id) {
        return ventaRepository.existsById(id);
    }

    @Override
    @Transactional
    public TicketDTO generarTicketVenta(Long clienteId, List<SolicitudVentaDTO.ItemVentaDTO> items) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un libro en la venta");
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now().toLocalDate());

        double subtotal = 0.0;

        for (SolicitudVentaDTO.ItemVentaDTO item : items) {
            Libro libro = libroRepository.findById(item.getLibroId())
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado con ID: " + item.getLibroId()));

            if (libro.getStock() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el libro: " + libro.getTitulo());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setLibro(libro);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(libro.getPrecio());
            detalle.calcularSubtotal();
            detalle.setVenta(venta);

            subtotal += detalle.getSubtotal();

            libro.reducirStock(detalle.getCantidad());
            libroRepository.save(libro);
            detalleVentaRepository.save(detalle);

            venta.getDetalles().add(detalle);
        }

        double descuento = cliente.isMatriculado() ? subtotal * 0.10 : 0.0;
        venta.setDescuento(descuento);
        venta.setTotal(subtotal - descuento);

        ventaRepository.save(venta);

        TicketDTO ticket = new TicketDTO();
        ticket.setClienteNombre(cliente.getNombre());
        ticket.setMatriculado(cliente.isMatriculado());
        
        List<TicketDTO.ItemTicketDTO> ticketItems = venta.getDetalles().stream()
            .map(d -> {
                TicketDTO.ItemTicketDTO item = new TicketDTO.ItemTicketDTO();
                item.setTituloLibro(d.getLibro().getTitulo());
                item.setAutorLibro(d.getLibro().getAutor());
                item.setPrecioUnitario(d.getPrecioUnitario());
                item.setCantidad(d.getCantidad());
                item.setSubtotal(d.getSubtotal());
                return item;
            })
            .collect(Collectors.toList());
        
        ticket.setItems(ticketItems);
        ticket.setSubtotal(subtotal);
        ticket.setDescuento(descuento);
        ticket.setTotal(venta.getTotal());
        ticket.setFecha(venta.getFecha().atStartOfDay());

        return ticket;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDTO> obtenerTicketPorVentaId(Long id) {
        return ventaRepository.findById(id).map(venta -> {
            TicketDTO ticket = new TicketDTO();
            ticket.setClienteNombre(venta.getCliente().getNombre());
            ticket.setMatriculado(venta.getCliente().isMatriculado());
            
            List<TicketDTO.ItemTicketDTO> ticketItems = venta.getDetalles().stream()
                .map(d -> {
                    TicketDTO.ItemTicketDTO item = new TicketDTO.ItemTicketDTO();
                    item.setTituloLibro(d.getLibro().getTitulo());
                    item.setAutorLibro(d.getLibro().getAutor());
                    item.setPrecioUnitario(d.getPrecioUnitario());
                    item.setCantidad(d.getCantidad());
                    item.setSubtotal(d.getSubtotal());
                    return item;
                })
                .collect(Collectors.toList());
            
            ticket.setItems(ticketItems);
            ticket.setSubtotal(venta.getTotal() + venta.getDescuento());
            ticket.setDescuento(venta.getDescuento());
            ticket.setTotal(venta.getTotal());
            ticket.setFecha(venta.getFecha().atStartOfDay());
            
            return ticket;
        });
    }
}