package duoc.rocio.cupones.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import duoc.rocio.cupones.dto.AplicarCuponDTO;
import duoc.rocio.cupones.dto.PedidoDTO;
import duoc.rocio.cupones.dto.ResultadoCuponDTO;
import duoc.rocio.cupones.model.Cupon;
import duoc.rocio.cupones.model.ValidacionCupon;
import duoc.rocio.cupones.repository.CuponRepository;
import duoc.rocio.cupones.repository.ValidacionCuponRepository;
import duoc.rocio.cupones.service.ValidacionCuponService;

public class ValidacionCuponServiceTest {

    @Mock
    private ValidacionCuponRepository validacionCuponRepository;

    @Mock
    private CuponRepository cuponRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ValidacionCuponService validacionCuponService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // MÉTODO AUXILIAR PARA GENERAR UN CUPÓN VÁLIDO RÁPIDAMENTE
    private Cupon crearCuponValido(String tipoDescuento, double valorDescuento) {
        duoc.rocio.cupones.model.Promocion promo = new duoc.rocio.cupones.model.Promocion();
        promo.setActiva(true);

        duoc.rocio.cupones.model.Descuento desc = new duoc.rocio.cupones.model.Descuento();
        desc.setTipoDescuento(tipoDescuento);
        desc.setValor(valorDescuento);

        Cupon cupon = new Cupon();
        cupon.setIdCupon(1L);
        cupon.setCodigo("FIESTA2026");
        cupon.setEstado("ACTIVO");
        cupon.setPromocion(promo);
        cupon.setDescuento(desc);
        cupon.setFechaInicio(LocalDate.now().minusDays(1));
        cupon.setFechaFin(LocalDate.now().plusDays(10));
        cupon.setUsoMaximo(100);
        cupon.setUsosActuales(0);
        return cupon;
    }


    @Test
    void obtenerValidaciones_ListaLlena() {
        ValidacionCupon v1 = new ValidacionCupon();
        v1.setResultado("APROBADO");
        
        when(validacionCuponRepository.findAllByOrderByFechaValidacionDesc()).thenReturn(List.of(v1));

        List<ValidacionCupon> resultado = validacionCuponService.obtenerValidaciones();

        assertEquals(1, resultado.size());
        verify(validacionCuponRepository, times(1)).findAllByOrderByFechaValidacionDesc();
    }

    @Test
    void obtenerValidaciones_ListaVacia() {
        when(validacionCuponRepository.findAllByOrderByFechaValidacionDesc()).thenReturn(new ArrayList<>());

        List<ValidacionCupon> resultado = validacionCuponService.obtenerValidaciones();

        assertTrue(resultado.isEmpty());
        verify(validacionCuponRepository, times(1)).findAllByOrderByFechaValidacionDesc();
    }

    @Test
    void obtenerValidacionPorId_Existe() {
        ValidacionCupon v1 = new ValidacionCupon();
        v1.setIdValidacion(1L);
        v1.setResultado("RECHAZADO");
        
        when(validacionCuponRepository.findById(1L)).thenReturn(Optional.of(v1));

        Optional<ValidacionCupon> resultado = validacionCuponService.obtenerValidacionPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("RECHAZADO", resultado.get().getResultado());
        verify(validacionCuponRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerValidacionPorId_NoExiste() {
        when(validacionCuponRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ValidacionCupon> resultado = validacionCuponService.obtenerValidacionPorId(99L);

        assertTrue(resultado.isEmpty());
        verify(validacionCuponRepository, times(1)).findById(99L);
    }

    @Test
    void obtenerValidacionesPorCliente_Exito() {
        ValidacionCupon v1 = new ValidacionCupon();
        v1.setIdCliente(5L);
        
        when(validacionCuponRepository.findByIdClienteOrderByFechaValidacionDesc(5L)).thenReturn(List.of(v1));

        List<ValidacionCupon> resultado = validacionCuponService.obtenerValidacionesPorCliente(5L);

        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdCliente());
        verify(validacionCuponRepository, times(1)).findByIdClienteOrderByFechaValidacionDesc(5L);
    }

    @Test
    void obtenerValidacionesPorPedido_Exito() {
        ValidacionCupon v1 = new ValidacionCupon();
        v1.setIdPedido(10L);
        
        when(validacionCuponRepository.findByIdPedidoOrderByFechaValidacionDesc(10L)).thenReturn(List.of(v1));

        List<ValidacionCupon> resultado = validacionCuponService.obtenerValidacionesPorPedido(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdPedido());
        verify(validacionCuponRepository, times(1)).findByIdPedidoOrderByFechaValidacionDesc(10L);
    }

    @Test
    void obtenerValidacionesPorCupon_Exito() {
        ValidacionCupon v1 = new ValidacionCupon();
        v1.setResultado("APROBADO");
        
        when(validacionCuponRepository.findByCupon_IdCuponOrderByFechaValidacionDesc(3L)).thenReturn(List.of(v1));

        List<ValidacionCupon> resultado = validacionCuponService.obtenerValidacionesPorCupon(3L);

        assertEquals(1, resultado.size());
        verify(validacionCuponRepository, times(1)).findByCupon_IdCuponOrderByFechaValidacionDesc(3L);
    }
    @Test
    void aplicarCupon_CuponNoEncontrado() {
        AplicarCuponDTO dto = new AplicarCuponDTO("INEXISTENTE", 1L, 5L);
        when(cuponRepository.findByCodigoIgnoreCase("INEXISTENTE")).thenReturn(Optional.empty());

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("ERROR_404", resultado.getResultado());
        assertEquals("Cupón no encontrado", resultado.getMensaje());
        verify(validacionCuponRepository, times(0)).save(any());
    }

    @Test
    void aplicarCupon_CuponInactivo() {
        AplicarCuponDTO dto = new AplicarCuponDTO("DESC10", 1L, 5L);
        Cupon cupon = new Cupon();
        cupon.setEstado("INACTIVO");

        when(cuponRepository.findByCodigoIgnoreCase("DESC10")).thenReturn(Optional.of(cupon));

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        assertEquals("El cupón se encuentra inactivo", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_PromocionInactiva() {
        AplicarCuponDTO dto = new AplicarCuponDTO("DESC10", 1L, 5L);
        
        duoc.rocio.cupones.model.Promocion promo = new duoc.rocio.cupones.model.Promocion();
        promo.setActiva(false);
        
        Cupon cupon = new Cupon();
        cupon.setEstado("ACTIVO");
        cupon.setPromocion(promo);

        when(cuponRepository.findByCodigoIgnoreCase("DESC10")).thenReturn(Optional.of(cupon));

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        assertEquals("La promoción del cupón se encuentra inactiva", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_FueraDeVigencia_AntesDeInicio() {
        AplicarCuponDTO dto = new AplicarCuponDTO("DESC10", 1L, 5L);
        
        duoc.rocio.cupones.model.Promocion promo = new duoc.rocio.cupones.model.Promocion();
        promo.setActiva(true);
        
        Cupon cupon = new Cupon();
        cupon.setEstado("ACTIVO");
        cupon.setPromocion(promo);
        cupon.setFechaInicio(LocalDate.now().plusDays(5));
        cupon.setFechaFin(LocalDate.now().plusDays(10));

        when(cuponRepository.findByCodigoIgnoreCase("DESC10")).thenReturn(Optional.of(cupon));

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        assertEquals("El cupón se encuentra fuera de vigencia", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_FueraDeVigencia_DespuesDeFin() {
        AplicarCuponDTO dto = new AplicarCuponDTO("DESC10", 1L, 5L);
        
        duoc.rocio.cupones.model.Promocion promo = new duoc.rocio.cupones.model.Promocion();
        promo.setActiva(true);
        
        Cupon cupon = new Cupon();
        cupon.setEstado("ACTIVO");
        cupon.setPromocion(promo);
        cupon.setFechaInicio(LocalDate.now().minusDays(10));
        cupon.setFechaFin(LocalDate.now().minusDays(2));

        when(cuponRepository.findByCodigoIgnoreCase("DESC10")).thenReturn(Optional.of(cupon));

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        assertEquals("El cupón se encuentra fuera de vigencia", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_LimiteUsosAlcanzado() {
        AplicarCuponDTO dto = new AplicarCuponDTO("DESC10", 1L, 5L);
        
        duoc.rocio.cupones.model.Promocion promo = new duoc.rocio.cupones.model.Promocion();
        promo.setActiva(true);
        
        Cupon cupon = new Cupon();
        cupon.setEstado("ACTIVO");
        cupon.setPromocion(promo);
        cupon.setFechaInicio(LocalDate.now().minusDays(1));
        cupon.setFechaFin(LocalDate.now().plusDays(10));
        cupon.setUsoMaximo(100);
        cupon.setUsosActuales(100);

        when(cuponRepository.findByCodigoIgnoreCase("DESC10")).thenReturn(Optional.of(cupon));

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        assertEquals("El cupón alcanzó su límite máximo de usos", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_YaAplicadoAlPedido() {
        AplicarCuponDTO dto = new AplicarCuponDTO("DESC10", 1L, 5L);
        
        duoc.rocio.cupones.model.Promocion promo = new duoc.rocio.cupones.model.Promocion();
        promo.setActiva(true);
        
        Cupon cupon = new Cupon();
        cupon.setIdCupon(3L);
        cupon.setEstado("ACTIVO");
        cupon.setPromocion(promo);
        cupon.setFechaInicio(LocalDate.now().minusDays(1));
        cupon.setFechaFin(LocalDate.now().plusDays(10));
        cupon.setUsoMaximo(100);
        cupon.setUsosActuales(10);

        when(cuponRepository.findByCodigoIgnoreCase("DESC10")).thenReturn(Optional.of(cupon));
        when(validacionCuponRepository.existsByCupon_IdCuponAndIdPedidoAndResultadoIgnoreCase(3L, 1L, "APROBADO")).thenReturn(true);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        assertEquals("El cupón ya fue aplicado a este pedido", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }


    @Test
    void aplicarCupon_PedidoNoEncontrado_404() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(crearCuponValido("PORCENTAJE", 10.0)));
        when(validacionCuponRepository.existsByCupon_IdCuponAndIdPedidoAndResultadoIgnoreCase(anyLong(), anyLong(), anyString())).thenReturn(false);

        org.springframework.http.HttpStatus status = org.springframework.http.HttpStatus.NOT_FOUND;
        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenThrow(org.springframework.web.client.HttpClientErrorException.create(status, "Not Found", null, null, null));

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("ERROR_404", resultado.getResultado());
        assertEquals("Pedido no encontrado", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_MicroservicioCaido_503() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(crearCuponValido("PORCENTAJE", 10.0)));
        when(validacionCuponRepository.existsByCupon_IdCuponAndIdPedidoAndResultadoIgnoreCase(anyLong(), anyLong(), anyString())).thenReturn(false);

        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenThrow(new org.springframework.web.client.RestClientException("Connection refused"));

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("ERROR_503", resultado.getResultado());
        assertEquals("No fue posible comunicarse con el microservicio Pedidos", resultado.getMensaje());
    }

    @Test
    void aplicarCupon_PedidoVacio() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(crearCuponValido("PORCENTAJE", 10.0)));
        when(validacionCuponRepository.existsByCupon_IdCuponAndIdPedidoAndResultadoIgnoreCase(anyLong(), anyLong(), anyString())).thenReturn(false);

        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenReturn(null);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("ERROR_404", resultado.getResultado());
        assertEquals("No se pudo obtener la información del pedido", resultado.getMensaje());
    }

    @Test
    void aplicarCupon_ClienteIncorrecto() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(crearCuponValido("PORCENTAJE", 10.0)));
        
        PedidoDTO pedido = new PedidoDTO(1L, 99L, "PENDIENTE", 10000.0);
        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenReturn(pedido);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        assertEquals("El pedido no pertenece al cliente indicado", resultado.getMensaje());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_TotalInvalido_Nulo() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(crearCuponValido("PORCENTAJE", 10.0)));
        
        PedidoDTO pedido = new PedidoDTO(1L, 5L, "PENDIENTE", null);
        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenReturn(pedido);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_TotalInvalido_Cero() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(crearCuponValido("PORCENTAJE", 10.0)));
        
        PedidoDTO pedido = new PedidoDTO(1L, 5L, "PENDIENTE", 0.0);
        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenReturn(pedido);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("RECHAZADO", resultado.getResultado());
        verify(validacionCuponRepository, times(1)).save(any(ValidacionCupon.class));
    }

    @Test
    void aplicarCupon_Exito_Porcentaje() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        Cupon cupon = crearCuponValido("PORCENTAJE", 20.0);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(cupon));
        
        PedidoDTO pedido = new PedidoDTO(1L, 5L, "PENDIENTE", 10000.0);
        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenReturn(pedido);

        ValidacionCupon validacionGuardada = new ValidacionCupon();
        validacionGuardada.setIdValidacion(99L);
        when(validacionCuponRepository.save(any(ValidacionCupon.class))).thenReturn(validacionGuardada);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("APROBADO", resultado.getResultado());
        assertEquals(10000.0, resultado.getMontoOriginal());
        assertEquals(2000.0, resultado.getMontoDescuento());
        assertEquals(8000.0, resultado.getMontoFinal());
        assertEquals(1, cupon.getUsosActuales());
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void aplicarCupon_Exito_MontoFijo() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);
        Cupon cupon = crearCuponValido("MONTO_FIJO", 5000.0);
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(cupon));
        
        PedidoDTO pedido = new PedidoDTO(1L, 5L, "PENDIENTE", 15000.0);
        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenReturn(pedido);

        ValidacionCupon validacionGuardada = new ValidacionCupon();
        validacionGuardada.setIdValidacion(99L);
        when(validacionCuponRepository.save(any(ValidacionCupon.class))).thenReturn(validacionGuardada);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("APROBADO", resultado.getResultado());
        assertEquals(5000.0, resultado.getMontoDescuento());
        assertEquals(10000.0, resultado.getMontoFinal());
    }

    @Test
    void aplicarCupon_Exito_MontoFijo_ExcedeTotal() {
        AplicarCuponDTO dto = new AplicarCuponDTO("FIESTA2026", 1L, 5L);

        Cupon cupon = crearCuponValido("MONTO_FIJO", 20000.0); 
        when(cuponRepository.findByCodigoIgnoreCase("FIESTA2026")).thenReturn(Optional.of(cupon));
        
        PedidoDTO pedido = new PedidoDTO(1L, 5L, "PENDIENTE", 15000.0);
        when(restTemplate.getForObject("http://localhost:8084/pedidos/1", PedidoDTO.class)).thenReturn(pedido);

        ValidacionCupon validacionGuardada = new ValidacionCupon();
        validacionGuardada.setIdValidacion(99L);
        when(validacionCuponRepository.save(any(ValidacionCupon.class))).thenReturn(validacionGuardada);

        ResultadoCuponDTO resultado = validacionCuponService.aplicarCupon(dto);

        assertEquals("APROBADO", resultado.getResultado());

        assertEquals(15000.0, resultado.getMontoDescuento()); 
        assertEquals(0.0, resultado.getMontoFinal());
    }
}