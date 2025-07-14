# ms-orq-consultar-datos-generales-cliente-micm.

Este proyecto es un microservicio SOAP funcionando como orquestador encargado de consumir el servicio **ClienteBanservas_SP** de legado.

Si quieres aprender más de Quarkus, por favor visita el [sitio web oficial](https://quarkus.io/).

## Ejecución de la aplicación en modo de desarrollo

Puedes ejecutar tu aplicación en modo de desarrollo, lo que permite la codificación en vivo, utilizando el siguiente comando:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTA:_**  Quarkus incluye una interfaz Dev UI que está disponible solo en modo de desarrollo en http://localhost:8080/q/dev/.

## Empaquetado y ejecución de la aplicación

La aplicación se puede empaquetar utilizando:

```shell script
./mvnw package
```
Este comando genera el archivo quarkus-run.jar en el directorio target/quarkus-app/. Ten en cuenta que no es un über-jar, ya que las dependencias se copian en el directorio target/quarkus-app/lib/.

La aplicación se puede ejecutar utilizando:

```shell script
java -jar target/quarkus-app/quarkus-run.jar
```
Si deseas construir un über-jar, ejecuta el siguiente comando:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

La aplicación empaquetada como un über-jar se puede ejecutar utilizando:

```shell script
java -jar target/*-runner.jar
```

## Creación de un ejecutable nativo

Puedes crear un ejecutable nativo utilizando:

```shell script
./mvnw package -Dnative
```

O, si no tienes GraalVM instalado, puedes construir el ejecutable nativo en un contenedor utilizando:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Luego, puedes ejecutar tu ejecutable nativo con:

```shell script
./target/ms-orq-consultar-datos-generales-cliente-micm-1.0.0-SNAPSHOT-runner
```

Para obtener más información sobre cómo construir ejecutables nativos, consulta https://quarkus.io/guides/maven-tooling.

## Guías Relacionadas

- **Quarkus CXF:** Capacidades principales para implementar clientes SOAP y servicios JAX-WS.
- **RESTEasy Reactive:** Implementación de Jakarta REST utilizando procesamiento en tiempo de compilación y Vert.x.
- **SmallRye OpenTracing:** Trazado de servicios con SmallRye OpenTracing.
- **SmallRye OpenAPI:** Documenta tus APIs REST con OpenAPI, incluye Swagger UI.
- **Jacoco - Cobertura de Código:** Soporte para cobertura de pruebas con Jacoco.
- **SmallRye Health:** Monitorea la salud de tu aplicación usando SmallRye Health.
- **Micrometer Metrics:** Instrumenta el tiempo de ejecución y tu aplicación con métricas dimensionales usando Micrometer.

## Endpoints

**POST:** /banreservas/client/api/v1/banreservasClient

**Descripción:** Este endpoint permite obtener la información de clientes banreservas.

**GET:** /banreservas/client/api/v1/health

**Descripción:** Monitorea la salud de tu aplicación utilizando SmallRye Health.

**GET:** /banreservas/client/api/v1/swagger-ui

**Descripción:** Para acceder a Swagger UI y ver la documentación de las apis.

**Códigos de Respuesta:**

    200 OK - Exitoso: Éxito en la operación.
    400 Bad Request - BAD_REQUEST: Solicitud incorrecta.
    401 Unauthorized - UNAUTHOTIZE: No autorizado.
    500 Internal Server Error - INTERNAL_SERVER_ERROR: Error interno del servidor.
    503 BAD_GATEWAY: Error de conexión al servicio.
    500 UNEXPECTED_ERROR: Error interno inesperado del servidor.

## Integración con Servicios Externos

El servicio invoca un servicio REST llamado ms-cliente-banreservas-sp, que devuelve información sobre clientes banreservas desde base de datos.



##### Variables de Entorno/Secrets

La integración con el servicio REST ms-orq-consultar-datos-generales-cliente-micm requiere la siguiente configuración para consumo:

```properties

banreservas.client.service.soap.address=/banreservasClient
banreservas.client.service.soap.endpoint=banreservasClient
banreservas.client.service.soap.logging=true
backend.banreservas.client.url=https://ms-cte-ban-dev.apps.az-aro-dev.banreservas.com/api/v1/cliente-banreservas
backend.service.timeout=35000
```

### Ejemplo de Solicitud (Request):

```curl
curl --location 'http://localhost:8080/banreservas/client/api/v1/banreservasClient' \
--header 'Content-Type: text/xml; charset=utf-8' \
--header 'SOAPAction: http://tempuri.org/ClienteBanreservas' \
--header 'sessionId: 123' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJHTUF4bDdCUmJnNU43bHZQOU9pLXpKZGpxb2xZcHBKTHhSd0x4YjhrQmNzIn0.eyJleHAiOjE3NTA0MzE4MjUsImlhdCI6MTc1MDQzMTUyNSwianRpIjoiMjk1OTZkMWYtZjBhOS00NzAwLTgxMzItNjM4YTg2MzRkZTcxIiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1kZXYuYXBwcy5hei1hcm8tZGV2LmJhbnJlc2VydmFzLmNvbS9yZWFsbXMvaW50ZWdyYXRpb24iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMThjMjNmYTUtODNkMy00OTQyLThlZDQtYzA0YmIyMjZkMmRhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlcyIsInNlc3Npb25fc3RhdGUiOiI4MjM4NWRmNC1mZDZjLTRlYTEtYTNmNS0wZjY0NTA2NGM2ZDUiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIk5vdGlQcnVlYmEiLCJjbGllbnRlYmFucmVzZXJ2YXNwbHVzIiwicG9zaWNpb24tY29uc29saWRhZGEtY29tcGxldGUiLCJjb25zdWx0YXBvcnRhZm9saW8iLCJnZW5lcmFyLU9UUCIsInRyYW5zYWNjaW9uLWNhZGVuY2llIiwiY29uc3VsdC1sb2dzLW1vdG9yIiwiY29uc3VsdGEtY2xpZW50ZS1zaWduYXR1cmUiLCJwYWdvdGFyamV0YSIsInRhcmpldGFzY2xpZW50ZSIsInZhbGlkYXItbGltaXRlcyIsImdlbmVyYS1uY2YiLCJ2YWxpZGEtY3VlbnRhcy1jb211bmVzIiwiY29uc3VsdGEtY3VlbnRhLW1pbyIsImVsaW1pbmFyYmVuZWZpY2lhcmlvdG9rZW4iLCJjb25zdWx0YS1lc3RhZG8tZGlzcG9uaWJsZSIsInVtYV9hdXRob3JpemF0aW9uIiwicmVnaXN0cmF0cmFuc2FjY2lvbmxvZ1NQIiwiY2FtYmlhci1tZWNhbmlzbW8tbXVsdGlmYWN0b3IiLCJjb25zdWx0YXRhc2FzIiwiY2xhc2lmaWNhY2lvbkNsaWVudGVTdXBlckludERFMDhzcCIsImNvbnN1bHRhLXJzLWVmZWN0aXZvIiwiY29uc3VsdGEtY29uc29saWRhY2lvbi1ycyIsImNvbnN1bHRhci1kZWZyYXVkYWRvciIsImNvbnN1bHRhY29ycG9yYWNpb25lcyIsInRyYW5mZXJlbmNpYWNvbSIsImJpZW5lc2FkanVkaWNhZG9zIiwiY29uc3VsdGEtcHJvZHVjdG8tYnIiLCJibG9xdWVvZm9uZG9zcHJvZHVjdG9zIiwiY29uc3VsdGFwbGFucGFnbyIsImFsaWFzLXByb2R1Y3RvIiwiYWN0dWFsaXphci1kYXRvcy1tYWVzdHJvLWNlZHVsYWRvcyIsImNoZWNrTm90aWZpY2F0aW9ucyIsImNvbnN1bHRhLWZlY2hhLWhhYmlsIiwiY2xhc2lmaWNhY2lvbi1jbGllbnRlLXN1cGVyLWludCIsInBvc2VlLWNvZGlnby1zZWd1cmlkYWQiLCJyZWRlbmNpb24tcnMiLCJwb3NpY2lvbmNvbnNvbGlkYWRhY29tcGxldGUiLCJ2ZXJpZmljYS1nZW5lcmFjaW9uLXRva2VuLUlTVCIsImNvbnN1bHRhLW1vdmltaWVudG9zLWNyZWRpbWFzIiwiY29uc3VsdGFwbGFucGFnb3MiLCJjb25zdWx0YXItY29tZXJjaW8iLCJjb25zdWx0YS1tb3ZpbC1jbGllbnRlIiwiZGV0YWxsZS1jdWVudGEtbWlvIiwicG9zZWVjb2RpZ29zZWd1cmlkYWQiLCJjb21wcmEtdmVudGEtZG9jdW1lbnRvcyIsInRyYW5zZmVyZW5jaWEtcnMiLCJjb25zdWx0YS1saW5lYXMiLCJzb2xpY2l0YS1hdXRvcml6YWNpb24tbWlvLWFjaGUiLCJjb25zdWx0YXItcmVzdHJpbmdpZG8iLCJ0b2tlbmpjZXdzIiwiY29uc3VsdGFyLWRhdG9zLW1hZXN0cm8tY2VkdWxhZG9zIiwiYnVzY2FyLW9maWNpbmEiLCJtb3ZpbWllbnRvcy10YXJqZXRhLWNybSIsImNvbnN1bHRhYmVuZWZpY2lhcmlvc3Rva2VuIiwiZGV0YWxsZS1zb2xpY2l0dWQtdG9rZW4tZGlnaXRhbCIsImNvbnN1bHRhLWdyYWR1YWRvLXByZXNlcnZhIiwibW92aW1pZW50b3MtcHJlc3RhbW8iLCJyZXN1bWVuLXByb2R1Y3RvcyIsIkNvbnN1bHRhU3VtYXRvcmlhQ3VlbnRhcyIsImRldGFsbGUtY3VlbnRhcy1kaW5hbWljbyIsImNvbnN1bHRhZ2VuZXJhbHByb2R1Y3RvcHJlc3RhbW8iLCJkZWZhdWx0LXJvbGVzLXF1YXJrdXMiLCJjb25zdWx0YXItdGlwby1wcm9kdWN0byIsImNvbnN1bHRhLXByb2R1Y3RvIiwiY29uc3VsdGF0b2tlbmNsaWVudGUiLCJkZXRhbGxlLXRhcmpldGFzIiwiY29ydGV4LXRva2VuaXplciIsImluc2VydGFyLXNvbGljaXR1ZC1wcmVzdGFtbyIsInJzY2xpZW50ZSIsImF2YW5jZS1lZmVjdGl2byIsImNvbnN1bHRhLWxpbWl0ZXMiLCJiYWxhbmNlLXRhcmpldGEtY29tcGxldGUiLCJjb21wcmEtdmVudGEtZWZlY3Rpdm8iLCJyZXZlcnNvcmVkZW5jaW9ucnMiLCJjb25zdWx0YS10b2tlbi1hc2lnbmFkbyIsImFjdHVhbGl6YXItcGVyZmlsLXNjLXdzIiwiY29uc3VsdGFjb2RpZ29hY3RpdmFjaW9udG9rZW4iLCJidXNjYXItb2ZpY2luYXMtY2FqZXJvcyIsImNvbnN1bHRhLWNsaWVudGUtaWQiLCJjb25zdWx0YS1pZGVudGlmaWNhY2lvbi1jbGllbnRlIiwiY2xpZW50ZS1wb3NlZS1jYW5hbC10dS1iYW5jbyIsImNvbnN1bHRhLXJlY2xhbWFjaW9uZXMiLCJjYWxzaWZpY2FjaW9uQ2xpZW50ZVN1cGVySW50REU4c3AiLCJjYXJnYXItZ2VzdG9yLWNhcnRlcmEiLCJjb25zdWx0YXItbm90aWZpY2F0aW9uZXMiLCJpbmdyZXNhYmVuZWZpY2lhcmlvdG9rZW4iLCJjb25zdWx0YWN1ZW50YSIsIlZlcmlmaWNhci1odWVsbGEiLCJ2YWxpZGEtdHJhbnNhY2Npb24iLCJkZXRhbGxlLXByZXN0YW1vIiwibmFydGljdWxvZGlzcG9uaWJsZSIsImF1dGVudGljYXJ0b2tlbmRpZ2l0YWwiLCJnZW5lcmFyLXBsYW4tcGFnb3MiLCJhc2lnbmFyLXNvbGljaXR1ZC10b2tlbi1kaWdpdGFsIiwiY29uc3VsdGFkb2N1bWVudG9TUCIsImJmZiIsImNvbnN1bHRhLWluZm8tdHJhbnNhY2Npb24iLCJjb3J0ZXh0b2tlbml6ZXIiLCJjb25zdWx0YS1iYWxhbmNlLXJzIiwiY29uc3VsdGFyZWNsYW1vIiwiY29uc3VsdGFyLXRyYW5zYWNjaW9uLWxvZy1zcCIsImNvbnN1bHRhci1kYXRvcy1qY2UiLCJjb25zdWx0YS1jdWVudGEtbWlvLWFjaGUiLCJjb25zdWx0YS1jbGllbnRlLXRyYW5zdW5pb24iLCJjb25zdWx0YXJlc3BvbnNhYmlsaWRhZHVzdWFyaW9zY3JtIiwicmVnaXN0cmEtdmFsaWRhY2lvbi1odSIsIkNsYXNpZmljYWNpb24tQ2xpZW50ZS1TdXBlci1JbnQtSGlzdCIsImNvbnN1bHRhLXByb2R1Y3Rvcy1taW8iLCJub21pbmEtZWxlY3Ryb25pY2EiLCJwZXJpb2Rvcy1uZmMtcGVuZGllbnRlcyIsImJ0cy1jb25maXJtYXItcmVtZXNhLXJlY2liaWRhIiwidmVyaWZpY2FyLXJlc3RyaW5naWRvIiwiY29uc3VsdGEtcHJlc3RhbW8iLCJjb25zdWx0YS1jbGllbnRlLW1pbyIsImNvbnN1bHRhdG9wb2ZpY2luYXMiLCJkZXNlc3RpbWFyLXNvbGljaXR1ZC10b2tlbi1kaWdpdGFsIiwiY29uc3VsdGFwbGFucGFnb2NsaWVudGUiLCJ2YWxpZGEtbG90ZS1jb2RpZ28tY2hlcXVlIiwib2J0ZW5lci1saXN0YS12YWxvcmVzIiwidmFsaWRhY2lvbi1ybmMiLCJ2YWxpZGFyLXRlbGVmb25vIiwiY2hlcXVlc3RyYW5zaXRvIiwicG9zaWNpb24tY29uc29saWRhZGEiLCJ0cmFuc2FjY2lvbkxCVFIiLCJ2YWxpZGFsb3RlY29kaWdvY2hlcXVlIiwicmV2ZXJzYXItY29tcHJhLXZlbnRhLWRpdmlzYXMiLCJjdWVudGFyZWxhY2lvbmFkb3MiLCJ2YWxpZGFyLWRhdG9zLWJvIiwiY29uc29saWRhY2lvbi1ycyIsIm9mZmxpbmVfYWNjZXNzIiwiZ2VuZXJhbmNmIiwiYWN0dWFsaXphLWNvbnRyb2wtY29udmVuaW9zIiwidmFsaWRhY3VlbnRhc21hbmNvbXVuYWRhcyIsImF1dGgtY29kZXMiLCJSZWRlbmNpb25ScyIsIm9sdmlkby1jb250cmFzZW5hIiwiZGV0YWxsZS1saW5lYS1jcmVkaXRvIiwiZGVzdmluY3VsYXItdG9rZW4tZXhpc3RlbnRlIiwiYWdyZWdhci11c3VhcmlvLXJzYSIsImNvbnN1bHRhLXJpZXNnby1jbGllbnRlIiwibGlzdGFyLW9maWNpbmFzIiwiY29uc3VsdGEtY29udGFjdG8tc3RhZ2dpbmctY3JtIiwiY29uc3VsdGEtYmVuZWZpY2lhcmlvcy1zZXJ2aWNpb3MiLCJjb25zdWx0YS1jb2RpZ28tYWN0aXZhY2lvbi10b2tlbiIsImNhbGN1bGFyY3VvdGFwcmVzdGFtb3JlZ3VsYXIiLCJ2YWxpZGFjb2RpZ29zIiwiY29uc3VsdGFyLWZhY3R1cmFzIiwiY29uc3VsdGFjb25zb2xpZGFjaW9ucnMiLCJtb3ZpbWllbnRvcy1jZXJ0aWZpY2FkbyIsImNvbnN1bHRhc2VndXJvcHJlc3QiLCJ2YWxpZGEtcGFnby1yZWZlcmVuY2lhIiwiY29uc3VsdGEtZXN0YWRvLWNsaWVudGUiLCJjb25zdWx0YWNvbnZlbmlvcyIsImNvbnN1bHRhYmFsYW5jZWFmcCIsInVzZXIiLCJjdWVudGEtcmVsYWNpb25hZG9zIiwiY29uc3VsdGEtY3VlbnRhLW5vbWluYSIsImNvbnN1bHRhbmNmZ2VuZXJhZG9zIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBUdUJhbmNvQXBwIHByb2ZpbGUiLCJzaWQiOiI4MjM4NWRmNC1mZDZjLTRlYTEtYTNmNS0wZjY0NTA2NGM2ZDUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicHJlZmVycmVkX3VzZXJuYW1lIjoicmtlcHAiLCJlbWFpbCI6InJrZXBwQGJhbnJlc2VydmFzLmNvbSJ9.nNMBKJd40ORk5UbFQ573Ew8r0rmB9l_hVxUgFF8_GBncBPH1QgU0hXmm6kA9KLODow0QDwVfZ1dbqJmo4njjPrgwmxzRiLiXRa5fKiyeN6k067jhNMDZTpOpNIiWC61FcAKE0rx2qzxxC8rfBQCthvPrA2e_8PblW1cZy83RcXXEj-p03aU9nx6_T_YdMJTkCJJo7WmPRkvo7A4ZeAiKY8OrIOtC8aVuUSYWvUXJhkEiFHJyVFeDsDGIkRoCLnuLDMDE-YDmbszuqYGvcoymm5Av8QEMfIgfY7gPvJ4oDYS6sRDX9HLr5lFSjnq-E0oUwsfY2VQPiHy9GWReb2PLDg' \
--header 'Cookie: 724643c9697e104ca4622155e69a7a07=328a5364347cb1fe4853334f5dee13e2' \
--data '<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ClienteBanreservas xmlns="http://tempuri.org/">
      <request>
        <Canal>InterTrade</Canal>
        <Usuario>Test</Usuario>
        <Terminal>172.22.50.250</Terminal>
        <FechaHora></FechaHora>
        <Version>1</Version>
        <Identificacion>
          <NumeroIdentificacion>G05762690</NumeroIdentificacion>
          <TipoIdentificacion>Pasaporte</TipoIdentificacion>
        </Identificacion>
      </request>
    </ClienteBanreservas>
  </soap:Body>
</soap:Envelope>'
```

### Encabezados de la Solicitud

- **SOAPAction:** http://tempuri.org/ClienteBanreservas
- **Authorization:** Bearer eyj
- **sessionId:** ID único de la transacción.

## Cuerpo de la Solicitud (Request Body)

```xml
   <?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ClienteBanreservas xmlns="http://tempuri.org/">
      <request>
        <Canal>InterTrade</Canal>
        <Usuario>Test</Usuario>
        <Terminal>172.22.50.250</Terminal>
        <FechaHora></FechaHora>
        <Version>1</Version>
        <Identificacion>
          <NumeroIdentificacion>G05762690</NumeroIdentificacion>
          <TipoIdentificacion>Pasaporte</TipoIdentificacion>
        </Identificacion>
      </request>
    </ClienteBanreservas>
  </soap:Body>
</soap:Envelope>
```

## Documentación de Respuestas

##### Códigos de Respuesta

La API de ms-orq-consultar-datos-generales-cliente-micm utiliza varios códigos de respuesta para indicar el estado de la solicitud. A continuación se describen los códigos de respuesta específicos utilizados por la API:

- **Código de Respuesta:** 200
- **Descripción:** Success.

```xml
 <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <ClienteBanreservasResponse xmlns="http://tempuri.org/">
            <ClienteBanreservasResult>
                <Canal>InterTrade</Canal>
                <Usuario>Test</Usuario>
                <Terminal>172.22.50.250</Terminal>
                <FechaHora>2025-06-19T21:52:49.680935200</FechaHora>
                <TRN_ID>123</TRN_ID>
                <Resultado>0</Resultado>
                <Mensaje>Consulta exitosa</Mensaje>
                <Version>1</Version>
                <Nombres>MANUEL GONZALO</Nombres>
                <Apellidos>CERVERA PEREZ</Apellidos>
                <Apodo/>
                <PrimerNombre>MANUEL</PrimerNombre>
                <SegundoNombre>GONZALO</SegundoNombre>
                <PrimerApellido>CERVERA</PrimerApellido>
                <SegundoApellido>PEREZ</SegundoApellido>
                <Sexo>MASCULINO</Sexo>
                <EstadoCivil>SOLTERO/A</EstadoCivil>
                <TipoCliente>ASALARIADO PRIVADO</TipoCliente>
                <TipoPersona>P7</TipoPersona>
                <EsClienteTop>false</EsClienteTop>
                <EsEmpleadoGrupo>false</EsEmpleadoGrupo>
                <Estado>PENDIENTE</Estado>
                <CodigoActividadEconomica>950004</CodigoActividadEconomica>
                <DescripcionActividadEconomica>Empleados (asalariados)</DescripcionActividadEconomica>
                <IngresosAnuales>8000000</IngresosAnuales>
                <MonedaIngresos>DOP</MonedaIngresos>
                <LugarNacimiento>MÉXICO</LugarNacimiento>
                <PaginaWeb/>
                <CodigoPaisOrigen>MX</CodigoPaisOrigen>
                <PaisOrigen>MÉXICO</PaisOrigen>
                <PaisResidencia/>
                <Extranjero>true</Extranjero>
                <Nivelgeografico/>
                <Fallecido>false</Fallecido>
                <TipoResidencia>NO RESIDENTE</TipoResidencia>
                <PuntoAtencionEmisora/>
                <Supervisor/>
                <EjecutivoDeCuenta>58094</EjecutivoDeCuenta>
                <EstaEnListaNegra>false</EstaEnListaNegra>
                <RepresentanteLegal/>
                <SegmentoCliente>13</SegmentoCliente>
                <LimiteEndeudamiento>0.00</LimiteEndeudamiento>
                <CentroDeCosto>41250</CentroDeCosto>
                <Identificaciones>
                    <Identificacion>
                        <TipoIdentificacion>Cedula</TipoIdentificacion>
                    </Identificacion>
                </Identificaciones>
                <Direcciones>
                    <Direccion>
                        <Apartamento>23301</Apartamento>
                        <Calle>AVENIDA ALEMANIA</Calle>
                        <Casa>s/n</Casa>
                        <Ciudad>HIGÜEY</Ciudad>
                        <CodigoPais>DO</CodigoPais>
                        <CodigoPostal/>
                        <Edificio>MASA BUSINESS D</Edificio>
                        <EsPrincipal>true</EsPrincipal>
                        <IdLocalidad>110101</IdLocalidad>
                        <Pais>REPUBLICA DOMINICANA</Pais>
                        <Provincia>LA ALTAGRACIA</Provincia>
                        <RecibeEstados>false</RecibeEstados>
                        <Sector>3RE NIVEL, EL CORTECITO</Sector>
                        <TipoDireccion>RESIDENCIA</TipoDireccion>
                        <TipoLocal/>
                    </Direccion>
                    <Direccion>
                        <Apartamento>3</Apartamento>
                        <Calle>3</Calle>
                        <Casa>2</Casa>
                        <Ciudad>EL VALLE</Ciudad>
                        <CodigoPais>DO</CodigoPais>
                        <CodigoPostal/>
                        <Edificio>1</Edificio>
                        <EsPrincipal>false</EsPrincipal>
                        <IdLocalidad>300301</IdLocalidad>
                        <Pais>REPUBLICA DOMINICANA</Pais>
                        <Provincia>HATO MAYOR</Provincia>
                        <RecibeEstados>false</RecibeEstados>
                        <Sector/>
                        <TipoDireccion>LABORAL</TipoDireccion>
                        <TipoLocal/>
                    </Direccion>
                </Direcciones>
                <Telefonos>
                    <Telefono>
                        <Compania>Claro</Compania>
                        <EsPrincipal>false</EsPrincipal>
                        <Extension>0</Extension>
                        <Numero>8097545943</Numero>
                        <RecibeSms>true</RecibeSms>
                        <Tipo>TELÉFONO FIJO</Tipo>
                    </Telefono>
                </Telefonos>
                <Correos>
                    <Correo>
                        <Direccion>mpantaleon@banreservas.com</Direccion>
                        <EsPrincipal>true</EsPrincipal>
                        <RecibeEstados>false</RecibeEstados>
                    </Correo>
                </Correos>
            </ClienteBanreservasResult>
        </ClienteBanreservasResponse>
    </soap:Body>
</soap:Envelope>
```


- **Código de Respuesta:** 400
- **Descripción:** Bad Request, uno o más encabezados parametros están vacíos.

```xml
  <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <ClienteBanreservasResponse xmlns="http://tempuri.org/">
            <ClienteBanreservasResult xmlns="">
                <canal>InterTrade</canal>
                <usuario>Test</usuario>
                <terminal>172.22.50.250</terminal>
                <fechaHora>2025-06-19T23:47:24.786822</fechaHora>
                <resultado>1</resultado>
                <mensaje>El campo 'Identificacion.NumeroIdentificacion' es obligatorio</mensaje>
                <version>1</version>
                <TRN_ID>123</TRN_ID>
            </ClienteBanreservasResult>
        </ClienteBanreservasResponse>
    </soap:Body>
</soap:Envelope>
```

- **Código de Respuesta:** 401
- **Descripción:** Unauthorized, autenticación fallida.

```xml
  <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <ClienteBanreservasResponse xmlns="http://tempuri.org/">
            <ClienteBanreservasResult xmlns="">
                <canal>InterTrade</canal>
                <usuario>Test</usuario>
                <terminal>172.22.50.250</terminal>
                <fechaHora>2025-06-20T00:40:35.137226600</fechaHora>
                <resultado>1</resultado>
                <mensaje>Token expirado o invalido</mensaje>
                <version>1</version>
                <TRN_ID>123</TRN_ID>
            </ClienteBanreservasResult>
        </ClienteBanreservasResponse>
    </soap:Body>
</soap:Envelope>
```

- **Código de Respuesta:** 500
- **Descripción:** Error interno del servidor.

```xml
  <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <ClienteBanreservasResponse xmlns="http://tempuri.org/">
            <ClienteBanreservasResult>
                <Canal>UNKNOWN</Canal>
                <Usuario>UNKNOWN</Usuario>
                <Terminal>UNKNOWN</Terminal>
                <FechaHora>2025-06-20T04:30:15.123456789</FechaHora>
                <TRN_ID>123456</TRN_ID>
                <Resultado>1</Resultado>
                <Mensaje>Error interno del servidor</Mensaje>
                <Version>1.0</Version>
            </ClienteBanreservasResult>
        </ClienteBanreservasResponse>
    </soap:Body>
</soap:Envelope>
```

- **Código de Respuesta:** 400
- **Descripción:** Uno o más encabezados necesarios están vacíos.
```xml
   <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <ClienteBanreservasResponse xmlns="http://tempuri.org/">
            <ClienteBanreservasResult xmlns="">
                <canal>InterTrade</canal>
                <usuario>Test</usuario>
                <terminal>172.22.50.250</terminal>
                <fechaHora>2025-06-20T00:41:12.951138800</fechaHora>
                <resultado>1</resultado>
                <mensaje>La cabecera 'sessionId' es obligatoria.</mensaje>
                <version>1</version>
                <TRN_ID>unknown</TRN_ID>
            </ClienteBanreservasResult>
        </ClienteBanreservasResponse>
    </soap:Body>
</soap:Envelope>
```

- **Código de Respuesta:** 503
- **Descripción:** Error de conexión al servicio.

```xml
 <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <ClienteBanreservasResponse xmlns="http://tempuri.org/">
            <ClienteBanreservasResult>
                <Canal>InterTrade</Canal>
                <Usuario>Test</Usuario>
                <Terminal>172.22.50.250</Terminal>
                <FechaHora>2025-06-20T04:30:15.123456789</FechaHora>
                <TRN_ID>123456</TRN_ID>
                <Resultado>1</Resultado>
                <Mensaje>Servicio de cliente Banreservas temporalmente no disponible</Mensaje>
                <Version>1</Version>
            </ClienteBanreservasResult>
        </ClienteBanreservasResponse>
    </soap:Body>
</soap:Envelope>
```

Cobertura de pruebas unitarias:
![alt text](coverage.png)

## Configuración de Quarkus

##### Configuración de CXF para el Cliente SOAP

La integración con el servicio REST ms-orq-consultar-datos-generales-cliente-micm requiere la siguiente configuración para consumo:

```properties

banreservas.client.service.soap.address=/banreservasClient
banreservas.client.service.soap.endpoint=banreservasClient
banreservas.client.service.soap.logging=true
backend.banreservas.client.url=https://ms-cte-ban-dev.apps.az-aro-dev.banreservas.com/api/v1/cliente-banreservas
backend.service.timeout=35000
```

##### Exclusión de Clases en Jacoco

Para excluir ciertas clases del reporte de cobertura de código con Jacoco, utiliza la siguiente configuración:

```properties
quarkus.jacoco.excludes=**/org/tempuri/*.class,**/banreservas/integration/util/**,**/banreservas/integration/model/**,**/banreservas/integration/exceptions/**,**/banreservas/integration/processors/**
```
##### Configuración de swagger-ui

Para documentar tus API REST utilizando swagger-ui y hacerlas accesibles:

```properties
quarkus.swagger-ui.path=/banreservas/client/api/v1/swagger-ui
```

##### Configuración de Health Check

Monitorea la salud de tu aplicación utilizando SmallRye Health:

```properties
quarkus.smallrye-health.root-path=/banreservas/client/api/v1/health
```

##### Configuración de LogAppender (Auditoría)

Para configurar de manera correcta la auditoría a nivel de logs requerimos:

```properties
log.appender.applicationName=ms-orq-consultar-datos-generales-cliente-micm
log.appender.urlService=https://ms-audit-receiver-dev.apps.az-aro-dev.banreservas.com/audit/api/v1/auditReceiver
log.appender.ofuscado=NumeroIdentificacion[0:12]
log.appender.queue=audit-queue
```

##### WSDL

WSDL del servicio:

```xml
<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://tempuri.org/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" targetNamespace="http://tempuri.org/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="http://tempuri.org/">
      <s:element name="ConsultaFechaHabil">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaFechaHabilRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaFechaHabilRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="FechaEntrada" type="s:dateTime" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="RequestHeader">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Canal" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Usuario" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Terminal" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FechaHora" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Version" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="ConsultaFechaHabilResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaFechaHabilResult" type="tns:ConsultaFechaHabilResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaFechaHabilResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="FechaHabil" type="s:dateTime" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ResponseHeader">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Canal" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Usuario" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Terminal" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FechaHora" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TRN_ID" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="Resultado" type="s:short" />
          <s:element minOccurs="0" maxOccurs="1" name="Mensaje" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Version" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="ConsultarDatosGeneralesCliente">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultarDatosGeneralesClienteRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultarDatosGeneralesClienteRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
              <s:element minOccurs="1" maxOccurs="1" name="ForzarActualizar" type="s:boolean" />
              <s:element minOccurs="1" maxOccurs="1" name="IncluirFotoBinaria" type="s:boolean" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="Identificacion">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="NumeroIdentificacion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="TipoIdentificacion" type="tns:TiposDocumento" />
        </s:sequence>
      </s:complexType>
      <s:simpleType name="TiposDocumento">
        <s:restriction base="s:string">
          <s:enumeration value="Cedula" />
          <s:enumeration value="Pasaporte" />
          <s:enumeration value="RNC" />
          <s:enumeration value="PermId" />
          <s:enumeration value="GrupoEconomico" />
        </s:restriction>
      </s:simpleType>
      <s:element name="ConsultarDatosGeneralesClienteResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultarDatosGeneralesClienteResult" type="tns:ConsultarDatosGeneralesClienteResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultarDatosGeneralesClienteResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
              <s:element minOccurs="0" maxOccurs="1" name="Nombres" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PrimerApellido" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="SegundoApellido" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaNacimiento" type="s:dateTime" />
              <s:element minOccurs="0" maxOccurs="1" name="LugarNacimiento" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CedulaVieja" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Sexo" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="EstadoCivil" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Categoria" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CausaInhabilidad" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoCausaCancelacion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Estatus" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaCancelacion" nillable="true" type="s:dateTime" />
              <s:element minOccurs="0" maxOccurs="1" name="FotoUrl" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="FotoBinario" type="s:base64Binary" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="CuentasCliente">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:CuentasClienteRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="CuentasClienteRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="CuentasClienteResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="CuentasClienteResult" type="tns:CuentasClienteResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="CuentasClienteResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoCuentas" type="tns:ConsolidadoCuenta" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ConsolidadoCuenta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="Alias" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoDeAtencion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="Balance" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Disponible" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoAutorizado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="PuntosGanados" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="PuntosAcumulados" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoEstado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Producto">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="NumeroProducto" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="TipoProducto" type="tns:TiposProductos" />
          <s:element minOccurs="1" maxOccurs="1" name="Moneda" type="tns:Moneda" />
        </s:sequence>
      </s:complexType>
      <s:simpleType name="TiposProductos">
        <s:restriction base="s:string">
          <s:enumeration value="CuentaCorriente" />
          <s:enumeration value="CuentaAhorro" />
          <s:enumeration value="TarjetaCredito" />
          <s:enumeration value="Certificado" />
          <s:enumeration value="TarjetaDebito" />
          <s:enumeration value="Prestamo" />
          <s:enumeration value="CartaCredito" />
        </s:restriction>
      </s:simpleType>
      <s:simpleType name="Moneda">
        <s:restriction base="s:string">
          <s:enumeration value="DOP" />
          <s:enumeration value="USD" />
          <s:enumeration value="EUR" />
          <s:enumeration value="MULTIMONEDA" />
          <s:enumeration value="DEFAULT" />
        </s:restriction>
      </s:simpleType>
      <s:element name="DebitoCuenta">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:DebitoCuentaRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DebitoCuentaRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
              <s:element minOccurs="0" maxOccurs="1" name="Importe" type="tns:Importe" />
              <s:element minOccurs="0" maxOccurs="1" name="Descripcion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoTransaccion" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="Importe">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="Moneda" type="tns:Moneda" />
          <s:element minOccurs="1" maxOccurs="1" name="Monto" nillable="true" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:element name="DebitoCuentaResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="DebitoCuentaResult" type="tns:DebitoCuentaResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DebitoCuentaResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader" />
        </s:complexContent>
      </s:complexType>
      <s:element name="DetalleCuentas">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:DetalleCuentasRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetalleCuentasRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="DetalleCuentasResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="DetalleCuentasResult" type="tns:DetalleCuentasResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetalleCuentasResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cuenta" type="tns:ProductoCuenta" />
              <s:element minOccurs="0" maxOccurs="1" name="Saldos" type="tns:Saldos" />
              <s:element minOccurs="0" maxOccurs="1" name="Fechas" type="tns:FechasCuenta" />
              <s:element minOccurs="0" maxOccurs="1" name="Cliente" type="tns:ClienteProducto" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoEstado" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="EstadoCuenta" type="tns:EstadoDeCuenta" />
              <s:element minOccurs="0" maxOccurs="1" name="TransitoCuenta" type="tns:TransitoCuenta" />
              <s:element minOccurs="0" maxOccurs="1" name="LineaDeEfectivo" type="tns:LineaDeEfectivo" />
              <s:element minOccurs="0" maxOccurs="1" name="SobreGiro" type="tns:SobreGiro" />
              <s:element minOccurs="0" maxOccurs="1" name="MontosBloqueados" type="tns:ArrayOfMontoBloqueado" />
              <s:element minOccurs="0" maxOccurs="1" name="Relacionados" type="tns:ArrayOfRelacionado" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ProductoCuenta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="1" maxOccurs="1" name="CodigoTipoProducto" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="DescripcionProducto" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoEstado" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="ClienteImportante" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoDeAtencion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="AliasCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoOficialCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="MotivoCancelacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NombreEjecutivoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="SubtituloCuenta" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Saldos">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="SaldoHoy" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoEfectivoHoy" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DepositoChequesHoy" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoDisponible" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoMinimo" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="BalanceAyer" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Tasa" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresAcumulado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresPeriodoAnterior" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoUltimoPosteo" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoPromedioAnterior" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionCierre" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionRetirosMensuales" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="BalanceTotalAyer" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoPromedioAnual" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DepositosUltimos6Meses" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CreditosUlt6Meses" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="SaldosPromedios" type="tns:ArrayOfDecimal" />
          <s:element minOccurs="0" maxOccurs="1" name="DepositosMes" type="tns:ArrayOfDecimal1" />
          <s:element minOccurs="0" maxOccurs="1" name="CreditosMes" type="tns:ArrayOfDecimal2" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfDecimal">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="SaldoPromedio" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfDecimal1">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="DepositoMes" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfDecimal2">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="CreditoMes" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="FechasCuenta">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="FechaReApertura" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaUltimaActividad" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaUltimoPosteo" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaCierre" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaCorte" type="s:dateTime" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ClienteProducto">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="NombresCliente" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ApellidosCliente" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="RazonSocial" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Identificaciones" type="tns:ArrayOfIdentificacion" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfIdentificacion">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Identificacion" nillable="true" type="tns:Identificacion" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="EstadoDeCuenta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="FrecuenciaEmisionEstadoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="MetodoEntregaEstadoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="DireccionEntregaEstadoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="EmailEntregaEstadoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="EstadoUltimoEnvioEstadoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="RetencionEstadosCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="OficinaEntregaEstadoCuenta" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="TransitoCuenta">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="SaldoAutorizadoTransito" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoUtilizadoTransito" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoEnTransito" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="DetallesTransito" type="tns:ArrayOfDetallesTransito" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfDetallesTransito">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="DetallesTransito" nillable="true" type="tns:DetallesTransito" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="DetallesTransito">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="DiasTransito" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="Dias" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="Monto" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="DetalleChequeTransito" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="LineaDeEfectivo">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="EstadoLineaEfectivo" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoAprobadoLineaEfectivo" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoDisponibleLineaEfectivo" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaVencimientoLineaEfectivo" type="s:dateTime" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="SobreGiro">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="SobreGiroVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DiasSobregiroVencido" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresPendienteSobregiro" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionPendienteSobregiroVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionesPendienteSobregiro" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TotalSobregiro" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfMontoBloqueado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="MontoBloqueado" nillable="true" type="tns:MontoBloqueado" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="MontoBloqueado">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="IdentificadorBloque" nillable="true" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaBloqueoInicio" nillable="true" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaBloqueoFinal" nillable="true" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="Monto" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="DescripcionBloqueo" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfRelacionado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Relacionado" nillable="true" type="tns:Relacionado" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Relacionado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="NombresRelacionado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ApellidosRelacionado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoRelacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="IdentificacionRelacionado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoIdentificacionRelacionado" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="DetallePrestamo">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:DetallePrestamoRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetallePrestamoRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="DetallePrestamoResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="DetallePrestamoResult" type="tns:DetallePrestamoResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetallePrestamoResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Prestamo" type="tns:ProductoPrestamos" />
              <s:element minOccurs="0" maxOccurs="1" name="CuentaDebitar" type="tns:Producto" />
              <s:element minOccurs="0" maxOccurs="1" name="Saldos" type="tns:SaldosPrestamos" />
              <s:element minOccurs="0" maxOccurs="1" name="Cuotas" type="tns:CuotasPrestamos" />
              <s:element minOccurs="0" maxOccurs="1" name="Fechas" type="tns:FechasPrestamos" />
              <s:element minOccurs="0" maxOccurs="1" name="Cliente" type="tns:ClienteProducto" />
              <s:element minOccurs="0" maxOccurs="1" name="Relacionados" type="tns:ArrayOfRelacionado" />
              <s:element minOccurs="1" maxOccurs="1" name="InteresesGenerados" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="Mora" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="OtrosAdeudados" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="Cuota" type="s:int" />
              <s:element minOccurs="0" maxOccurs="1" name="Oficial" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="Tasa" type="s:decimal" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ProductoPrestamos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoLineaCredito" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoTipoProducto" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="DescripcionProducto" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="AliasCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoAtencion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NombreEjecutivoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NumeroEjecutivoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Segmento" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="SectorPrestamo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ClasificacionRiesgo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Cobro" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CuentaCatalogo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CuentaCIIU" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CuentaDestinoSIB" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FormulaPago" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FrecuenciaPago" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="DiaPago" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="MesesGracia" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoGracia" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoGarantia" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="QuincenaTipo" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="TasaInteres" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TasaMora" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Plazo" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="CuentaDebitar" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoEstado" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="SaldosPrestamos">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="DeudaActual" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DeudaVencida" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CapitalAdeudado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CapitalFacturadoNoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CapitalFacturadoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionAdeudada" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionDevengadoNoFact" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionFacturadoNoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ComisionFacturadoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="GastosAdeudado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="GastosDevengadoNoFact" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="GastosFacturadoNoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="GastosFacturadoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoAprobado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoCuotas" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoDesembolsado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MoraAdeudada" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MoraDevengadoNoFact" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MoraFacturadoNoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MoraFacturadoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresAdeudado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresDevengadoNoFact" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresFacturadoNoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresFacturadoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TotalAdeudado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TotalDevengadoNoFact" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TotalFacturadoNoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TotalFacturadoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ParteCapital" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ParteComision" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ParteInteres" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="ParteMora" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoTeorico" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="CuotasPrestamos">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="CuotaRegular" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Cuotas" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CuotasPendientes" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CuotasVencidas" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Facturas" type="s:boolean" />
          <s:element minOccurs="1" maxOccurs="1" name="NumeroFacturasImpagas" type="s:int" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="FechasPrestamos">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="FechaDesembolso" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaPago" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaUltPago" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="UltimaModificacionTasa" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="UltimaProrroga" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="UltimaRenovacion" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="ProximoPago" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaSaldo" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaUltimoCierre" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaVencimiento" type="s:dateTime" />
        </s:sequence>
      </s:complexType>
      <s:element name="ListadoOficinas">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ListadoOficinasRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ListadoOficinasRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Oficina" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ListadoOficinasResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ListadoOficinasResult" type="tns:ListadoOficinasResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ListadoOficinasResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Oficinas" type="tns:ArrayOfOficina" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfOficina">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Oficina" nillable="true" type="tns:Oficina" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Oficina">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="CodigoOficina" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="DescripcionOficina" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="DireccionOficina" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="NominaElectronica">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:NominaElectronicaRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="NominaElectronicaRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="NominaElectronicaResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="NominaElectronicaResult" type="tns:NominaElectronicaResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="NominaElectronicaResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cuentas" type="tns:ArrayOfCuentas" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfCuentas">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Cuenta" nillable="true" type="tns:Cuentas" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Cuentas">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="Titular" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="SubTitular" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="IdentificacionEmpresa" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="PosicionConsolidada">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:PosicionConsolidadaRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="PosicionConsolidadaRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="PosicionConsolidadaResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="PosicionConsolidadaResult" type="tns:PosicionConsolidadaResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="PosicionConsolidadaResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cuentas" type="tns:ArrayOfConsolidadoCuenta" />
              <s:element minOccurs="0" maxOccurs="1" name="Tarjetas" type="tns:ArrayOfConsolidadoTarjeta" />
              <s:element minOccurs="0" maxOccurs="1" name="Prestamos" type="tns:ArrayOfConsolidadoPrestamo" />
              <s:element minOccurs="0" maxOccurs="1" name="Certificados" type="tns:ArrayOfConsolidadoCertificado" />
              <s:element minOccurs="0" maxOccurs="1" name="TarjetasDebito" type="tns:ArrayOfConsolidadoTarjetaDebito" />
              <s:element minOccurs="0" maxOccurs="1" name="CartasCredito" type="tns:ArrayOfConsolidadoCartaCredito" />
              <s:element minOccurs="0" maxOccurs="1" name="LineasCredito" type="tns:ArrayOfConsolidadoLineaCredito" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfConsolidadoCuenta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoCuenta" nillable="true" type="tns:ConsolidadoCuenta" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfConsolidadoTarjeta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoTarjeta" nillable="true" type="tns:ConsolidadoTarjeta" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ConsolidadoTarjeta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoDeAtencion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="BalanceRD" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DisponibleRD" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="BalanceUS" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DisponibleUS" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="VencimientoPago" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="PendienteActivacion" type="s:boolean" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresALaFecha" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresALaFechaUS" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="BalanceCorte" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="PuntosGanados" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="PuntosAcumulados" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="UltimaActividad" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NombrePlastico" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaApertura" nillable="true" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" name="CodMotivoBloqueo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="MotivoBloqueo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfConsolidadoPrestamo">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoPrestamo" nillable="true" type="tns:ConsolidadoPrestamo" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ConsolidadoPrestamo">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="Alias" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoDeAtencion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ClientePrestamos" type="tns:ClientePrestamos" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoDesembolsado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoAprobado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoInicial" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Cuotas" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="DeudaActual" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DeudaVencida" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaProximoPago" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresALaFecha" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoCuotas" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CuotasPendientes" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Tasa" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TasaMora" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NombreEjecutivoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NumeroEjecutivoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoOrigenFondos" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="DetalleOrigenFondos" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ClientePrestamos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="NombresCliente" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ApellidosCliente" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="RazonSocial" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Identificaciones" type="tns:ArrayOfIdentificacion" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfConsolidadoCertificado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoCertificado" nillable="true" type="tns:ConsolidadoCertificado" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ConsolidadoCertificado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="Alias" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoDeAtencion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoActual" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TasaVigente" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaVencimiento" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoInicial" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresMes" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfConsolidadoTarjetaDebito">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoTarjetaDebito" nillable="true" type="tns:ConsolidadoTarjetaDebito" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ConsolidadoTarjetaDebito">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoDeAtencion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Token" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaEmision" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="LimiteATM" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="LimitePOS" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="CuentaDesignada" type="tns:Producto" />
          <s:element minOccurs="1" maxOccurs="1" name="Estado" type="tns:EstadosTarjetaDebito" />
          <s:element minOccurs="0" maxOccurs="1" name="DescripcionEstado" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:simpleType name="EstadosTarjetaDebito">
        <s:restriction base="s:string">
          <s:enumeration value="Normal" />
          <s:enumeration value="MaximoIntentosFallidosExcedido" />
          <s:enumeration value="NoEmitida" />
          <s:enumeration value="Expirada" />
          <s:enumeration value="Perdida" />
          <s:enumeration value="Robada" />
          <s:enumeration value="CerradaPorElCliente" />
          <s:enumeration value="CanceladaPorElBanco" />
          <s:enumeration value="UsoFraudulento" />
          <s:enumeration value="Inactiva" />
          <s:enumeration value="Bloqueda" />
          <s:enumeration value="Deteriorada" />
        </s:restriction>
      </s:simpleType>
      <s:complexType name="ArrayOfConsolidadoCartaCredito">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoCartaCredito" nillable="true" type="tns:ConsolidadoCartaCredito" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ConsolidadoCartaCredito">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Importe" type="tns:Importe" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresALaFecha" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfConsolidadoLineaCredito">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ConsolidadoLineaCredito" nillable="true" type="tns:ConsolidadoLineaCredito" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ConsolidadoLineaCredito">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoDeAtencion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaApertura" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaVencimiento" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoTotal" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoAdeudado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Disponible" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="VerificarListasNegras">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:VerificarListasNegrasRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="VerificarListasNegrasRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
              <s:element minOccurs="0" maxOccurs="1" name="PrimerNombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="SegundoNombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PrimerApellido" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="SegundoApellido" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Nacionalidad" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="FechaNacimiento" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="VerificarListasNegrasResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="VerificarListasNegrasResult" type="tns:VerificarListasNegrasResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="VerificarListasNegrasResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="Coincidencia" type="s:boolean" />
              <s:element minOccurs="0" maxOccurs="1" name="Listas" type="tns:ArrayOfLista" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfLista">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Lista" nillable="true" type="tns:Lista" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Lista">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Nombre" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Origen" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="ConsultaClienteSignature">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaClienteSignatureRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaClienteSignatureRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaClienteSignatureResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaClienteSignatureResult" type="tns:ConsultaClienteSignatureResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaClienteSignatureResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificaciones" type="tns:ArrayOfIdentificacion" />
              <s:element minOccurs="0" maxOccurs="1" name="NombreCliente" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Oficina" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaNacimiento" nillable="true" type="s:dateTime" />
              <s:element minOccurs="0" maxOccurs="1" name="EstadoCliente" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoPostal" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="NumerosTelefono" type="tns:ArrayOfNumerosTelefono" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfNumerosTelefono">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="NumerosTelefono" nillable="true" type="tns:NumerosTelefono" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="NumerosTelefono">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="NumeroTelefono" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoTelefono" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="ClienteBanreservas">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ClienteBanreservasRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ClienteBanreservasRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ClienteBanreservasResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ClienteBanreservasResult" type="tns:ClienteBanreservasResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ClienteBanreservasResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Nombres" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Apellidos" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Apodo" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PrimerNombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="SegundoNombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PrimerApellido" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="SegundoApellido" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Sexo" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaNacimiento" type="s:dateTime" />
              <s:element minOccurs="0" maxOccurs="1" name="EstadoCivil" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="TipoCliente" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="TipoPersona" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="EsClienteTop" type="s:boolean" />
              <s:element minOccurs="1" maxOccurs="1" name="EsEmpleadoGrupo" type="s:boolean" />
              <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoActividadEconomica" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="DescripcionActividadEconomica" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="IngresosAnuales" type="s:decimal" />
              <s:element minOccurs="0" maxOccurs="1" name="MonedaIngresos" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="LugarNacimiento" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PaginaWeb" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoPaisOrigen" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PaisOrigen" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PaisResidencia" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="OtroPaisResidencia" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="Extranjero" type="s:boolean" />
              <s:element minOccurs="0" maxOccurs="1" name="Nivelgeografico" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="Fallecido" type="s:boolean" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaFallecimiento" type="s:dateTime" />
              <s:element minOccurs="0" maxOccurs="1" name="TipoResidencia" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="AfectoaISR" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="EmpresaApertura" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="PuntoAtencionEmisora" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Supervisor" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="EjecutivoDeCuenta" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="EstaEnListaNegra" type="s:boolean" />
              <s:element minOccurs="0" maxOccurs="1" name="RepresentanteLegal" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="SegmentoCliente" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="LimiteEndeudamiento" type="s:decimal" />
              <s:element minOccurs="0" maxOccurs="1" name="CentroDeCosto" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Identificaciones" type="tns:ArrayOfIdentificacion" />
              <s:element minOccurs="0" maxOccurs="1" name="Direcciones" type="tns:ArrayOfDireccionesDatos" />
              <s:element minOccurs="0" maxOccurs="1" name="Telefonos" type="tns:ArrayOfTelefonos" />
              <s:element minOccurs="0" maxOccurs="1" name="Correos" type="tns:ArrayOfCorreos" />
              <s:element minOccurs="0" maxOccurs="1" name="SistemasRelacionados" type="tns:ArrayOfSistemaRelacionado" />
              <s:element minOccurs="0" maxOccurs="1" name="PEPs" type="tns:ArrayOfPEPRelacionado" />
              <s:element minOccurs="0" maxOccurs="1" name="ActividadEconomica" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfDireccionesDatos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Direccion" nillable="true" type="tns:DireccionesDatos" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="DireccionesDatos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Apartamento" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Calle" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Casa" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Ciudad" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoPais" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoPostal" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Edificio" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="EsPrincipal" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" name="IdLocalidad" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Pais" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Provincia" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="RecibeEstados" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" name="Sector" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoDireccion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoLocal" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfTelefonos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Telefono" nillable="true" type="tns:Telefonos" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Telefonos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Compania" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="EsPrincipal" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" name="Extension" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Numero" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="RecibeSms" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" name="Tipo" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfCorreos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Correo" nillable="true" type="tns:Correos" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Correos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Direccion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="EsPrincipal" type="s:boolean" />
          <s:element minOccurs="1" maxOccurs="1" name="RecibeEstados" type="s:boolean" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfSistemaRelacionado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="SistemaRelacionado" nillable="true" type="tns:SistemaRelacionado" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="SistemaRelacionado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Sistema" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TiposProductos" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfPEPRelacionado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="PEP" nillable="true" type="tns:PEPRelacionado" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="PEPRelacionado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Nombres" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Apellidos" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoRelacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoIdentificacion" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="VerificaClienteDataCredito">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:VerificaClienteDataCreditoRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="VerificaClienteDataCreditoRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="TipoIdentificacion" type="tns:TiposDocumento" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="VerificaClienteDataCreditoResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="VerificaClienteDataCreditoResult" type="tns:VerificaClienteDataCreditoResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="VerificaClienteDataCreditoResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="ReporteCreditoIndividuo" type="tns:ReporteCreditoIndividuo" />
              <s:element minOccurs="0" maxOccurs="1" name="XMLResponse" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ReporteCreditoIndividuo">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="DatosGenerales" type="tns:DatosGenerales" />
          <s:element minOccurs="0" maxOccurs="1" name="InformacionAdicional" type="tns:InformacionAdicional" />
          <s:element minOccurs="0" maxOccurs="1" name="DireccionesTelefonos" type="tns:DireccionesTelefonos" />
          <s:element minOccurs="0" maxOccurs="1" name="Direcciones" type="tns:Direcciones" />
          <s:element minOccurs="0" maxOccurs="1" name="Telefonos" type="tns:ArrayOfDataCreditoTelefono" />
          <s:element minOccurs="0" maxOccurs="1" name="ResumenTransacciones" type="tns:ArrayOfResumenTrxs" />
          <s:element minOccurs="0" maxOccurs="1" name="Activas" type="tns:ArrayOfTransac" />
          <s:element minOccurs="0" maxOccurs="1" name="Inactivas" type="tns:ArrayOfTransac" />
          <s:element minOccurs="0" maxOccurs="1" name="Indagaciones" type="tns:ArrayOfInquiry" />
          <s:element minOccurs="0" maxOccurs="1" name="VariablesBR" type="tns:VariablesBR" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="DatosGenerales">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="PrimerNombre" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PrimerApellido" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="SegundoApellido" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CedulaNueva" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CedulaVieja" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Sexo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="LugarNacimiento" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FechaNacimiento" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="EstadoCivil" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Edad" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Ocupacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Categoria" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Padre" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Madre" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="InformacionAdicional">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="ColorPiel" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Nacionalidad" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FechaExpiracionCedula" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="DireccionesTelefonos">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="DirTel" type="tns:ArrayOfDirTel" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfDirTel">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="DirTel" nillable="true" type="tns:DirTel" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="DirTel">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Fecha" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoTel" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Dir" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Tel" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Direcciones">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Dir" type="s:string" />
        </s:sequence>
        <s:attribute name="Fecha" type="s:string" />
      </s:complexType>
      <s:complexType name="ArrayOfDataCreditoTelefono">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="DataCreditoTelefono" nillable="true" type="tns:DataCreditoTelefono" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="DataCreditoTelefono">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="TipoTel" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Tel" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Suscriptor" type="s:string" />
        </s:sequence>
        <s:attribute name="Fecha" type="s:string" />
      </s:complexType>
      <s:complexType name="ArrayOfResumenTrxs">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="ResumenTrxs" nillable="true" type="tns:ResumenTrxs" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ResumenTrxs">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Categoria" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Suscriptor" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CantidadCuentas" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="LimiteCredito" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Balance" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PctUtilizacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="MontoAtraso" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="LimiteCreditoUS" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="BalanceUS" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PctUtilizacionUS" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="MontoAtrasoUS" type="s:string" />
        </s:sequence>
        <s:attribute name="Moneda" type="s:string" />
      </s:complexType>
      <s:complexType name="ArrayOfTransac">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Transac" nillable="true" type="tns:Transac" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Transac">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Categoria" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Suscriptor" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_FechaUltimaAct" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_LimiteCredito" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_Balance" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_MontoAtraso" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_Estatus" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_Comportamiento" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_NoCuotas" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="U_MontoCuotas" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FechaApertura" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FechaUltimaAct" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="LimiteCredito" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Balance" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="MontoAtraso" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estatus" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Comportamiento" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NoCuotas" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="MontoCuotas" type="s:string" />
        </s:sequence>
        <s:attribute name="U_30" type="s:string" />
        <s:attribute name="U_60" type="s:string" />
        <s:attribute name="U_90" type="s:string" />
        <s:attribute name="U_120" type="s:string" />
        <s:attribute name="U_150" type="s:string" />
        <s:attribute name="D30" type="s:string" />
        <s:attribute name="D60" type="s:string" />
        <s:attribute name="D90" type="s:string" />
        <s:attribute name="D120" type="s:string" />
        <s:attribute name="D150" type="s:string" />
      </s:complexType>
      <s:complexType name="ArrayOfInquiry">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Inquiry" nillable="true" type="tns:Inquiry" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Inquiry">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="FechaIndagacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Suscriptor" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Hora" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Tipo" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="VariablesBR">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="SumCuotas" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoCred" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="AntHist" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ExpPago" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PoseeTC" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="AtrasosActTC" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ActJud" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="SobregirosTC" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="D30" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="D60" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="D90" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="D120" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="AcuerdoP" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CuentasCastigadas" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ExpCredFin" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="DecisionAutomatica" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Score2" type="tns:Score2" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Score2">
        <s:simpleContent>
          <s:extension base="s:string">
            <s:attribute name="Rsc1" type="s:string" />
            <s:attribute name="Rsc2" type="s:string" />
            <s:attribute name="Rsc3" type="s:string" />
          </s:extension>
        </s:simpleContent>
      </s:complexType>
      <s:element name="DetalleCertificado">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:DetalleCertificadoRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetalleCertificadoRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="DetalleCertificadoResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="DetalleCertificadoResult" type="tns:DetalleCertificadoResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetalleCertificadoResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Certificado" type="tns:ProductoCertificado" />
              <s:element minOccurs="0" maxOccurs="1" name="Saldos" type="tns:SaldosCertificados" />
              <s:element minOccurs="0" maxOccurs="1" name="Fechas" type="tns:FechasCertificado" />
              <s:element minOccurs="0" maxOccurs="1" name="Intereses" type="tns:InteresesCertificado" />
              <s:element minOccurs="0" maxOccurs="1" name="MontosBloqueados" type="tns:ArrayOfMontoBloqueado" />
              <s:element minOccurs="0" maxOccurs="1" name="Cliente" type="tns:ClienteProducto" />
              <s:element minOccurs="0" maxOccurs="1" name="Relacionados" type="tns:ArrayOfRelacionado" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ProductoCertificado">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="FormaPago" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="AliasCuenta" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="CodigoTipoProducto" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="DescripcionProducto" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoEstado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="DiasTermino" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NombreEjecutivoCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="OficialCuenta" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PuntoAtencion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FormaPagoIntereses" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NumeroDocumento" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="NumRenovaciones" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="CuentaAcreditar" type="tns:Producto" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="SaldosCertificados">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="SaldoActual" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoCancelacion" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoEmbargos" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoFianzas" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoInicial" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoPenalizacion" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="SaldoTransito" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TasaCancelacion" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="FechasCertificado">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="FechaApertura" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaCancelacion" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaDespignoracion" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaEmisionCheque" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaPignoracion" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaRenovacion" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaRevisionTasa" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaVencimiento" type="s:dateTime" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="InteresesCertificado">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="InteresesAcumulados" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresesCapitalizados" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresesMes" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresesMesAnterior" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresesNoCapitalizados" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresesPagados" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresesPorPagar" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="InteresesRetenidos" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TasaInteres" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:element name="GenerarPinTarjetaCodigo">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:GenerarPinTarjetaCodigoRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="GenerarPinTarjetaCodigoRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Tarjeta" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="GenerarPinTarjetaCodigoResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="GenerarPinTarjetaCodigoResult" type="tns:GenerarPinTarjetaCodigoResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="GenerarPinTarjetaCodigoResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Codigos" type="tns:ArrayOfCodigo" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfCodigo">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Codigo" nillable="true" type="tns:Codigo" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Codigo">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="Posicion" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="Valor" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="MovimientosTarjeta">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:MovimientosTarjetaRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="MovimientosTarjetaRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
              <s:element minOccurs="1" maxOccurs="1" name="Moneda" type="tns:Moneda" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaInicial" type="s:dateTime" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaFinal" type="s:dateTime" />
              <s:element minOccurs="1" maxOccurs="1" name="MontoInicial" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="MontoFinal" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="TipoTransaccion" type="tns:TiposTransacciones" />
              <s:element minOccurs="1" maxOccurs="1" name="CantidadMovimientos" type="s:int" />
              <s:element minOccurs="0" maxOccurs="1" name="IdMovimiento" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="Direccion" type="tns:DireccionPaginacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:simpleType name="TiposTransacciones">
        <s:restriction base="s:string">
          <s:enumeration value="Debito" />
          <s:enumeration value="Credito" />
          <s:enumeration value="Ambos" />
        </s:restriction>
      </s:simpleType>
      <s:simpleType name="DireccionPaginacion">
        <s:restriction base="s:string">
          <s:enumeration value="Proximo" />
          <s:enumeration value="Anterior" />
        </s:restriction>
      </s:simpleType>
      <s:element name="MovimientosTarjetaResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="MovimientosTarjetaResult" type="tns:MovimientosTarjetaResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="MovimientosTarjetaResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="UltimaPagina" type="s:boolean" />
              <s:element minOccurs="0" maxOccurs="1" name="Movimientos" type="tns:ArrayOfMovimientoTarjeta" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfMovimientoTarjeta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="MovimientoTarjeta" nillable="true" type="tns:MovimientoTarjeta" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="MovimientoTarjeta">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="NroTransaccion" type="s:long" />
          <s:element minOccurs="1" maxOccurs="1" name="Fecha" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="Debito" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Credito" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="Concepto" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="Balance" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Moneda" type="tns:Moneda" />
          <s:element minOccurs="0" maxOccurs="1" name="Referencia" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Descripcion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="IdPaginacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Oficina" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaReal" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" name="Causal" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="TarjetasCliente">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:TarjetasClienteRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="TarjetasClienteRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="TarjetasClienteResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="TarjetasClienteResult" type="tns:TarjetasClienteResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="TarjetasClienteResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="ConsolidadoTarjetas" type="tns:ArrayOfConsolidadoTarjeta" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="DetalleTarjeta">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:DetalleTarjetaRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetalleTarjetaRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="DetalleTarjetaResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="DetalleTarjetaResult" type="tns:DetalleTarjetaResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="DetalleTarjetaResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
              <s:element minOccurs="1" maxOccurs="1" name="RsAcumuladas" type="s:int" />
              <s:element minOccurs="0" maxOccurs="1" name="Detalle" type="tns:ArrayOfDetalleTarjeta" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaCorte" type="s:dateTime" />
              <s:element minOccurs="1" maxOccurs="1" name="PageAntesDe" type="s:dateTime" />
              <s:element minOccurs="1" maxOccurs="1" name="MontoUltimoPago" type="s:decimal" />
              <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Nombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
              <s:element minOccurs="1" maxOccurs="1" name="LimiteCredimas" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="CapitaPendienteCredimas" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="DisponibleCredimas" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="PagoPendienteCredimas" type="s:decimal" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaApertura" nillable="true" type="s:dateTime" />
              <s:element minOccurs="1" maxOccurs="1" name="FechaCancelacion" nillable="true" type="s:dateTime" />
              <s:element minOccurs="0" maxOccurs="1" name="NombrePlastico" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="TipoProducto" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="TarjetaAdicional" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodMotivoBloqueo" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="MotivoBloqueo" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="Corporacion" type="s:boolean" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfDetalleTarjeta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="DetalleTarjeta" nillable="true" type="tns:DetalleTarjeta" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="DetalleTarjeta">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="Moneda" type="tns:Moneda" />
          <s:element minOccurs="1" maxOccurs="1" name="Limite" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="BalanceCorte" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="TotalAutorizaciones" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="BalanceUltEstado" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CuotasVencidas" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoVencido" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="PagoMinimo" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="DisponibleConsumo" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaUltActividad" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaUltPago" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoUltPago" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="LimiteCliente" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="BalancealaFecha" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Tasa" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoTransito" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="CargoBonificable" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:element name="ConsultarCodigosCheques">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultarCodigosChequesRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultarCodigosChequesRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cheques" type="tns:Cheques" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="Cheques">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="NumeroCheque" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="ConsultarCodigosChequesResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultarCodigosChequesResult" type="tns:ConsultarCodigosChequesResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultarCodigosChequesResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="CodigosCheques" type="tns:ArrayOfCodigoCheque" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfCodigoCheque">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="CodigoCheque" nillable="true" type="tns:CodigoCheque" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="CodigoCheque">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="NumeroCheque" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Codigo" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="CrearCodigosCheques">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:CrearCodigosChequesRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="CrearCodigosChequesRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cheques" type="tns:ArrayOfCheque" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfCheque">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Cheque" nillable="true" type="tns:Cheque" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Cheque">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Numero" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Codigo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Tipo" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="CrearCodigosChequesResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="CrearCodigosChequesResult" type="tns:CrearCodigosChequesResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="CrearCodigosChequesResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader" />
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaBancosCorresponsales">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaBancosCorresponsalesRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaBancosCorresponsalesRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="Tipo" type="tns:TipoEntidad" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:simpleType name="TipoEntidad">
        <s:restriction base="s:string">
          <s:enumeration value="Local" />
          <s:enumeration value="PuestoBolsa" />
          <s:enumeration value="Regional" />
        </s:restriction>
      </s:simpleType>
      <s:element name="ConsultaBancosCorresponsalesResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaBancosCorresponsalesResult" type="tns:ConsultaBancosCorresponsalesResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaBancosCorresponsalesResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="BancosCorresponsales" type="tns:ArrayOfBancoCorresponsal" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfBancoCorresponsal">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="BancoCorresponsal" nillable="true" type="tns:BancoCorresponsal" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="BancoCorresponsal">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Pais" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoSwift" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="NombreBanco" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoBancoCorresponsal" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="Moneda" type="tns:Moneda" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoACH" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoPais" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoBIC" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="CatalogoPagosServicios">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:CatalogoPagosServiciosRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="CatalogoPagosServiciosRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader" />
        </s:complexContent>
      </s:complexType>
      <s:element name="CatalogoPagosServiciosResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="CatalogoPagosServiciosResult" type="tns:CatalogoPagosServiciosResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="CatalogoPagosServiciosResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Servicios" type="tns:ArrayOfServiciosApp" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfServiciosApp">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Servicio" nillable="true" type="tns:ServiciosApp" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ServiciosApp">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Categoria" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoServicio" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Nombre" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="FormaPago" type="tns:FormasPago" />
          <s:element minOccurs="1" maxOccurs="1" name="TipoOperacion" type="tns:TipoOperacion" />
          <s:element minOccurs="0" maxOccurs="1" name="ServicioConsultar" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ServicioEjecutar" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:simpleType name="FormasPago">
        <s:restriction base="s:string">
          <s:enumeration value="Cuenta" />
          <s:enumeration value="TarjetaCredito" />
          <s:enumeration value="Todos" />
        </s:restriction>
      </s:simpleType>
      <s:simpleType name="TipoOperacion">
        <s:restriction base="s:string">
          <s:enumeration value="Pago" />
          <s:enumeration value="Recarga" />
          <s:enumeration value="Impuesto" />
        </s:restriction>
      </s:simpleType>
      <s:element name="ConsultaGeneralProducto">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaGeneralProductoRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaGeneralProductoRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaGeneralProductoResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaGeneralProductoResult" type="tns:ConsultaGeneralProductoResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaGeneralProductoResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Nombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
              <s:element minOccurs="1" maxOccurs="1" name="Moneda" type="tns:Moneda" />
              <s:element minOccurs="0" maxOccurs="1" name="FechaExpiracion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="TipoProducto" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="UltimosMovimientosCuentasTarjetas">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:UltimosMovimientosCuentasTarjetasRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="UltimosMovimientosCuentasTarjetasRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
              <s:element minOccurs="1" maxOccurs="1" name="CantidadMovimientos" type="s:int" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="UltimosMovimientosCuentasTarjetasResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="UltimosMovimientosCuentasTarjetasResult" type="tns:UltimosMovimientosCuentasTarjetasResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="UltimosMovimientosCuentasTarjetasResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Movimientos" type="tns:ArrayOfUltimoMovimientoCuentaTarjeta" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfUltimoMovimientoCuentaTarjeta">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="UltimoMovimientoCuentaTarjeta" nillable="true" type="tns:UltimoMovimientoCuentaTarjeta" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="UltimoMovimientoCuentaTarjeta">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="NroTransaccion" type="s:long" />
          <s:element minOccurs="1" maxOccurs="1" name="Debito" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Credito" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Fecha" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" name="Concepto" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="BalanceDespuesMovimientos" type="tns:Importe" />
        </s:sequence>
      </s:complexType>
      <s:element name="UltimosMovimientosPrestamos">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:UltimosMovimientosPrestamosRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="UltimosMovimientosPrestamosRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
              <s:element minOccurs="1" maxOccurs="1" name="CantidadMovimientos" type="s:int" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="UltimosMovimientosPrestamosResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="UltimosMovimientosPrestamosResult" type="tns:UltimosMovimientosPrestamosResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="UltimosMovimientosPrestamosResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Movimientos" type="tns:ArrayOfUltimoMovimientoPrestamo" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfUltimoMovimientoPrestamo">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="UltimoMovimientoPrestamo" nillable="true" type="tns:UltimoMovimientoPrestamo" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="UltimoMovimientoPrestamo">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="Mora" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Capital" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Interes" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Fecha" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" name="Concepto" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="MontoTotal" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:element name="ValidaCodigoCheque">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ValidaCodigoChequeRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ValidaCodigoChequeRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cheque" type="tns:ChequeValida" />
              <s:element minOccurs="0" maxOccurs="1" name="Oficina" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ChequeValida">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Numero" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoSeguridad" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="Monto" type="s:decimal" />
        </s:sequence>
      </s:complexType>
      <s:element name="ValidaCodigoChequeResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ValidaCodigoChequeResult" type="tns:ValidaCodigoChequeResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ValidaCodigoChequeResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cheque" type="tns:ChequeValidaResponse" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ChequeValidaResponse">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="Valido" type="s:boolean" />
        </s:sequence>
      </s:complexType>
      <s:element name="ValidaLoteCodigoCheque">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ValidaLoteCodigoChequeRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ValidaLoteCodigoChequeRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cheques" type="tns:ArrayOfChequeValidaLote" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfChequeValidaLote">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Cheque" nillable="true" type="tns:ChequeValidaLote" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ChequeValidaLote">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Numero" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="0" maxOccurs="1" name="CodigoSeguridad" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="LecturaCodigo" type="s:boolean" />
          <s:element minOccurs="1" maxOccurs="1" name="Monto" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="Oficina" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="ValidaLoteCodigoChequeResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ValidaLoteCodigoChequeResult" type="tns:ValidaLoteCodigoChequeResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ValidaLoteCodigoChequeResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Cheques" type="tns:ArrayOfChequeValidaLoteResponse" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfChequeValidaLoteResponse">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Cheque" nillable="true" type="tns:ChequeValidaLoteResponse" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ChequeValidaLoteResponse">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Numero" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
          <s:element minOccurs="1" maxOccurs="1" name="Valido" type="tns:ValidoOr" />
        </s:sequence>
      </s:complexType>
      <s:simpleType name="ValidoOr">
        <s:restriction base="s:string">
          <s:enumeration value="Valido" />
          <s:enumeration value="Invalido" />
          <s:enumeration value="NoExiste" />
        </s:restriction>
      </s:simpleType>
      <s:element name="Transferencia">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:TransferenciaRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="TransferenciaRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
              <s:element minOccurs="0" maxOccurs="1" name="ProductoDestino" type="tns:Producto" />
              <s:element minOccurs="0" maxOccurs="1" name="Importe" type="tns:Importe" />
              <s:element minOccurs="0" maxOccurs="1" name="Descripcion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Tipo" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="TransferenciaResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="TransferenciaResult" type="tns:TransferenciaResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="TransferenciaResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader" />
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultarComprobantePorNCF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultarComprobantePorNCFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultarComprobantePorNCFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Ncf" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultarComprobantePorNCFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultarComprobantePorNCFResult" type="tns:ConsultarComprobantePorNCFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultarComprobantePorNCFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Comprobante" type="tns:Comprobante" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="Comprobante">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Id" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Ncf" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Oficina" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="FechaEmision" type="s:dateTime" />
          <s:element minOccurs="1" maxOccurs="1" name="Periodo" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" name="Estatus" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Cliente" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Direccion" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="Monto" type="s:decimal" />
          <s:element minOccurs="1" maxOccurs="1" name="Itbis" type="s:decimal" />
          <s:element minOccurs="0" maxOccurs="1" name="Concepto" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="RecepcionECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:RecepcionECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="RecepcionECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Archivo" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="RecepcionECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="RecepcionECFResult" type="tns:RecepcionECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="RecepcionECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="RespuestaECF" type="tns:RespuestaECF" />
              <s:element minOccurs="0" maxOccurs="1" name="RespuestaRFCE" type="tns:RespuestaRFCE" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoSeguridad" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="FechaHoraFirma" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="RespuestaECF">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="TrackID" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Error" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Mensaje" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="RespuestaRFCE">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="SecuenciaUtilizada" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" name="Codigo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Mensaje" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ENCF" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Mensajes" type="tns:ArrayOfMensajes" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfMensajes">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Mensajes" nillable="true" type="tns:Mensajes" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="Mensajes">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Valor" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Codigo" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="RecepcionFC">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:RecepcionFCRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="RecepcionFCRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="Archivo" type="s:unsignedByte" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="RecepcionFCResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="RecepcionFCResult" type="tns:RecepcionFCResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="RecepcionFCResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="codigo" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="valor" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="xsdBaseInvalido" type="s:boolean" />
              <s:element minOccurs="0" maxOccurs="1" name="Mensajes" type="tns:ArrayOfMensajes1" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfMensajes1">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="Mensaje" nillable="true" type="tns:Mensajes" />
        </s:sequence>
      </s:complexType>
      <s:element name="ConsultaResultadoECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaResultadoECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaResultadoECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="TrackID" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaResultadoECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaResultadoECFResult" type="tns:ConsultaResultadoECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaResultadoECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="SecuenciaUtilizada" type="s:boolean" />
              <s:element minOccurs="0" maxOccurs="1" name="TrackID" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Codigo" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="RNC" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="ENCF" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="FechaRecepcion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Mensajes" type="tns:ArrayOfMensajes1" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaEstadoECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaEstadoECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaEstadoECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="RncEmisor" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="NcfElectronico" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="RncComprador" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoSeguridad" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaEstadoECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaEstadoECFResult" type="tns:ConsultaEstadoECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaEstadoECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="Codigo" type="s:float" />
              <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="RNCEmisor" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="NCFElectronico" type="s:string" />
              <s:element minOccurs="1" maxOccurs="1" name="MontoTotal" type="s:float" />
              <s:element minOccurs="1" maxOccurs="1" name="TotalItbis" type="s:float" />
              <s:element minOccurs="0" maxOccurs="1" name="FechaEmision" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="FechaFirma" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="RNCComprador" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="CodigoSeguridad" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="IDExtranjero" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaTracksIdsECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaTracksIdsECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaTracksIdsECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="RNCEmisor" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="ENCF" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaTracksIdsECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaTracksIdsECFResult" type="tns:ConsultaTracksIdsECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaTracksIdsECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="TracksIds" type="tns:ArrayOfTracksIds" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfTracksIds">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="TracksIds" nillable="true" type="tns:TracksIds" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="TracksIds">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="TrackID" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="FechaRecepcion" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="AprobacionComercialECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:AprobacionComercialECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="AprobacionComercialECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="1" maxOccurs="1" name="Archivo" type="s:unsignedByte" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="AprobacionComercialECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="AprobacionComercialECFResult" type="tns:AprobacionComercialECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="AprobacionComercialECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Estado" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Codigo" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="MensajesDGII" type="tns:ArrayOfMensajeDGII" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfMensajeDGII">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="MensajeDGII" nillable="true" type="tns:MensajeDGII" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="MensajeDGII">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Mensaje" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="AnulacionRangosECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:AnulacionRangosECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="AnulacionRangosECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Archivo" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="AnulacionRangosECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="AnulacionRangosECFResult" type="tns:AnulacionRangosECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="AnulacionRangosECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="RNC" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Codigo" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Nombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="MensajesDGII" type="tns:ArrayOfMensajeDGII" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaDirectorioECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ConsultaDirectorioECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaDirectorioECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader" />
        </s:complexContent>
      </s:complexType>
      <s:element name="ConsultaDirectorioECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ConsultaDirectorioECFResult" type="tns:ConsultaDirectorioECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ConsultaDirectorioECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Nombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="RNC" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="URLRecepcion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="URLAceptacion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="URLOpcional" type="s:string" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="ObtenerEstatusAPIECF">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:ObtenerEstatusAPIECFRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ObtenerEstatusAPIECFRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader" />
        </s:complexContent>
      </s:complexType>
      <s:element name="ObtenerEstatusAPIECFResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ObtenerEstatusAPIECFResult" type="tns:ObtenerEstatusAPIECFResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="ObtenerEstatusAPIECFResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="EstatusServicios" type="tns:ArrayOfEstatusServicio" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:complexType name="ArrayOfEstatusServicio">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="EstatusServicio" nillable="true" type="tns:EstatusServicio" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="EstatusServicio">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Servicio" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Estatus" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="TransaccionMIOACH">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="request" type="tns:TransaccionMIOACHRequest" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="TransaccionMIOACHRequest">
        <s:complexContent mixed="false">
          <s:extension base="tns:RequestHeader">
            <s:sequence>
              <s:element minOccurs="0" maxOccurs="1" name="Banco" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Producto" type="tns:Producto" />
              <s:element minOccurs="0" maxOccurs="1" name="ProductoDestino" type="tns:Producto" />
              <s:element minOccurs="0" maxOccurs="1" name="Importe" type="tns:Importe" />
              <s:element minOccurs="0" maxOccurs="1" name="Descripcion" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Nombre" type="s:string" />
              <s:element minOccurs="0" maxOccurs="1" name="Identificacion" type="tns:Identificacion" />
            </s:sequence>
          </s:extension>
        </s:complexContent>
      </s:complexType>
      <s:element name="TransaccionMIOACHResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="TransaccionMIOACHResult" type="tns:TransaccionMIOACHResponse" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="TransaccionMIOACHResponse">
        <s:complexContent mixed="false">
          <s:extension base="tns:ResponseHeader" />
        </s:complexContent>
      </s:complexType>
    </s:schema>
  </wsdl:types>
  <wsdl:message name="ConsultaFechaHabilSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaFechaHabil" />
  </wsdl:message>
  <wsdl:message name="ConsultaFechaHabilSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaFechaHabilResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultarDatosGeneralesClienteSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultarDatosGeneralesCliente" />
  </wsdl:message>
  <wsdl:message name="ConsultarDatosGeneralesClienteSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultarDatosGeneralesClienteResponse" />
  </wsdl:message>
  <wsdl:message name="CuentasClienteSoapIn">
    <wsdl:part name="parameters" element="tns:CuentasCliente" />
  </wsdl:message>
  <wsdl:message name="CuentasClienteSoapOut">
    <wsdl:part name="parameters" element="tns:CuentasClienteResponse" />
  </wsdl:message>
  <wsdl:message name="DebitoCuentaSoapIn">
    <wsdl:part name="parameters" element="tns:DebitoCuenta" />
  </wsdl:message>
  <wsdl:message name="DebitoCuentaSoapOut">
    <wsdl:part name="parameters" element="tns:DebitoCuentaResponse" />
  </wsdl:message>
  <wsdl:message name="DetalleCuentasSoapIn">
    <wsdl:part name="parameters" element="tns:DetalleCuentas" />
  </wsdl:message>
  <wsdl:message name="DetalleCuentasSoapOut">
    <wsdl:part name="parameters" element="tns:DetalleCuentasResponse" />
  </wsdl:message>
  <wsdl:message name="DetallePrestamoSoapIn">
    <wsdl:part name="parameters" element="tns:DetallePrestamo" />
  </wsdl:message>
  <wsdl:message name="DetallePrestamoSoapOut">
    <wsdl:part name="parameters" element="tns:DetallePrestamoResponse" />
  </wsdl:message>
  <wsdl:message name="ListadoOficinasSoapIn">
    <wsdl:part name="parameters" element="tns:ListadoOficinas" />
  </wsdl:message>
  <wsdl:message name="ListadoOficinasSoapOut">
    <wsdl:part name="parameters" element="tns:ListadoOficinasResponse" />
  </wsdl:message>
  <wsdl:message name="NominaElectronicaSoapIn">
    <wsdl:part name="parameters" element="tns:NominaElectronica" />
  </wsdl:message>
  <wsdl:message name="NominaElectronicaSoapOut">
    <wsdl:part name="parameters" element="tns:NominaElectronicaResponse" />
  </wsdl:message>
  <wsdl:message name="PosicionConsolidadaSoapIn">
    <wsdl:part name="parameters" element="tns:PosicionConsolidada" />
  </wsdl:message>
  <wsdl:message name="PosicionConsolidadaSoapOut">
    <wsdl:part name="parameters" element="tns:PosicionConsolidadaResponse" />
  </wsdl:message>
  <wsdl:message name="VerificarListasNegrasSoapIn">
    <wsdl:part name="parameters" element="tns:VerificarListasNegras" />
  </wsdl:message>
  <wsdl:message name="VerificarListasNegrasSoapOut">
    <wsdl:part name="parameters" element="tns:VerificarListasNegrasResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultaClienteSignatureSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaClienteSignature" />
  </wsdl:message>
  <wsdl:message name="ConsultaClienteSignatureSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaClienteSignatureResponse" />
  </wsdl:message>
  <wsdl:message name="ClienteBanreservasSoapIn">
    <wsdl:part name="parameters" element="tns:ClienteBanreservas" />
  </wsdl:message>
  <wsdl:message name="ClienteBanreservasSoapOut">
    <wsdl:part name="parameters" element="tns:ClienteBanreservasResponse" />
  </wsdl:message>
  <wsdl:message name="VerificaClienteDataCreditoSoapIn">
    <wsdl:part name="parameters" element="tns:VerificaClienteDataCredito" />
  </wsdl:message>
  <wsdl:message name="VerificaClienteDataCreditoSoapOut">
    <wsdl:part name="parameters" element="tns:VerificaClienteDataCreditoResponse" />
  </wsdl:message>
  <wsdl:message name="DetalleCertificadoSoapIn">
    <wsdl:part name="parameters" element="tns:DetalleCertificado" />
  </wsdl:message>
  <wsdl:message name="DetalleCertificadoSoapOut">
    <wsdl:part name="parameters" element="tns:DetalleCertificadoResponse" />
  </wsdl:message>
  <wsdl:message name="GenerarPinTarjetaCodigoSoapIn">
    <wsdl:part name="parameters" element="tns:GenerarPinTarjetaCodigo" />
  </wsdl:message>
  <wsdl:message name="GenerarPinTarjetaCodigoSoapOut">
    <wsdl:part name="parameters" element="tns:GenerarPinTarjetaCodigoResponse" />
  </wsdl:message>
  <wsdl:message name="MovimientosTarjetaSoapIn">
    <wsdl:part name="parameters" element="tns:MovimientosTarjeta" />
  </wsdl:message>
  <wsdl:message name="MovimientosTarjetaSoapOut">
    <wsdl:part name="parameters" element="tns:MovimientosTarjetaResponse" />
  </wsdl:message>
  <wsdl:message name="TarjetasClienteSoapIn">
    <wsdl:part name="parameters" element="tns:TarjetasCliente" />
  </wsdl:message>
  <wsdl:message name="TarjetasClienteSoapOut">
    <wsdl:part name="parameters" element="tns:TarjetasClienteResponse" />
  </wsdl:message>
  <wsdl:message name="DetalleTarjetaSoapIn">
    <wsdl:part name="parameters" element="tns:DetalleTarjeta" />
  </wsdl:message>
  <wsdl:message name="DetalleTarjetaSoapOut">
    <wsdl:part name="parameters" element="tns:DetalleTarjetaResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultarCodigosChequesSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultarCodigosCheques" />
  </wsdl:message>
  <wsdl:message name="ConsultarCodigosChequesSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultarCodigosChequesResponse" />
  </wsdl:message>
  <wsdl:message name="CrearCodigosChequesSoapIn">
    <wsdl:part name="parameters" element="tns:CrearCodigosCheques" />
  </wsdl:message>
  <wsdl:message name="CrearCodigosChequesSoapOut">
    <wsdl:part name="parameters" element="tns:CrearCodigosChequesResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultaBancosCorresponsalesSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaBancosCorresponsales" />
  </wsdl:message>
  <wsdl:message name="ConsultaBancosCorresponsalesSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaBancosCorresponsalesResponse" />
  </wsdl:message>
  <wsdl:message name="CatalogoPagosServiciosSoapIn">
    <wsdl:part name="parameters" element="tns:CatalogoPagosServicios" />
  </wsdl:message>
  <wsdl:message name="CatalogoPagosServiciosSoapOut">
    <wsdl:part name="parameters" element="tns:CatalogoPagosServiciosResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultaGeneralProductoSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaGeneralProducto" />
  </wsdl:message>
  <wsdl:message name="ConsultaGeneralProductoSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaGeneralProductoResponse" />
  </wsdl:message>
  <wsdl:message name="UltimosMovimientosCuentasTarjetasSoapIn">
    <wsdl:part name="parameters" element="tns:UltimosMovimientosCuentasTarjetas" />
  </wsdl:message>
  <wsdl:message name="UltimosMovimientosCuentasTarjetasSoapOut">
    <wsdl:part name="parameters" element="tns:UltimosMovimientosCuentasTarjetasResponse" />
  </wsdl:message>
  <wsdl:message name="UltimosMovimientosPrestamosSoapIn">
    <wsdl:part name="parameters" element="tns:UltimosMovimientosPrestamos" />
  </wsdl:message>
  <wsdl:message name="UltimosMovimientosPrestamosSoapOut">
    <wsdl:part name="parameters" element="tns:UltimosMovimientosPrestamosResponse" />
  </wsdl:message>
  <wsdl:message name="ValidaCodigoChequeSoapIn">
    <wsdl:part name="parameters" element="tns:ValidaCodigoCheque" />
  </wsdl:message>
  <wsdl:message name="ValidaCodigoChequeSoapOut">
    <wsdl:part name="parameters" element="tns:ValidaCodigoChequeResponse" />
  </wsdl:message>
  <wsdl:message name="ValidaLoteCodigoChequeSoapIn">
    <wsdl:part name="parameters" element="tns:ValidaLoteCodigoCheque" />
  </wsdl:message>
  <wsdl:message name="ValidaLoteCodigoChequeSoapOut">
    <wsdl:part name="parameters" element="tns:ValidaLoteCodigoChequeResponse" />
  </wsdl:message>
  <wsdl:message name="TransferenciaSoapIn">
    <wsdl:part name="parameters" element="tns:Transferencia" />
  </wsdl:message>
  <wsdl:message name="TransferenciaSoapOut">
    <wsdl:part name="parameters" element="tns:TransferenciaResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultarComprobantePorNCFSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultarComprobantePorNCF" />
  </wsdl:message>
  <wsdl:message name="ConsultarComprobantePorNCFSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultarComprobantePorNCFResponse" />
  </wsdl:message>
  <wsdl:message name="RecepcionECFSoapIn">
    <wsdl:part name="parameters" element="tns:RecepcionECF" />
  </wsdl:message>
  <wsdl:message name="RecepcionECFSoapOut">
    <wsdl:part name="parameters" element="tns:RecepcionECFResponse" />
  </wsdl:message>
  <wsdl:message name="RecepcionFCSoapIn">
    <wsdl:part name="parameters" element="tns:RecepcionFC" />
  </wsdl:message>
  <wsdl:message name="RecepcionFCSoapOut">
    <wsdl:part name="parameters" element="tns:RecepcionFCResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultaResultadoECFSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaResultadoECF" />
  </wsdl:message>
  <wsdl:message name="ConsultaResultadoECFSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaResultadoECFResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultaEstadoECFSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaEstadoECF" />
  </wsdl:message>
  <wsdl:message name="ConsultaEstadoECFSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaEstadoECFResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultaTracksIdsECFSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaTracksIdsECF" />
  </wsdl:message>
  <wsdl:message name="ConsultaTracksIdsECFSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaTracksIdsECFResponse" />
  </wsdl:message>
  <wsdl:message name="AprobacionComercialECFSoapIn">
    <wsdl:part name="parameters" element="tns:AprobacionComercialECF" />
  </wsdl:message>
  <wsdl:message name="AprobacionComercialECFSoapOut">
    <wsdl:part name="parameters" element="tns:AprobacionComercialECFResponse" />
  </wsdl:message>
  <wsdl:message name="AnulacionRangosECFSoapIn">
    <wsdl:part name="parameters" element="tns:AnulacionRangosECF" />
  </wsdl:message>
  <wsdl:message name="AnulacionRangosECFSoapOut">
    <wsdl:part name="parameters" element="tns:AnulacionRangosECFResponse" />
  </wsdl:message>
  <wsdl:message name="ConsultaDirectorioECFSoapIn">
    <wsdl:part name="parameters" element="tns:ConsultaDirectorioECF" />
  </wsdl:message>
  <wsdl:message name="ConsultaDirectorioECFSoapOut">
    <wsdl:part name="parameters" element="tns:ConsultaDirectorioECFResponse" />
  </wsdl:message>
  <wsdl:message name="ObtenerEstatusAPIECFSoapIn">
    <wsdl:part name="parameters" element="tns:ObtenerEstatusAPIECF" />
  </wsdl:message>
  <wsdl:message name="ObtenerEstatusAPIECFSoapOut">
    <wsdl:part name="parameters" element="tns:ObtenerEstatusAPIECFResponse" />
  </wsdl:message>
  <wsdl:message name="TransaccionMIOACHSoapIn">
    <wsdl:part name="parameters" element="tns:TransaccionMIOACH" />
  </wsdl:message>
  <wsdl:message name="TransaccionMIOACHSoapOut">
    <wsdl:part name="parameters" element="tns:TransaccionMIOACHResponse" />
  </wsdl:message>
  <wsdl:portType name="BackEndServiceSoap">
    <wsdl:operation name="ConsultaFechaHabil">
      <wsdl:input message="tns:ConsultaFechaHabilSoapIn" />
      <wsdl:output message="tns:ConsultaFechaHabilSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultarDatosGeneralesCliente">
      <wsdl:input message="tns:ConsultarDatosGeneralesClienteSoapIn" />
      <wsdl:output message="tns:ConsultarDatosGeneralesClienteSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="CuentasCliente">
      <wsdl:input message="tns:CuentasClienteSoapIn" />
      <wsdl:output message="tns:CuentasClienteSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="DebitoCuenta">
      <wsdl:input message="tns:DebitoCuentaSoapIn" />
      <wsdl:output message="tns:DebitoCuentaSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="DetalleCuentas">
      <wsdl:input message="tns:DetalleCuentasSoapIn" />
      <wsdl:output message="tns:DetalleCuentasSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="DetallePrestamo">
      <wsdl:input message="tns:DetallePrestamoSoapIn" />
      <wsdl:output message="tns:DetallePrestamoSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ListadoOficinas">
      <wsdl:input message="tns:ListadoOficinasSoapIn" />
      <wsdl:output message="tns:ListadoOficinasSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="NominaElectronica">
      <wsdl:input message="tns:NominaElectronicaSoapIn" />
      <wsdl:output message="tns:NominaElectronicaSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="PosicionConsolidada">
      <wsdl:input message="tns:PosicionConsolidadaSoapIn" />
      <wsdl:output message="tns:PosicionConsolidadaSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="VerificarListasNegras">
      <wsdl:input message="tns:VerificarListasNegrasSoapIn" />
      <wsdl:output message="tns:VerificarListasNegrasSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultaClienteSignature">
      <wsdl:input message="tns:ConsultaClienteSignatureSoapIn" />
      <wsdl:output message="tns:ConsultaClienteSignatureSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ClienteBanreservas">
      <wsdl:input message="tns:ClienteBanreservasSoapIn" />
      <wsdl:output message="tns:ClienteBanreservasSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="VerificaClienteDataCredito">
      <wsdl:input message="tns:VerificaClienteDataCreditoSoapIn" />
      <wsdl:output message="tns:VerificaClienteDataCreditoSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="DetalleCertificado">
      <wsdl:input message="tns:DetalleCertificadoSoapIn" />
      <wsdl:output message="tns:DetalleCertificadoSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="GenerarPinTarjetaCodigo">
      <wsdl:input message="tns:GenerarPinTarjetaCodigoSoapIn" />
      <wsdl:output message="tns:GenerarPinTarjetaCodigoSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="MovimientosTarjeta">
      <wsdl:input message="tns:MovimientosTarjetaSoapIn" />
      <wsdl:output message="tns:MovimientosTarjetaSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="TarjetasCliente">
      <wsdl:input message="tns:TarjetasClienteSoapIn" />
      <wsdl:output message="tns:TarjetasClienteSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="DetalleTarjeta">
      <wsdl:input message="tns:DetalleTarjetaSoapIn" />
      <wsdl:output message="tns:DetalleTarjetaSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultarCodigosCheques">
      <wsdl:input message="tns:ConsultarCodigosChequesSoapIn" />
      <wsdl:output message="tns:ConsultarCodigosChequesSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="CrearCodigosCheques">
      <wsdl:input message="tns:CrearCodigosChequesSoapIn" />
      <wsdl:output message="tns:CrearCodigosChequesSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultaBancosCorresponsales">
      <wsdl:input message="tns:ConsultaBancosCorresponsalesSoapIn" />
      <wsdl:output message="tns:ConsultaBancosCorresponsalesSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="CatalogoPagosServicios">
      <wsdl:input message="tns:CatalogoPagosServiciosSoapIn" />
      <wsdl:output message="tns:CatalogoPagosServiciosSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultaGeneralProducto">
      <wsdl:input message="tns:ConsultaGeneralProductoSoapIn" />
      <wsdl:output message="tns:ConsultaGeneralProductoSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="UltimosMovimientosCuentasTarjetas">
      <wsdl:input message="tns:UltimosMovimientosCuentasTarjetasSoapIn" />
      <wsdl:output message="tns:UltimosMovimientosCuentasTarjetasSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="UltimosMovimientosPrestamos">
      <wsdl:input message="tns:UltimosMovimientosPrestamosSoapIn" />
      <wsdl:output message="tns:UltimosMovimientosPrestamosSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ValidaCodigoCheque">
      <wsdl:input message="tns:ValidaCodigoChequeSoapIn" />
      <wsdl:output message="tns:ValidaCodigoChequeSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ValidaLoteCodigoCheque">
      <wsdl:input message="tns:ValidaLoteCodigoChequeSoapIn" />
      <wsdl:output message="tns:ValidaLoteCodigoChequeSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="Transferencia">
      <wsdl:input message="tns:TransferenciaSoapIn" />
      <wsdl:output message="tns:TransferenciaSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultarComprobantePorNCF">
      <wsdl:input message="tns:ConsultarComprobantePorNCFSoapIn" />
      <wsdl:output message="tns:ConsultarComprobantePorNCFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="RecepcionECF">
      <wsdl:input message="tns:RecepcionECFSoapIn" />
      <wsdl:output message="tns:RecepcionECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="RecepcionFC">
      <wsdl:input message="tns:RecepcionFCSoapIn" />
      <wsdl:output message="tns:RecepcionFCSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultaResultadoECF">
      <wsdl:input message="tns:ConsultaResultadoECFSoapIn" />
      <wsdl:output message="tns:ConsultaResultadoECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultaEstadoECF">
      <wsdl:input message="tns:ConsultaEstadoECFSoapIn" />
      <wsdl:output message="tns:ConsultaEstadoECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultaTracksIdsECF">
      <wsdl:input message="tns:ConsultaTracksIdsECFSoapIn" />
      <wsdl:output message="tns:ConsultaTracksIdsECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="AprobacionComercialECF">
      <wsdl:input message="tns:AprobacionComercialECFSoapIn" />
      <wsdl:output message="tns:AprobacionComercialECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="AnulacionRangosECF">
      <wsdl:input message="tns:AnulacionRangosECFSoapIn" />
      <wsdl:output message="tns:AnulacionRangosECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ConsultaDirectorioECF">
      <wsdl:input message="tns:ConsultaDirectorioECFSoapIn" />
      <wsdl:output message="tns:ConsultaDirectorioECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="ObtenerEstatusAPIECF">
      <wsdl:input message="tns:ObtenerEstatusAPIECFSoapIn" />
      <wsdl:output message="tns:ObtenerEstatusAPIECFSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="TransaccionMIOACH">
      <wsdl:input message="tns:TransaccionMIOACHSoapIn" />
      <wsdl:output message="tns:TransaccionMIOACHSoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="BackEndServiceSoap" type="tns:BackEndServiceSoap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="ConsultaFechaHabil">
      <soap:operation soapAction="http://tempuri.org/ConsultaFechaHabil" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultarDatosGeneralesCliente">
      <soap:operation soapAction="http://tempuri.org/ConsultarDatosGeneralesCliente" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="CuentasCliente">
      <soap:operation soapAction="http://tempuri.org/CuentasCliente" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="DebitoCuenta">
      <soap:operation soapAction="http://tempuri.org/DebitoCuenta" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="DetalleCuentas">
      <soap:operation soapAction="http://tempuri.org/DetalleCuentas" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="DetallePrestamo">
      <soap:operation soapAction="http://tempuri.org/DetallePrestamo" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ListadoOficinas">
      <soap:operation soapAction="http://tempuri.org/ListadoOficinas" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="NominaElectronica">
      <soap:operation soapAction="http://tempuri.org/NominaElectronica" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="PosicionConsolidada">
      <soap:operation soapAction="http://tempuri.org/PosicionConsolidada" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="VerificarListasNegras">
      <soap:operation soapAction="http://tempuri.org/VerificarListasNegras" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultaClienteSignature">
      <soap:operation soapAction="http://tempuri.org/ConsultaClienteSignature" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ClienteBanreservas">
      <soap:operation soapAction="http://tempuri.org/ClienteBanreservas" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="VerificaClienteDataCredito">
      <soap:operation soapAction="http://tempuri.org/VerificaClienteDataCredito" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="DetalleCertificado">
      <soap:operation soapAction="http://tempuri.org/DetalleCertificado" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GenerarPinTarjetaCodigo">
      <soap:operation soapAction="http://tempuri.org/GenerarPinTarjetaCodigo" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="MovimientosTarjeta">
      <soap:operation soapAction="http://tempuri.org/MovimientosTarjeta" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="TarjetasCliente">
      <soap:operation soapAction="http://tempuri.org/TarjetasCliente" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="DetalleTarjeta">
      <soap:operation soapAction="http://tempuri.org/DetalleTarjeta" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultarCodigosCheques">
      <soap:operation soapAction="http://tempuri.org/ConsultarCodigosCheques" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="CrearCodigosCheques">
      <soap:operation soapAction="http://tempuri.org/CrearCodigosCheques" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultaBancosCorresponsales">
      <soap:operation soapAction="http://tempuri.org/ConsultaBancosCorresponsales" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="CatalogoPagosServicios">
      <soap:operation soapAction="http://tempuri.org/CatalogoPagosServicios" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultaGeneralProducto">
      <soap:operation soapAction="http://tempuri.org/ConsultaGeneralProducto" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="UltimosMovimientosCuentasTarjetas">
      <soap:operation soapAction="http://tempuri.org/UltimosMovimientosCuentasTarjetas" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="UltimosMovimientosPrestamos">
      <soap:operation soapAction="http://tempuri.org/UltimosMovimientosPrestamos" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ValidaCodigoCheque">
      <soap:operation soapAction="http://tempuri.org/ValidaCodigoCheque" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ValidaLoteCodigoCheque">
      <soap:operation soapAction="http://tempuri.org/ValidaLoteCodigoCheque" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="Transferencia">
      <soap:operation soapAction="http://tempuri.org/Transferencia" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultarComprobantePorNCF">
      <soap:operation soapAction="http://tempuri.org/ConsultarComprobantePorNCF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="RecepcionECF">
      <soap:operation soapAction="http://tempuri.org/RecepcionECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="RecepcionFC">
      <soap:operation soapAction="http://tempuri.org/RecepcionFC" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultaResultadoECF">
      <soap:operation soapAction="http://tempuri.org/ConsultaResultadoECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultaEstadoECF">
      <soap:operation soapAction="http://tempuri.org/ConsultaEstadoECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultaTracksIdsECF">
      <soap:operation soapAction="http://tempuri.org/ConsultaTracksIdsECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="AprobacionComercialECF">
      <soap:operation soapAction="http://tempuri.org/AprobacionComercialECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="AnulacionRangosECF">
      <soap:operation soapAction="http://tempuri.org/AnulacionRangosECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ConsultaDirectorioECF">
      <soap:operation soapAction="http://tempuri.org/ConsultaDirectorioECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="ObtenerEstatusAPIECF">
      <soap:operation soapAction="http://tempuri.org/ObtenerEstatusAPIECF" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="TransaccionMIOACH">
      <soap:operation soapAction="http://tempuri.org/TransaccionMIOACH" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="BackEndService">
    <wsdl:port name="BackEndServiceSoap" binding="tns:BackEndServiceSoap">
      <soap:address location="http://localhost:51106/BackEndService.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>
```

## Author

* **Domingo Ruiz** - *Desarrollador* - [C-DJRuiz@banreservas.com]