package duoc.rocio.cupones.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import duoc.rocio.cupones.dto.AplicarCuponDTO;
import duoc.rocio.cupones.dto.PedidoDTO;
import duoc.rocio.cupones.dto.ResultadoCuponDTO;
import duoc.rocio.cupones.model.Cupon;
import duoc.rocio.cupones.model.Descuento;
import duoc.rocio.cupones.model.ValidacionCupon;
import duoc.rocio.cupones.repository.CuponRepository;
import duoc.rocio.cupones.repository.ValidacionCuponRepository;

@Service
public class ValidacionCuponService {

    @Autowired
    private ValidacionCuponRepository validacionCuponRepository;

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private RestTemplate restTemplate;

    // OBTIENE TODAS LAS VALIDACIONES
    public List<ValidacionCupon> obtenerValidaciones() {
        return validacionCuponRepository.findAllByOrderByFechaValidacionDesc();
    }

    // OBTIENE UNA VALIDACIÓN POR SU ID
    public Optional<ValidacionCupon> obtenerValidacionPorId(Long idValidacion) {
        return validacionCuponRepository.findById(idValidacion);
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS POR UN CLIENTE
    public List<ValidacionCupon> obtenerValidacionesPorCliente(Long idCliente) {
        return validacionCuponRepository.findByIdClienteOrderByFechaValidacionDesc(idCliente);
    }

    // OBTIENE LAS VALIDACIONES POR PEDIDO
    public List<ValidacionCupon> obtenerValidacionesPorPedido(Long idPedido) {
        return validacionCuponRepository.findByIdPedidoOrderByFechaValidacionDesc(idPedido);
    }

    // OBTIENE LAS VALIDACIONES REALIZADAS POR CUPÓN
    public List<ValidacionCupon> obtenerValidacionesPorCupon(Long idCupon) {
        return validacionCuponRepository.findByCupon_IdCuponOrderByFechaValidacionDesc(idCupon);
    }

    // APLICA UN CUPÓN A UN PEDIDO (Retorna el DTO con el estado de la operación)
    @Transactional
    public ResultadoCuponDTO aplicarCupon(AplicarCuponDTO datosSolicitud) {

        Optional<Cupon> cuponEncontrado = cuponRepository.findByCodigoIgnoreCase(datosSolicitud.getCodigo());

        if (cuponEncontrado.isEmpty()) {
            return crearRespuestaError("ERROR_404", "Cupón no encontrado");
        }

        Cupon cupon = cuponEncontrado.get();

        if (!cupon.getEstado().equalsIgnoreCase("ACTIVO")) {
            guardarValidacionRechazada(cupon, datosSolicitud, "El cupón se encuentra inactivo");
            return crearRespuestaError("RECHAZADO", "El cupón se encuentra inactivo");
        }

        if (!cupon.getPromocion().isActiva()) {
            guardarValidacionRechazada(cupon, datosSolicitud, "La promoción del cupón se encuentra inactiva");
            return crearRespuestaError("RECHAZADO", "La promoción del cupón se encuentra inactiva");
        }

        LocalDate fechaActual = LocalDate.now();

        if (fechaActual.isBefore(cupon.getFechaInicio()) || fechaActual.isAfter(cupon.getFechaFin())) {
            guardarValidacionRechazada(cupon, datosSolicitud, "El cupón se encuentra fuera de vigencia");
            return crearRespuestaError("RECHAZADO", "El cupón se encuentra fuera de vigencia");
        }

        if (cupon.getUsosActuales() >= cupon.getUsoMaximo()) {
            guardarValidacionRechazada(cupon, datosSolicitud, "El cupón alcanzó su límite máximo de usos");
            return crearRespuestaError("RECHAZADO", "El cupón alcanzó su límite máximo de usos");
        }

        boolean cuponYaAplicado = validacionCuponRepository.existsByCupon_IdCuponAndIdPedidoAndResultadoIgnoreCase(cupon.getIdCupon(), datosSolicitud.getIdPedido(),"APROBADO");

        if (cuponYaAplicado) {
            guardarValidacionRechazada(cupon, datosSolicitud, "El cupón ya fue aplicado a este pedido");
            return crearRespuestaError("RECHAZADO", "El cupón ya fue aplicado a este pedido");
        }

        PedidoDTO pedido;

        try {
            String urlPedido = "http://localhost:8084/pedidos/" + datosSolicitud.getIdPedido();
            pedido = restTemplate.getForObject(urlPedido, PedidoDTO.class);

        } catch (HttpClientErrorException.NotFound error) {
            guardarValidacionRechazada(cupon, datosSolicitud, "Pedido no encontrado");
            return crearRespuestaError("ERROR_404", "Pedido no encontrado");
        } catch (RestClientException error) {
            return crearRespuestaError("ERROR_503", "No fue posible comunicarse con el microservicio Pedidos");
        }

        if (pedido == null) {
            return crearRespuestaError("ERROR_404", "No se pudo obtener la información del pedido");
        }

        if (!datosSolicitud.getIdCliente().equals(pedido.getIdCliente())) {
            guardarValidacionRechazada(cupon, datosSolicitud, "El pedido no pertenece al cliente indicado");
            return crearRespuestaError("RECHAZADO", "El pedido no pertenece al cliente indicado");
        }

        if (pedido.getTotal() == null || pedido.getTotal() <= 0) {
            guardarValidacionRechazada(cupon, datosSolicitud, "El pedido no tiene un monto válido para aplicar descuento");
            return crearRespuestaError("RECHAZADO", "El pedido no tiene un monto válido para aplicar descuento");
        }

        double montoOriginal = pedido.getTotal();
        double montoDescuento = calcularDescuento(cupon.getDescuento(), montoOriginal);
        double montoFinal = montoOriginal - montoDescuento;

        ValidacionCupon validacion = new ValidacionCupon();
        validacion.setIdCliente(datosSolicitud.getIdCliente());
        validacion.setIdPedido(datosSolicitud.getIdPedido());
        validacion.setFechaValidacion(LocalDateTime.now());
        validacion.setResultado("APROBADO");
        validacion.setMotivoRechazo(null);
        validacion.setMontoOriginal(montoOriginal);
        validacion.setMontoDescuento(montoDescuento);
        validacion.setMontoFinal(montoFinal);
        validacion.setCupon(cupon);

        ValidacionCupon validacionGuardada = validacionCuponRepository.save(validacion);

        cupon.setUsosActuales(cupon.getUsosActuales() + 1);
        cuponRepository.save(cupon);

        ResultadoCuponDTO resultado = new ResultadoCuponDTO();
        resultado.setIdValidacion(validacionGuardada.getIdValidacion());
        resultado.setIdPedido(datosSolicitud.getIdPedido());
        resultado.setIdCliente(datosSolicitud.getIdCliente());
        resultado.setCodigoCupon(cupon.getCodigo());
        resultado.setMontoOriginal(montoOriginal);
        resultado.setMontoDescuento(montoDescuento);
        resultado.setMontoFinal(montoFinal);
        resultado.setResultado("APROBADO");
        resultado.setMensaje("Cupón aplicado correctamente");

        return resultado;
    }

    // MÉTODO AUXILIAR PARA GENERAR ERRORES LIMPIOS
    private ResultadoCuponDTO crearRespuestaError(String estado, String mensaje) {
        ResultadoCuponDTO error = new ResultadoCuponDTO();
        error.setResultado(estado);
        error.setMensaje(mensaje);
        return error;
    }

    // GUARDA UN INTENTO FALLIDO DE APLICAR UN CUPÓN
    private void guardarValidacionRechazada(Cupon cupon, AplicarCuponDTO datosSolicitud, String motivo) {
        ValidacionCupon validacion = new ValidacionCupon();
        validacion.setIdCliente(datosSolicitud.getIdCliente());
        validacion.setIdPedido(datosSolicitud.getIdPedido());
        validacion.setFechaValidacion(LocalDateTime.now());
        validacion.setResultado("RECHAZADO");
        validacion.setMotivoRechazo(motivo);
        validacion.setMontoOriginal(null);
        validacion.setMontoDescuento(null);
        validacion.setMontoFinal(null);
        validacion.setCupon(cupon);

        validacionCuponRepository.save(validacion);
    }

    // CALCULA EL DESCUENTO SEGÚN EL TIPO DE CUPÓN ASOCIADO
    private double calcularDescuento(Descuento descuento, double montoOriginal) {
        double montoDescuento;

        if (descuento.getTipoDescuento().equalsIgnoreCase("PORCENTAJE")) {
            montoDescuento = montoOriginal * descuento.getValor() / 100;
        } else {
            montoDescuento = descuento.getValor();
        }

        if (montoDescuento > montoOriginal) {
            montoDescuento = montoOriginal;
        }

        return Math.round(montoDescuento * 100.0) / 100.0;
    }
}