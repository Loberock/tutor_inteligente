package com.example.TutorInteligente.ClasesDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoRequest {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 100, message = "El nombre del curso no debe superar 100 caracteres")
    private String nombreCurso;
}
