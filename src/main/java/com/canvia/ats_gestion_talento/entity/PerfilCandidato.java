package com.canvia.ats_gestion_talento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "perfil_candidato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilCandidato {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(length = 20)
    private String telefono;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "resumen_profesional", columnDefinition = "TEXT")
    private String resumenProfesional;

    @Column(name = "anios_experiencia", nullable = false)
    private Integer aniosExperiencia = 0;
}