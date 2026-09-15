package integracion;

import datos.XmlManager;
import modelo.Categoria;
import modelo.Recurso;
import modelo.Reserva;
import modelo.EstadoReserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlManagerIT {
    @TempDir
    Path tempDir;

    @Test
    void guardaYLeeCatalogosYReservasEnXml() throws Exception {
        XmlManager xmlManager = new XmlManager(tempDir.resolve("reservas.xml").toString());

        xmlManager.guardarCategorias(List.of(new Categoria("CAT-900001", "Laboratorio")));
        xmlManager.guardarRecursos(List.of(new Recurso("LAB-01", "CAT-900001", "Laboratorio principal")));
        xmlManager.guardarReservas(List.of(new Reserva(
                "RES-900001",
                "111",
                "Practica de integracion",
                LocalDate.now().plusDays(7),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of("LAB-01"),
                EstadoReserva.ACTIVA
        )));

        assertEquals("Laboratorio", xmlManager.leerCategorias().get(0).getDescripcion());
        assertEquals("LAB-01", xmlManager.leerRecursos().get(0).getId());
        assertEquals("Practica de integracion", xmlManager.leerReservas().get(0).getActividad());
    }
}
