package vista;

import modelo.Rol;
import modelo.Usuario;
import utilitarios.EstilosUI;

import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    private final Usuario usuarioActual;
    private JTabbedPane tabbedPane;

    private PanelReservas panelReservas;
    private PanelFuncionarios panelFuncionarios;
    private PanelCategorias panelCategorias;
    private PanelRecursos panelRecursos;
    private PanelCalendarizacionRecursos panelCalendarizacion;
    private PanelCalendarizacionActividades panelActividades;
    private PanelEstadisticas panelEstadisticas;

    public VentanaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        inicializarUI();
    }

    private void inicializarUI() {
        String rolStr = (usuarioActual.getRol() == Rol.ADMINISTRADOR) ? "ADMINISTRADOR" : "FUNCIONARIO";
        setTitle("SISTEMA DE RESERVAS DE RECURSOS - " + usuarioActual.getId() + " (" + rolStr + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 760);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(EstilosUI.FUENTE_BOLD);
        tabbedPane.setBackground(EstilosUI.COLOR_FONDO);

        panelCalendarizacion = new PanelCalendarizacionRecursos();
        panelActividades = new PanelCalendarizacionActividades();
        panelEstadisticas = new PanelEstadisticas();

        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
            panelFuncionarios = new PanelFuncionarios();
            panelCategorias = new PanelCategorias();
            panelRecursos = new PanelRecursos();

            tabbedPane.addTab("Funcionarios", panelFuncionarios);
            tabbedPane.addTab("Categorias", panelCategorias);
            tabbedPane.addTab("Recursos", panelRecursos);
            tabbedPane.addTab("Calendarizacion", panelCalendarizacion);
            tabbedPane.addTab("Actividades", panelActividades);
            tabbedPane.addTab("Estadisticas", panelEstadisticas);
        } else {
            panelReservas = new PanelReservas();
            tabbedPane.addTab("Reservas", panelReservas);
            tabbedPane.addTab("Calendarizacion", panelCalendarizacion);
            tabbedPane.addTab("Actividades", panelActividades);
            tabbedPane.addTab("Estadisticas", panelEstadisticas);
        }

        setContentPane(tabbedPane);
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public PanelReservas getPanelReservas() { return panelReservas; }
    public PanelFuncionarios getPanelFuncionarios() { return panelFuncionarios; }
    public PanelCategorias getPanelCategorias() { return panelCategorias; }
    public PanelRecursos getPanelRecursos() { return panelRecursos; }
    public PanelCalendarizacionRecursos getPanelCalendarizacion() { return panelCalendarizacion; }
    public PanelCalendarizacionActividades getPanelActividades() { return panelActividades; }
    public PanelEstadisticas getPanelEstadisticas() { return panelEstadisticas; }
}

