package duoc.rocio.cupones.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import duoc.rocio.cupones.model.Cupon;
import duoc.rocio.cupones.service.CuponService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ecomarket/v1")
public class CuponController {

    @Autowired
    private CuponService cuponService;

    // OBTIENE TODOS LOS CUPONES
    @GetMapping("/cupones")
    public ResponseEntity<?> obtenerCupones() {
        List<Cupon> cupones = cuponService.obtenerCupones();

        if (cupones.isEmpty()) {
            return ResponseEntity.status(404).body("No existen cupones registrados");
        }

        return ResponseEntity.status(200).body(cupones);
    }

    // OBTIENE LOS CUPONES DE UNA PROMOCION
    @GetMapping("/promociones/{idPromocion}/cupones")
    public ResponseEntity<List<Cupon>> obtenerCuponesPorPromocion(@PathVariable Long idPromocion) {
        List<Cupon> cupones = cuponService.obtenerCuponesPorPromocion(idPromocion);
    
        return ResponseEntity.status(200).body(cupones);
    }

    // OBTIENE UN CUPON POR SU ID
    @GetMapping("/cupones/{idCupon}")
    public ResponseEntity<?> obtenerCuponPorId(@PathVariable Long idCupon) {
        Optional<Cupon> cupon = cuponService.obtenerCuponPorId(idCupon);
        
        if (cupon.isEmpty()) {
            return ResponseEntity.status(404).body("Cupón no encontrado");
        }
        
        return ResponseEntity.status(200).body(cupon.get());
    }

    // BUSCA UN CUPÓN POR SU CÓDIGO
    @GetMapping("/cupones/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Cupon> cupon = cuponService.buscarPorCodigo(codigo);

        if (cupon.isEmpty()) {
            return ResponseEntity.status(404).body("Cupón no encontrado");
        }
        
        return ResponseEntity.status(200).body(cupon.get());
    }

    // BUSCA CUPONES ACTIVOS
    @GetMapping("/cupones/estado")
    public ResponseEntity<?> buscarPorEstado(@RequestParam String estado) {
        List<Cupon> cupones = cuponService.buscarPorEstado(estado);

        if (cupones.isEmpty()) {
            return ResponseEntity.status(404).body("No existen cupones con el estado solicitado");
        }

        return ResponseEntity.status(200).body(cupones);
    }

    // CREA UN CUPÓN Y LO ASOCIA A UN DESCUENTO Y PROMOCIÓN
    @PostMapping("/promociones/{idPromocion}/cupones/descuento/{idDescuento}")
    public ResponseEntity<String> guardarCupon(@PathVariable Long idPromocion, @PathVariable Long idDescuento, @Valid @RequestBody Cupon cuponNuevo) {
        int resultado = cuponService.guardarCupon(idPromocion, idDescuento, cuponNuevo);

        if (resultado == 0) return ResponseEntity.status(201).body("Cupón registrado correctamente");
        if (resultado == 1) return ResponseEntity.status(404).body("Promoción no encontrada");
        if (resultado == 2) return ResponseEntity.status(404).body("Descuento no encontrado");
        if (resultado == 3) return ResponseEntity.status(409).body("Ya existe un cupón con ese código");
        if (resultado == 4) return ResponseEntity.status(409).body("El descuento ya se encuentra asignado a otro cupón");
        if (resultado == 5) return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");
        if (resultado == 6) return ResponseEntity.status(400).body("La vigencia del cupón debe estar dentro de la vigencia de la promoción");
        if (resultado == 7) return ResponseEntity.status(400).body("El estado del cupón debe ser ACTIVO o INACTIVO");
        
        return ResponseEntity.status(500).body("Error interno");

    }

    // ACTUALIZA LOS DAOTS DE UN CUPÓN
    @PutMapping("/cupones/{idCupon}")
    public ResponseEntity<String> actualizarCupon(@PathVariable Long idCupon, @Valid @RequestBody Cupon cuponActualizado) {
        int resultado = cuponService.actualizarCupon(idCupon, cuponActualizado);

        if (resultado == 0) return ResponseEntity.ok("Cupón actualizado correctamente");
        if (resultado == 1) return ResponseEntity.status(404).body("Cupón no encontrado");
        if (resultado == 2) return ResponseEntity.status(409).body("Ya existe otro cupón con ese código");
        if (resultado == 3) return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");
        if (resultado == 4) return ResponseEntity.status(400).body("La vigencia del cupón debe estar dentro de la vigencia de la promoción");
        if (resultado == 5) return ResponseEntity.status(400).body("El estado del cupón debe ser ACTIVO o INACTIVO");
        
        return ResponseEntity.status(500).body("Error interno");
    }

    // ACTIVA O DESACTIVA UN CUPÓN
    @PutMapping("/cupones/{idCupon}/estado")
    public ResponseEntity<String> cambiarEstadoCupon(@PathVariable Long idCupon, @RequestParam String estado) {
        int resultado = cuponService.cambiarEstadoCupon(idCupon, estado);

        if (resultado == 0) return ResponseEntity.ok("Estado del cupón actualizado correctamente");
        if (resultado == 1) return ResponseEntity.status(404).body("Cupón no encontrado");
        if (resultado == 2) return ResponseEntity.status(400).body("El estado del cupón debe ser ACTIVO o INACTIVO");
        
        return ResponseEntity.internalServerError().body("Error interno");
    }

    // ELIMINA UN CUPON
    @DeleteMapping("/cupones/{idCupon}")
    public ResponseEntity<String> eliminarCupon(@PathVariable Long idCupon) {
        int resultado = cuponService.eliminarCupon(idCupon);
        
        if (resultado == 0) return ResponseEntity.ok("Cupón eliminado correctamente");
        if (resultado == 1) return ResponseEntity.status(404).body("Cupón no encontrado");
        
        return ResponseEntity.internalServerError().body("Error interno");
    }
}