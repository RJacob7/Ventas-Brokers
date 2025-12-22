package com.jacob.ventas.dto.Ventas;

import java.math.BigDecimal;

public record VentaPorVendedor(
        String vendedor,
        BigDecimal totalVentas
) { }
