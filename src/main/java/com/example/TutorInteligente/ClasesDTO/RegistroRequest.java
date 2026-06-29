package com.example.TutorInteligente.ClasesDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {

    private String nombre;

    private String apellido;

    private String correo;

    private String contrasena;

    // Si no se envia, el backend registra ALUMNO automaticamente.
    private String tipo;

    private String grado;
}
