package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.ActualizarNivelesRequest;
import com.example.TutorInteligente.Servicios.AlumnoCursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alumno-curso")
public class AlumnoCursoController {

    @Autowired
    private AlumnoCursoService service;

    @PutMapping("/niveles")
    public ResponseEntity<?> actualizarNiveles(
            @RequestBody ActualizarNivelesRequest dto
    ) {

        try {
            return ResponseEntity.ok(
                    service.actualizarNiveles(dto)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}