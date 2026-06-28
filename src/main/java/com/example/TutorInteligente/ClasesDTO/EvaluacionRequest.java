package com.example.TutorInteligente.ClasesDTO;

import java.util.List;

public class EvaluacionRequest {

    private Integer alumnoId;

    private List<RespuestaPreguntaDTO> respuestas;

    private List<CursoNivelDTO> cursos;

    public Integer getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Integer alumnoId) {
        this.alumnoId = alumnoId;
    }

    public List<RespuestaPreguntaDTO> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<RespuestaPreguntaDTO> respuestas) {
        this.respuestas = respuestas;
    }

    public List<CursoNivelDTO> getCursos() {
        return cursos;
    }

    public void setCursos(List<CursoNivelDTO> cursos) {
        this.cursos = cursos;
    }
}