package com.jacob.ventas.models;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data //Incluye Getter, Setter, toString
@AllArgsConstructor
@NoArgsConstructor
public class Venta {

    @NotBlank(message = "El vendedor debe ser obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String vendedor;

    @NotBlank(message = "La zona debe ser obligatoria")
    @Size(min = 5, max = 30, message = "La zona debe contener entre 5 y 30 caracteres")
    private String zona;

    //Recibimos el monto total
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "3000.00", message = "El monto minimo debe de ser de $3,000 MXN")
    @DecimalMax(value = "100000.00", message = "El monto maximo debe de ser $100,000 MXN")
    private BigDecimal monto;

}
