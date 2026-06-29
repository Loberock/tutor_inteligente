package com.example.TutorInteligente.ClasesDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;

    private String tipo;

    private Integer usuarioId;

    private Integer perfilId;

    private String nombre;
}
