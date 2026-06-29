package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.PreguntaBatchResponse;
import com.example.TutorInteligente.ClasesDTO.PreguntaRequest;
import com.example.TutorInteligente.Servicios.PreguntaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
@RequestMapping("/v1/preguntas")
public class PreguntaController {

    @Autowired
    private PreguntaService service;

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) Integer cursoId,
            @RequestParam(required = false) String grado,
            @RequestParam(required = false) String dificultad
    ) {
        return ResponseEntity.ok(service.listar(cursoId, grado, dificultad));
    }

    @GetMapping("/{preguntaId}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer preguntaId) {
        return ResponseEntity.ok(service.obtenerPorId(preguntaId));
    }

    @PostMapping
    public ResponseEntity<?> registrarPregunta(
            @Valid @RequestBody PreguntaRequest request
    ) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PostMapping("/lote")
    public ResponseEntity<?> registrarPreguntas(
            @RequestBody @NotEmpty List<@Valid PreguntaRequest> lista
    ) {

        PreguntaBatchResponse response =
                service.registrarLista(lista);

        if (response.getExitoso()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @PutMapping("/{preguntaId}")
    public ResponseEntity<?> actualizarPregunta(
            @PathVariable Integer preguntaId,
            @Valid @RequestBody PreguntaRequest request
    ) {
        return ResponseEntity.ok(service.actualizar(preguntaId, request));
    }

    @DeleteMapping("/{preguntaId}")
    public ResponseEntity<?> eliminarPregunta(@PathVariable Integer preguntaId) {
        return ResponseEntity.ok(service.eliminar(preguntaId));
    }
}
