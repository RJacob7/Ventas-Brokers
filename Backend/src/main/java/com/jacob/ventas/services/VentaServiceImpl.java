package com.jacob.ventas.services;

import com.jacob.ventas.dto.ErrorValidacion;
import com.jacob.ventas.dto.Ventas.RespuestaVentas;
import com.jacob.ventas.dto.Ventas.VentaPorVendedor;
import com.jacob.ventas.mappers.VentaMapper;
import com.jacob.ventas.models.Venta;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService{

    private final VentaMapper ventaMapper;
    private final Validator validator;


    @Override
    public RespuestaVentas procesarArchivos(List<MultipartFile> archivos) {

        validarArchivosEntrada(archivos);

        List<Venta> todasLasVentas = new ArrayList<>();
        List<ErrorValidacion> errores = new ArrayList<>();

        //Procesamos cada archivo
        for (MultipartFile archivo : archivos){
            try{
                procesarArchivo(archivo, todasLasVentas, errores);
            }catch (IOException e){
                errores.add(new ErrorValidacion(
                        archivo.getOriginalFilename(),
                        0,
                        "Error al leer el archivo " + e.getMessage()
                ));
            }
        }

        //ConsolidarVentas
        List<VentaPorVendedor> ventaPorVendedor = consolidarVentas(todasLasVentas);
        return new RespuestaVentas(ventaPorVendedor, errores);
    }


    private void validarArchivosEntrada(List<MultipartFile> archivos) {
        // Validar que se envíen archivos
        if (archivos == null || archivos.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos un archivo");
        }

        // Validar cada archivo
        for (MultipartFile archivo : archivos) {
            String nombreArchivo = archivo.getOriginalFilename();

            // Validar nombre del archivo
            if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
                throw new IllegalArgumentException("Nombre de archivo inválido");
            }

            // Validar extensión .txt
            if (!nombreArchivo.toLowerCase().endsWith(".txt")) {
                throw new IllegalArgumentException(
                        "Solo se permiten archivos .txt. Archivo rechazado: " + nombreArchivo
                );
            }

            // Validar que no esté vacío
            if (archivo.isEmpty()) {
                throw new IllegalArgumentException(
                        "El archivo está vacío: " + nombreArchivo
                );
            }

            // Validar tamaño (opcional)
            long maxSize = 10 * 1024 * 1024; // 10 MB
            if (archivo.getSize() > maxSize) {
                throw new IllegalArgumentException(
                        "El archivo excede el tamaño máximo permitido (10MB): " + nombreArchivo
                );
            }
        }
    }



    //Lectura de archivo .TXT
    private void procesarArchivo(MultipartFile archivo, List<Venta> ventas, List<ErrorValidacion> errores) throws IOException {
        //Convertimos el MultiPartFile a bufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8)); //lee el archivo en flujo de bytes con getInputStream

        String linea; //esta pasando todo a un string
        int numeroFila = 0;

        //Leer línea por línea
        while ((linea = reader.readLine()) != null){ //asignar una linea de bufferedReader con readLine a una String linea
            numeroFila++; //cuando termina de leer una línea asigna que existe una línea +1

            //saltar lineas vacias
            if (linea.trim().isEmpty()) continue;

            //Saltar primera linea si es header
            if (numeroFila == 1 && linea.toLowerCase().contains("vendedor")) continue;

            try{
                //Convertir CSV a objeto model usando el mapper
                Venta venta = ventaMapper.lineCsvToVenta(linea);

                //validar la venta
                Set<ConstraintViolation<Venta>> violations = validator.validate(venta);

                if (violations.isEmpty()){
                    ventas.add(venta); //Si no hay errores entonces se agrega a la lista
                } else {
                    //Sí hay errores registrarlas
                    for (ConstraintViolation<Venta> violation : violations){
                        errores.add(new ErrorValidacion(
                                archivo.getOriginalFilename(),
                                numeroFila,
                                violation.getMessage()
                        ));
                    }
                }
            }catch (IllegalArgumentException e){
                //Error al parsear la linea
                errores.add(new ErrorValidacion(
                        archivo.getOriginalFilename(),
                        numeroFila,
                        e.getMessage()
                ));
            }
        }
        reader.close();
    }



    //Consolidar Ventas por vendedor (agrupar y sumar)
    private List<VentaPorVendedor> consolidarVentas(List<Venta> ventas){

        //Usar Map para agrupar por vendedor y sumar montos
        Map<String, BigDecimal> ventasPorVendedorMap = new HashMap<>();

        for (Venta venta: ventas){
            ventasPorVendedorMap.merge( //Si la clave no existe la crea, o si existe combina el valor viejo con el nuevo (map.merge)
                    //Dame dos valores y dime cuál guardar
                    venta.getVendedor(),
                    venta.getMonto(),
                    BigDecimal::add //Si el vendedor ya existe suma el monto
            );
        }

        //Despues convertimos el map con las sumas a List<VentaPorVendedor>
        return ventasPorVendedorMap.entrySet().stream()
                .map(entry -> new VentaPorVendedor(
                        entry.getKey(),
                        entry.getValue()
                ))
                .sorted(Comparator.comparing(v -> v.vendedor()))
                .toList();
    }
}
