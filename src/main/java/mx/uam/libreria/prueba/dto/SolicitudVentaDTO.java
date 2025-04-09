package mx.uam.libreria.prueba.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class SolicitudVentaDTO {
    @NotNull(message = "El ID de cliente es obligatorio")
    private Long clienteId;
    
    @NotEmpty(message = "Debe incluir al menos un libro")
    private List<ItemVentaDTO> items;

    public static class ItemVentaDTO {
        @NotNull
        private Long libroId;
        
        @Min(1)
        private int cantidad;

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

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemVentaDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemVentaDTO> items) {
        this.items = items;
    }
}