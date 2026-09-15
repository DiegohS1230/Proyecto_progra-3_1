package vista;

import modelo.*;
import utilitarios.EstilosUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PanelCalendarizacionRecursos extends JPanel {
    private JTextField txtFecha;
    private JComboBox<String> cboxCategoria;
    private JButton btnCargar;
    private JButton btnImprimir;

    private JTable tabla;
    private DefaultTableModel modelo;

    private List<Categoria> categoriasList = new ArrayList<>();
    private final String[] horas = new String[]{
            "07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
            "19:00", "20:00", "21:00"
    };

    public PanelCalendarizacionRecursos() {
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
        cboxCategoria = new JComboBox<>();
        cboxCategoria.setFont(EstilosUI.FUENTE_REGULAR);

        btnCargar = new JButton("Cargar Horario");
        EstilosUI.estilizarBoton(btnCargar, true);

        btnImprimir = new JButton("Exportar (PDF)");
        EstilosUI.estilizarBoton(btnImprimir, false);

        JLabel lblFecha = new JLabel("Fecha (YYYY-MM-DD):");
        lblFecha.setFont(EstilosUI.FUENTE_BOLD);
        panelControles.add(lblFecha);
        panelControles.add(txtFecha);

        JLabel lblCat = new JLabel("Categoria:");
        lblCat.setFont(EstilosUI.FUENTE_BOLD);
        panelControles.add(lblCat);
        panelControles.add(cboxCategoria);
        panelControles.add(btnCargar);
        panelControles.add(btnImprimir);

        add(panelControles, BorderLayout.NORTH);

        List<String> columnas = new ArrayList<>();
        columnas.add("Recurso");
        for (String h : horas) {
            columnas.add(h);
        }

        modelo = new DefaultTableModel(columnas.toArray(), 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        EstilosUI.estilizarTabla(tabla);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(170);
        for (int i = 1; i <= horas.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(125);
        }

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1));
        add(scrollTabla, BorderLayout.CENTER);
    }

    public void actualizarCategorias(List<Categoria> categorias) {
        this.categoriasList = categorias;
        cboxCategoria.removeAllItems();
        for (Categoria c : categorias) {
            cboxCategoria.addItem(c.getDescripcion());
        }
    }

    public String getCategoriaSeleccionadaId() {
        int idx = cboxCategoria.getSelectedIndex();
        if (idx < 0 || idx >= categoriasList.size()) return null;
        return categoriasList.get(idx).getId();
    }

    public void cargarMatriz(LocalDate fecha, String categoriaId, List<Recurso> recursos, List<Reserva> reservas, List<Funcionario> funcionarios) {
        modelo.setRowCount(0);
        for (Recurso r : recursos) {
            if (categoriaId == null || r.getCategoriaId().equalsIgnoreCase(categoriaId)) {
                Object[] fila = new Object[horas.length + 1];
                fila[0] = r.getDescripcion() + " (" + r.getId() + ")";

                for (int i = 0; i < horas.length; i++) {
                    LocalTime slotHora = LocalTime.parse(horas[i]);
                    String estadoSlot = "DISPONIBLE";

                    for (Reserva res : reservas) {
                        if (res.getEstado() == EstadoReserva.ACTIVA && res.getFecha().equals(fecha)) {
                            if (res.getRecursosIds().contains(r.getId())) {
                                if (!slotHora.isBefore(res.getHoraInicio()) && slotHora.isBefore(res.getHoraFin())) {
                                    String nombreFunc = res.getFuncionarioId();
                                    for (Funcionario f : funcionarios) {
                                        if (f.getId().equalsIgnoreCase(res.getFuncionarioId())) {
                                            nombreFunc = f.getNombre();
                                            break;
                                        }
                                    }
                                    estadoSlot = res.getActividad() + " [" + nombreFunc + "]";
                                    break;
                                }
                            }
                        }
                    }
                    fila[i + 1] = estadoSlot;
                }
                modelo.addRow(fila);
            }
        }
    }

    public JTextField getTxtFecha() { return txtFecha; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnImprimir() { return btnImprimir; }
    public JTable getTabla() { return tabla; }
}

