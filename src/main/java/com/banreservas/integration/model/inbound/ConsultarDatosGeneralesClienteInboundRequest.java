package com.banreservas.integration.model.inbound;

import java.io.Serializable;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request inbound para el servicio ConsultarDatosGeneralesCliente MICM.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteInboundRequest(
        String identificacion,
        String tipoIdentificacion,
        String forzarActualizar,
        String incluirFotoBinaria) implements Serializable {
}