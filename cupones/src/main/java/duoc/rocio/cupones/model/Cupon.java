package duoc.rocio.cupones.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cupon")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCupon;

    @NotBlank
    @Column(name = "codigo_cupon", nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "descripcion_cupon", nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "fecha_inicio_cupon", nullable = false)
    private LocalDate fechaInicio;

    @NotNull
    @Column(name = "fecha_fin_cupon", nullable = false)
    private LocalDate fechaFin;

    // ACTIVO o INACTIVO
    @NotBlank
    @Column(name = "estado_cupon", nullable = false)
    private String estado;

    @Min(1)
    @Column(name = "uso_maximo", nullable = false)
    private int usoMaximo;

    @Min(0)
    @Column(name = "usos_actuales", nullable = false)
    private int usosActuales;


    // Muchos cupones pueden pertenecer a una promoción.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_promocion", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Promocion promocion;

    // Vincula un descuento con un cupón para evitar usar el mismo descuento en más de un cupón
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_descuento", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Descuento descuento;
}