package duoc.rocio.cupones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long idPedido;
    private Long idCliente;
    private String estado;
    private Double total;
}

/*
{
  "idPedido": 1,
  "idCliente": 4,
  "estado": "VALIDACION",
  "total": 100
}
*/