package com.jacob.ventas.dto.Ventas;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

//No usamos validaciones aca porque no es Json lo que recibe
public record VentaRequest(
        String vendedor,
        String zona,
        BigDecimal monto
) { }
