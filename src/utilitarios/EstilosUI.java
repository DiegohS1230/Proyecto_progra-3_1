package utilitarios;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class EstilosUI {
    public static final Color COLOR_FONDO = new Color(244, 246, 249);
    public static final Color COLOR_PRIMARIO = new Color(44, 62, 80);
    public static final Color COLOR_SECUNDARIO = new Color(52, 73, 94);
    public static final Color COLOR_BLANCO = Color.WHITE;
    public static final Color COLOR_BORDE = new Color(210, 215, 222);
    public static final Color COLOR_TEXTO = new Color(40, 44, 52);
    public static final Color COLOR_REJILLA = new Color(235, 238, 242);
    public static final Color COLOR_ACCENTO = new Color(41, 128, 185);

    public static final Font FUENTE_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 13);

    public static final Border BORDE_CAMPO = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
    );

    // Aplica formato minimalista corporativo a tablas JTable
    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(25);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(COLOR_REJILLA);
        tabla.setBackground(COLOR_BLANCO);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setFont(FUENTE_REGULAR);
        tabla.setSelectionBackground(COLOR_SECUNDARIO);
        tabla.setSelectionForeground(COLOR_BLANCO);

        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 30));
        header.setFont(FUENTE_BOLD);
        header.setBackground(COLOR_PRIMARIO);
        header.setForeground(COLOR_BLANCO);
        header.setOpaque(true);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(COLOR_PRIMARIO);
        headerRenderer.setForeground(COLOR_BLANCO);
        headerRenderer.setFont(FUENTE_BOLD);
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        header.setDefaultRenderer(headerRenderer);
    }

    public static void estilizarCampo(JTextField campo) {
        campo.setFont(FUENTE_REGULAR);
        campo.setBorder(BORDE_CAMPO);
        campo.setBackground(COLOR_BLANCO);
        campo.setForeground(COLOR_TEXTO);
    }

    public static void estilizarBoton(JButton boton, boolean primario) {
        boton.setFont(FUENTE_BOLD);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (primario) {
            boton.setBackground(COLOR_PRIMARIO);
            boton.setForeground(COLOR_BLANCO);
            boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARIO, 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)
            ));
        } else {
            boton.setBackground(COLOR_BLANCO);
            boton.setForeground(COLOR_PRIMARIO);
            boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDE, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        }
    }
}
