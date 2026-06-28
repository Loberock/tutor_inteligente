package com.example.TutorInteligente.ClasesDTO;

import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.Profesor;
import com.example.TutorInteligente.Entidades.Usuario;

public class RegistroResponse {

    private Boolean creado;

    private String tipo;


    private Alumno alumno;

    private Profesor profesor;

    public Boolean getCreado() {
        return creado;
    }

    public void setCreado(Boolean creado) {
        this.creado = creado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

}