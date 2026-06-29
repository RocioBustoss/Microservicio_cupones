package duoc.rocio.cupones.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import duoc.rocio.cupones.model.Descuento;
import duoc.rocio.cupones.repository.DescuentoRepository;
import duoc.rocio.cupones.service.DescuentoService;

public class DescuentoServiceTest {
    @Mock
    private DescuentoRepository descuentoRepository;

    @InjectMocks
    private DescuentoService descuentoService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerDescuentos_ListaLlena() {
        // Preparación
        Descuento d1 = new Descuento(1L, "PORCENTAJE", 15.0);
        Descuento d2 = new Descuento(2L, "MONTO_FIJO", 5000.0);
        List<Descuento> listaSimulada = List.of(d1, d2);

        // Configuración
        when(descuentoRepository.findAll()).thenReturn(listaSimulada);

        // Testeo
        List<Descuento> resultado = descuentoService.obtenerDescuentos();

        // Verificación
        assertEquals(2, resultado.size());
        assertEquals("PORCENTAJE", resultado.get(0).getTipoDescuento());
        assertEquals(5000.0, resultado.get(1).getValor());
        verify(descuentoRepository, times(1)).findAll();
    }

    @Test
    void obtenerDescuentos_ListaVacia() {
        // Configuración
        when(descuentoRepository.findAll()).thenReturn(new ArrayList<>());

        // Testeo
        List<Descuento> resultado = descuentoService.obtenerDescuentos();

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(descuentoRepository, times(1)).findAll();
    }

    @Test
    void obtenerDescuentoPorId_Existe() {
        // Preparación
        Descuento descuento = new Descuento(1L, "PORCENTAJE", 20.0);

        // Configuración
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(descuento));

        // Testeo
        Optional<Descuento> resultado = descuentoService.obtenerDescuentoPorId(1L);

        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals("PORCENTAJE", resultado.get().getTipoDescuento());
        assertEquals(20.0, resultado.get().getValor());
        verify(descuentoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerDescuentoPorId_NoExiste() {
        // Configuración
        when(descuentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Testeo
        Optional<Descuento> resultado = descuentoService.obtenerDescuentoPorId(99L);

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(descuentoRepository, times(1)).findById(99L);
    }

    @Test
    void buscarPorTipo_Encontrados() {
        // Preparación
        Descuento d1 = new Descuento(1L, "PORCENTAJE", 20.0);
        Descuento d2 = new Descuento(2L, "PORCENTAJE", 15.0);
        List<Descuento> listaDescuentos = List.of(d1, d2);

        // Configuración
        when(descuentoRepository.findByTipoDescuentoIgnoreCase("PORCENTAJE")).thenReturn(listaDescuentos);

        // Testeo
        List<Descuento> resultado = descuentoService.buscarPorTipo("PORCENTAJE");

        // Verificación
        assertEquals(2, resultado.size());
        assertEquals("PORCENTAJE", resultado.get(0).getTipoDescuento());
        verify(descuentoRepository, times(1)).findByTipoDescuentoIgnoreCase("PORCENTAJE");
    }

    @Test
    void buscarPorTipo_ListaVacia() {
        // Configuración
        when(descuentoRepository.findByTipoDescuentoIgnoreCase("INVALIDO")).thenReturn(new ArrayList<>());

        // Testeo
        List<Descuento> resultado = descuentoService.buscarPorTipo("INVALIDO");

        // Verificación
        assertTrue(resultado.isEmpty());
        verify(descuentoRepository, times(1)).findByTipoDescuentoIgnoreCase("INVALIDO");
    }

    @Test
    void guardarDescuento_TipoInvalido() {
        // Preparación
        Descuento descuento = new Descuento();
        descuento.setTipoDescuento("INVALIDO");
        descuento.setValor(50.0);

        // Testeo
        int resultado = descuentoService.guardarDescuento(descuento);

        // Verificación
        assertEquals(1, resultado);
        verify(descuentoRepository, times(0)).save(any());
    }

    @Test
    void guardarDescuento_PorcentajeMayor100() {
        // Preparación
        Descuento descuento = new Descuento();
        descuento.setTipoDescuento("PORCENTAJE");
        descuento.setValor(150.0);

        // Testeo
        int resultado = descuentoService.guardarDescuento(descuento);

        // Verificación
        assertEquals(2, resultado);
        verify(descuentoRepository, times(0)).save(any());
    }

    @Test
    void guardarDescuento_Porcentaje_Exito() {
        // Preparación
        Descuento descuento = new Descuento();
        descuento.setTipoDescuento("porcentaje");
        descuento.setValor(50.0);

        // Testeo
        int resultado = descuentoService.guardarDescuento(descuento);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("PORCENTAJE", descuento.getTipoDescuento());
        verify(descuentoRepository, times(1)).save(descuento);
    }

    @Test
    void guardarDescuento_MontoFijo_Exito() {
        // Preparación
        Descuento descuento = new Descuento();
        descuento.setTipoDescuento("MONTO_FIJO");
        descuento.setValor(5000.0);

        // Testeo
        int resultado = descuentoService.guardarDescuento(descuento);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("MONTO_FIJO", descuento.getTipoDescuento());
        verify(descuentoRepository, times(1)).save(descuento);
    }

    @Test
    void actualizarDescuento_NoEncontrado() {
        // Configuración
        when(descuentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Testeo
        int resultado = descuentoService.actualizarDescuento(99L, new Descuento());

        // Verificación
        assertEquals(1, resultado);
        verify(descuentoRepository, times(0)).save(any());
    }

    @Test
    void actualizarDescuento_TipoInvalido() {
        // Preparación
        Descuento descuentoOriginal = new Descuento(1L, "PORCENTAJE", 10.0);
        
        Descuento descuentoActualizado = new Descuento();
        descuentoActualizado.setTipoDescuento("INVALIDO");
        descuentoActualizado.setValor(50.0);

        // Configuración
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(descuentoOriginal));

        // Testeo
        int resultado = descuentoService.actualizarDescuento(1L, descuentoActualizado);

        // Verificación
        assertEquals(2, resultado);
        verify(descuentoRepository, times(0)).save(any());
    }

    @Test
    void actualizarDescuento_PorcentajeMayor100() {
        // Preparación
        Descuento descuentoOriginal = new Descuento(1L, "MONTO_FIJO", 5000.0);
        
        Descuento descuentoActualizado = new Descuento();
        descuentoActualizado.setTipoDescuento("PORCENTAJE");
        descuentoActualizado.setValor(150.0);

        // Configuración
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(descuentoOriginal));

        // Testeo
        int resultado = descuentoService.actualizarDescuento(1L, descuentoActualizado);

        // Verificación
        assertEquals(3, resultado);
        verify(descuentoRepository, times(0)).save(any());
    }

    @Test
    void actualizarDescuento_Porcentaje_Exito() {
        // Preparación
        Descuento descuentoOriginal = new Descuento(1L, "MONTO_FIJO", 5000.0);
        
        Descuento descuentoActualizado = new Descuento();
        descuentoActualizado.setTipoDescuento("porcentaje");
        descuentoActualizado.setValor(25.0);

        // Configuración
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(descuentoOriginal));

        // Testeo
        int resultado = descuentoService.actualizarDescuento(1L, descuentoActualizado);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("PORCENTAJE", descuentoOriginal.getTipoDescuento());
        assertEquals(25.0, descuentoOriginal.getValor());
        verify(descuentoRepository, times(1)).save(descuentoOriginal);
    }

    @Test
    void actualizarDescuento_MontoFijo_Exito() {
        // Preparación
        Descuento descuentoOriginal = new Descuento(1L, "PORCENTAJE", 10.0);
        
        Descuento descuentoActualizado = new Descuento();
        descuentoActualizado.setTipoDescuento("MONTO_FIJO");
        descuentoActualizado.setValor(15000.0);

        // Configuración
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(descuentoOriginal));

        // Testeo
        int resultado = descuentoService.actualizarDescuento(1L, descuentoActualizado);

        // Verificación
        assertEquals(0, resultado);
        assertEquals("MONTO_FIJO", descuentoOriginal.getTipoDescuento());
        assertEquals(15000.0, descuentoOriginal.getValor());
        verify(descuentoRepository, times(1)).save(descuentoOriginal);
    }

    @Test
    void eliminarDescuento_Exito() {
        // Configuración
        when(descuentoRepository.existsById(1L)).thenReturn(true);

        // Testeo
        int resultado = descuentoService.eliminarDescuento(1L);

        // Verificación
        assertEquals(0, resultado);
        verify(descuentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarDescuento_NoEncontrado() {
        // Configuración
        when(descuentoRepository.existsById(99L)).thenReturn(false);

        // Testeo
        int resultado = descuentoService.eliminarDescuento(99L);

        // Verificación
        assertEquals(1, resultado);
        verify(descuentoRepository, times(0)).deleteById(anyLong());
    }
}
