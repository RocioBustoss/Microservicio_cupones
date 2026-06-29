package duoc.rocio.cupones.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import duoc.rocio.cupones.model.Descuento;
import duoc.rocio.cupones.service.DescuentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ecomarket/v1/descuentos")
public class DescuentoController {

    @Autowired
    private DescuentoService descuentoService;

    // OBTENER TODOS LOS DESCUENTOS
    @GetMapping
    public ResponseEntity<?> obtenerDescuentos() {

        List<Descuento> descuentos = descuentoService.obtenerDescuentos();

        if (descuentos.isEmpty()) {
            return ResponseEntity.status(200).body("No existen descuentos registrados");
        }

        return ResponseEntity.status(200).body(descuentos);
    }

    // OBTIENE UN DESCUENTO POR ID
    @GetMapping("/{idDescuento}")
    public ResponseEntity<?> obtenerDescuentoPorId(@PathVariable Long idDescuento) {

        Optional<Descuento> descuento = descuentoService.obtenerDescuentoPorId(idDescuento);

        if (descuento.isPresent()) {
            return ResponseEntity.status(200).body(descuento.get());
        }

        return ResponseEntity.status(404).body("Descuento no encontrado");
    }

    // BUSCA DESCUENTO POR TIPO
    @GetMapping("/tipo")
    public ResponseEntity<?> buscarPorTipo(@RequestParam String tipo) {

        List<Descuento> descuentos = descuentoService.buscarPorTipo(tipo);

        if (descuentos.isEmpty()) {
            return ResponseEntity.status(200).body("No existen descuentos del tipo solicitado");
        }

        return ResponseEntity.status(200).body(descuentos);
    }

    // CREA UN DESCUENTO
    @PostMapping
    public ResponseEntity<String> guardarDescuento(@Valid @RequestBody Descuento descuentoNuevo) {
        int res = descuentoService.guardarDescuento(descuentoNuevo);

        if (res == 0) return ResponseEntity.status(201).body("Descuento registrado correctamente");
        if (res == 1) return ResponseEntity.status(400).body("El tipo de descuento debe ser PORCENTAJE o MONTO_FIJO");
        if (res == 2) return ResponseEntity.status(400).body("El porcentaje de descuento no puede ser mayor a 100");
        
        return ResponseEntity.internalServerError().build();
    }


    // ACTUALIZA UN DESCUENTO
    @PutMapping("/{idDescuento}")
    public ResponseEntity<String> actualizarDescuento(@PathVariable Long idDescuento, @Valid @RequestBody Descuento descuentoActualizado) {
        int res = descuentoService.actualizarDescuento(idDescuento, descuentoActualizado);

        if (res == 0) return ResponseEntity.ok("Descuento actualizado correctamente");
        if (res == 1) return ResponseEntity.status(404).body("Descuento no encontrado");
        if (res == 2) return ResponseEntity.status(400).body("El tipo de descuento debe ser PORCENTAJE o MONTO_FIJO");
        if (res == 3) return ResponseEntity.status(400).body("El porcentaje de descuento no puede ser mayor a 100");

        return ResponseEntity.internalServerError().build();
    }

    // ELIMINA UN DESCUENTO
    @DeleteMapping("/{idDescuento}")
    public ResponseEntity<String> eliminarDescuento(@PathVariable Long idDescuento) {
        int res = descuentoService.eliminarDescuento(idDescuento);

        if (res == 0) return ResponseEntity.ok("Descuento eliminado correctamente");
        if (res == 1) return ResponseEntity.status(404).body("Descuento no encontrado");

        return ResponseEntity.internalServerError().build();
    }
}