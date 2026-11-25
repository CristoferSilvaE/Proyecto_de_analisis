package com.canvia.ats_gestion_talento.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/** Datos del formulario "Programar entrevista" (CU07). */
@Data
public class EntrevistaDto {

    @NotNull(message = "La postulación es obligatoria")
    private Integer idPostulacion;

    /** Compatible con <input type="datetime-local"> (formato yyyy-MM-ddTHH:mm). */
    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La entrevista debe programarse en una fecha futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaHora;

    /** VIRTUAL o PRESENCIAL. */
    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    /** GENERAL, RRHH, TECNICA o FINAL. */
    private String tipo = "GENERAL";

    /** Obligatorio cuando la modalidad es VIRTUAL. */
    @Size(max = 500, message = "El enlace no debe superar 500 caracteres")
    private String enlaceReunion;

    private String observaciones;
}
