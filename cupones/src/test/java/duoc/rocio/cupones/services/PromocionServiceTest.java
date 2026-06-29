package duoc.rocio.cupones.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import duoc.rocio.cupones.model.Promocion;
import duoc.rocio.cupones.repository.PromocionRepository;
import duoc.rocio.cupones.service.PromocionService;

public class PromocionServiceTest {
    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionService promocionService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerPromociones_ListaLlena() {
        // Preparación
        Promocion p1 = new Promocion(1L, "Promo 1", "Desc 1", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Promocion p2 = new Promocion(2L, "Promo 2", "Desc 2", LocalDate.now(), LocalDate.now().plusDays(10), false);
        
        // Configuración
        when(promocionRepository.findAll()).thenReturn(List.of(p1, p2));

        // Testeo
        List<Promocion> resultado = promocionService.obtenerPromociones();
    
        // Verificación
        assertEquals(2, resultado.size());
        verify(promocionRepository, times(1)).findAll();
    }

    @Test
    void obtenerPromociones_ListaVacia() {
        //Configuración
        when(promocionRepository.findAll()).thenReturn(new ArrayList<>());

        // Testeo
        List<Promocion> resultado = promocionService.obtenerPromociones();

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(promocionRepository, times(1)).findAll();
    }

    @Test
    void obtenerPromocionPorId_Existe() {
        // Preparación
        Promocion p1 = new Promocion(1L, "Promo 1", "Desc", LocalDate.now(), LocalDate.now().plusDays(5), true);
        
        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(p1));

        // Testeo
        Optional<Promocion> resultado = promocionService.obtenerPromocionPorId(1L);

        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals("Promo 1", resultado.get().getNombre());
        verify(promocionRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPromocionPorId_NoExiste() {
        // Configuración
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        // Testeo
        Optional<Promocion> resultado = promocionService.obtenerPromocionPorId(99L);

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(promocionRepository, times(1)).findById(99L);
    }

    @Test
    void buscarPorEstado_Encontrados() {
        // Preparación
        Promocion p1 = new Promocion(1L, "Promo Activa", "Desc", LocalDate.now(), LocalDate.now().plusDays(5), true);
        
        // Configuración
        when(promocionRepository.findByActiva(true)).thenReturn(List.of(p1));

        // Testeo
        List<Promocion> resultado = promocionService.buscarPorEstado(true);

        // Verificación
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isActiva());
        verify(promocionRepository, times(1)).findByActiva(true);
    }

    @Test
    void buscarPorEstado_Vacia() {
        // Configuración
        when(promocionRepository.findByActiva(false)).thenReturn(new ArrayList<>());

        // Testeo
        List<Promocion> resultado = promocionService.buscarPorEstado(false);

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(promocionRepository, times(1)).findByActiva(false);
    }


    @Test
    void guardarPromocion_NombreDuplicado() {
        // Preparación
        Promocion promocionNueva = new Promocion();
        promocionNueva.setNombre("CYBER");

        // Configuración
        when(promocionRepository.existsByNombreIgnoreCase("CYBER")).thenReturn(true);

        // Testeo
        int resultado = promocionService.guardarPromocion(promocionNueva);

        // Verificación
        assertEquals(1, resultado, "Debería retornar 1 (Nombre duplicado)");
        verify(promocionRepository, times(0)).save(any());
    }

    @Test
    void guardarPromocion_FechasInvalidas() {
        // Preparación
        Promocion promocionNueva = new Promocion();
        promocionNueva.setNombre("NAVIDAD");
        promocionNueva.setFechaInicio(LocalDate.now().plusDays(10));
        promocionNueva.setFechaFin(LocalDate.now());

        // Configuración
        when(promocionRepository.existsByNombreIgnoreCase("NAVIDAD")).thenReturn(false);

        // Testeo
        int resultado = promocionService.guardarPromocion(promocionNueva);

        // Verificación
        assertEquals(2, resultado, "Debería retornar 2 (Fechas inválidas)");
        verify(promocionRepository, times(0)).save(any());
    }

    @Test
    void guardarPromocion_Exito() {
        // Preparación
        Promocion promocionNueva = new Promocion();
        promocionNueva.setNombre("VERANO");
        promocionNueva.setFechaInicio(LocalDate.now());
        promocionNueva.setFechaFin(LocalDate.now().plusDays(10));

        // Configuración
        when(promocionRepository.existsByNombreIgnoreCase("VERANO")).thenReturn(false);

        // Testeo
        int resultado = promocionService.guardarPromocion(promocionNueva);

        // Verificación
        assertEquals(0, resultado, "Debería retornar 0 (Éxito)");
        verify(promocionRepository, times(1)).save(promocionNueva);
    }

    @Test
    void actualizarPromocion_NoEncontrada() {
        // Configuración
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        // Testeo
        int resultado = promocionService.actualizarPromocion(99L, new Promocion());

        // Verificación
        assertEquals(1, resultado, "Debería retornar 1 (No encontrado)");
        verify(promocionRepository, times(0)).save(any());
    }

    @Test
    void actualizarPromocion_FechasInvalidas() {
        // Preparación
        Promocion promocionOriginal = new Promocion(1L, "ANTIGUA", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), true);
        
        Promocion promocionActualizada = new Promocion();
        promocionActualizada.setFechaInicio(LocalDate.now().plusDays(5));
        promocionActualizada.setFechaFin(LocalDate.now());

        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocionOriginal));

        // Testeo
        int resultado = promocionService.actualizarPromocion(1L, promocionActualizada);

        // Verificación
        assertEquals(2, resultado, "Debería retornar 2 (Fechas inválidas)");
        verify(promocionRepository, times(0)).save(any());
    }

    @Test
    void actualizarPromocion_Exito() {
        // Preparación
        Promocion promocionOriginal = new Promocion(1L, "ANTIGUA", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), true);
        
        Promocion promocionActualizada = new Promocion();
        promocionActualizada.setNombre("NUEVA");
        promocionActualizada.setDescripcion("Nueva desc");
        promocionActualizada.setFechaInicio(LocalDate.now());
        promocionActualizada.setFechaFin(LocalDate.now().plusDays(15));
        promocionActualizada.setActiva(false);

        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocionOriginal));

        // Testeo
        int resultado = promocionService.actualizarPromocion(1L, promocionActualizada);

        // Verificación
        assertEquals(0, resultado, "Debería retornar 0 (Éxito)");
        assertEquals("NUEVA", promocionOriginal.getNombre());
        assertEquals(false, promocionOriginal.isActiva());
        verify(promocionRepository, times(1)).save(promocionOriginal);
    }


    @Test
    void cambiarEstadoPromocion_NoEncontrada() {
        // Configuración
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        // Testeo
        int resultado = promocionService.cambiarEstadoPromocion(99L, true);

        // Verificación
        assertEquals(1, resultado, "Debería retornar 1 (No encontrado)");
        verify(promocionRepository, times(0)).save(any());
    }

    @Test
    void cambiarEstadoPromocion_Exito() {
        // Preparación
        Promocion promocion = new Promocion();
        promocion.setActiva(false);

        // Configuración
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));

        // Testeo
        int resultado = promocionService.cambiarEstadoPromocion(1L, true);

        // Verificación
        assertEquals(0, resultado, "Debería retornar 0 (Éxito)");
        assertTrue(promocion.isActiva());
        verify(promocionRepository, times(1)).save(promocion);
    }

    @Test
    void eliminarPromocion_NoEncontrada() {
        // Configuración
        when(promocionRepository.existsById(99L)).thenReturn(false);

        // Testeo
        int resultado = promocionService.eliminarPromocion(99L);

        // Verificación
        assertEquals(1, resultado, "Debería retornar 1 (No encontrado)");
        verify(promocionRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void eliminarPromocion_Exito() {
        // Configuración
        when(promocionRepository.existsById(1L)).thenReturn(true);

        // Testeo
        int resultado = promocionService.eliminarPromocion(1L);

        // Verificación
        assertEquals(0, resultado, "Debería retornar 0 (Éxito)");
        verify(promocionRepository, times(1)).deleteById(1L);
    }
}
