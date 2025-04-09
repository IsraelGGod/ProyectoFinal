package mx.uam.libreria.prueba.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(name = "matricula", unique = true, length = 20)
    @Size(max = 20, message = "La matrícula no puede exceder 20 caracteres")
    @Pattern(regexp = "^[A-Za-z]\\d*$", message = "Formato de matrícula inválido. Debe comenzar con letra seguida de dígitos")
    private String matricula;

    @Column(nullable = false)
    private boolean matriculado = false;

    // Constructores
    public Cliente() {
    }

    public Cliente(String nombre, String email, String telefono, String matricula) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.setMatricula(matricula); // Usa el setter para validación
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        if (matricula != null && !matricula.trim().isEmpty()) {
            this.matricula = matricula.toUpperCase();
            this.matriculado = true;
        } else {
            this.matricula = null;
            this.matriculado = false;
        }
    }

    public boolean isMatriculado() {
        return matriculado;
    }

    // Método de negocio para validar formato específico
    public boolean validarFormatoMatricula() {
        return this.matricula != null && this.matricula.matches("^[A-Z]\\d{4,8}$");
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", matricula='" + matricula + '\'' +
                ", matriculado=" + matriculado +
                '}';
    }

    // Método equals() y hashCode() para comparación por matrícula/email
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return email.equals(cliente.email) || 
               (matricula != null && matricula.equals(cliente.matricula));
    }

    @Override
    public int hashCode() {
        return 31 * (email.hashCode() + (matricula != null ? matricula.hashCode() : 0));
    }
}