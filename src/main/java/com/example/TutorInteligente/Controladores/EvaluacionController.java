package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.EvaluacionRequest;
import com.example.TutorInteligente.Servicios.EvaluacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService service;

    @PostMapping
    public ResponseEntity<?> procesar(
            @Valid @RequestBody EvaluacionRequest dto
    ) {
        return ResponseEntity.ok(service.procesarEvaluacion(dto));
    }
}
