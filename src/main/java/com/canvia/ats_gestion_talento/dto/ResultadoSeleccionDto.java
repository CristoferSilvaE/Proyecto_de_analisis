package com.canvia.ats_gestion_talento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Datos del formulario "Registrar resultado de selección" (CU08). */
@Data
public class ResultadoSeleccionDto {

    @NotNull(message = "La postulación es obligatoria")
    private Integer idPostulacion;

    /** SELECCIONADO, NO_SELECCIONADO o PENDIENTE. */
    @NotBlank(message = "El resultado es obligatorio")
    private String resultado;

    private String observaciones;
}
