package duoc.rocio.cupones.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import duoc.rocio.cupones.model.Promocion;
import duoc.rocio.cupones.service.PromocionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ecomarket/v1/promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    // OBTIENE TODAS LAS PROMOCIONES
    @GetMapping
    public ResponseEntity<?> obtenerPromociones() {

        List<Promocion> promociones = promocionService.obtenerPromociones();

        if (promociones.isEmpty()) {
            return ResponseEntity.status(200).body("No existen promociones registradas");
        }

        return ResponseEntity.status(200).body(promociones);
    }

    // OBTIENE UNA PROMOCIÓN POR SU ID
    @GetMapping("/{idPromocion}")
    public ResponseEntity<?> obtenerPromocionPorId(@PathVariable Long idPromocion) {

        Optional<Promocion> promocion = promocionService.obtenerPromocionPorId(idPromocion);

        if (promocion.isPresent()) {
            return ResponseEntity.status(200).body(promocion.get());
        }

        return ResponseEntity.status(404).body("Promoción no encontrada");
    }

    // BUSCA PROMOCIONES SEGÚN ESTADO
    @GetMapping("/estado")
    public ResponseEntity<?> buscarPorEstado(@RequestParam boolean activa) {

        List<Promocion> promociones = promocionService.buscarPorEstado(activa);

        if (promociones.isEmpty()) {
            return ResponseEntity.status(200).body("No existen promociones con el estado solicitado");
        }

        return ResponseEntity.status(200).body(promociones);
    }

    // CREA UNA NUEVA PROMOCIÓN
    @PostMapping
    public ResponseEntity<String> guardarPromocion(@Valid @RequestBody Promocion promocionNueva) {

        return promocionService.guardarPromocion(promocionNueva);
    }

    // ACTUALIZA UNA PROMOCIÓN
    @PutMapping("/{idPromocion}")
    public ResponseEntity<String> actualizarPromocion(@PathVariable Long idPromocion, @Valid @RequestBody Promocion promocionActualizada) {

        return promocionService.actualizarPromocion(idPromocion, promocionActualizada);
    }

    // ACTIVA O DESACTIVA UNA PROMOCIÓN
    @PutMapping("/{idPromocion}/estado")
    public ResponseEntity<String> cambiarEstadoPromocion(@PathVariable Long idPromocion, @RequestParam boolean activa) {

        return promocionService.cambiarEstadoPromocion(idPromocion, activa);
    }

    // ELIMINA UNA PROMOCIÓN
    @DeleteMapping("/{idPromocion}")
    public ResponseEntity<String> eliminarPromocion(@PathVariable Long idPromocion) {

        return promocionService.eliminarPromocion(idPromocion);
    }
}