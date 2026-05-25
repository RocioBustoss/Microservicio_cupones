package duoc.rocio.cupones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AplicarCuponDTO {

    @NotBlank
    private String codigo;

    @NotNull
    private Long idPedido;

    @NotNull
    private Long idCliente;
}

/*

{
  "codigo": "CODIGO_DESCUENTO",
  "idPedido": 1,
  "idCliente": 4
}

*/