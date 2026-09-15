package vista;

import modelo.Categoria;
import modelo.Recurso;
import utilitarios.EstilosUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelRecursos extends JPanel {
    private JComboBox<String> cboxFiltroCategoria;
    private JTextField txtBusqueda;
    private JButton btnBuscar;

    private JTextField txtId;
    private JComboBox<String> cboxCategoriaForm;
    private JTextField txtDescripcion;

    private JButton btnGuardar;
    private JButton btnBorrar;
    private JButton btnLimpiar;
    private JButton btnImprimir;

    private JTable tabla;
    private DefaultTableModel modelo;

    private List<Categoria> categoriasList;

    public PanelRecursos() {
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

        cboxFiltroCategoria = new JComboBox<>();
        cboxFiltroCategoria.setFont(EstilosUI.FUENTE_REGULAR);
        txtBusqueda = new JTextField(16);
        EstilosUI.estilizarCampo(txtBusqueda);

        btnBuscar = new JButton("Buscar");
        EstilosUI.estilizarBoton(btnBuscar, true);

        panelBusqueda.add(new JLabel("Categoria:"));
        panelBusqueda.add(cboxFiltroCategoria);
        panelBusqueda.add(new JLabel("Descripcion:"));
        panelBusqueda.add(txtBusqueda);
        panelBusqueda.add(btnBuscar);

        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
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
        panelFormulario.add(txtId);

        JLabel lblCat = new JLabel("Categoria Asignada:");
        lblCat.setFont(EstilosUI.FUENTE_BOLD);
        panelFormulario.add(lblCat);
        cboxCategoriaForm = new JComboBox<>();
        cboxCategoriaForm.setFont(EstilosUI.FUENTE_REGULAR);
        panelFormulario.add(cboxCategoriaForm);

        JLabel lblDesc = new JLabel("Descripcion / Marca:");
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

        String[] columnas = new String[]{"ID", "Categoria", "Descripcion"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstilosUI.estilizarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int r = tabla.getSelectedRow();
                txtId.setText(modelo.getValueAt(r, 0).toString());
                txtId.setEditable(false);
                String catDesc = modelo.getValueAt(r, 1).toString();
                cboxCategoriaForm.setSelectedItem(catDesc);
                txtDescripcion.setText(modelo.getValueAt(r, 2).toString());
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1));
        add(scrollTabla, BorderLayout.CENTER);
    }

    public void actualizarCategorias(List<Categoria> categorias) {
        this.categoriasList = categorias;
        cboxFiltroCategoria.removeAllItems();
        cboxFiltroCategoria.addItem("TODAS");

        cboxCategoriaForm.removeAllItems();
        for (Categoria c : categorias) {
            cboxFiltroCategoria.addItem(c.getDescripcion());
            cboxCategoriaForm.addItem(c.getDescripcion());
        }
    }

    public String getCategoriaFiltroId() {
        int idx = cboxFiltroCategoria.getSelectedIndex();
        if (idx <= 0 || categoriasList == null) return "TODAS";
        return categoriasList.get(idx - 1).getId();
    }

    public String getCategoriaFormularioId() {
        int idx = cboxCategoriaForm.getSelectedIndex();
        if (idx < 0 || categoriasList == null || idx >= categoriasList.size()) return "";
        return categoriasList.get(idx).getId();
    }

    public void actualizarTabla(List<Recurso> lista) {
        modelo.setRowCount(0);
        for (Recurso r : lista) {
            String catNom = r.getCategoriaId();
            if (categoriasList != null) {
                for (Categoria c : categoriasList) {
                    if (c.getId().equalsIgnoreCase(r.getCategoriaId())) {
                        catNom = c.getDescripcion();
                        break;
                    }
                }
            }
            modelo.addRow(new Object[]{r.getId(), catNom, r.getDescripcion()});
        }
    }

    public void limpiar() {
        txtId.setText("");
        txtId.setEditable(true);
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

