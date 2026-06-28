package com.example.TutorInteligente.Entidades;

import jakarta.persistence.*;

@Entity
    @Table(name="alumno_curso")
public class AlumnoCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alumnoCursoId;

    @ManyToOne
    @JoinColumn(name="alumno_id")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name="curso_id")
    private Curso curso;

    private String nivel;

    public Integer getAlumnoCursoId() {
        return alumnoCursoId;
    }

    public void setAlumnoCursoId(Integer alumnoCursoId) {
        this.alumnoCursoId = alumnoCursoId;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

}