package duoc.rocio.cupones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import duoc.rocio.cupones.model.Cupon;
import duoc.rocio.cupones.model.Descuento;
import duoc.rocio.cupones.model.Promocion;
import duoc.rocio.cupones.repository.CuponRepository;
import duoc.rocio.cupones.repository.DescuentoRepository;
import duoc.rocio.cupones.repository.PromocionRepository;

@Service
public class CuponService {

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private DescuentoRepository descuentoRepository;

    // OBTENER TODOS LOS CUPONES
    public List<Cupon> obtenerCupones() {
        return cuponRepository.findAll();
    }

    // OBTENER LOS CUPONES DE UNA PROMOCIÓN
    public List<Cupon> obtenerCuponesPorPromocion(Long idPromocion) {
        return cuponRepository.findByPromocion_IdPromocion(idPromocion);
    }

    // OBTIENR UN CUPÓN POR ID
    public Optional<Cupon> obtenerCuponPorId(Long idCupon) {
        return cuponRepository.findById(idCupon);
    }

    // BUSCAR CUPÓN POR SU CÓDIGO
    public Optional<Cupon> buscarPorCodigo(String codigo) {
        return cuponRepository.findByCodigoIgnoreCase(codigo);
    }

    // BUSCAR CUPONES POR ESTADO
    public List<Cupon> buscarPorEstado(String estado) {
        return cuponRepository.findByEstadoIgnoreCase(estado);
    }

    // CREAR UN CUPÓN Y ASOCIARLO A UNA PROMOCIÓN Y DESCUENTO
    public int guardarCupon(Long idPromocion, Long idDescuento, Cupon cuponNuevo) {
        Optional<Promocion> promoOpt = promocionRepository.findById(idPromocion);
        if (promoOpt.isEmpty()) return 1;

        Optional<Descuento> descOpt = descuentoRepository.findById(idDescuento);
        if (descOpt.isEmpty()) return 2;

        if (cuponRepository.existsByCodigoIgnoreCase(cuponNuevo.getCodigo())) return 3;
        if (cuponRepository.existsByDescuento_IdDescuento(idDescuento)) return 4;
        if (cuponNuevo.getFechaFin().isBefore(cuponNuevo.getFechaInicio())) return 5;
        
        Promocion promo = promoOpt.get();
        if (cuponNuevo.getFechaInicio().isBefore(promo.getFechaInicio()) || 
            cuponNuevo.getFechaFin().isAfter(promo.getFechaFin())) return 6;

        String estado = cuponNuevo.getEstado().toUpperCase();
        if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) return 7;

        // Guardado
        cuponNuevo.setCodigo(cuponNuevo.getCodigo().toUpperCase());
        cuponNuevo.setEstado(estado);
        cuponNuevo.setUsosActuales(0);
        cuponNuevo.setPromocion(promo);
        cuponNuevo.setDescuento(descOpt.get());

        cuponRepository.save(cuponNuevo);
        return 0;
    }


    // ACTUALIZA DATOS DEL CUPÓN
    public int actualizarCupon(Long idCupon, Cupon cuponActualizado) {
        Optional<Cupon> cuponOpt = cuponRepository.findById(idCupon);
        if (cuponOpt.isEmpty()) return 1;

        Cupon cupon = cuponOpt.get();

        if (!cupon.getCodigo().equalsIgnoreCase(cuponActualizado.getCodigo()) 
            && cuponRepository.existsByCodigoIgnoreCase(cuponActualizado.getCodigo())) {
            return 2;
        }

        if (cuponActualizado.getFechaFin().isBefore(cuponActualizado.getFechaInicio())) return 3;

        Promocion promocion = cupon.getPromocion();
        if (cuponActualizado.getFechaInicio().isBefore(promocion.getFechaInicio()) || 
            cuponActualizado.getFechaFin().isAfter(promocion.getFechaFin())) return 4;

        String estado = cuponActualizado.getEstado().toUpperCase();
        if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) return 5;

        // Actualización
        cupon.setCodigo(cuponActualizado.getCodigo().toUpperCase());
        cupon.setDescripcion(cuponActualizado.getDescripcion());
        cupon.setFechaInicio(cuponActualizado.getFechaInicio());
        cupon.setFechaFin(cuponActualizado.getFechaFin());
        cupon.setEstado(estado);
        cupon.setUsoMaximo(cuponActualizado.getUsoMaximo());

        cuponRepository.save(cupon);
        return 0;
    }

    // ACTIVAR O DESACTIVAR UN CUPÓN
    public int cambiarEstadoCupon(Long idCupon, String estado) {
        Optional<Cupon> cuponOpt = cuponRepository.findById(idCupon);
        if (cuponOpt.isEmpty()) return 1;

        String estadoNuevo = estado.toUpperCase();
        if (!estadoNuevo.equals("ACTIVO") && !estadoNuevo.equals("INACTIVO")) return 2;

        Cupon cupon = cuponOpt.get();
        cupon.setEstado(estadoNuevo);
        cuponRepository.save(cupon);
        return 0;
    }


    // ELIMINAR UN CUPÓN
    public int eliminarCupon(Long idCupon) {
        if (!cuponRepository.existsById(idCupon)) {
            return 1;
        }
        
        cuponRepository.deleteById(idCupon);
        return 0;
    }
}