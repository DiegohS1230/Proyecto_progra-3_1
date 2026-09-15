package vista;

import utilitarios.EstilosUI;
import utilitarios.GeneradorGraficas;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class PanelEstadisticas extends JPanel {
    private JTextField txtDesdeRec;
    private JTextField txtHastaRec;
    private JButton btnCargarRecursos;
    private JButton btnImprimirRecursos;
    private JPanel panelGraficoRecursos;
    private JTable tablaRecursos;
    private DefaultTableModel modeloRecursos;

    private JTextField txtDesdeAct;
    private JTextField txtHastaAct;
    private JButton btnCargarActividades;
    private JButton btnImprimirActividades;
    private JPanel panelGraficoActividades;
    private JTable tablaActividades;
    private DefaultTableModel modeloActividades;

    public PanelEstadisticas() {
        setLayout(new GridLayout(1, 2, 12, 12));
        setBackground(EstilosUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Grafico 1: Recursos reservados por categoria
        JPanel panelRec = new JPanel(new BorderLayout(8, 8));
        panelRec.setBackground(EstilosUI.COLOR_BLANCO);
        panelRec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel controlesRec = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        controlesRec.setBackground(EstilosUI.COLOR_BLANCO);
        txtDesdeRec = new JTextField(LocalDate.now().minusMonths(1).toString(), 8);
        EstilosUI.estilizarCampo(txtDesdeRec);
        txtHastaRec = new JTextField(LocalDate.now().plusMonths(2).toString(), 8);
        EstilosUI.estilizarCampo(txtHastaRec);

        btnCargarRecursos = new JButton("Actualizar");
        EstilosUI.estilizarBoton(btnCargarRecursos, true);
        btnImprimirRecursos = new JButton("Exportar (PDF)");
        EstilosUI.estilizarBoton(btnImprimirRecursos, false);

        controlesRec.add(new JLabel("Desde:"));
        controlesRec.add(txtDesdeRec);
        controlesRec.add(new JLabel("Hasta:"));
        controlesRec.add(txtHastaRec);
        controlesRec.add(btnCargarRecursos);
        controlesRec.add(btnImprimirRecursos);

        panelGraficoRecursos = new JPanel(new BorderLayout());
        panelGraficoRecursos.setBackground(EstilosUI.COLOR_BLANCO);
        modeloRecursos = new DefaultTableModel(new String[]{"Categoria", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRecursos = new JTable(modeloRecursos);
        EstilosUI.estilizarTabla(tablaRecursos);

        JSplitPane splitRec = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelGraficoRecursos, new JScrollPane(tablaRecursos));
        splitRec.setResizeWeight(0.72);
        splitRec.setDividerSize(6);
        panelRec.add(controlesRec, BorderLayout.NORTH);
        panelRec.add(splitRec, BorderLayout.CENTER);

        // Grafico 2: Actividades por semana
        JPanel panelAct = new JPanel(new BorderLayout(8, 8));
        panelAct.setBackground(EstilosUI.COLOR_BLANCO);
        panelAct.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel controlesAct = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        controlesAct.setBackground(EstilosUI.COLOR_BLANCO);
        txtDesdeAct = new JTextField(LocalDate.now().minusMonths(1).toString(), 8);
        EstilosUI.estilizarCampo(txtDesdeAct);
        txtHastaAct = new JTextField(LocalDate.now().plusMonths(2).toString(), 8);
        EstilosUI.estilizarCampo(txtHastaAct);

        btnCargarActividades = new JButton("Actualizar");
        EstilosUI.estilizarBoton(btnCargarActividades, true);
        btnImprimirActividades = new JButton("Exportar (PDF)");
        EstilosUI.estilizarBoton(btnImprimirActividades, false);

        controlesAct.add(new JLabel("Desde:"));
        controlesAct.add(txtDesdeAct);
        controlesAct.add(new JLabel("Hasta:"));
        controlesAct.add(txtHastaAct);
        controlesAct.add(btnCargarActividades);
        controlesAct.add(btnImprimirActividades);

        panelGraficoActividades = new JPanel(new BorderLayout());
        panelGraficoActividades.setBackground(EstilosUI.COLOR_BLANCO);
        modeloActividades = new DefaultTableModel(new String[]{"Semana", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaActividades = new JTable(modeloActividades);
        EstilosUI.estilizarTabla(tablaActividades);

        JSplitPane splitAct = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelGraficoActividades, new JScrollPane(tablaActividades));
        splitAct.setResizeWeight(0.72);
        splitAct.setDividerSize(6);
        panelAct.add(controlesAct, BorderLayout.NORTH);
        panelAct.add(splitAct, BorderLayout.CENTER);

        add(panelRec);
        add(panelAct);
    }

    public void actualizarRecursos(Map<String, Integer> datos) {
        modeloRecursos.setRowCount(0);
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            modeloRecursos.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        panelGraficoRecursos.removeAll();
        ChartPanel cp = GeneradorGraficas.crearGraficoBarras("Recursos por Categoria", "Categoria", "Total", datos, EstilosUI.COLOR_PRIMARIO);
        panelGraficoRecursos.add(cp, BorderLayout.CENTER);
        panelGraficoRecursos.revalidate();
        panelGraficoRecursos.repaint();
    }

    public void actualizarActividades(Map<String, Integer> datos) {
        modeloActividades.setRowCount(0);
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            modeloActividades.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        panelGraficoActividades.removeAll();
        ChartPanel cp = GeneradorGraficas.crearGraficoBarras("Actividades por Semana", "Semana (Lunes)", "Total", datos, EstilosUI.COLOR_SECUNDARIO);
        panelGraficoActividades.add(cp, BorderLayout.CENTER);
        panelGraficoActividades.revalidate();
        panelGraficoActividades.repaint();
    }

    public JTextField getTxtDesdeRec() { return txtDesdeRec; }
    public JTextField getTxtHastaRec() { return txtHastaRec; }
    public JButton getBtnCargarRecursos() { return btnCargarRecursos; }
    public JButton getBtnImprimirRecursos() { return btnImprimirRecursos; }
    public JTable getTablaRecursos() { return tablaRecursos; }
    public JTextField getTxtDesdeAct() { return txtDesdeAct; }
    public JTextField getTxtHastaAct() { return txtHastaAct; }
    public JButton getBtnCargarActividades() { return btnCargarActividades; }
    public JButton getBtnImprimirActividades() { return btnImprimirActividades; }
    public JTable getTablaActividades() { return tablaActividades; }
}

