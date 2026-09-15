package vista;

import modelo.Categoria;
import modelo.Reserva;
import utilitarios.EstilosUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanelReservas extends JPanel {
    private JTextField txtFrase;
    private JButton btnExtraer;

    private JTextField txtActividad;
    private JTextField txtFecha;
    private JComboBox<String> cboxHoraInicio;
    private JComboBox<String> cboxHoraFin;
    private JPanel panelCheckboxesCategorias;
    private List<JCheckBox> checkBoxesCategorias = new ArrayList<>();
    private List<Categoria> categoriasList = new ArrayList<>();

    private JButton btnReservar;
    private JButton btnLimpiar;
    private JButton btnCancelarReserva;
    private JButton btnImprimir;

    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;

    public PanelReservas() {
        setLayout(new BorderLayout(12, 12));
        setBackground(EstilosUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Asistente IA con campo de frase y boton Extraer contiguo
        JPanel panelIA = new JPanel(new BorderLayout(10, 6));
        panelIA.setBackground(EstilosUI.COLOR_BLANCO);
        panelIA.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblFrase = new JLabel("Frase:");
        lblFrase.setFont(EstilosUI.FUENTE_BOLD);
        lblFrase.setForeground(EstilosUI.COLOR_PRIMARIO);

        txtFrase = new JTextField();
        EstilosUI.estilizarCampo(txtFrase);
        txtFrase.setToolTipText("Ejemplo: Reserva sala para 10 personas y proyector manana de 8 a 10 para reunion");

        btnExtraer = new JButton("Extraer");
        EstilosUI.estilizarBoton(btnExtraer, true);

        panelIA.add(lblFrase, BorderLayout.WEST);
        panelIA.add(txtFrase, BorderLayout.CENTER);
        panelIA.add(btnExtraer, BorderLayout.EAST);

        // Formulario de campos de reserva
        JPanel panelForm = new JPanel(new GridLayout(2, 4, 10, 8));
        panelForm.setBackground(EstilosUI.COLOR_BLANCO);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblAct = new JLabel("Actividad:");
        lblAct.setFont(EstilosUI.FUENTE_BOLD);
        panelForm.add(lblAct);
        txtActividad = new JTextField();
        EstilosUI.estilizarCampo(txtActividad);
        panelForm.add(txtActividad);

        JLabel lblFecha = new JLabel("Fecha (YYYY-MM-DD):");
        lblFecha.setFont(EstilosUI.FUENTE_BOLD);
        panelForm.add(lblFecha);
        txtFecha = new JTextField(LocalDate.now().plusDays(1).toString());
        EstilosUI.estilizarCampo(txtFecha);
        panelForm.add(txtFecha);

        String[] horas = new String[]{
                "07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
                "19:00", "20:00", "21:00", "22:00"
        };
        JLabel lblHoraIni = new JLabel("Hora Inicio:");
        lblHoraIni.setFont(EstilosUI.FUENTE_BOLD);
        panelForm.add(lblHoraIni);
        cboxHoraInicio = new JComboBox<>(horas);
        cboxHoraInicio.setFont(EstilosUI.FUENTE_REGULAR);
        cboxHoraInicio.setSelectedItem("08:00");
        panelForm.add(cboxHoraInicio);

        JLabel lblHoraFin = new JLabel("Hora Fin:");
        lblHoraFin.setFont(EstilosUI.FUENTE_BOLD);
        panelForm.add(lblHoraFin);
        cboxHoraFin = new JComboBox<>(horas);
        cboxHoraFin.setFont(EstilosUI.FUENTE_REGULAR);
        cboxHoraFin.setSelectedItem("10:00");
        panelForm.add(cboxHoraFin);

        // Panel de checkboxes de categorias requeridas
        panelCheckboxesCategorias = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        panelCheckboxesCategorias.setBackground(EstilosUI.COLOR_BLANCO);
        JScrollPane scrollCategorias = new JScrollPane(panelCheckboxesCategorias);
        scrollCategorias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        scrollCategorias.setPreferredSize(new Dimension(500, 68));

        // Botones de accion
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        panelBotones.setBackground(EstilosUI.COLOR_FONDO);

        btnReservar = new JButton("Reservar");
        EstilosUI.estilizarBoton(btnReservar, true);

        btnLimpiar = new JButton("Limpiar");
        EstilosUI.estilizarBoton(btnLimpiar, false);

        btnCancelarReserva = new JButton("Cancelar Reserva");
        EstilosUI.estilizarBoton(btnCancelarReserva, false);

        btnImprimir = new JButton("Exportar (PDF)");
        EstilosUI.estilizarBoton(btnImprimir, false);

        panelBotones.add(btnReservar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCancelarReserva);
        panelBotones.add(btnImprimir);

        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel panelSuperior = new JPanel(new BorderLayout(8, 8));
        panelSuperior.setBackground(EstilosUI.COLOR_FONDO);
        panelSuperior.add(panelIA, BorderLayout.NORTH);
        panelSuperior.add(panelForm, BorderLayout.CENTER);
        panelSuperior.add(scrollCategorias, BorderLayout.SOUTH);

        JPanel panelCabecera = new JPanel(new BorderLayout(6, 6));
        panelCabecera.setBackground(EstilosUI.COLOR_FONDO);
        panelCabecera.add(panelSuperior, BorderLayout.CENTER);
        panelCabecera.add(panelBotones, BorderLayout.SOUTH);

        add(panelCabecera, BorderLayout.NORTH);

        // Tabla de reservas estilizada segun directrices
        String[] columnas = new String[]{"ID", "Actividad", "Fecha", "Hora Inicio", "Hora Fin", "Recursos Asignados", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaReservas = new JTable(modeloTabla);
        tablaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstilosUI.estilizarTabla(tablaReservas);

        JScrollPane scrollTabla = new JScrollPane(tablaReservas);
        scrollTabla.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1));
        add(scrollTabla, BorderLayout.CENTER);
    }

    public void actualizarCategorias(List<Categoria> categorias) {
        this.categoriasList = categorias;
        panelCheckboxesCategorias.removeAll();
        checkBoxesCategorias.clear();

        for (Categoria cat : categorias) {
            JCheckBox cb = new JCheckBox(cat.getDescripcion());
            cb.setFont(EstilosUI.FUENTE_REGULAR);
            cb.setBackground(EstilosUI.COLOR_BLANCO);
            cb.setName(cat.getId());
            checkBoxesCategorias.add(cb);
            panelCheckboxesCategorias.add(cb);
        }
        panelCheckboxesCategorias.revalidate();
        panelCheckboxesCategorias.repaint();
    }

    public void seleccionarCategoriasPorNombre(List<String> nombres) {
        for (JCheckBox cb : checkBoxesCategorias) {
            boolean marcar = false;
            if (nombres != null) {
                for (String n : nombres) {
                    if (cb.getText().toLowerCase().contains(n.toLowerCase())) {
                        marcar = true;
                        break;
                    }
                }
            }
            cb.setSelected(marcar);
        }
    }

    public List<String> getCategoriasSeleccionadasIds() {
        List<String> ids = new ArrayList<>();
        for (JCheckBox cb : checkBoxesCategorias) {
            if (cb.isSelected()) {
                ids.add(cb.getName());
            }
        }
        return ids;
    }

    public void actualizarTabla(List<Reserva> reservas) {
        modeloTabla.setRowCount(0);
        for (Reserva r : reservas) {
            modeloTabla.addRow(new Object[]{
                    r.getId(),
                    r.getActividad(),
                    r.getFecha().toString(),
                    r.getHoraInicio().toString(),
                    r.getHoraFin().toString(),
                    String.join(", ", r.getRecursosIds()),
                    r.getEstado().name()
            });
        }
    }

    public String getReservaSeleccionadaId() {
        int fila = tablaReservas.getSelectedRow();
        if (fila != -1) {
            return (String) modeloTabla.getValueAt(fila, 0);
        }
        return null;
    }

    public void limpiarFormulario() {
        txtFrase.setText("");
        txtActividad.setText("");
        txtFecha.setText(LocalDate.now().plusDays(1).toString());
        cboxHoraInicio.setSelectedItem("08:00");
        cboxHoraFin.setSelectedItem("10:00");
        for (JCheckBox cb : checkBoxesCategorias) {
            cb.setSelected(false);
        }
    }

    public JTextField getTxtFrase() { return txtFrase; }
    public JButton getBtnExtraer() { return btnExtraer; }
    public JButton getBtnExtraerAI() { return btnExtraer; }
    public JTextField getTxtActividad() { return txtActividad; }
    public JTextField getTxtFecha() { return txtFecha; }
    public JComboBox<String> getCboxHoraInicio() { return cboxHoraInicio; }
    public JComboBox<String> getCboxHoraFin() { return cboxHoraFin; }
    public JButton getBtnReservar() { return btnReservar; }
    public JButton getBtnCancelarReserva() { return btnCancelarReserva; }
    public JButton getBtnImprimir() { return btnImprimir; }
    public JTable getTablaReservas() { return tablaReservas; }
}

