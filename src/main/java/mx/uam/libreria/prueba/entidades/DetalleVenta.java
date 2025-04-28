package mx.uam.libreria.prueba.entidades;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    private int cantidad;
    private double subtotal;

    // Constructores
    public DetalleVenta() {
    }

    public DetalleVenta(Libro libro, int cantidad, double subtotal) {
        this.libro = libro;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = Objects.requireNonNull(libro, "El libro no puede ser nulo");
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo");
        }
        this.subtotal = subtotal;
    }

    // Métodos de negocio
    public void calcularSubtotal() {
        if (libro != null) {
            this.subtotal = libro.getPrecio() * cantidad;
        }
    }

    // equals() y hashCode() basados en ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVenta that = (DetalleVenta) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString() informativo
    @Override
    public String toString() {
        return "DetalleVenta{" +
                "id=" + id +
                ", libro=" + (libro != null ? libro.getTitulo() : "null") +
                ", cantidad=" + cantidad +
                ", subtotal=" + subtotal +
                '}';
    }
}