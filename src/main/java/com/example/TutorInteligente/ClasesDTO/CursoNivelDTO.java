package com.example.TutorInteligente.ClasesDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoNivelDTO {

    @NotNull(message = "El cursoId es obligatorio")
    private Integer cursoId;

    @NotBlank(message = "El nivel es obligatorio")
    @Pattern(regexp = "(?i)BASICO|INTERMEDIO|AVANZADO", message = "El nivel debe ser BASICO, INTERMEDIO o AVANZADO")
    private String nivel;
}
