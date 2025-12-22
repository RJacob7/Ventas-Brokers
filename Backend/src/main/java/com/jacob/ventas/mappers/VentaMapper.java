package com.jacob.ventas.mappers;

import com.jacob.ventas.dto.Ventas.VentaRequest;
import com.jacob.ventas.models.Venta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VentaMapper implements CommonMapper<VentaRequest, String, Venta>{

    @Override
    public Venta lineCsvToVenta(String model) throws IllegalArgumentException{
        String[] partes = model.split(",");
        if (partes.length != 3)
            throw new IllegalArgumentException("La linea debe tener 3 columnas");

        try{
            return new Venta(
                    partes[0].trim(),
                    partes[1].trim(),
                    new BigDecimal(partes[2].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El monto debe ser numerico");
        }
    }

    @Override
    public Venta requestToEntity(VentaRequest request) {
        if (request == null) return null;
        Venta venta = new Venta();
        venta.setVendedor(request.vendedor());
        venta.setZona(request.zona());
        venta.setMonto(request.monto());
        return venta;
    }
}
