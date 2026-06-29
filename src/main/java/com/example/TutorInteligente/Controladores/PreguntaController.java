package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.PreguntaBatchResponse;
import com.example.TutorInteligente.ClasesDTO.PreguntaRequest;
import com.example.TutorInteligente.Servicios.PreguntaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/preguntas")
public class PreguntaController {

    @Autowired
    private PreguntaService service;

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
}
