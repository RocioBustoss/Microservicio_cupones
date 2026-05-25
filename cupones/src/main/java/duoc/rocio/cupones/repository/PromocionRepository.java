package duoc.rocio.cupones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import duoc.rocio.cupones.model.Promocion;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByActiva(boolean activa);

    boolean existsByNombreIgnoreCase(String nombre);
}