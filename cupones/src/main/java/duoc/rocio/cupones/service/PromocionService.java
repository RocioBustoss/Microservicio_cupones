package duoc.rocio.cupones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> guardarPromocion(Promocion promocionNueva) {

        if (promocionRepository.existsByNombreIgnoreCase(promocionNueva.getNombre())) {
            return ResponseEntity.status(409).body("La promoción ya existe");
        }

        if (promocionNueva.getFechaFin().isBefore(promocionNueva.getFechaInicio())) {
            return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");
        }

        promocionRepository.save(promocionNueva);
        return ResponseEntity.status(201).body("Promoción registrada correctamente");
    }

    // ACTUALIZA LOS DATOS DE UNA PROMOCIÓN
    public ResponseEntity<String> actualizarPromocion(Long idPromocion, Promocion promocionActualizada) {

        Optional<Promocion> promocionEncontrada = promocionRepository.findById(idPromocion);

        if (promocionEncontrada.isEmpty()) {
            return ResponseEntity.status(404).body("Promoción no encontrada");
        }

        if (promocionActualizada.getFechaFin().isBefore(promocionActualizada.getFechaInicio())) {
            return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");
        }

        Promocion promocion = promocionEncontrada.get();

        promocion.setNombre(promocionActualizada.getNombre());
        promocion.setDescripcion(promocionActualizada.getDescripcion());
        promocion.setFechaInicio(promocionActualizada.getFechaInicio());
        promocion.setFechaFin(promocionActualizada.getFechaFin());
        promocion.setActiva(promocionActualizada.isActiva());

        promocionRepository.save(promocion);

        return ResponseEntity.status(200).body("Promoción actualizada correctamente");
    }

    // ACTIVA O DESACTIVA UNA PROMOCIÓN
    public ResponseEntity<String> cambiarEstadoPromocion(Long idPromocion, boolean activa) {

        Optional<Promocion> promocionEncontrada = promocionRepository.findById(idPromocion);

        if (promocionEncontrada.isEmpty()) {
            return ResponseEntity.status(404).body("Promoción no encontrada");
        }

        Promocion promocion = promocionEncontrada.get();
        promocion.setActiva(activa);

        promocionRepository.save(promocion);

        if (activa) {
            return ResponseEntity.status(200).body("Promoción activada correctamente");
        }
        return ResponseEntity.status(200).body("Promoción desactivada correctamente");
    }

    // ELIMINAR PROMOCION
    public ResponseEntity<String> eliminarPromocion(Long idPromocion) {

        if (!promocionRepository.existsById(idPromocion)) {
            return ResponseEntity.status(404).body("Promoción no encontrada");
        }

        promocionRepository.deleteById(idPromocion);

        return ResponseEntity.status(200).body("Promoción eliminada correctamente");
    }
}