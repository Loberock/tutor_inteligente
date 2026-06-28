package com.example.TutorInteligente.ClasesDTO;

import java.util.List;

public class ActualizarNivelesRequest {

    private Integer usuarioId;

    private List<CursoNivelDTO> cursos;

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<CursoNivelDTO> getCursos() {
        return cursos;
    }

    public void setCursos(List<CursoNivelDTO> cursos) {
        this.cursos = cursos;
    }
}