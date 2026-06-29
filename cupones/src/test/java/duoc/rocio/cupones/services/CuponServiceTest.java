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

import duoc.rocio.cupones.model.Cupon;
import duoc.rocio.cupones.model.Descuento;
import duoc.rocio.cupones.model.Promocion;
import duoc.rocio.cupones.repository.CuponRepository;
import duoc.rocio.cupones.repository.DescuentoRepository;
import duoc.rocio.cupones.repository.PromocionRepository;
import duoc.rocio.cupones.service.CuponService;

public class CuponServiceTest {
        
    @Mock
    private CuponRepository cuponRepository;

    @Mock
    private PromocionRepository promocionRepository;

    @Mock
    private DescuentoRepository descuentoRepository;

    @InjectMocks
    private CuponService cuponService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerCupones_ListaLlena() {
        // Preparación
        Cupon c1 = new Cupon();
        c1.setCodigo("VERANO2026");

        Cupon c2 = new Cupon();
        c2.setCodigo("INVIERNO2026");
        
        List<Cupon> listaCupones = List.of(c1, c2);

        // Configuración
        when(cuponRepository.findAll()).thenReturn(listaCupones);

        // Testeo
        List<Cupon> resultado = cuponService.obtenerCupones();

        // Verificación
        assertEquals(2, resultado.size());
        assertEquals("VERANO2026", resultado.get(0).getCodigo());
        verify(cuponRepository, times(1)).findAll();
    }

    @Test
    void obtenerCupones_ListaVacia() {
        // Configuración
        when(cuponRepository.findAll()).thenReturn(new ArrayList<>());

        // Testeo
        List<Cupon> resultado = cuponService.obtenerCupones();

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(cuponRepository, times(1)).findAll();
    }

    @Test
    void obtenerCuponesPorPromocion_Existe() {
        // Preparación
        Cupon c1 = new Cupon();
        c1.setCodigo("PROMO10");
        
        when(cuponRepository.findByPromocion_IdPromocion(1L)).thenReturn(List.of(c1));

        // Testeo
        List<Cupon> resultado = cuponService.obtenerCuponesPorPromocion(1L);

        // Verificación
        assertEquals(1, resultado.size());
        assertEquals("PROMO10", resultado.get(0).getCodigo());
        verify(cuponRepository, times(1)).findByPromocion_IdPromocion(1L);
    }

    @Test
    void obtenerCuponesPorPromocion_Vacia() {
        // Configuración
        when(cuponRepository.findByPromocion_IdPromocion(99L)).thenReturn(new ArrayList<>());

        // Testeo
        List<Cupon> resultado = cuponService.obtenerCuponesPorPromocion(99L);

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(cuponRepository, times(1)).findByPromocion_IdPromocion(99L);
    }

    @Test
    void obtenerCuponPorId_Existe() {
        // Preparación
        Cupon cupon = new Cupon();
        cupon.setIdCupon(1L);
        cupon.setCodigo("DESCUENTO20");

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cupon));

        // Testeo
        Optional<Cupon> resultado = cuponService.obtenerCuponPorId(1L);

        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals("DESCUENTO20", resultado.get().getCodigo());
        verify(cuponRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerCuponPorId_NoExiste() {
        // Configuración
        when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

        // Testeo
        Optional<Cupon> resultado = cuponService.obtenerCuponPorId(99L);

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(cuponRepository, times(1)).findById(99L);
    }

    @Test
    void buscarPorCodigo_Encontrado() {
        // Preparación
        Cupon cupon = new Cupon();
        cupon.setCodigo("NAVIDAD2026");
        
        // Configuración
        when(cuponRepository.findByCodigoIgnoreCase("NAVIDAD2026")).thenReturn(Optional.of(cupon));

        // Testeo
        Optional<Cupon> resultado = cuponService.buscarPorCodigo("NAVIDAD2026");

        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals("NAVIDAD2026", resultado.get().getCodigo());
        verify(cuponRepository, times(1)).findByCodigoIgnoreCase("NAVIDAD2026");
        }

    @Test
    void buscarPorCodigo_NoEncontrado() {
        // Configuración
        when(cuponRepository.findByCodigoIgnoreCase("INVALIDO")).thenReturn(Optional.empty());

        // Testeo
        Optional<Cupon> resultado = cuponService.buscarPorCodigo("INVALIDO");

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(cuponRepository, times(1)).findByCodigoIgnoreCase("INVALIDO");
    }

    @Test
    void buscarPorEstado_Encontrados() {
        // Preparación
        Cupon c1 = new Cupon();
        c1.setCodigo("ACTIVO1");
        c1.setEstado("ACTIVO");

        // Configuración
        when(cuponRepository.findByEstadoIgnoreCase("ACTIVO")).thenReturn(List.of(c1));

        // Testeo
        List<Cupon> resultado = cuponService.buscarPorEstado("ACTIVO");

        // Verificación
        assertEquals(1, resultado.size());
        assertEquals("ACTIVO", resultado.get(0).getEstado());
        verify(cuponRepository, times(1)).findByEstadoIgnoreCase("ACTIVO");
    }

    @Test
    void buscarPorEstado_Vacia() {
        // Preparación
        // Configuración
        when(cuponRepository.findByEstadoIgnoreCase("SUSPENDIDO")).thenReturn(new ArrayList<>());

        // Testeo
        List<Cupon> resultado = cuponService.buscarPorEstado("SUSPENDIDO");

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(cuponRepository, times(1)).findByEstadoIgnoreCase("SUSPENDIDO");
    }

    @Test
    void guardarCupon_PromocionNoEncontrada() {
        // Preparación
        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.empty());

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, new Cupon());

        // Verificación
        assertEquals(1, resultado);
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void guardarCupon_DescuentoNoEncontrado() {
        // Preparación
        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(new Promocion()));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, new Cupon());

        // Verificación
        assertEquals(2, resultado);
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void guardarCupon_CodigoDuplicado() {
        // Preparación
        Cupon cuponExistente = new Cupon();
        cuponExistente.setCodigo("OFERTA10");

        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(new Promocion()));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));
        when(cuponRepository.existsByCodigoIgnoreCase("OFERTA10")).thenReturn(true);

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, cuponExistente);

        // Verificación
        assertEquals(3, resultado);
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void guardarCupon_DescuentoYaAsignado() {
        // Preparación
        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(new Promocion()));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));
        when(cuponRepository.existsByCodigoIgnoreCase(anyString())).thenReturn(false);
        when(cuponRepository.existsByDescuento_IdDescuento(1L)).thenReturn(true);

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, new Cupon());

        // Verificación
        assertEquals(4, resultado);
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void guardarCupon_FechaFinAnteriorAInicio() {
        // Preparación
        Cupon cupon = new Cupon();
        cupon.setFechaInicio(LocalDate.now().plusDays(5));
        cupon.setFechaFin(LocalDate.now());

        when(promocionRepository.findById(1L)).thenReturn(Optional.of(new Promocion()));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, cupon);

        // Verificación
        assertEquals(5, resultado);
    }

    @Test
    void guardarCupon_FueraDeVigenciaPromocion() {
        // Preparación
        Promocion promo = new Promocion();
        promo.setFechaInicio(LocalDate.now());
        promo.setFechaFin(LocalDate.now().plusDays(10));

        Cupon cupon = new Cupon();
        cupon.setFechaInicio(LocalDate.now().minusDays(1));
        cupon.setFechaFin(LocalDate.now().plusDays(5));

        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, cupon);

        // Verificación
        assertEquals(6, resultado);
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void guardarCupon_FueraDeVigenciaPromocion_PorFechaFin() {
        // Preparación
        Promocion promo = new Promocion();
        promo.setFechaInicio(LocalDate.now());
        promo.setFechaFin(LocalDate.now().plusDays(10));

        Cupon cupon = new Cupon();
        cupon.setFechaInicio(LocalDate.now().plusDays(2)); // Inicio VÁLIDO
        cupon.setFechaFin(LocalDate.now().plusDays(15));   // Fin INVÁLIDO (después de la promo)

        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, cupon);

        // Verificación
        assertEquals(6, resultado, "Debería fallar por la fecha de fin");
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void guardarCupon_EstadoInvalido() {
        // Preparación
        Promocion promo = new Promocion(1L, "Promoción 1", "Descripción 1", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Cupon cupon = new Cupon();
        cupon.setFechaInicio(LocalDate.now());
        cupon.setFechaFin(LocalDate.now().plusDays(5));
        cupon.setEstado("PENDIENTE");

        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, cupon);

        // Verificación
        assertEquals(7, resultado);
        verify(cuponRepository, times(0)).save(any());
    }


    @Test
    void guardarCupon_Exito_Inactivo() {
        // Preparación
        Promocion promo = new Promocion(1L, "Promoción 1", "Descripción 1", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Cupon cupon = new Cupon();
        cupon.setCodigo("INVIERNO26");
        cupon.setEstado("INACTIVO");
        cupon.setFechaInicio(LocalDate.now());
        cupon.setFechaFin(LocalDate.now().plusDays(5));

        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));
        when(cuponRepository.existsByCodigoIgnoreCase("INVIERNO26")).thenReturn(false);
        when(cuponRepository.existsByDescuento_IdDescuento(1L)).thenReturn(false);

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, cupon);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("INACTIVO", cupon.getEstado());
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void guardarCupon_Exito() {
        // Preparación
        Promocion promo = new Promocion(1L, "Promoción 1", "Descripción 1", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Cupon cupon = new Cupon();
        cupon.setCodigo("FESTIVAL2026");
        cupon.setEstado("ACTIVO");
        cupon.setFechaInicio(LocalDate.now());
        cupon.setFechaFin(LocalDate.now().plusDays(5));

        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(new Descuento()));
        when(cuponRepository.existsByCodigoIgnoreCase("FESTIVAL2026")).thenReturn(false);
        when(cuponRepository.existsByDescuento_IdDescuento(1L)).thenReturn(false);

        // Testeo
        int resultado = cuponService.guardarCupon(1L, 1L, cupon);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("ACTIVO", cupon.getEstado());
        assertEquals("FESTIVAL2026", cupon.getCodigo());
        assertEquals(0, cupon.getUsosActuales());
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void actualizarCupon_NoEncontrado() {
        // Configuración
        when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

        // Testeo
        int resultado = cuponService.actualizarCupon(99L, new Cupon());

        // Verificación
        assertEquals(1, resultado, "Debería retornar 1 (Cupón no encontrado)");
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void actualizarCupon_CodigoYaExiste() {
        // Preparación
        Cupon cuponOriginal = new Cupon();
        cuponOriginal.setCodigo("ANTIGUO");
        
        Cupon cuponActualizado = new Cupon();
        cuponActualizado.setCodigo("EXISTENTE");

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponOriginal));
        when(cuponRepository.existsByCodigoIgnoreCase("EXISTENTE")).thenReturn(true);

        // Testeo
        int resultado = cuponService.actualizarCupon(1L, cuponActualizado);

        // Verificación
        assertEquals(2, resultado, "Debería retornar 2 (Código duplicado)");
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void actualizarCupon_FechasInvalidas() {
        // Preparación
        Cupon cuponOriginal = new Cupon();
        cuponOriginal.setCodigo("TEST");
        
        Cupon cuponActualizado = new Cupon();
        cuponActualizado.setCodigo("TEST");
        cuponActualizado.setFechaInicio(LocalDate.now().plusDays(10));
        cuponActualizado.setFechaFin(LocalDate.now());

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponOriginal));

        // Testeo
        int resultado = cuponService.actualizarCupon(1L, cuponActualizado);

        // Verificación
        assertEquals(3, resultado, "Debería retornar 3 (Fecha inválida)");
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void actualizarCupon_FueraDeVigenciaPromocion() {
        // Preparación
        Promocion promo = new Promocion();
        promo.setFechaInicio(LocalDate.now());
        promo.setFechaFin(LocalDate.now().plusDays(10));

        Cupon cuponOriginal = new Cupon();
        cuponOriginal.setCodigo("TEST");
        cuponOriginal.setPromocion(promo);

        Cupon cuponActualizado = new Cupon();
        cuponActualizado.setCodigo("TEST");
        cuponActualizado.setFechaInicio(LocalDate.now().minusDays(5));
        cuponActualizado.setFechaFin(LocalDate.now().plusDays(5));

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponOriginal));

        // Testeo
        int resultado = cuponService.actualizarCupon(1L, cuponActualizado);

        // Verificación
        assertEquals(4, resultado);
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void actualizarCupon_FueraDeVigenciaPromocion_PorFechaFin() {
        // Preparación
        Promocion promo = new Promocion();
        promo.setFechaInicio(LocalDate.now());
        promo.setFechaFin(LocalDate.now().plusDays(10));

        Cupon cuponOriginal = new Cupon();
        cuponOriginal.setCodigo("TEST");
        cuponOriginal.setPromocion(promo);

        Cupon cuponActualizado = new Cupon();
        cuponActualizado.setCodigo("TEST");
        cuponActualizado.setFechaInicio(LocalDate.now().plusDays(2)); 
        cuponActualizado.setFechaFin(LocalDate.now().plusDays(15));   

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponOriginal));

        // Testeo
        int resultado = cuponService.actualizarCupon(1L, cuponActualizado);

        // Verificación
        assertEquals(4, resultado, "Debería fallar por la fecha de fin");
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void actualizarCupon_EstadoInvalido() {
        // Preparación
        Promocion promo = new Promocion(1L, "Promoción 1", "Descripción 1", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Cupon cuponOriginal = new Cupon();
        cuponOriginal.setCodigo("TEST");
        cuponOriginal.setPromocion(promo);

        Cupon cuponActualizado = new Cupon();
        cuponActualizado.setCodigo("TEST");
        cuponActualizado.setFechaInicio(LocalDate.now());
        cuponActualizado.setFechaFin(LocalDate.now().plusDays(5));
        cuponActualizado.setEstado("PENDIENTE");

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponOriginal));

        // Testeo
        int resultado = cuponService.actualizarCupon(1L, cuponActualizado);

        // Verificación
        assertEquals(5, resultado);
    }


    @Test
    void actualizarCupon_Exito_Inactivo() {
        // Preparación
        Promocion promo = new Promocion(1L, "Promoción 1", "Descripción 1", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Cupon cuponOriginal = new Cupon();
        cuponOriginal.setCodigo("ANTIGUO");
        cuponOriginal.setPromocion(promo);

        Cupon cuponActualizado = new Cupon();
        cuponActualizado.setCodigo("NUEVO");
        cuponActualizado.setDescripcion("Nueva desc");
        cuponActualizado.setFechaInicio(LocalDate.now());
        cuponActualizado.setFechaFin(LocalDate.now().plusDays(5));
        cuponActualizado.setEstado("INACTIVO");
        cuponActualizado.setUsoMaximo(100);

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponOriginal));
        when(cuponRepository.existsByCodigoIgnoreCase("NUEVO")).thenReturn(false);

        // Testeo
        int resultado = cuponService.actualizarCupon(1L, cuponActualizado);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("INACTIVO", cuponOriginal.getEstado());
        verify(cuponRepository, times(1)).save(cuponOriginal);
    }

    @Test
    void actualizarCupon_ExitoTotal() {
        // Preparación: Promoción válida
        Promocion promo = new Promocion(1L, "Promoción 1", "Descripción 1", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Cupon cuponOriginal = new Cupon();
        cuponOriginal.setCodigo("ANTIGUO");
        cuponOriginal.setPromocion(promo);

        Cupon cuponActualizado = new Cupon();
        cuponActualizado.setCodigo("NUEVO");
        cuponActualizado.setDescripcion("Nueva desc");
        cuponActualizado.setFechaInicio(LocalDate.now());
        cuponActualizado.setFechaFin(LocalDate.now().plusDays(5));
        cuponActualizado.setEstado("ACTIVO");
        cuponActualizado.setUsoMaximo(100);

        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponOriginal));
        when(cuponRepository.existsByCodigoIgnoreCase("NUEVO")).thenReturn(false);

        // Testeo
        int resultado = cuponService.actualizarCupon(1L, cuponActualizado);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("NUEVO", cuponOriginal.getCodigo());
        assertEquals("ACTIVO", cuponOriginal.getEstado());
        verify(cuponRepository, times(1)).save(cuponOriginal);
    }

    @Test
    void cambiarEstado_Exito() {
        // Preparación
        Cupon cupon = new Cupon();
        cupon.setEstado("INACTIVO");
    
        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cupon));

        // Testeo
        int res = cuponService.cambiarEstadoCupon(1L, "ACTIVO");

        // Verificación
        assertEquals(0, res);
        assertEquals("ACTIVO", cupon.getEstado());
        verify(cuponRepository, times(1)).save(cupon);
    }

    @Test
    void cambiarEstado_NoEncontrado() {
        // Preparación
        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.empty());

        // Testeo
        int res = cuponService.cambiarEstadoCupon(1L, "ACTIVO");

        // Verificación
        assertEquals(1, res);
        verify(cuponRepository, times(0)).save(any());
    }

    @Test
    void cambiarEstado_Invalido() {
        // Preparación
        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(new Cupon()));

        // Testeo
        int res = cuponService.cambiarEstadoCupon(1L, "PENDIENTE");

        // Verificación
        assertEquals(2, res);
        verify(cuponRepository, times(0)).save(any());
    }


    @Test
    void cambiarEstado_Exito_Inactivo() {
        // Preparación
        Cupon cupon = new Cupon();
        cupon.setEstado("ACTIVO");
        
        // Configuración
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cupon));

        // Testeo
        int res = cuponService.cambiarEstadoCupon(1L, "INACTIVO");

        // Verificación
        assertEquals(0, res);
        assertEquals("INACTIVO", cupon.getEstado());
        verify(cuponRepository, times(1)).save(cupon);
    }



    @Test
    void eliminarCupon_Exito() {
        // Configuración
        when(cuponRepository.existsById(1L)).thenReturn(true);

        // Testeo
        int res = cuponService.eliminarCupon(1L);

        // Verificación
        assertEquals(0, res);
        verify(cuponRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarCupon_NoEncontrado() {
        // Configuración
        when(cuponRepository.existsById(99L)).thenReturn(false);

        // Testeo
        int res = cuponService.eliminarCupon(99L);

        // Verificación
        assertEquals(1, res);
        verify(cuponRepository, times(0)).deleteById(anyLong());
    }





}
