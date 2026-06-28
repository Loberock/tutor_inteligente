package com.example.TutorInteligente.Entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "preguntas_resueltas")
public class PreguntasResueltas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer preguntaResueltaId;

    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "pregunta_id")
    private Pregunta pregunta;

    private String respuestaSeleccionada;

    private Boolean correcta;

    private LocalDate fecha;

    public PreguntasResueltas() {
    }

    public Integer getPreguntaResueltaId() {
        return preguntaResueltaId;
    }

    public void setPreguntaResueltaId(Integer preguntaResueltaId) {
        this.preguntaResueltaId = preguntaResueltaId;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuestaSeleccionada() {
        return respuestaSeleccionada;
    }

    public void setRespuestaSeleccionada(String respuestaSeleccionada) {
        this.respuestaSeleccionada = respuestaSeleccionada;
    }

    public Boolean getCorrecta() {
        return correcta;
    }

    public void setCorrecta(Boolean correcta) {
        this.correcta = correcta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}