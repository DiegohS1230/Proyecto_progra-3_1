package vista;

import modelo.EstadoReserva;
import modelo.Funcionario;
import modelo.Reserva;
import utilitarios.EstilosUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PanelCalendarizacionActividades extends JPanel {
    private JTextField txtFecha;
    private JButton btnCargar;
    private JButton btnImprimir;

    private JTable tabla;
    private DefaultTableModel modelo;

    private final String[] horas = new String[]{
            "07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
            "19:00", "20:00", "21:00"
    };

    public PanelCalendarizacionActividades() {
        setLayout(new BorderLayout(12, 12));
        setBackground(EstilosUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelControles.setBackground(EstilosUI.COLOR_BLANCO);
        panelControles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        txtFecha = new JTextField(LocalDate.now().toString(), 10);
        EstilosUI.estilizarCampo(txtFecha);

        btnCargar = new JButton("Cargar Semana");
        EstilosUI.estilizarBoton(btnCargar, true);

        btnImprimir = new JButton("Exportar (PDF)");
        EstilosUI.estilizarBoton(btnImprimir, false);

        JLabel lblFecha = new JLabel("Fecha de Referencia (YYYY-MM-DD):");
        lblFecha.setFont(EstilosUI.FUENTE_BOLD);
        panelControles.add(lblFecha);
        panelControles.add(txtFecha);
        panelControles.add(btnCargar);
        panelControles.add(btnImprimir);

        add(panelControles, BorderLayout.NORTH);

        String[] columnas = new String[]{"Hora", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        EstilosUI.estilizarTabla(tabla);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(95);
        for (int i = 1; i <= 7; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(145);
        }

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1));
        add(scrollTabla, BorderLayout.CENTER);
    }

    public void cargarMatriz(LocalDate fechaRef, List<Reserva> reservas, List<Funcionario> funcionarios) {
        modelo.setRowCount(0);
        LocalDate lunes = fechaRef.with(DayOfWeek.MONDAY);

        for (String h : horas) {
            LocalTime slotHora = LocalTime.parse(h);
            Object[] fila = new Object[8];
            fila[0] = h;

            for (int d = 0; d < 7; d++) {
                LocalDate diaSemana = lunes.plusDays(d);
                String contenido = "-";

                for (Reserva res : reservas) {
                    if (res.getEstado() == EstadoReserva.ACTIVA && res.getFecha().equals(diaSemana)) {
                        if (!slotHora.isBefore(res.getHoraInicio()) && slotHora.isBefore(res.getHoraFin())) {
                            String nomFunc = res.getFuncionarioId();
                            for (Funcionario f : funcionarios) {
                                if (f.getId().equalsIgnoreCase(res.getFuncionarioId())) {
                                    nomFunc = f.getNombre();
                                    break;
                                }
                            }
                            contenido = res.getActividad() + " (" + nomFunc + ")";
                            break;
                        }
                    }
                }
                fila[d + 1] = contenido;
            }
            modelo.addRow(fila);
        }
    }

    public JTextField getTxtFecha() { return txtFecha; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnImprimir() { return btnImprimir; }
    public JTable getTabla() { return tabla; }
}

