package com.canvia.ats_gestion_talento.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class PostulacionRequestDto {

    @NotNull(message = "La vacante es obligatoria")
    private Integer idVacante;

    @NotNull(message = "El candidato es obligatorio")
    private Integer idCandidato; // Se enviará de forma oculta hasta integrar la sesión

    @NotNull(message = "Debe adjuntar su currículum en formato PDF")
    private MultipartFile archivoCv;
}