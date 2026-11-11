package com.canvia.ats_gestion_talento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_evaluacion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_evaluacion", "id_criterio"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private EvaluacionPreseleccion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_criterio", nullable = false)
    private CriterioVacante criterio;

    @Column(nullable = false)
    private Boolean cumple;

    @Column(precision = 5, scale = 2)
    private BigDecimal puntaje;

    @Column(length = 500)
    private String observacion;
}