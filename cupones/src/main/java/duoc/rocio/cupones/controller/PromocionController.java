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
    public ResponseEntity<List<Promocion>> obtenerPromociones() {
        return ResponseEntity.ok(promocionService.obtenerPromociones());
    }

    // OBTIENE UNA PROMOCIÓN POR SU ID
    @GetMapping("/{idPromocion}")
    public ResponseEntity<?> obtenerPromocionPorId(@PathVariable Long idPromocion) {
        Optional<Promocion> promocion = promocionService.obtenerPromocionPorId(idPromocion);

        if (promocion.isPresent()) {
            return ResponseEntity.ok(promocion.get());
        }
        return ResponseEntity.status(404).body("Promoción no encontrada");
    }
    
    // BUSCA PROMOCIONES SEGÚN ESTADO
    @GetMapping("/estado")
    public ResponseEntity<List<Promocion>> buscarPorEstado(@RequestParam boolean activa) {
        return ResponseEntity.ok(promocionService.buscarPorEstado(activa));
    }

    // CREA UNA NUEVA PROMOCIÓN
    @PostMapping
    public ResponseEntity<String> guardarPromocion(@Valid @RequestBody Promocion promocionNueva) {
        int res = promocionService.guardarPromocion(promocionNueva);

        if (res == 0) return ResponseEntity.status(201).body("Promoción registrada correctamente");
        if (res == 1) return ResponseEntity.status(409).body("La promoción ya existe");
        if (res == 2) return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");

        return ResponseEntity.internalServerError().build();
    }

    // ACTUALIZA UNA PROMOCIÓN
    @PutMapping("/{idPromocion}")
    public ResponseEntity<String> actualizarPromocion(@PathVariable Long idPromocion, @Valid @RequestBody Promocion promocionActualizada) {
        int res = promocionService.actualizarPromocion(idPromocion, promocionActualizada);

        if (res == 0) return ResponseEntity.ok("Promoción actualizada correctamente");
        if (res == 1) return ResponseEntity.status(404).body("Promoción no encontrada");
        if (res == 2) return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");

        return ResponseEntity.internalServerError().build();
    }

    // ACTIVA O DESACTIVA UNA PROMOCIÓN
    @PutMapping("/{idPromocion}/estado")
    public ResponseEntity<String> cambiarEstadoPromocion(@PathVariable Long idPromocion, @RequestParam boolean activa) {
        int res = promocionService.cambiarEstadoPromocion(idPromocion, activa);

        if (res == 0) {
            String mensaje = activa ? "Promoción activada correctamente" : "Promoción desactivada correctamente";
            return ResponseEntity.ok(mensaje);
        }
        if (res == 1) return ResponseEntity.status(404).body("Promoción no encontrada");

        return ResponseEntity.internalServerError().build();
    }
    // ELIMINA UNA PROMOCIÓN
    @DeleteMapping("/{idPromocion}")
    public ResponseEntity<String> eliminarPromocion(@PathVariable Long idPromocion) {
        int res = promocionService.eliminarPromocion(idPromocion);

        if (res == 0) return ResponseEntity.ok("Promoción eliminada correctamente");
        if (res == 1) return ResponseEntity.status(404).body("Promoción no encontrada");

        return ResponseEntity.internalServerError().build();
    }
}