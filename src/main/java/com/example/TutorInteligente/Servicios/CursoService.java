package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.CursoRequest;
import com.example.TutorInteligente.ClasesDTO.CursoResponse;
import com.example.TutorInteligente.Entidades.Curso;
import com.example.TutorInteligente.Repositorios.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CursoService {

    @Autowired
    private CursoRepository cursoRepo;

    public List<CursoResponse> listar() {
        return cursoRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CursoResponse obtenerPorId(Integer cursoId) {
        Curso curso = buscarCurso(cursoId);
        return toResponse(curso);
    }

    public CursoResponse crear(CursoRequest request) {
        validarNombreDuplicado(request.getNombreCurso(), null);

        Curso curso = new Curso();
        curso.setNombreCurso(request.getNombreCurso().trim());

        return toResponse(cursoRepo.save(curso));
    }

    public CursoResponse actualizar(Integer cursoId, CursoRequest request) {
        Curso curso = buscarCurso(cursoId);
        validarNombreDuplicado(request.getNombreCurso(), cursoId);

        curso.setNombreCurso(request.getNombreCurso().trim());

        return toResponse(cursoRepo.save(curso));
    }

    public String eliminar(Integer cursoId) {
        Curso curso = buscarCurso(cursoId);
        cursoRepo.delete(curso);
        return "CURSO ELIMINADO";
    }

    private Curso buscarCurso(Integer cursoId) {
        return cursoRepo.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    private void validarNombreDuplicado(String nombreCurso, Integer cursoIdActual) {
        cursoRepo.findByNombreCursoIgnoreCase(nombreCurso.trim())
                .filter(curso -> cursoIdActual == null || !curso.getCursoId().equals(cursoIdActual))
                .ifPresent(curso -> {
                    throw new RuntimeException("Ya existe un curso con ese nombre");
                });
    }

    private CursoResponse toResponse(Curso curso) {
        return new CursoResponse(
                curso.getCursoId(),
                curso.getNombreCurso()
        );
    }
}
