package vista;

import utilitarios.EstilosUI;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    private JTextField txtId;
    private JPasswordField txtClave;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private JButton btnCambiar;

    public VentanaLogin() {
        super("SISTEMA DE RESERVAS");
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 240);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(14, 14));
        panelPrincipal.setBackground(EstilosUI.COLOR_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 12));
        panelCampos.setBackground(EstilosUI.COLOR_FONDO);

        JLabel lblId = new JLabel("ID Usuario:");
        lblId.setFont(EstilosUI.FUENTE_BOLD);
        lblId.setForeground(EstilosUI.COLOR_TEXTO);
        panelCampos.add(lblId);

        txtId = new JTextField();
        EstilosUI.estilizarCampo(txtId);
        panelCampos.add(txtId);

        JLabel lblClave = new JLabel("Clave de Acceso:");
        lblClave.setFont(EstilosUI.FUENTE_BOLD);
        lblClave.setForeground(EstilosUI.COLOR_TEXTO);
        panelCampos.add(lblClave);

        txtClave = new JPasswordField();
        txtClave.setBorder(EstilosUI.BORDE_CAMPO);
        txtClave.setFont(EstilosUI.FUENTE_REGULAR);
        panelCampos.add(txtClave);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(EstilosUI.COLOR_FONDO);

        btnIngresar = new JButton("Ingresar");
        EstilosUI.estilizarBoton(btnIngresar, true);

        btnCancelar = new JButton("Cancelar");
        EstilosUI.estilizarBoton(btnCancelar, false);

        btnCambiar = new JButton("Cambiar Clave");
        EstilosUI.estilizarBoton(btnCambiar, false);

        panelBotones.add(btnIngresar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnCambiar);

        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    public JTextField getTxtId() { return txtId; }
    public JPasswordField getTxtClave() { return txtClave; }
    public JButton getBtnIngresar() { return btnIngresar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getBtnCambiar() { return btnCambiar; }
}

