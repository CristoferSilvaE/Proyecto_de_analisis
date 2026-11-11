package com.canvia.ats_gestion_talento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrevista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entrevista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrevista")
    private Integer idEntrevista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulacion", nullable = false)
    private Postulacion postulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reclutador", nullable = false)
    private Usuario reclutador;

    @Column(nullable = false, length = 30)
    private String tipo = "GENERAL";

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 20)
    private String modalidad;

    @Column(name = "enlace_reunion", length = 500)
    private String enlaceReunion;

    @Column(nullable = false, length = 20)
    private String estado = "PROGRAMADA";

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
