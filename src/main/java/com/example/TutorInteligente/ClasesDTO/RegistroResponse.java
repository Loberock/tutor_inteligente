package com.example.TutorInteligente.ClasesDTO;

import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.Profesor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroResponse {

    private Boolean creado;

    private String tipo;

    private Alumno alumno;

    private Profesor profesor;
}
