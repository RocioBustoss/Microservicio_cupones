package duoc.rocio.cupones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import duoc.rocio.cupones.model.Cupon;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> {

    // Obtener cupones de una promoción
    List<Cupon> findByPromocion_IdPromocion(Long idPromocion);

    // Buscar cupón por código
    Optional<Cupon> findByCodigoIgnoreCase(String codigo);

    // Buscar cupones por estado
    List<Cupon> findByEstadoIgnoreCase(String estado);

    // Evitar códigos repetidos
    boolean existsByCodigoIgnoreCase(String codigo);

    // Evitar usar el mismo descuento en más de un cupón
    boolean existsByDescuento_IdDescuento(Long idDescuento);
}