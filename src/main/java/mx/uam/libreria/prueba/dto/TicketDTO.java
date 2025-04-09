package mx.uam.libreria.prueba.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TicketDTO {
    private String numeroTicket;
    private LocalDateTime fecha;
    private String clienteNombre;
    private boolean matriculado;
    private double descuentoAplicado;
    private List<ItemTicketDTO> items;
    private double subtotal;
    private double descuento;
    private double total;
    private String metodoPago;
    private String codigoBarras;

    public static class ItemTicketDTO {
        private String tituloLibro;
        private String autorLibro;
        private double precioUnitario;
        private int cantidad;
        private double subtotal;

        public String getTituloLibro() { return tituloLibro; }
        public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
        public String getAutorLibro() { return autorLibro; }
        public void setAutorLibro(String autorLibro) { this.autorLibro = autorLibro; }
        public double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getSubtotal() { return subtotal; }
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    }

    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public boolean isMatriculado() { return matriculado; }
    public void setMatriculado(boolean matriculado) { this.matriculado = matriculado; }
    public double getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(double descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }
    public List<ItemTicketDTO> getItems() { return items; }
    public void setItems(List<ItemTicketDTO> items) { this.items = items; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
}