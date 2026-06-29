package com.example.TutorInteligente.Entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
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
}
