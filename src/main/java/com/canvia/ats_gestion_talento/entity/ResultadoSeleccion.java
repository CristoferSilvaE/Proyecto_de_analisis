package com.canvia.ats_gestion_talento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultado_seleccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSeleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Integer idResultado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulacion", nullable = false, unique = true)
    private Postulacion postulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reclutador", nullable = false)
    private Usuario reclutador;

    @Column(nullable = false, length = 30)
    private String resultado;

    @Column(name = "fecha_resultado", nullable = false)
    private LocalDateTime fechaResultado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        this.fechaResultado = LocalDateTime.now();
    }
}