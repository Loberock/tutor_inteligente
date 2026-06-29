package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.CursoRequest;
import com.example.TutorInteligente.Servicios.CursoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cursos")
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{cursoId}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer cursoId) {
        return ResponseEntity.ok(service.obtenerPorId(cursoId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CursoRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PutMapping("/{cursoId}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer cursoId,
            @Valid @RequestBody CursoRequest request
    ) {
        return ResponseEntity.ok(service.actualizar(cursoId, request));
    }

    @DeleteMapping("/{cursoId}")
    public ResponseEntity<?> eliminar(@PathVariable Integer cursoId) {
        return ResponseEntity.ok(service.eliminar(cursoId));
    }
}
