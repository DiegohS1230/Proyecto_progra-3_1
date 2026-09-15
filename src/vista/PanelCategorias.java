package vista;

import modelo.Categoria;
import utilitarios.EstilosUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelCategorias extends JPanel {
    private JTextField txtBusqueda;
    private JButton btnBuscar;

    private JTextField txtId;
    private JTextField txtDescripcion;

    private JButton btnGuardar;
    private JButton btnBorrar;
    private JButton btnLimpiar;
    private JButton btnImprimir;

    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelCategorias() {
        setLayout(new BorderLayout(12, 12));
        setBackground(EstilosUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelBusqueda.setBackground(EstilosUI.COLOR_BLANCO);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblBuscar = new JLabel("Buscar por Descripcion:");
        lblBuscar.setFont(EstilosUI.FUENTE_BOLD);
        txtBusqueda = new JTextField(20);
        EstilosUI.estilizarCampo(txtBusqueda);

        btnBuscar = new JButton("Buscar");
        EstilosUI.estilizarBoton(btnBuscar, true);

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBusqueda);
        panelBusqueda.add(btnBuscar);

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 10));
        panelFormulario.setBackground(EstilosUI.COLOR_BLANCO);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblId = new JLabel("Codigo / ID:");
        lblId.setFont(EstilosUI.FUENTE_BOLD);
        panelFormulario.add(lblId);
        txtId = new JTextField();
        EstilosUI.estilizarCampo(txtId);
        txtId.setEditable(false);
        panelFormulario.add(txtId);

        JLabel lblDesc = new JLabel("Descripcion:");
        lblDesc.setFont(EstilosUI.FUENTE_BOLD);
        panelFormulario.add(lblDesc);
        txtDescripcion = new JTextField();
        EstilosUI.estilizarCampo(txtDescripcion);
        panelFormulario.add(txtDescripcion);

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

        String[] columnas = new String[]{"ID", "Descripcion"};
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
                txtDescripcion.setText(modelo.getValueAt(r, 1).toString());
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1));
        add(scrollTabla, BorderLayout.CENTER);
    }

    public void actualizarTabla(List<Categoria> lista) {
        modelo.setRowCount(0);
        for (Categoria c : lista) {
            modelo.addRow(new Object[]{c.getId(), c.getDescripcion()});
        }
    }

    public void limpiar() {
        txtId.setText("");
        txtDescripcion.setText("");
        tabla.clearSelection();
    }

    public JTextField getTxtBusqueda() { return txtBusqueda; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtDescripcion() { return txtDescripcion; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnBorrar() { return btnBorrar; }
    public JButton getBtnImprimir() { return btnImprimir; }
    public JTable getTabla() { return tabla; }
}

