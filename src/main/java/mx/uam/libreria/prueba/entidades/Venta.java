package mx.uam.libreria.prueba.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private double descuento;

    // Constructores
    public Venta() {
        this.fecha = LocalDate.now(); // Fecha actual por defecto
        this.descuento = 0.0; // Descuento inicial 0
    }

    public Venta(Cliente cliente) {
        this();
        this.cliente = Objects.requireNonNull(cliente, "El cliente no puede ser nulo");
    }

    // Getters y Setters con validaciones
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
        this.cliente = Objects.requireNonNull(cliente, "El cliente no puede ser nulo");
    }

    public List<DetalleVenta> getDetalles() {
        return new ArrayList<>(detalles); // Devuelve copia para proteger la lista original
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = Objects.requireNonNull(detalles, "Los detalles no pueden ser nulos");
        this.detalles.forEach(det -> det.setVenta(this)); // Actualiza la relación bidireccional
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        if (total < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo");
        }
        this.total = total;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        if (descuento < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        this.descuento = descuento;
    }

    // Métodos de negocio mejorados
    public void agregarDetalle(DetalleVenta detalle) {
        Objects.requireNonNull(detalle, "El detalle no puede ser nulo");
        detalles.add(detalle);
        detalle.setVenta(this);
        calcularTotal(); // Recalcula el total automáticamente
    }

    public void calcularTotal() {
        this.total = detalles.stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum() * (1 - descuento / 100);
    }

    // equals() y hashCode() basados en ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return Objects.equals(id, venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString() informativo
    @Override
    public String toString() {
        return String.format(
                "Venta[id=%d, cliente=%s, fecha=%s, total=%.2f, descuento=%.1f%%, detalles=%d]",
                id,
                cliente != null ? cliente.getNombre() : "null",
                fecha,
                total,
                descuento,
                detalles.size()
        );
    }
}