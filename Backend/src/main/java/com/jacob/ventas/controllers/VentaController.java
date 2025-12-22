package com.jacob.ventas.controllers;

import com.jacob.ventas.dto.Ventas.RespuestaVentas;
import com.jacob.ventas.services.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //permitir peticiones desde cualquier origen (Fronted)
public class VentaController {

    private final VentaService ventaService;

    @PostMapping("/procesar")
    public ResponseEntity<RespuestaVentas> procesarArchivos(@RequestParam("files") List<MultipartFile> archivos){
        RespuestaVentas respuestaVentas = ventaService.procesarArchivos(archivos);
        return ResponseEntity.ok(respuestaVentas);
    }
}
