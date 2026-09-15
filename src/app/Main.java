package app;

import controlador.ControladorPrincipal;
import datos.XmlManager;
import servicio.ServicioReservas;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configuracion del Look and Feel Nimbus con paleta minimalista corporativa
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }

                // Ajustes globales de apariencia: fondo gris claro #F4F6F9 y acentos azul pizarra #2C3E50
                UIManager.put("control", new Color(244, 246, 249));
                UIManager.put("nimbusBase", new Color(44, 62, 80));
                UIManager.put("nimbusBlueGrey", new Color(220, 225, 230));
                UIManager.put("nimbusSelectionBackground", new Color(52, 73, 94));
                UIManager.put("textHighlight", new Color(52, 73, 94));
                UIManager.put("Table.rowHeight", 25);
                UIManager.put("Table.showVerticalLines", false);
                UIManager.put("Table.showHorizontalLines", true);
                UIManager.put("Table.gridColor", new Color(235, 238, 242));
            } catch (Exception ignored) {
            }

            // Inicializacion de capas: datos (DOM), servicio y controlador
            XmlManager xmlManager = new XmlManager("data/reservas.xml");
            ServicioReservas servicio = new ServicioReservas(xmlManager);
            ControladorPrincipal controlador = new ControladorPrincipal(servicio);
            controlador.iniciar();
        });
    }
}

