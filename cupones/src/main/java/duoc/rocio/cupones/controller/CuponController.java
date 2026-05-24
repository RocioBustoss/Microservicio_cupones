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
            return ResponseEntity.status(200).body("No existen cupones registrados");
        }

        return ResponseEntity.status(200).body(cupones);
    }

    // OBTIENE LOS CUPONES DE UNA PROMOCION
    @GetMapping("/promociones/{idPromocion}/cupones")
    public ResponseEntity<?> obtenerCuponesPorPromocion(@PathVariable Long idPromocion) {

        List<Cupon> cupones = cuponService.obtenerCuponesPorPromocion(idPromocion);

        if (cupones.isEmpty()) {
            return ResponseEntity.status(200).body("La promoción no contiene cupones");
        }

        return ResponseEntity.status(200).body(cupones);
    }

    // OBTIENE UN CUPON POR SU ID
    @GetMapping("/cupones/{idCupon}")
    public ResponseEntity<?> obtenerCuponPorId(@PathVariable Long idCupon) {
        Optional<Cupon> cupon = cuponService.obtenerCuponPorId(idCupon);

        if (cupon.isPresent()) {
            return ResponseEntity.status(200).body(cupon.get());
        }

        return ResponseEntity.status(404).body("Cupón no encontrado");
    }

    // BUSCA UN CUPÓN POR SU CÓDIGO
    @GetMapping("/cupones/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        return cuponService.buscarPorCodigo(codigo);
    }

    // BUSCA CUPONES ACTIVOS
    @GetMapping("/cupones/estado")
    public ResponseEntity<?> buscarPorEstado(@RequestParam String estado) {
        List<Cupon> cupones = cuponService.buscarPorEstado(estado);

        if (cupones.isEmpty()) {
            return ResponseEntity.status(200).body("No existen cupones con el estado solicitado");
        }

        return ResponseEntity.status(200).body(cupones);
    }

    // CREA UN CUPÓN Y LO ASOCIA A UN DESCUENTO Y PROMOCIÓN
    @PostMapping("/promociones/{idPromocion}/cupones/descuento/{idDescuento}")
    public ResponseEntity<String> guardarCupon(@PathVariable Long idPromocion, @PathVariable Long idDescuento, @Valid @RequestBody Cupon cuponNuevo) {
        return cuponService.guardarCupon(idPromocion, idDescuento, cuponNuevo);
    }

    // ACTUALIZA LOS DAOTS DE UN CUPÓN
    @PutMapping("/cupones/{idCupon}")
    public ResponseEntity<String> actualizarCupon(@PathVariable Long idCupon, @Valid @RequestBody Cupon cuponActualizado) {
        return cuponService.actualizarCupon(idCupon, cuponActualizado);
    }

    // ACTIVA O DESACTIVA UN CUPÓN
    @PutMapping("/cupones/{idCupon}/estado")
    public ResponseEntity<String> cambiarEstadoCupon(@PathVariable Long idCupon, @RequestParam String estado) {
        return cuponService.cambiarEstadoCupon(idCupon, estado);
    }

    // ELIMINA UN CUPON
    @DeleteMapping("/cupones/{idCupon}")
    public ResponseEntity<String> eliminarCupon(@PathVariable Long idCupon) {
        return cuponService.eliminarCupon(idCupon);
    }
}