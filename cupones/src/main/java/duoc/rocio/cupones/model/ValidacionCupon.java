package duoc.rocio.cupones.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "validacion_cupon")
public class ValidacionCupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idValidacion;

    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @NotNull
    @Column(name = "id_pedido", nullable = false)
    private Long idPedido;

    @NotNull
    @Column(name = "fecha_validacion", nullable = false)
    private LocalDateTime fechaValidacion;

    @NotBlank
    @Column(name = "resultado_validacion", nullable = false)
    private String resultado;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @Column(name = "monto_original")
    private Double montoOriginal;

    @Column(name = "monto_descuento")
    private Double montoDescuento;

    @Column(name = "monto_final")
    private Double montoFinal;


    // UN CUPÓN PUEDE SER UTILIZADO MUCHAS VECES, POR ENDE TIENE MUCHAS VALIDACIONES
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cupon", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cupon cupon;
}