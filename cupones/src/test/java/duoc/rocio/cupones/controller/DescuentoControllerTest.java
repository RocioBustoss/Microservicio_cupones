package duoc.rocio.cupones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import duoc.rocio.cupones.model.Descuento;
import duoc.rocio.cupones.service.DescuentoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DescuentoController.class)
@ActiveProfiles("test")
public class DescuentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DescuentoService descuentoService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }


    @Test
    void obtenerDescuentos_Exito() throws Exception {
        Descuento d1 = new Descuento(1L, "PORCENTAJE", 20.0);
        Descuento d2 = new Descuento(2L, "MONTO_FIJO", 5000.0);

        Mockito.when(descuentoService.obtenerDescuentos()).thenReturn(Arrays.asList(d1, d2));

        mockMvc.perform(get("/api/ecomarket/v1/descuentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tipoDescuento", is("PORCENTAJE")))
                .andExpect(jsonPath("$[1].valor", is(5000.0)));
    }

    @Test
    void obtenerDescuentos_Vacio() throws Exception {
        Mockito.when(descuentoService.obtenerDescuentos()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ecomarket/v1/descuentos"))
                .andExpect(status().isOk())
                .andExpect(content().string("No existen descuentos registrados"));
    }


    @Test
    void obtenerDescuentoPorId_Existe() throws Exception {
        Descuento d = new Descuento(1L, "PORCENTAJE", 15.0);
        Mockito.when(descuentoService.obtenerDescuentoPorId(1L)).thenReturn(Optional.of(d));

        mockMvc.perform(get("/api/ecomarket/v1/descuentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDescuento", is(1)))
                .andExpect(jsonPath("$.valor", is(15.0)));
    }

    @Test
    void obtenerDescuentoPorId_NoExiste() throws Exception {
        Mockito.when(descuentoService.obtenerDescuentoPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ecomarket/v1/descuentos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Descuento no encontrado"));
    }


    @Test
    void buscarPorTipo_Exito() throws Exception {
        Descuento d = new Descuento(1L, "MONTO_FIJO", 10000.0);
        Mockito.when(descuentoService.buscarPorTipo("MONTO_FIJO")).thenReturn(List.of(d));

        mockMvc.perform(get("/api/ecomarket/v1/descuentos/tipo").param("tipo", "MONTO_FIJO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoDescuento", is("MONTO_FIJO")));
    }

    @Test
    void buscarPorTipo_Vacio() throws Exception {
        Mockito.when(descuentoService.buscarPorTipo("FANTASMA")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ecomarket/v1/descuentos/tipo").param("tipo", "FANTASMA"))
                .andExpect(status().isOk())
                .andExpect(content().string("No existen descuentos del tipo solicitado"));
    }


    @Test
    void guardarDescuento_Casos() throws Exception {
        Descuento d = new Descuento(null, "PORCENTAJE", 50.0);
        String json = objectMapper.writeValueAsString(d);
        String url = "/api/ecomarket/v1/descuentos";

        Mockito.when(descuentoService.guardarDescuento(any(Descuento.class))).thenReturn(0);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(content().string("Descuento registrado correctamente"));

        Mockito.when(descuentoService.guardarDescuento(any(Descuento.class))).thenReturn(1);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("El tipo de descuento debe ser PORCENTAJE o MONTO_FIJO"));

        Mockito.when(descuentoService.guardarDescuento(any(Descuento.class))).thenReturn(2);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("El porcentaje de descuento no puede ser mayor a 100"));

        Mockito.when(descuentoService.guardarDescuento(any(Descuento.class))).thenReturn(99);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void actualizarDescuento_Casos() throws Exception {
        Descuento d = new Descuento(null, "MONTO_FIJO", 5000.0);
        String json = objectMapper.writeValueAsString(d);
        String url = "/api/ecomarket/v1/descuentos/1";

        Mockito.when(descuentoService.actualizarDescuento(eq(1L), any(Descuento.class))).thenReturn(0);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(content().string("Descuento actualizado correctamente"));

        Mockito.when(descuentoService.actualizarDescuento(eq(1L), any(Descuento.class))).thenReturn(1);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound()).andExpect(content().string("Descuento no encontrado"));

        Mockito.when(descuentoService.actualizarDescuento(eq(1L), any(Descuento.class))).thenReturn(2);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("El tipo de descuento debe ser PORCENTAJE o MONTO_FIJO"));

        Mockito.when(descuentoService.actualizarDescuento(eq(1L), any(Descuento.class))).thenReturn(3);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("El porcentaje de descuento no puede ser mayor a 100"));

        Mockito.when(descuentoService.actualizarDescuento(eq(1L), any(Descuento.class))).thenReturn(99);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void eliminarDescuento_Casos() throws Exception {
        String url = "/api/ecomarket/v1/descuentos/1";

        Mockito.when(descuentoService.eliminarDescuento(1L)).thenReturn(0);
        mockMvc.perform(delete(url))
                .andExpect(status().isOk()).andExpect(content().string("Descuento eliminado correctamente"));

        Mockito.when(descuentoService.eliminarDescuento(1L)).thenReturn(1);
        mockMvc.perform(delete(url))
                .andExpect(status().isNotFound()).andExpect(content().string("Descuento no encontrado"));

        Mockito.when(descuentoService.eliminarDescuento(1L)).thenReturn(99);
        mockMvc.perform(delete(url))
                .andExpect(status().isInternalServerError());
    }
}