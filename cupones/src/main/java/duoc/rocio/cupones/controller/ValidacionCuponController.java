package duoc.rocio.cupones.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import duoc.rocio.cupones.dto.AplicarCuponDTO;
import duoc.rocio.cupones.dto.ResultadoCuponDTO;
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
        ResultadoCuponDTO res = validacionCuponService.aplicarCupon(datosSolicitud);

        // Traducimos los estados internos a códigos HTTP correspondientes
        if ("ERROR_404".equals(res.getResultado())) return ResponseEntity.status(404).body(res.getMensaje());
        if ("RECHAZADO".equals(res.getResultado())) return ResponseEntity.status(409).body(res.getMensaje());
        if ("ERROR_503".equals(res.getResultado())) return ResponseEntity.status(503).body(res.getMensaje());

        // Si el resultado es APROBADO, devolvemos el objeto completo con Status 200 (OK)
        return ResponseEntity.ok(res);
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS
    @GetMapping("/validaciones")
    public ResponseEntity<List<ValidacionCupon>> obtenerValidaciones() {
        return ResponseEntity.ok(validacionCuponService.obtenerValidaciones());
    }

    // OBTIENE UNA VALIDACIÓN POR ID
    @GetMapping("/validaciones/{idValidacion}")
    public ResponseEntity<?> obtenerValidacionPorId(@PathVariable Long idValidacion) {
        Optional<ValidacionCupon> validacion = validacionCuponService.obtenerValidacionPorId(idValidacion);

        if (validacion.isPresent()) {
            return ResponseEntity.ok(validacion.get());
        }
        return ResponseEntity.status(404).body("Validación no encontrada");
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS POR UN CLIENTE
    @GetMapping("/validaciones/cliente/{idCliente}")
    public ResponseEntity<List<ValidacionCupon>> obtenerValidacionesPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(validacionCuponService.obtenerValidacionesPorCliente(idCliente));
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS PARA UN PEDIDO
    @GetMapping("/validaciones/pedido/{idPedido}")
    public ResponseEntity<List<ValidacionCupon>> obtenerValidacionesPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(validacionCuponService.obtenerValidacionesPorPedido(idPedido));
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS PARA UN CUPÓN
    @GetMapping("/validaciones/cupon/{idCupon}")
    public ResponseEntity<List<ValidacionCupon>> obtenerValidacionesPorCupon(@PathVariable Long idCupon) {
        return ResponseEntity.ok(validacionCuponService.obtenerValidacionesPorCupon(idCupon));
    }
}