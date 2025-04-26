package mx.uam.libreria.prueba.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class SolicitudVentaDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    private double descuento;

    @NotNull(message = "La lista de detalles no puede ser nula")
    @Size(min = 1, message = "Debe haber al menos un detalle de venta")
    @Valid
    private List<DetalleVentaDTO> detalles;

    // Getters y Setters

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }

    // Clase interna para los detalles
    public static class DetalleVentaDTO {

        @NotNull(message = "El ID del libro no puede ser nulo")
        private Long libroId;

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private int cantidad;

        // Getters y Setters

        public Long getLibroId() {
            return libroId;
        }

        public void setLibroId(Long libroId) {
            this.libroId = libroId;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }
}
