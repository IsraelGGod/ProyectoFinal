package mx.uam.libreria.prueba.dto;

import java.util.List;

public class SolicitudVentaDTO {
    private Long clienteId;
    private double descuento;
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
        private Long libroId;
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