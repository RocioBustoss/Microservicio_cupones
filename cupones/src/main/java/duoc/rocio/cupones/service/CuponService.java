package duoc.rocio.cupones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> buscarPorCodigo(String codigo) {

        Optional<Cupon> cupon = cuponRepository.findByCodigoIgnoreCase(codigo);

        if (cupon.isEmpty()) {
            return ResponseEntity.status(404).body("Cupón no encontrado");
        }

        return ResponseEntity.status(200)
                .body(cupon.get());
    }


    // BUSCAR CUPONES POR ESTADO
    public List<Cupon> buscarPorEstado(String estado) {
        return cuponRepository.findByEstadoIgnoreCase(estado);
    }


    // CREAR UN CUPÓN Y ASOCIARLO A UNA PROMOCIÓN Y DESCUENTO
    public ResponseEntity<String> guardarCupon(Long idPromocion, Long idDescuento, Cupon cuponNuevo) {

        Optional<Promocion> promocionEncontrada = promocionRepository.findById(idPromocion);

        if (promocionEncontrada.isEmpty()) {
            return ResponseEntity.status(404).body("Promoción no encontrada");
        }

        Optional<Descuento> descuentoEncontrado = descuentoRepository.findById(idDescuento);

        if (descuentoEncontrado.isEmpty()) {
            return ResponseEntity.status(404).body("Descuento no encontrado");
        }

        if (cuponRepository.existsByCodigoIgnoreCase(cuponNuevo.getCodigo())) {
            return ResponseEntity.status(409).body("Ya existe un cupón con ese código");
        }

        if (cuponRepository.existsByDescuento_IdDescuento(idDescuento)) {
            return ResponseEntity.status(409).body("El descuento ya se encuentra asignado a otro cupón");
        }

        if (cuponNuevo.getFechaFin().isBefore(cuponNuevo.getFechaInicio())) {
            return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");
        }

        Promocion promocion = promocionEncontrada.get();

        if (cuponNuevo.getFechaInicio().isBefore(promocion.getFechaInicio()) || cuponNuevo.getFechaFin().isAfter(promocion.getFechaFin())) {

            return ResponseEntity.status(400).body("La vigencia del cupón debe estar dentro de la vigencia de la promoción");
        }

        String estado = cuponNuevo.getEstado().toUpperCase();

        if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) {
            return ResponseEntity.status(400).body("El estado del cupón debe ser ACTIVO o INACTIVO");
        }

        cuponNuevo.setCodigo(cuponNuevo.getCodigo().toUpperCase());
        cuponNuevo.setEstado(estado);
        cuponNuevo.setUsosActuales(0);
        cuponNuevo.setPromocion(promocion);
        cuponNuevo.setDescuento(descuentoEncontrado.get());

        cuponRepository.save(cuponNuevo);

        return ResponseEntity.status(201).body("Cupón registrado correctamente");
    }

    // ACTUALIZA DATOS DEL CUPÓN
    public ResponseEntity<String> actualizarCupon(Long idCupon, Cupon cuponActualizado) {

        Optional<Cupon> cuponEncontrado = cuponRepository.findById(idCupon);

        if (cuponEncontrado.isEmpty()) {
            return ResponseEntity.status(404).body("Cupón no encontrado");
        }

        Cupon cupon = cuponEncontrado.get();

        if (!cupon.getCodigo().equalsIgnoreCase(cuponActualizado.getCodigo()) && cuponRepository.existsByCodigoIgnoreCase(cuponActualizado.getCodigo())) {
            return ResponseEntity.status(409).body("Ya existe otro cupón con ese código");
        }

        if (cuponActualizado.getFechaFin().isBefore(cuponActualizado.getFechaInicio())) {
            return ResponseEntity.status(400).body("La fecha de término no puede ser anterior a la fecha de inicio");
        }

        Promocion promocion = cupon.getPromocion();

        if (cuponActualizado.getFechaInicio().isBefore(promocion.getFechaInicio()) || cuponActualizado.getFechaFin().isAfter(promocion.getFechaFin())) {

            return ResponseEntity.status(400).body("La vigencia del cupón debe estar dentro de la vigencia de la promoción");
        }

        String estado = cuponActualizado.getEstado().toUpperCase();

        if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) {
            return ResponseEntity.status(400).body("El estado del cupón debe ser ACTIVO o INACTIVO");
        }


        cupon.setCodigo(cuponActualizado.getCodigo().toUpperCase());
        cupon.setDescripcion(cuponActualizado.getDescripcion());
        cupon.setFechaInicio(cuponActualizado.getFechaInicio());
        cupon.setFechaFin(cuponActualizado.getFechaFin());
        cupon.setEstado(estado);
        cupon.setUsoMaximo(cuponActualizado.getUsoMaximo());

        cuponRepository.save(cupon);

        return ResponseEntity.status(200).body("Cupón actualizado correctamente");
    }

    // ACTIVAR O DESACTIVAR UN CUPÓN
    public ResponseEntity<String> cambiarEstadoCupon(Long idCupon, String estado) {

        Optional<Cupon> cuponEncontrado = cuponRepository.findById(idCupon);

        if (cuponEncontrado.isEmpty()) {
            return ResponseEntity.status(404).body("Cupón no encontrado");
        }

        String estadoNuevo = estado.toUpperCase();

        if (!estadoNuevo.equals("ACTIVO") && !estadoNuevo.equals("INACTIVO")) {
            return ResponseEntity.status(400).body("El estado del cupón debe ser ACTIVO o INACTIVO");
        }

        Cupon cupon = cuponEncontrado.get();
        cupon.setEstado(estadoNuevo);

        cuponRepository.save(cupon);

        return ResponseEntity.status(200).body("Estado del cupón actualizado correctamente");
    }

    // ELIMINAR UN CUPÓN
    public ResponseEntity<String> eliminarCupon(Long idCupon) {

        if (!cuponRepository.existsById(idCupon)) {
            return ResponseEntity.status(404).body("Cupón no encontrado");
        }

        cuponRepository.deleteById(idCupon);

        return ResponseEntity.status(200).body("Cupón eliminado correctamente");
    }
}