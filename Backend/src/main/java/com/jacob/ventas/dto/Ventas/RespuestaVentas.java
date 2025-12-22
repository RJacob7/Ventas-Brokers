package com.jacob.ventas.dto.Ventas;

import com.jacob.ventas.dto.ErrorValidacion;

import java.util.List;

public record RespuestaVentas(
        List<VentaPorVendedor> ventaPorVendedor,
        List<ErrorValidacion> errores
) { }
