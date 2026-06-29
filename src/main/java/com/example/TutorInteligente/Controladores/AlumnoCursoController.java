package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.ActualizarNivelesRequest;
import com.example.TutorInteligente.Servicios.AlumnoCursoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alumno-cursos")
public class AlumnoCursoController {

    @Autowired
    private AlumnoCursoService service;

    @PutMapping("/niveles")
    public ResponseEntity<?> actualizarNiveles(
            @Valid @RequestBody ActualizarNivelesRequest dto
    ) {
        return ResponseEntity.ok(service.actualizarNiveles(dto));
    }
}
