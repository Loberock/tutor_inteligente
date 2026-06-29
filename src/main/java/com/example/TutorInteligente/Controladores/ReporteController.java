package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.Servicios.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService service;

    @GetMapping("/rendimiento")
    public ResponseEntity<?> obtener() {
        return ResponseEntity.ok(service.obtenerRendimiento());
    }
}
