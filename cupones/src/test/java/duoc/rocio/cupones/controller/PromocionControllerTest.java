package duoc.rocio.cupones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import duoc.rocio.cupones.model.Promocion;
import duoc.rocio.cupones.service.PromocionService;

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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromocionController.class)
@ActiveProfiles("test")
public class PromocionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromocionService promocionService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void obtenerPromociones_Exito() throws Exception {
        Promocion p1 = new Promocion(1L, "Promo Verano", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), true);
        Promocion p2 = new Promocion(2L, "Promo Invierno", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), true);

        Mockito.when(promocionService.obtenerPromociones()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/ecomarket/v1/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Promo Verano")))
                .andExpect(jsonPath("$[1].nombre", is("Promo Invierno")));
    }

    // --- GET /promociones/{idPromocion} ---

    @Test
    void obtenerPromocionPorId_Existe() throws Exception {
        Promocion p = new Promocion(1L, "Promo Primavera", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), true);

        Mockito.when(promocionService.obtenerPromocionPorId(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/ecomarket/v1/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPromocion", is(1)))
                .andExpect(jsonPath("$.nombre", is("Promo Primavera")));
    }

    @Test
    void obtenerPromocionPorId_NoExiste() throws Exception {
        Mockito.when(promocionService.obtenerPromocionPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ecomarket/v1/promociones/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Promoción no encontrada"));
    }


    @Test
    void buscarPorEstado_Exito() throws Exception {
        Promocion p = new Promocion(1L, "Promo Activa", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), true);

        Mockito.when(promocionService.buscarPorEstado(true)).thenReturn(List.of(p));

        mockMvc.perform(get("/api/ecomarket/v1/promociones/estado").param("activa", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].activa", is(true)));
    }


    @Test
    void guardarPromocion_Exito() throws Exception {
        Promocion nueva = new Promocion(null, "Nueva Promo", "Desc", LocalDate.now(), LocalDate.now().plusDays(5), true);

        Mockito.when(promocionService.guardarPromocion(any(Promocion.class))).thenReturn(0);

        mockMvc.perform(post("/api/ecomarket/v1/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Promoción registrada correctamente"));
    }

    @Test
    void guardarPromocion_ConflictoYaExiste() throws Exception {
        Promocion nueva = new Promocion(null, "Promo Duplicada", "Desc", LocalDate.now(), LocalDate.now().plusDays(5), true);

        Mockito.when(promocionService.guardarPromocion(any(Promocion.class))).thenReturn(1);

        mockMvc.perform(post("/api/ecomarket/v1/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isConflict())
                .andExpect(content().string("La promoción ya existe"));
    }

    @Test
    void guardarPromocion_FechasInvalidas() throws Exception {
        Promocion nueva = new Promocion(null, "Fechas Malas", "Desc", LocalDate.now(), LocalDate.now().minusDays(5), true);

        Mockito.when(promocionService.guardarPromocion(any(Promocion.class))).thenReturn(2);

        mockMvc.perform(post("/api/ecomarket/v1/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha de término no puede ser anterior a la fecha de inicio"));
    }

    @Test
    void guardarPromocion_Error500() throws Exception {
        Promocion nueva = new Promocion(null, "Error Interno", "Desc", LocalDate.now(), LocalDate.now().plusDays(5), true);

        Mockito.when(promocionService.guardarPromocion(any(Promocion.class))).thenReturn(99);

        mockMvc.perform(post("/api/ecomarket/v1/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void actualizarPromocion_Exito() throws Exception {
        Promocion actualizada = new Promocion(null, "Editada", "Desc", LocalDate.now(), LocalDate.now().plusDays(5), true);

        Mockito.when(promocionService.actualizarPromocion(eq(1L), any(Promocion.class))).thenReturn(0);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk())
                .andExpect(content().string("Promoción actualizada correctamente"));
    }

    @Test
    void actualizarPromocion_NoEncontrada() throws Exception {
        Promocion actualizada = new Promocion(null, "Editada", "Desc", LocalDate.now(), LocalDate.now().plusDays(5), true);

        Mockito.when(promocionService.actualizarPromocion(eq(99L), any(Promocion.class))).thenReturn(1);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Promoción no encontrada"));
    }

    @Test
    void actualizarPromocion_FechasInvalidas() throws Exception {
        Promocion actualizada = new Promocion(null, "Fechas Malas", "Desc", LocalDate.now(), LocalDate.now().minusDays(5), true);

        Mockito.when(promocionService.actualizarPromocion(eq(1L), any(Promocion.class))).thenReturn(2);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha de término no puede ser anterior a la fecha de inicio"));
    }

    @Test
    void actualizarPromocion_Error500() throws Exception {
        Promocion actualizada = new Promocion(null, "Fechas Malas", "Desc", LocalDate.now(), LocalDate.now().minusDays(5), true);

        Mockito.when(promocionService.actualizarPromocion(eq(1L), any(Promocion.class))).thenReturn(99);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void cambiarEstadoPromocion_ActivarExito() throws Exception {
        Mockito.when(promocionService.cambiarEstadoPromocion(1L, true)).thenReturn(0);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/1/estado").param("activa", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Promoción activada correctamente"));
    }

    @Test
    void cambiarEstadoPromocion_DesactivarExito() throws Exception {
        Mockito.when(promocionService.cambiarEstadoPromocion(1L, false)).thenReturn(0);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/1/estado").param("activa", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Promoción desactivada correctamente"));
    }

    @Test
    void cambiarEstadoPromocion_NoEncontrada() throws Exception {
        Mockito.when(promocionService.cambiarEstadoPromocion(99L, true)).thenReturn(1);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/99/estado").param("activa", "true"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Promoción no encontrada"));
    }

    @Test
    void cambiarEstadoPromocion_Error500() throws Exception {
        Mockito.when(promocionService.cambiarEstadoPromocion(1L, true)).thenReturn(99);

        mockMvc.perform(put("/api/ecomarket/v1/promociones/1/estado").param("activa", "true"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void eliminarPromocion_Exito() throws Exception {
        Mockito.when(promocionService.eliminarPromocion(1L)).thenReturn(0);

        mockMvc.perform(delete("/api/ecomarket/v1/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Promoción eliminada correctamente"));
    }

    @Test
    void eliminarPromocion_NoEncontrada() throws Exception {
        Mockito.when(promocionService.eliminarPromocion(99L)).thenReturn(1);

        mockMvc.perform(delete("/api/ecomarket/v1/promociones/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Promoción no encontrada"));
    }

    @Test
    void eliminarPromocion_Error500() throws Exception {
        Mockito.when(promocionService.eliminarPromocion(1L)).thenReturn(99);

        mockMvc.perform(delete("/api/ecomarket/v1/promociones/1"))
                .andExpect(status().isInternalServerError());
    }
}