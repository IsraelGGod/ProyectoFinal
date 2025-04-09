package mx.uam.libreria.prueba.repositorio;

import mx.uam.libreria.prueba.entidades.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repositorio para operaciones de base de datos con DetalleVenta.
 * Proporciona métodos estándar CRUD a través de JpaRepository
 * y consultas personalizadas.
 */
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    /**
     * Busca detalles de venta por ID de venta (derivado query)
     * @param ventaId ID de la venta asociada
     * @return Lista de detalles de venta
     */
    List<DetalleVenta> findByVentaId(Long ventaId);

    /**
     * Versión paginada de findByVentaId
     * @param ventaId ID de la venta asociada
     * @param pageable Configuración de paginación
     * @return Página de detalles de venta
     */
    Page<DetalleVenta> findByVentaId(Long ventaId, Pageable pageable);

    /**
     * Consulta personalizada con JPQL para detalles de venta
     * @param ventaId ID de la venta
     * @return Lista de detalles con proyección específica
     */
    @Query("SELECT dv FROM DetalleVenta dv JOIN FETCH dv.libro WHERE dv.venta.id = :ventaId")
    List<DetalleVenta> findDetallesConLibrosPorVentaId(@Param("ventaId") Long ventaId);

    /**
     * Consulta nativa para obtener el total de ventas por libro
     * @param libroId ID del libro
     * @return Cantidad total vendida
     */
    @Query(value = "SELECT SUM(dv.cantidad) FROM detalles_venta dv WHERE dv.libro_id = :libroId", 
           nativeQuery = true)
    Integer sumCantidadVendidaPorLibro(@Param("libroId") Long libroId);
}