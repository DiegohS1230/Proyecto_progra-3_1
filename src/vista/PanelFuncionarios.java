package vista;

import modelo.Funcionario;
import utilitarios.EstilosUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelFuncionarios extends JPanel {
    private JTextField txtBusqueda;
    private JButton btnBuscar;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTelefono;

    private JButton btnGuardar;
    private JButton btnBorrar;
    private JButton btnLimpiar;
    private JButton btnImprimir;

    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelFuncionarios() {
        setLayout(new BorderLayout(12, 12));
        setBackground(EstilosUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Buscador
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelBusqueda.setBackground(EstilosUI.COLOR_BLANCO);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblBuscar = new JLabel("Buscar (ID o Nombre):");
        lblBuscar.setFont(EstilosUI.FUENTE_BOLD);
        txtBusqueda = new JTextField(20);
        EstilosUI.estilizarCampo(txtBusqueda);

        btnBuscar = new JButton("Buscar");
        EstilosUI.estilizarBoton(btnBuscar, true);

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBusqueda);
        panelBusqueda.add(btnBuscar);

        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        panelFormulario.setBackground(EstilosUI.COLOR_BLANCO);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblId = new JLabel("ID / Cedula:");
        lblId.setFont(EstilosUI.FUENTE_BOLD);
        panelFormulario.add(lblId);
        txtId = new JTextField();
        EstilosUI.estilizarCampo(txtId);
        panelFormulario.add(txtId);

        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(EstilosUI.FUENTE_BOLD);
        panelFormulario.add(lblNombre);
        txtNombre = new JTextField();
        EstilosUI.estilizarCampo(txtNombre);
        panelFormulario.add(txtNombre);

        JLabel lblTel = new JLabel("Telefono:");
        lblTel.setFont(EstilosUI.FUENTE_BOLD);
        panelFormulario.add(lblTel);
        txtTelefono = new JTextField();
        EstilosUI.estilizarCampo(txtTelefono);
        panelFormulario.add(txtTelefono);

        // Botones de accion
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        panelAcciones.setBackground(EstilosUI.COLOR_FONDO);

        btnGuardar = new JButton("Guardar");
        EstilosUI.estilizarBoton(btnGuardar, true);

        btnBorrar = new JButton("Borrar");
        EstilosUI.estilizarBoton(btnBorrar, false);

        btnLimpiar = new JButton("Limpiar");
        EstilosUI.estilizarBoton(btnLimpiar, false);

        btnImprimir = new JButton("Exportar (PDF)");
        EstilosUI.estilizarBoton(btnImprimir, false);

        panelAcciones.add(btnGuardar);
        panelAcciones.add(btnBorrar);
        panelAcciones.add(btnLimpiar);
        panelAcciones.add(btnImprimir);

        btnLimpiar.addActionListener(e -> limpiar());

        JPanel panelNorte = new JPanel(new BorderLayout(8, 8));
        panelNorte.setBackground(EstilosUI.COLOR_FONDO);
        panelNorte.add(panelBusqueda, BorderLayout.NORTH);
        panelNorte.add(panelFormulario, BorderLayout.CENTER);
        panelNorte.add(panelAcciones, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // Tabla con altura de fila 25px y rejilla sobria
        String[] columnas = new String[]{"ID", "Nombre", "Telefono"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstilosUI.estilizarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int r = tabla.getSelectedRow();
                txtId.setText(modelo.getValueAt(r, 0).toString());
                txtNombre.setText(modelo.getValueAt(r, 1).toString());
                txtTelefono.setText(modelo.getValueAt(r, 2).toString());
                txtId.setEditable(false);
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1));
        add(scrollTabla, BorderLayout.CENTER);
    }

    public void actualizarTabla(List<Funcionario> lista) {
        modelo.setRowCount(0);
        for (Funcionario f : lista) {
            modelo.addRow(new Object[]{f.getId(), f.getNombre(), f.getTelefono()});
        }
    }

    public void limpiar() {
        txtId.setText("");
        txtId.setEditable(true);
        txtNombre.setText("");
        txtTelefono.setText("");
        tabla.clearSelection();
    }

    public JTextField getTxtBusqueda() { return txtBusqueda; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnBorrar() { return btnBorrar; }
    public JButton getBtnImprimir() { return btnImprimir; }
    public JTable getTabla() { return tabla; }
}

