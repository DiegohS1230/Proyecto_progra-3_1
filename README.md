# Sistema de Reserva de Recursos

## Tecnologias principales

- Maven
- XML con DOM nativo de Java
- OpenPDF para reportes en PDF
- JFreeChart para graficas estadisticas
- JUnit Jupiter para pruebas
- API de Gemini para asistencia opcional en el registro de reservas

## Estructura del proyecto

- `src/app`: punto de entrada de la aplicacion.
- `src/controlador`: coordinacion entre interfaz grafica y servicios.
- `src/datos`: lectura y escritura del archivo XML.
- `src/modelo`: clases del dominio del sistema.
- `src/servicio`: reglas de negocio, validaciones y operaciones principales.
- `src/utilitarios`: clases de apoyo para estilos, graficas, PDF y datos auxiliares.
- `src/vista`: ventanas y paneles de Java Swing.
- `data/reservas.xml`: archivo principal de almacenamiento local.
- `test`: pruebas unitarias e integracion.

## Credenciales de acceso iniciales

### Administrador

- Usuario: `admin`
- Clave: `admin`
- Acceso: gestion de funcionarios, categorias, recursos, calendarizacion, actividades y estadisticas.

### Funcionarios

- Usuario: `111` | Clave: `111` | Juan Perez
- Usuario: `222` | Clave: `222` | Maria Perez
- Usuario: `333` | Clave: `333` | Carlos Rodriguez
- Acceso: reservas, calendarizacion, actividades y estadisticas.

## Asistente Gemini

El sistema puede usar la API de Gemini para extraer datos de una solicitud escrita en lenguaje natural y completar una reserva. Para activar esta funcion se debe configurar la variable de entorno:

```text
GEMINI_API_KEY=tu_clave_api
```

Si la variable no esta configurada, la aplicacion sigue funcionando con un extractor local de respaldo.

## Pruebas

```bash
mvn test
mvn verify
```
