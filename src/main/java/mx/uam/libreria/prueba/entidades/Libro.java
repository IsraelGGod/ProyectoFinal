package mx.uam.libreria.prueba.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

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
    }

    public Libro(String titulo, String autor, double precio, Integer stock) {
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
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede estar vacío");
        }
        this.autor = autor;
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
        return stock;
    }

    public void setStock(Integer stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }

    // Métodos de negocio
    /**
     * Reduce el stock del libro cuando se vende
     * @param cantidad La cantidad a reducir
     * @throws IllegalArgumentException Si la cantidad no es positiva
     * @throws IllegalStateException Si no hay suficiente stock
     */
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (this.stock < cantidad) {
            throw new IllegalStateException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", this.stock, cantidad)
            );
        }
        this.stock -= cantidad;
    }

    /**
     * Aumenta el stock del libro (para reposiciones)
     * @param cantidad La cantidad a aumentar
     * @throws IllegalArgumentException Si la cantidad no es positiva
     */
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.stock += cantidad;
    }

    // Métodos de comparación
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return titulo.equalsIgnoreCase(libro.titulo) && 
               autor.equalsIgnoreCase(libro.autor);
    }

    @Override
    public int hashCode() {
        return (titulo.toLowerCase() + autor.toLowerCase()).hashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "Libro [id=%d, titulo='%s', autor='%s', precio=%.2f, stock=%d]",
            id, titulo, autor, precio, stock
        );
    }
}