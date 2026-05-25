package duoc.rocio.cupones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import duoc.rocio.cupones.model.ValidacionCupon;

@Repository
public interface ValidacionCuponRepository extends JpaRepository<ValidacionCupon, Long> {

    // Obtiene todas las validaciones, mostrando primero las más recientes
    List<ValidacionCupon> findAllByOrderByFechaValidacionDesc();

    // Obtiene validaciones de un cliente
    List<ValidacionCupon> findByIdClienteOrderByFechaValidacionDesc(Long idCliente);

    // Obtiene validaciones de un pedido
    List<ValidacionCupon> findByIdPedidoOrderByFechaValidacionDesc(Long idPedido);

    // Obtiene validaciones de un cupón
    List<ValidacionCupon> findByCupon_IdCuponOrderByFechaValidacionDesc(Long idCupon);

    // Evita aplicar nuevamente el mismo cupón al mismo pedido
    boolean existsByCupon_IdCuponAndIdPedidoAndResultadoIgnoreCase(Long idCupon, Long idPedido, String resultado);
}