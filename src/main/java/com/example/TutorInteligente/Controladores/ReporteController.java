package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.Servicios.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService service;

    @GetMapping("/rendimiento")
    public ResponseEntity<?> obtener(
            @RequestParam(required = false) Integer alumnoId,
            @RequestParam(required = false) Integer cursoId,
            @RequestParam(required = false) String grado,
            @RequestParam(required = false) Boolean soloRefuerzo
    ) {
        return ResponseEntity.ok(
                service.obtenerRendimiento(alumnoId, cursoId, grado, soloRefuerzo)
        );
    }

    @GetMapping("/refuerzos")
    public ResponseEntity<?> obtenerRefuerzos(
            @RequestParam(required = false) Integer alumnoId,
            @RequestParam(required = false) Integer cursoId,
            @RequestParam(required = false) String grado
    ) {
        return ResponseEntity.ok(
                service.obtenerRendimiento(alumnoId, cursoId, grado, true)
        );
    }
}
