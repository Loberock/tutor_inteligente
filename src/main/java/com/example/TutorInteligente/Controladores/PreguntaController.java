package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.PreguntaBatchResponse;
import com.example.TutorInteligente.ClasesDTO.PreguntaRequest;
import com.example.TutorInteligente.Servicios.PreguntaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pregunta")
public class PreguntaController {

    @Autowired
    private PreguntaService service;

    @PostMapping("/batch")
    public ResponseEntity<?> registrarPreguntas(
            @RequestBody List<PreguntaRequest> lista
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
}