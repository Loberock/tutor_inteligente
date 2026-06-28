package com.example.TutorInteligente.Controladores;

import com.example.TutorInteligente.ClasesDTO.RegistroRequest;
import com.example.TutorInteligente.Servicios.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    RegistroService service;


    @PostMapping
    public ResponseEntity<?> registrar(
            @RequestBody
            RegistroRequest dto
    ) {

        try {

            return ResponseEntity
                    .ok(
                            service.registrar(dto)
                    );

        }

        catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );

        }

    }

}