package com.jacob.ventas.services;

import com.jacob.ventas.dto.Ventas.RespuestaVentas;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VentaService{
    RespuestaVentas procesarArchivos(List<MultipartFile> archivos);
}
