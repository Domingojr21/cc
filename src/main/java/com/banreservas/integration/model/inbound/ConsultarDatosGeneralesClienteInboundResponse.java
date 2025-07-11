package com.banreservas.integration.model.inbound;

import java.io.Serializable;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Response inbound para el servicio ConsultarDatosGeneralesCliente MICM.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteInboundResponse(
        Identificacion identificacion,
        String nombres,
        String primerApellido,
        String segundoApellido,
        String fechaNacimiento,
        String lugarNacimiento,
        String cedulaVieja,
        String sexo,
        String estadoCivil,
        String categoria,
        String causaInhabilidad,
        String codigoCausaCancelacion,
        String estatus,
        String fechaCancelacion,
        String fotoUrl,
        String fotoBinario) implements Serializable {

    @RegisterForReflection
    public record Identificacion(
            String numeroIdentificacion,
            String tipoIdentificacion) implements Serializable {
    }
}