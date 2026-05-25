package duoc.rocio.cupones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import duoc.rocio.cupones.model.Descuento;
import duoc.rocio.cupones.repository.DescuentoRepository;

@Service
public class DescuentoService {

    @Autowired
    private DescuentoRepository descuentoRepository;

    // OBTENER TODOS LOS DESCUENTOS
    public List<Descuento> obtenerDescuentos() {
        return descuentoRepository.findAll();
    }

    // OBTENER DESCUENTOS POR ID
    public Optional<Descuento> obtenerDescuentoPorId(Long idDescuento) {
        return descuentoRepository.findById(idDescuento);
    }

    // BUSCAR DESCUENTO POR TIPO DE DESCUENTO
    public List<Descuento> buscarPorTipo(String tipoDescuento) {
        return descuentoRepository.findByTipoDescuentoIgnoreCase(tipoDescuento);
    }

    // CREAR UN NUEVO DESCUENTO
    public ResponseEntity<String> guardarDescuento(Descuento descuentoNuevo) {

        String tipo = descuentoNuevo.getTipoDescuento().toUpperCase();

        if (!tipo.equals("PORCENTAJE") && !tipo.equals("MONTO_FIJO")) {
            return ResponseEntity.status(400).body("El tipo de descuento debe ser PORCENTAJE o MONTO_FIJO");
        }

        if (tipo.equals("PORCENTAJE") && descuentoNuevo.getValor() > 100) {
            return ResponseEntity.status(400).body("El porcentaje de descuento no puede ser mayor a 100");
        }

        descuentoNuevo.setTipoDescuento(tipo);
        descuentoRepository.save(descuentoNuevo);
        return ResponseEntity.status(201).body("Descuento registrado correctamente");
    }

    // ACTUALIZAR UN DESCUENTO
    public ResponseEntity<String> actualizarDescuento(Long idDescuento, Descuento descuentoActualizado) {

        Optional<Descuento> descuentoEncontrado = descuentoRepository.findById(idDescuento);

        if (descuentoEncontrado.isEmpty()) {
            return ResponseEntity.status(404).body("Descuento no encontrado");
        }

        String tipo = descuentoActualizado.getTipoDescuento().toUpperCase();

        if (!tipo.equals("PORCENTAJE") && !tipo.equals("MONTO_FIJO")) {
            return ResponseEntity.status(400).body("El tipo de descuento debe ser PORCENTAJE o MONTO_FIJO");
        }

        if (tipo.equals("PORCENTAJE") && descuentoActualizado.getValor() > 100) {
            return ResponseEntity.status(400).body("El porcentaje de descuento no puede ser mayor a 100");
        }

        Descuento descuento = descuentoEncontrado.get();

        descuento.setTipoDescuento(tipo);
        descuento.setValor(descuentoActualizado.getValor());

        descuentoRepository.save(descuento);
        return ResponseEntity.status(200).body("Descuento actualizado correctamente");
    }

    // ELIMINAR DESCUENTO
    public ResponseEntity<String> eliminarDescuento(Long idDescuento) {

        if (!descuentoRepository.existsById(idDescuento)) {
            return ResponseEntity.status(404).body("Descuento no encontrado");
        }

        descuentoRepository.deleteById(idDescuento);
        return ResponseEntity.status(200).body("Descuento eliminado correctamente");
    }
}