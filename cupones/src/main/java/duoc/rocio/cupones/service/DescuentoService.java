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
    public int guardarDescuento(Descuento descuentoNuevo) {
        String tipo = descuentoNuevo.getTipoDescuento().toUpperCase();

        if (!tipo.equals("PORCENTAJE") && !tipo.equals("MONTO_FIJO")) return 1;
        if (tipo.equals("PORCENTAJE") && descuentoNuevo.getValor() > 100) return 2;

        descuentoNuevo.setTipoDescuento(tipo);
        descuentoRepository.save(descuentoNuevo);
        return 0;
    }

    // ACTUALIZAR UN DESCUENTO
    public int actualizarDescuento(Long idDescuento, Descuento descuentoActualizado) {
        Optional<Descuento> descuentoOpt = descuentoRepository.findById(idDescuento);
        if (descuentoOpt.isEmpty()) return 1;

        String tipo = descuentoActualizado.getTipoDescuento().toUpperCase();
        if (!tipo.equals("PORCENTAJE") && !tipo.equals("MONTO_FIJO")) return 2;
        if (tipo.equals("PORCENTAJE") && descuentoActualizado.getValor() > 100) return 3;

        Descuento descuento = descuentoOpt.get();
        descuento.setTipoDescuento(tipo);
        descuento.setValor(descuentoActualizado.getValor());

        descuentoRepository.save(descuento);
        return 0;
    }

    // ELIMINAR DESCUENTO
    public int eliminarDescuento(Long idDescuento) {
        if (!descuentoRepository.existsById(idDescuento)) return 1;
        
        descuentoRepository.deleteById(idDescuento);
        return 0;
    }
}