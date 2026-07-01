package duoc.rocio.cupones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import duoc.rocio.cupones.dto.AplicarCuponDTO;
import duoc.rocio.cupones.dto.ResultadoCuponDTO;
import duoc.rocio.cupones.model.ValidacionCupon;
import duoc.rocio.cupones.service.ValidacionCuponService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValidacionCuponController.class)
@ActiveProfiles("test")
public class ValidacionCuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ValidacionCuponService validacionCuponService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void aplicarCupon_Error404() throws Exception {
        AplicarCuponDTO req = new AplicarCuponDTO("CUPON_NO_EXISTE", 1L, 1L);
        ResultadoCuponDTO res = new ResultadoCuponDTO();
        res.setResultado("ERROR_404");
        res.setMensaje("Cupón no encontrado");

        Mockito.when(validacionCuponService.aplicarCupon(any(AplicarCuponDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/ecomarket/v1/cupones/aplicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound()) 
                .andExpect(content().string("Cupón no encontrado"));
    }

    @Test
    void aplicarCupon_Rechazado() throws Exception {
        AplicarCuponDTO req = new AplicarCuponDTO("CUPON_VENCIDO", 1L, 1L);
        ResultadoCuponDTO res = new ResultadoCuponDTO();
        res.setResultado("RECHAZADO");
        res.setMensaje("El cupón se encuentra fuera de vigencia");

        Mockito.when(validacionCuponService.aplicarCupon(any(AplicarCuponDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/ecomarket/v1/cupones/aplicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict()) 
                .andExpect(content().string("El cupón se encuentra fuera de vigencia"));
    }

    @Test
    void aplicarCupon_Error503() throws Exception {
        AplicarCuponDTO req = new AplicarCuponDTO("CUPON_VALIDO", 1L, 1L);
        ResultadoCuponDTO res = new ResultadoCuponDTO();
        res.setResultado("ERROR_503");
        res.setMensaje("No fue posible comunicarse con el microservicio Pedidos");

        Mockito.when(validacionCuponService.aplicarCupon(any(AplicarCuponDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/ecomarket/v1/cupones/aplicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isServiceUnavailable()) 
                .andExpect(content().string("No fue posible comunicarse con el microservicio Pedidos"));
    }

    @Test
    void aplicarCupon_Aprobado() throws Exception {
        AplicarCuponDTO req = new AplicarCuponDTO("CUPON_VALIDO", 1L, 1L);
        ResultadoCuponDTO res = new ResultadoCuponDTO();
        res.setResultado("APROBADO");
        res.setMensaje("Cupón aplicado correctamente");
        res.setMontoFinal(24000.0);

        Mockito.when(validacionCuponService.aplicarCupon(any(AplicarCuponDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/ecomarket/v1/cupones/aplicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.resultado", is("APROBADO")))
                .andExpect(jsonPath("$.montoFinal", is(24000.0)));
    }


    @Test
    void obtenerValidaciones_Exito() throws Exception {
        ValidacionCupon v1 = new ValidacionCupon(1L, 1L, 1L, LocalDateTime.now(), "APROBADO", null, 30000.0, 6000.0, 24000.0, null);
        Mockito.when(validacionCuponService.obtenerValidaciones()).thenReturn(List.of(v1));

        mockMvc.perform(get("/api/ecomarket/v1/validaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].resultado", is("APROBADO")));
    }


    @Test
    void obtenerValidacionPorId_Existe() throws Exception {
        ValidacionCupon v1 = new ValidacionCupon(1L, 1L, 1L, LocalDateTime.now(), "APROBADO", null, 30000.0, 6000.0, 24000.0, null);
        Mockito.when(validacionCuponService.obtenerValidacionPorId(1L)).thenReturn(Optional.of(v1));

        mockMvc.perform(get("/api/ecomarket/v1/validaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idValidacion", is(1)));
    }

    @Test
    void obtenerValidacionPorId_NoExiste() throws Exception {
        Mockito.when(validacionCuponService.obtenerValidacionPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ecomarket/v1/validaciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Validación no encontrada"));
    }


    @Test
    void obtenerValidacionesPorCliente_Exito() throws Exception {
        ValidacionCupon v1 = new ValidacionCupon(1L, 10L, 1L, LocalDateTime.now(), "APROBADO", null, 30000.0, 6000.0, 24000.0, null);
        Mockito.when(validacionCuponService.obtenerValidacionesPorCliente(10L)).thenReturn(List.of(v1));

        mockMvc.perform(get("/api/ecomarket/v1/validaciones/cliente/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente", is(10)));
    }


    @Test
    void obtenerValidacionesPorPedido_Exito() throws Exception {
        ValidacionCupon v1 = new ValidacionCupon(1L, 10L, 5L, LocalDateTime.now(), "APROBADO", null, 30000.0, 6000.0, 24000.0, null);
        Mockito.when(validacionCuponService.obtenerValidacionesPorPedido(5L)).thenReturn(List.of(v1));

        mockMvc.perform(get("/api/ecomarket/v1/validaciones/pedido/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idPedido", is(5)));
    }


    @Test
    void obtenerValidacionesPorCupon_Exito() throws Exception {
        ValidacionCupon v1 = new ValidacionCupon(1L, 10L, 5L, LocalDateTime.now(), "APROBADO", null, 30000.0, 6000.0, 24000.0, null);
        Mockito.when(validacionCuponService.obtenerValidacionesPorCupon(7L)).thenReturn(List.of(v1));

        mockMvc.perform(get("/api/ecomarket/v1/validaciones/cupon/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}