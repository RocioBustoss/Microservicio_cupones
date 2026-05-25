package duoc.rocio.cupones.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import duoc.rocio.cupones.dto.AplicarCuponDTO;
import duoc.rocio.cupones.model.ValidacionCupon;
import duoc.rocio.cupones.service.ValidacionCuponService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ecomarket/v1")
public class ValidacionCuponController {

    @Autowired
    private ValidacionCuponService validacionCuponService;

    // APLICA UN CUPÓN A UN PEDIDO
    @PostMapping("/cupones/aplicar")
    public ResponseEntity<?> aplicarCupon(@Valid @RequestBody AplicarCuponDTO datosSolicitud) {

        return validacionCuponService.aplicarCupon(datosSolicitud);
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS
    @GetMapping("/validaciones")
    public ResponseEntity<?> obtenerValidaciones() {

        List<ValidacionCupon> validaciones = validacionCuponService.obtenerValidaciones();

        if (validaciones.isEmpty()) {
            return ResponseEntity.status(200).body("No existen validaciones registradas");
        }

        return ResponseEntity.status(200).body(validaciones);
    }

    // OBTIENE UNA VALIDACIÓN POR ID
    @GetMapping("/validaciones/{idValidacion}")
    public ResponseEntity<?> obtenerValidacionPorId(
            @PathVariable Long idValidacion) {

        Optional<ValidacionCupon> validacion = validacionCuponService.obtenerValidacionPorId(idValidacion);

        if (validacion.isPresent()) {
            return ResponseEntity.status(200).body(validacion.get());
        }

        return ResponseEntity.status(404).body("Validación no encontrada");
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS POR UN CLIENTE
    @GetMapping("/validaciones/cliente/{idCliente}")
    public ResponseEntity<?> obtenerValidacionesPorCliente(@PathVariable Long idCliente) {

        List<ValidacionCupon> validaciones = validacionCuponService.obtenerValidacionesPorCliente(idCliente);

        if (validaciones.isEmpty()) {
            return ResponseEntity.status(200).body("No existen validaciones para el cliente indicado");
        }

        return ResponseEntity.status(200).body(validaciones);
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS PARA UN PEDIDO
    @GetMapping("/validaciones/pedido/{idPedido}")
    public ResponseEntity<?> obtenerValidacionesPorPedido(@PathVariable Long idPedido) {

        List<ValidacionCupon> validaciones = validacionCuponService.obtenerValidacionesPorPedido(idPedido);

        if (validaciones.isEmpty()) {
            return ResponseEntity.status(200).body("No existen validaciones para el pedido indicado");
        }

        return ResponseEntity.status(200).body(validaciones);
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS PARA UN CUPÓN
    @GetMapping("/validaciones/cupon/{idCupon}")
    public ResponseEntity<?> obtenerValidacionesPorCupon(@PathVariable Long idCupon) {

        List<ValidacionCupon> validaciones = validacionCuponService.obtenerValidacionesPorCupon(idCupon);

        if (validaciones.isEmpty()) {
            return ResponseEntity.status(200).body("No existen validaciones para el cupón indicado");
        }

        return ResponseEntity.status(200).body(validaciones);
    }
}