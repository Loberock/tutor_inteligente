package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.EvaluacionRequest;
import com.example.TutorInteligente.Servicios.EvaluacionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/v1/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService service;

    @GetMapping("/diagnostico")
    public ResponseEntity<?> obtenerDiagnostico(
            @RequestParam @NotNull(message = "El cursoId es obligatorio") Integer cursoId,
            @RequestParam @NotBlank(message = "El grado es obligatorio") String grado,
            @RequestParam(defaultValue = "BASICO") String nivel,
            @RequestParam(defaultValue = "10") Integer cantidad
    ) {
        return ResponseEntity.ok(service.obtenerDiagnostico(cursoId, grado, nivel, cantidad));
    }

    @PostMapping
    public ResponseEntity<?> procesar(
            @Valid @RequestBody EvaluacionRequest dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.procesarEvaluacion(dto, authentication.getName()));
    }

    @GetMapping("/progreso")
    public ResponseEntity<?> obtenerProgreso(
            @RequestParam(required = false) Integer cursoId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.obtenerUltimoProgreso(authentication.getName(), cursoId));
    }
}
