package duoc.rocio.cupones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import duoc.rocio.cupones.model.Promocion;
import duoc.rocio.cupones.repository.PromocionRepository;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    // OBTIENE TODAS LAS PROMOCIONES
    public List<Promocion> obtenerPromociones() {
        return promocionRepository.findAll();
    }

    // OBTIENE UNA PROMOCION POR ID
    public Optional<Promocion> obtenerPromocionPorId(Long idPromocion) {
        return promocionRepository.findById(idPromocion);
    }

    // OBTIENE LAS PROMOCIONES ACTIVAS O INACTIVAS SEGÚN ESTADO
    public List<Promocion> buscarPorEstado(boolean activa) {
        return promocionRepository.findByActiva(activa);
    }

    // CREA UNA NUEVA PROMOCION
    public int guardarPromocion(Promocion promocionNueva) {
        if (promocionRepository.existsByNombreIgnoreCase(promocionNueva.getNombre())) return 1;
        if (promocionNueva.getFechaFin().isBefore(promocionNueva.getFechaInicio())) return 2;

        promocionRepository.save(promocionNueva);
        return 0;
    }

    // ACTUALIZA LOS DATOS DE UNA PROMOCIÓN
    public int actualizarPromocion(Long idPromocion, Promocion promocionActualizada) {  
        Optional<Promocion> promocionOpt = promocionRepository.findById(idPromocion);
        
        if (promocionOpt.isEmpty()) return 1;
        if (promocionActualizada.getFechaFin().isBefore(promocionActualizada.getFechaInicio())) return 2;

        Promocion promocion = promocionOpt.get();
        promocion.setNombre(promocionActualizada.getNombre());
        promocion.setDescripcion(promocionActualizada.getDescripcion());
        promocion.setFechaInicio(promocionActualizada.getFechaInicio());
        promocion.setFechaFin(promocionActualizada.getFechaFin());
        promocion.setActiva(promocionActualizada.isActiva());

        promocionRepository.save(promocion);
        return 0;
    }

    // ACTIVA O DESACTIVA UNA PROMOCIÓN
    public int cambiarEstadoPromocion(Long idPromocion, boolean activa) {
        Optional<Promocion> promocionOpt = promocionRepository.findById(idPromocion);
        
        if (promocionOpt.isEmpty()) return 1;

        Promocion promocion = promocionOpt.get();
        promocion.setActiva(activa);
        promocionRepository.save(promocion);
        return 0;
    }

    // ELIMINAR PROMOCION
    public int eliminarPromocion(Long idPromocion) {
        if (!promocionRepository.existsById(idPromocion)) return 1;

        promocionRepository.deleteById(idPromocion);
        return 0;
    }
}