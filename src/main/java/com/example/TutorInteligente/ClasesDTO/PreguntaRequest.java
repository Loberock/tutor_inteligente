package com.example.TutorInteligente.ClasesDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreguntaRequest {

    @NotNull(message = "El profesorId es obligatorio")
    private Integer profesorId;

    @NotNull(message = "El cursoId es obligatorio")
    private Integer cursoId;

    @NotBlank(message = "El contenido de la pregunta es obligatorio")
    private String contenidoPregunta;

    @NotBlank(message = "El grado es obligatorio")
    private String grado;

    @NotBlank(message = "La dificultad es obligatoria")
    private String dificultad;

    @NotBlank(message = "La opcion A es obligatoria")
    private String opcionA;

    @NotBlank(message = "La opcion B es obligatoria")
    private String opcionB;

    @NotBlank(message = "La opcion C es obligatoria")
    private String opcionC;

    @NotBlank(message = "La opcion D es obligatoria")
    private String opcionD;

    @NotBlank(message = "La respuesta correcta es obligatoria")
    @Pattern(regexp = "(?i)A|B|C|D", message = "La respuesta correcta debe ser A, B, C o D")
    private String respuestaCorrecta;

    @NotBlank(message = "El refuerzo es obligatorio")
    private String refuerzo;
}
