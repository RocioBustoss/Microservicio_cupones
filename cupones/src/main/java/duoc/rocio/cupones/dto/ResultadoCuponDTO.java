package duoc.rocio.cupones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoCuponDTO {

    private Long idValidacion;
    private Long idPedido;
    private Long idCliente;
    private String codigoCupon;
    private Double montoOriginal;
    private Double montoDescuento;
    private Double montoFinal;
    private String resultado;
    private String mensaje;
}

/*
{
  "idValidacion": 1,
  "idPedido": 1,
  "idCliente": 4,
  "codigoCupon": "CODIGO_DESCUENTO",
  "montoOriginal": 30000.0,
  "montoDescuento": 6000.0,
  "montoFinal": 24000.0,
  "resultado": "APROBADO",
  "mensaje": "Cupón aplicado correctamente"
}
*/