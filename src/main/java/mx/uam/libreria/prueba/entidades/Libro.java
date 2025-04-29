package mx.uam.libreria.prueba.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 100, message = "El nombre del autor no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String autor;

    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private double precio;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    // Constructores
    public Libro() {
        this.stock = 0; // Valor por defecto
    }

    public Libro(String titulo, String autor, double precio, Integer stock) {
        this();
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y Setters con validaciones
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = Objects.requireNonNull(titulo, "El título no puede ser nulo").trim();
        if (this.titulo.isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = Objects.requireNonNull(autor, "El autor no puede ser nulo").trim();
        if (this.autor.isEmpty()) {
            throw new IllegalArgumentException("El autor no puede estar vacío");
        }
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        this.precio = precio;
    }

    public Integer getStock() {
        return stock != null ? stock : 0;
    }

    public void setStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }

    // Métodos de negocio mejorados
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser positiva");
        }
        if (getStock() < cantidad) {
            throw new IllegalStateException(
                    String.format("No hay suficiente stock. Disponible: %d, Solicitado: %d", getStock(), cantidad)
            );
        }
        this.stock -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser positiva");
        }
        this.stock += cantidad;
    }

    // equals() y hashCode() mejorados
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return Objects.equals(titulo.toLowerCase(), libro.titulo.toLowerCase()) &&
                Objects.equals(autor.toLowerCase(), libro.autor.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo.toLowerCase(), autor.toLowerCase());
    }

    // toString() más seguro para valores nulos
    @Override
    public String toString() {
        return String.format(
                "Libro[id=%d, titulo='%s', autor='%s', precio=%.2f, stock=%d]",
                id != null ? id : 0,
                titulo != null ? titulo : "",
                autor != null ? autor : "",
                precio,
                getStock()
        );
    }
}