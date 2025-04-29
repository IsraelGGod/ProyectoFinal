

// ==================  Agregado ==========================
package mx.uam.libreria.prueba.servicio.Impl;

import mx.uam.libreria.prueba.dto.SolicitudVentaDTO;
import mx.uam.libreria.prueba.dto.TicketDTO;
import mx.uam.libreria.prueba.entidades.*;
import mx.uam.libreria.prueba.repositorio.*;
import mx.uam.libreria.prueba.servicio.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.time.LocalDate;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final LibroRepository libroRepository;

<<<<<<< HEAD
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

=======
    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ClienteRepository clienteRepository;

>>>>>>> origin/charly-rama
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
    public Venta procesarVenta(Long clienteId, List<DetalleVenta> detallesRequest) {
        return null;
    }

    @Override
<<<<<<< HEAD
    public TicketDTO generarTicketVenta(Long clienteId, List<SolicitudVentaDTO.DetalleVentaDTO> items) {
        return null;
    }

    @Override
    public Optional<TicketDTO> obtenerTicketPorVentaId(Long id) {
        return Optional.empty();
    }

    @Override
=======
>>>>>>> origin/charly-rama
    public boolean existeVenta(Long id) {
        return ventaRepository.existsById(id);
    }

    @Override
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional
    public Venta registrarVenta(SolicitudVentaDTO solicitud) {
        // 1. Validar y obtener cliente
        Cliente cliente = clienteRepository.findById(solicitud.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + solicitud.getClienteId()));

        // 2. Crear y configurar la venta
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDate.now());

        // 3. Validar descuento
        double descuento = solicitud.getDescuento();
        if (descuento < 0 || descuento > 100) {
            throw new IllegalArgumentException("Descuento inválido (" + descuento + "%). Debe estar entre 0% y 100%");
        }
        venta.setDescuento(descuento);

        // 4. Procesar detalles de venta
        List<DetalleVenta> detalles = new ArrayList<>();
        double subtotal = 0.0;

        for (SolicitudVentaDTO.DetalleVentaDTO detalleDTO : solicitud.getDetalles()) {
            Libro libro = libroRepository.findById(detalleDTO.getLibroId())
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + detalleDTO.getLibroId()));

            // Validar stock
            if (libro.getStock() < detalleDTO.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el libro: " + libro.getTitulo() +
                        ". Stock actual: " + libro.getStock() +
                        ", solicitado: " + detalleDTO.getCantidad());
            }

            // Actualizar stock
            libro.setStock(libro.getStock() - detalleDTO.getCantidad());
            libroRepository.save(libro);

            // Crear detalle de venta
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setLibro(libro);
            detalleVenta.setCantidad(detalleDTO.getCantidad());
            detalleVenta.setPrecioUnitario(libro.getPrecio());
            detalleVenta.setVenta(venta);

            // Calcular subtotal
            double subtotalDetalle = libro.getPrecio() * detalleDTO.getCantidad();
            subtotal += subtotalDetalle;

            detalles.add(detalleVenta);
        }

        // Validar subtotal
        if (subtotal <= 0) {
            throw new IllegalArgumentException("El subtotal de la venta no puede ser cero o negativo");
        }

        // Calcular total con descuento
        double total = subtotal * (1 - (descuento / 100.0));
        venta.setTotal(total);
        venta.setDetalles(detalles);

        return ventaRepository.save(venta);
    }

    @Override
    public TicketDTO generarTicketVenta(Long clienteId, List<SolicitudVentaDTO.DetalleVentaDTO> items) {
        // Este método es para previsualización del ticket antes de la venta
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        List<TicketDTO.ItemTicketDTO> itemsTicket = new ArrayList<>();
        double subtotal = 0.0;

        for (SolicitudVentaDTO.DetalleVentaDTO item : items) {
            Libro libro = libroRepository.findById(item.getLibroId())
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + item.getLibroId()));

            // Validar stock (solo para visualización)
            if (libro.getStock() < item.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + libro.getTitulo());
            }

            double itemSubtotal = libro.getPrecio() * item.getCantidad();
            subtotal += itemSubtotal;

            TicketDTO.ItemTicketDTO itemTicket = new TicketDTO.ItemTicketDTO();
            itemTicket.setTituloLibro(libro.getTitulo());
            itemTicket.setAutorLibro(libro.getAutor());
            itemTicket.setPrecioUnitario(libro.getPrecio());
            itemTicket.setCantidad(item.getCantidad());
            itemTicket.setSubtotal(itemSubtotal);

            itemsTicket.add(itemTicket);
        }

        // Crear ticket DTO
        TicketDTO ticket = new TicketDTO();
        ticket.setNumeroTicket("PREVIEW-" + UUID.randomUUID().toString());
        ticket.setFecha(LocalDateTime.now());
        ticket.setClienteNombre(cliente.getNombre());
        ticket.setMatriculado(cliente.isMatriculado());
        ticket.setItems(itemsTicket);
        ticket.setSubtotal(subtotal);
        ticket.setTotal(subtotal); // Sin descuento en previsualización
        ticket.setMetodoPago("NO APLICA");
        ticket.setCodigoBarras(UUID.randomUUID().toString());

        return ticket;
    }

    @Override
    public Optional<TicketDTO> obtenerTicketPorVentaId(Long id) {
        return ventaRepository.findById(id)
                .map(venta -> {
                    TicketDTO ticket = new TicketDTO();
                    ticket.setNumeroTicket(venta.getId().toString());
                    ticket.setFecha(venta.getFecha());
                    ticket.setClienteNombre(venta.getCliente().getNombre());
                    ticket.setMatriculado(venta.getCliente().isMatriculado());

                    List<TicketDTO.ItemTicketDTO> items = new ArrayList<>();
                    double subtotal = 0.0;

                    for (DetalleVenta detalle : venta.getDetalles()) {
                        Libro libro = detalle.getLibro();
                        double itemSubtotal = detalle.getPrecioUnitario() * detalle.getCantidad();
                        subtotal += itemSubtotal;

                        TicketDTO.ItemTicketDTO item = new TicketDTO.ItemTicketDTO();
                        item.setTituloLibro(libro.getTitulo());
                        item.setAutorLibro(libro.getAutor());
                        item.setPrecioUnitario(detalle.getPrecioUnitario());
                        item.setCantidad(detalle.getCantidad());
                        item.setSubtotal(itemSubtotal);

                        items.add(item);
                    }

                    double descuento = venta.getDescuento();
                    double total = subtotal * (1 - (descuento / 100.0));

                    ticket.setItems(items);
                    ticket.setSubtotal(subtotal);
                    ticket.setDescuento(descuento);
                    ticket.setTotal(total);
                    ticket.setMetodoPago(venta.getMetodoPago() != null ? venta.getMetodoPago() : "Efectivo");
                    ticket.setCodigoBarras(UUID.randomUUID().toString());

                    return ticket;
                });
    }
}
