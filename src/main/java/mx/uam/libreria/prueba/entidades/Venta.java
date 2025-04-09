package mx.uam.libreria.prueba.entidades;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private Double descuento = 0.0; // Nuevo campo para almacenar el descuento aplicado

    // Constructor
    public Venta() {
        this.fecha = LocalDate.now();
        this.total = 0.0;
        this.descuento = 0.0;
    }

    public Venta(Cliente cliente, List<DetalleVenta> detalles) {
        this();
        this.cliente = cliente;
        this.detalles = detalles;
        calcularTotal();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        calcularTotal();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    // Métodos de negocio (NUEVOS)
    /**
     * Calcula el total de la venta aplicando descuento si el cliente está matriculado
     */
    public void calcularTotal() {
        if (detalles == null || detalles.isEmpty()) {
            this.total = 0.0;
            this.descuento = 0.0;
            return;
        }

        double subtotal = detalles.stream()
                .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                .sum();

        // Aplica 10% de descuento si el cliente está matriculado
        if (cliente != null && cliente.isMatriculado()) {
            this.descuento = subtotal * 0.10;
            this.total = subtotal - descuento;
        } else {
            this.descuento = 0.0;
            this.total = subtotal;
        }
    }

    /**
     * Agrega un detalle de venta a la lista
     * @param detalle El detalle a agregar
     */
    public void agregarDetalle(DetalleVenta detalle) {
        detalle.setVenta(this);
        this.detalles.add(detalle);
        calcularTotal();
    }

    // Método toString() mejorado
    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                ", fecha=" + fecha +
                ", total=" + total +
                ", descuento=" + descuento +
                ", detalles=" + detalles.size() +
                '}';
    }
}