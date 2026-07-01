package duoc.rocio.cupones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import duoc.rocio.cupones.model.Cupon;
import duoc.rocio.cupones.service.CuponService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuponController.class)
@ActiveProfiles("test")
public class CuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuponService cuponService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void obtenerCupones_Exito() throws Exception {
        Cupon c1 = new Cupon(1L, "VERANO20", "Desc verano", LocalDate.now(), LocalDate.now(), "ACTIVO", 100, 0, null, null);
        Mockito.when(cuponService.obtenerCupones()).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/ecomarket/v1/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo", is("VERANO20")));
    }

    @Test
    void obtenerCupones_Vacio() throws Exception {
        Mockito.when(cuponService.obtenerCupones()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ecomarket/v1/cupones"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No existen cupones registrados"));
    }


    @Test
    void obtenerCuponesPorPromocion_Exito() throws Exception {
        Cupon c1 = new Cupon(1L, "PROMO10", "Promo10", LocalDate.now(), LocalDate.now(), "ACTIVO", 10, 0, null, null);
        Mockito.when(cuponService.obtenerCuponesPorPromocion(1L)).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/ecomarket/v1/promociones/1/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }


    @Test
    void obtenerCuponPorId_Existe() throws Exception {
        Cupon c1 = new Cupon(1L, "CUPON1", "Desc", LocalDate.now(), LocalDate.now(), "ACTIVO", 10, 0, null, null);
        Mockito.when(cuponService.obtenerCuponPorId(1L)).thenReturn(Optional.of(c1));

        mockMvc.perform(get("/api/ecomarket/v1/cupones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("CUPON1")));
    }

    @Test
    void obtenerCuponPorId_NoExiste() throws Exception {
        Mockito.when(cuponService.obtenerCuponPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ecomarket/v1/cupones/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cupón no encontrado"));
    }


    @Test
    void buscarPorCodigo_Existe() throws Exception {
        Cupon c1 = new Cupon(1L, "OFF50", "Desc", LocalDate.now(), LocalDate.now(), "ACTIVO", 10, 0, null, null);
        Mockito.when(cuponService.buscarPorCodigo("OFF50")).thenReturn(Optional.of(c1));

        mockMvc.perform(get("/api/ecomarket/v1/cupones/codigo/OFF50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("OFF50")));
    }

    @Test
    void buscarPorCodigo_NoExiste() throws Exception {
        Mockito.when(cuponService.buscarPorCodigo("FANTASMA")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ecomarket/v1/cupones/codigo/FANTASMA"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cupón no encontrado"));
    }


    @Test
    void buscarPorEstado_Exito() throws Exception {
        Cupon c1 = new Cupon(1L, "ACTIVO1", "Desc", LocalDate.now(), LocalDate.now(), "ACTIVO", 10, 0, null, null);
        Mockito.when(cuponService.buscarPorEstado("ACTIVO")).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/ecomarket/v1/cupones/estado").param("estado", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void buscarPorEstado_Vacio() throws Exception {
        Mockito.when(cuponService.buscarPorEstado("INACTIVO")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ecomarket/v1/cupones/estado").param("estado", "INACTIVO"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No existen cupones con el estado solicitado"));
    }


    @Test
    void guardarCupon_Casos() throws Exception {
        Cupon nuevo = new Cupon(null, "NUEVO", "Desc", LocalDate.now(), LocalDate.now(), "ACTIVO", 10, 0, null, null);
        String json = objectMapper.writeValueAsString(nuevo);
        String url = "/api/ecomarket/v1/promociones/1/cupones/descuento/1";

        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(0);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(content().string("Cupón registrado correctamente"));

        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(1);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound()).andExpect(content().string("Promoción no encontrada"));

        
        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(2);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound()).andExpect(content().string("Descuento no encontrado"));

        
        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(3);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict()).andExpect(content().string("Ya existe un cupón con ese código"));

        
        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(4);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict()).andExpect(content().string("El descuento ya se encuentra asignado a otro cupón"));

        
        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(5);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("La fecha de término no puede ser anterior a la fecha de inicio"));

        
        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(6);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("La vigencia del cupón debe estar dentro de la vigencia de la promoción"));

        
        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(7);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("El estado del cupón debe ser ACTIVO o INACTIVO"));

        
        Mockito.when(cuponService.guardarCupon(eq(1L), eq(1L), any(Cupon.class))).thenReturn(99);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isInternalServerError()).andExpect(content().string("Error interno"));
    }

    

    @Test
    void actualizarCupon_Casos() throws Exception {
        Cupon act = new Cupon(null, "ACT", "Desc", LocalDate.now(), LocalDate.now(), "ACTIVO", 10, 0, null, null);
        String json = objectMapper.writeValueAsString(act);
        String url = "/api/ecomarket/v1/cupones/1";

        
        Mockito.when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(0);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(content().string("Cupón actualizado correctamente"));

        
        Mockito.when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(1);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound()).andExpect(content().string("Cupón no encontrado"));

        
        Mockito.when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(2);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict()).andExpect(content().string("Ya existe otro cupón con ese código"));

        
        Mockito.when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(3);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("La fecha de término no puede ser anterior a la fecha de inicio"));

        
        Mockito.when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(4);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("La vigencia del cupón debe estar dentro de la vigencia de la promoción"));

        
        Mockito.when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(5);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(content().string("El estado del cupón debe ser ACTIVO o INACTIVO"));

        
        Mockito.when(cuponService.actualizarCupon(eq(1L), any(Cupon.class))).thenReturn(99);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isInternalServerError()).andExpect(content().string("Error interno"));
    }

    

    @Test
    void cambiarEstadoCupon_Casos() throws Exception {
        String url = "/api/ecomarket/v1/cupones/1/estado";

        
        Mockito.when(cuponService.cambiarEstadoCupon(1L, "INACTIVO")).thenReturn(0);
        mockMvc.perform(put(url).param("estado", "INACTIVO"))
                .andExpect(status().isOk()).andExpect(content().string("Estado del cupón actualizado correctamente"));

        
        Mockito.when(cuponService.cambiarEstadoCupon(1L, "INACTIVO")).thenReturn(1);
        mockMvc.perform(put(url).param("estado", "INACTIVO"))
                .andExpect(status().isNotFound()).andExpect(content().string("Cupón no encontrado"));

        
        Mockito.when(cuponService.cambiarEstadoCupon(1L, "INACTIVO")).thenReturn(2);
        mockMvc.perform(put(url).param("estado", "INACTIVO"))
                .andExpect(status().isBadRequest()).andExpect(content().string("El estado del cupón debe ser ACTIVO o INACTIVO"));

        
        Mockito.when(cuponService.cambiarEstadoCupon(1L, "INACTIVO")).thenReturn(99);
        mockMvc.perform(put(url).param("estado", "INACTIVO"))
                .andExpect(status().isInternalServerError()).andExpect(content().string("Error interno"));
    }

    

    @Test
    void eliminarCupon_Casos() throws Exception {
        String url = "/api/ecomarket/v1/cupones/1";


        Mockito.when(cuponService.eliminarCupon(1L)).thenReturn(0);
        mockMvc.perform(delete(url))
                .andExpect(status().isOk()).andExpect(content().string("Cupón eliminado correctamente"));

        Mockito.when(cuponService.eliminarCupon(1L)).thenReturn(1);
        mockMvc.perform(delete(url))
                .andExpect(status().isNotFound()).andExpect(content().string("Cupón no encontrado"));

        Mockito.when(cuponService.eliminarCupon(1L)).thenReturn(99);
        mockMvc.perform(delete(url))
                .andExpect(status().isInternalServerError()).andExpect(content().string("Error interno"));
    }
}