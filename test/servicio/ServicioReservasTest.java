package servicio;

import datos.XmlManager;
import modelo.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServicioReservasTest {
    @TempDir
    Path tempDir;

    private ServicioReservas servicio;

    @BeforeEach
    void setUp() {
        XmlManager xmlManager = new XmlManager(tempDir.resolve("reservas.xml").toString());
        servicio = new ServicioReservas(xmlManager);
    }

    @Test
    void autenticarFuncionarioConCredencialesCorrectas() {
        assertNotNull(servicio.autenticar("111", "111"));
    }

    @Test
    void rechazaReservaEnFechaPasada() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                servicio.crearReserva(
                        "111",
                        "Actividad pasada",
                        LocalDate.now().minusDays(1),
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0),
                        List.of("CAT-000001")
                )
        );

        assertTrue(ex.getMessage().contains("pasado"));
    }

    @Test
    void asignaPrimerRecursoDisponiblePorCategoria() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(10);

        Reserva reserva = servicio.crearReserva(
                "111",
                "Reunion de proyecto",
                fecha,
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                List.of("CAT-000001", "CAT-000004")
        );

        assertEquals(List.of("34343", "562100"), reserva.getRecursosIds());
    }

    @Test
    void rechazaCategoriaSinRecursosDisponibles() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(15);
        servicio.crearReserva("111", "Primera laptop", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0), List.of("CAT-000002"));
        servicio.crearReserva("222", "Segunda laptop", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0), List.of("CAT-000002"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                servicio.crearReserva("333", "Tercera laptop", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0), List.of("CAT-000002"))
        );

        assertTrue(ex.getMessage().contains("Laptop"));
    }

    @Test
    void calculaEstadisticasDeRecursosPorCategoria() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(20);
        servicio.crearReserva(
                "111",
                "Sesion estadistica",
                fecha,
                LocalTime.of(13, 0),
                LocalTime.of(15, 0),
                List.of("CAT-000002", "CAT-000004")
        );

        Map<String, Integer> estadisticas = servicio.estadisticasRecursos(fecha, fecha);

        assertEquals(1, estadisticas.get("Laptop windows"));
        assertEquals(1, estadisticas.get("Proyector Multimedia"));
    }
}
