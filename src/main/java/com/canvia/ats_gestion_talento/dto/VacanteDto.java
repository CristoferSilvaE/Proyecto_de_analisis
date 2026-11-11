package com.canvia.ats_gestion_talento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class VacanteDto {
    private Integer idVacante;
    
    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    private String area;
    private String ubicacion;
    private String modalidad;
    private LocalDate fechaCierre;
}