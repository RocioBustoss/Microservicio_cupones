package duoc.rocio.cupones.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "promocion")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPromocion;

    @NotBlank
    @Column(name = "nombre_promocion", nullable = false)
    private String nombre;

    @NotBlank
    @Column(name = "descripcion_promocion", nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "fecha_inicio_promocion", nullable = false)
    private LocalDate fechaInicio;

    @NotNull
    @Column(name = "fecha_fin_promocion", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "promocion_activa", nullable = false)
    private boolean activa;
}