package com.jacob.ventas.dto;

public record ErrorValidacion(
        String archivo,
        int fila,
        String mensaje
) { }
