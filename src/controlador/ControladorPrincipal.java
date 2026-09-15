package controlador;

import modelo.*;
import servicio.ServicioReservas;
import utilitarios.ExportadorPDF;
import utilitarios.GeminiReservaDTO;
import vista.*;

import javax.swing.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ControladorPrincipal {
    private final ServicioReservas servicio;
    private VentanaLogin vistaLogin;
    private VentanaPrincipal vistaPrincipal;

    public ControladorPrincipal(ServicioReservas servicio) {
        this.servicio = servicio;
        this.vistaLogin = new VentanaLogin();
    }

    public void iniciar() {
        configurarEventosLogin();
        vistaLogin.setVisible(true);
    }

    private void configurarEventosLogin() {
        vistaLogin.getBtnIngresar().addActionListener(e -> autenticar());
        vistaLogin.getBtnCancelar().addActionListener(e -> System.exit(0));
        vistaLogin.getBtnCambiar().addActionListener(e -> cambiarClave());
    }

    private void autenticar() {
        String id = vistaLogin.getTxtId().getText().trim();
        String pass = new String(vistaLogin.getTxtClave().getPassword()).trim();

        if (id.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vistaLogin, "Ingrese ID y clave", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = servicio.autenticar(id, pass);
        if (u == null) {
            JOptionPane.showMessageDialog(vistaLogin, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        vistaLogin.dispose();
        vistaPrincipal = new VentanaPrincipal(u);
        configurarVistasSegunRol(u);
        vistaPrincipal.setVisible(true);
    }

    private void cambiarClave() {
        String id = vistaLogin.getTxtId().getText().trim();
        String pass = new String(vistaLogin.getTxtClave().getPassword()).trim();
        if (id.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vistaLogin, "Ingrese su ID y clave actual", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nueva = JOptionPane.showInputDialog(vistaLogin, "Ingrese la nueva clave:");
        if (nueva != null && !nueva.trim().isEmpty()) {
            try {
                boolean ok = servicio.cambiarClave(id, pass, nueva.trim());
                if (ok) {
                    JOptionPane.showMessageDialog(vistaLogin, "Clave actualizada exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vistaLogin, "No se pudo actualizar. Verifique usuario o clave actual", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaLogin, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void configurarVistasSegunRol(Usuario u) {
        if (u.getRol() == Rol.ADMINISTRADOR) {
            configurarModuloFuncionarios();
            configurarModuloCategorias();
            configurarModuloRecursos();
        } else {
            configurarModuloReservas(u.getId());
        }
        configurarModuloCalendarizacion();
        configurarModuloActividades();
        configurarModuloEstadisticas();
    }

    // Gestion de funcionarios
    private void configurarModuloFuncionarios() {
        PanelFuncionarios pf = vistaPrincipal.getPanelFuncionarios();
        pf.actualizarTabla(servicio.buscarFuncionarios(""));

        pf.getBtnBuscar().addActionListener(e -> {
            String q = pf.getTxtBusqueda().getText().trim();
            pf.actualizarTabla(servicio.buscarFuncionarios(q));
        });

        pf.getBtnGuardar().addActionListener(e -> {
            String id = pf.getTxtId().getText().trim();
            String nombre = pf.getTxtNombre().getText().trim();
            String tel = pf.getTxtTelefono().getText().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "ID y Nombre son obligatorios", "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Funcionario f = new Funcionario(id, id, nombre, tel);
                servicio.guardarFuncionario(f);
                pf.actualizarTabla(servicio.buscarFuncionarios(""));
                pf.limpiar();
                JOptionPane.showMessageDialog(vistaPrincipal, "Funcionario guardado", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pf.getBtnBorrar().addActionListener(e -> {
            String id = pf.getTxtId().getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Seleccione un funcionario para borrar", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int opt = JOptionPane.showConfirmDialog(vistaPrincipal, "Desea borrar al funcionario " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                try {
                    servicio.borrarFuncionario(id);
                    pf.actualizarTabla(servicio.buscarFuncionarios(""));
                    pf.limpiar();
                    JOptionPane.showMessageDialog(vistaPrincipal, "Funcionario eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vistaPrincipal, "Error al borrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pf.getBtnImprimir().addActionListener(e -> exportarTablaPDF(pf.getTabla(), "Reporte de Funcionarios", "reporte_funcionarios.pdf"));
    }

    // Gestion de categorias
    private void configurarModuloCategorias() {
        PanelCategorias pc = vistaPrincipal.getPanelCategorias();
        pc.actualizarTabla(servicio.buscarCategorias(""));

        pc.getBtnBuscar().addActionListener(e -> {
            String q = pc.getTxtBusqueda().getText().trim();
            pc.actualizarTabla(servicio.buscarCategorias(q));
        });

        pc.getBtnGuardar().addActionListener(e -> {
            String id = pc.getTxtId().getText().trim();
            String desc = pc.getTxtDescripcion().getText().trim();

            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "La descripcion es obligatoria", "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Categoria c = new Categoria(id, desc);
                servicio.guardarCategoria(c);
                pc.actualizarTabla(servicio.buscarCategorias(""));
                pc.limpiar();
                actualizarCatalogosEnVistas();
                JOptionPane.showMessageDialog(vistaPrincipal, "Categoria guardada exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Error al guardar categoria: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pc.getBtnBorrar().addActionListener(e -> {
            String id = pc.getTxtId().getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Seleccione una categoria para borrar", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int opt = JOptionPane.showConfirmDialog(vistaPrincipal, "Desea borrar la categoria " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                try {
                    servicio.borrarCategoria(id);
                    pc.actualizarTabla(servicio.buscarCategorias(""));
                    pc.limpiar();
                    actualizarCatalogosEnVistas();
                    JOptionPane.showMessageDialog(vistaPrincipal, "Categoria eliminada", "Exito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vistaPrincipal, "Error al borrar categoria: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pc.getBtnImprimir().addActionListener(e -> exportarTablaPDF(pc.getTabla(), "Reporte de Categorias", "reporte_categorias.pdf"));
    }

    // Gestion de recursos
    private void configurarModuloRecursos() {
        PanelRecursos pr = vistaPrincipal.getPanelRecursos();
        pr.actualizarCategorias(servicio.getTodasCategorias());
        pr.actualizarTabla(servicio.filtrarRecursos("TODAS", ""));

        pr.getBtnBuscar().addActionListener(e -> {
            String cat = pr.getCategoriaFiltroId();
            String q = pr.getTxtBusqueda().getText().trim();
            pr.actualizarTabla(servicio.filtrarRecursos(cat, q));
        });

        pr.getBtnGuardar().addActionListener(e -> {
            String id = pr.getTxtId().getText().trim();
            String catId = pr.getCategoriaFormularioId();
            String desc = pr.getTxtDescripcion().getText().trim();

            if (id.isEmpty() || catId.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Todos los campos de recurso son requeridos", "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Recurso r = new Recurso(id, catId, desc);
                servicio.guardarRecurso(r);
                pr.actualizarTabla(servicio.filtrarRecursos(pr.getCategoriaFiltroId(), ""));
                pr.limpiar();
                JOptionPane.showMessageDialog(vistaPrincipal, "Recurso guardado con exito", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Error al guardar recurso: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pr.getBtnBorrar().addActionListener(e -> {
            String id = pr.getTxtId().getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Seleccione un recurso para borrar", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int opt = JOptionPane.showConfirmDialog(vistaPrincipal, "Desea borrar el recurso " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                try {
                    servicio.borrarRecurso(id);
                    pr.actualizarTabla(servicio.filtrarRecursos(pr.getCategoriaFiltroId(), ""));
                    pr.limpiar();
                    JOptionPane.showMessageDialog(vistaPrincipal, "Recurso eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vistaPrincipal, "Error al borrar recurso: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pr.getBtnImprimir().addActionListener(e -> exportarTablaPDF(pr.getTabla(), "Catalogo de Recursos", "reporte_recursos.pdf"));
    }

    // Gestion de reservas del funcionario con asistente IA Gemini
    private void configurarModuloReservas(String funcionarioId) {
        PanelReservas pres = vistaPrincipal.getPanelReservas();
        pres.actualizarCategorias(servicio.getTodasCategorias());
        pres.actualizarTabla(servicio.obtenerReservasFuncionario(funcionarioId));

        // Evento del boton Extraer para invocar a GeminiService y autocompletar formulario
        pres.getBtnExtraer().addActionListener(e -> {
            String texto = pres.getTxtFrase().getText().trim();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Escriba una frase en lenguaje natural en el campo Frase", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<String> nombresCats = new ArrayList<>();
            for (Categoria c : servicio.getTodasCategorias()) {
                nombresCats.add(c.getDescripcion());
            }

            try {
                GeminiReservaDTO dto = servicio.getGeminiService().extraerDatosReserva(texto, nombresCats);
                if (dto.getActividad() != null && !dto.getActividad().isEmpty()) {
                    pres.getTxtActividad().setText(dto.getActividad());
                }
                if (dto.getFecha() != null && !dto.getFecha().isEmpty()) {
                    pres.getTxtFecha().setText(dto.getFecha());
                }
                if (dto.getHoraInicio() != null && !dto.getHoraInicio().isEmpty()) {
                    pres.getCboxHoraInicio().setSelectedItem(dto.getHoraInicio());
                }
                if (dto.getHoraFin() != null && !dto.getHoraFin().isEmpty()) {
                    pres.getCboxHoraFin().setSelectedItem(dto.getHoraFin());
                }
                if (dto.getCategorias() != null && !dto.getCategorias().isEmpty()) {
                    pres.seleccionarCategoriasPorNombre(dto.getCategorias());
                }
                JOptionPane.showMessageDialog(vistaPrincipal, "Campos autocompletados por IA. Verifique y presione Reservar", "Asistente IA", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Error procesando IA: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Crear reserva
        pres.getBtnReservar().addActionListener(e -> {
            String act = pres.getTxtActividad().getText().trim();
            String fechaStr = pres.getTxtFecha().getText().trim();
            String hIniStr = (String) pres.getCboxHoraInicio().getSelectedItem();
            String hFinStr = (String) pres.getCboxHoraFin().getSelectedItem();
            List<String> catIds = pres.getCategoriasSeleccionadasIds();

            if (act.isEmpty() || fechaStr.isEmpty() || catIds.isEmpty()) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Actividad, fecha y al menos una categoria son requeridos", "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate fecha = LocalDate.parse(fechaStr);
                LocalTime hIni = LocalTime.parse(hIniStr);
                LocalTime hFin = LocalTime.parse(hFinStr);

                if (!hIni.isBefore(hFin)) {
                    JOptionPane.showMessageDialog(vistaPrincipal, "La hora de inicio debe ser anterior a la hora de fin", "Validacion", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Reserva r = servicio.crearReserva(funcionarioId, act, fecha, hIni, hFin, catIds);
                pres.actualizarTabla(servicio.obtenerReservasFuncionario(funcionarioId));
                pres.limpiarFormulario();
                JOptionPane.showMessageDialog(vistaPrincipal, "Reserva creada: " + r.getId() + "\nRecursos asignados: " + String.join(", ", r.getRecursosIds()), "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "No se pudo realizar la reserva: " + ex.getMessage(), "Error de Disponibilidad", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancelar reserva seleccionada
        pres.getBtnCancelarReserva().addActionListener(e -> {
            String rId = pres.getReservaSeleccionadaId();
            if (rId == null) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Seleccione una reserva de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int opt = JOptionPane.showConfirmDialog(vistaPrincipal, "Desea cancelar la reserva " + rId + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                try {
                    boolean ok = servicio.cancelarReserva(rId, funcionarioId);
                    if (ok) {
                        pres.actualizarTabla(servicio.obtenerReservasFuncionario(funcionarioId));
                        JOptionPane.showMessageDialog(vistaPrincipal, "Reserva cancelada correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(vistaPrincipal, "No se pudo cancelar la reserva", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vistaPrincipal, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pres.getBtnImprimir().addActionListener(e -> exportarTablaPDF(pres.getTablaReservas(), "Mis Reservas", "reporte_reservas.pdf"));
    }

    private void configurarModuloCalendarizacion() {
        PanelCalendarizacionRecursos pcal = vistaPrincipal.getPanelCalendarizacion();
        pcal.actualizarCategorias(servicio.getTodasCategorias());

        Runnable recargarMatriz = () -> {
            try {
                LocalDate f = LocalDate.parse(pcal.getTxtFecha().getText().trim());
                String catId = pcal.getCategoriaSeleccionadaId();
                pcal.cargarMatriz(f, catId, servicio.getTodosRecursos(), servicio.getTodasReservas(), servicio.getTodosFuncionarios());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Formato de fecha invalido (YYYY-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        pcal.getBtnCargar().addActionListener(e -> recargarMatriz.run());
        pcal.getBtnImprimir().addActionListener(e -> exportarTablaPDF(pcal.getTabla(), "Calendarizacion de Recursos", "reporte_calendarizacion_recursos.pdf"));
        recargarMatriz.run();
    }

    private void configurarModuloActividades() {
        PanelCalendarizacionActividades pact = vistaPrincipal.getPanelActividades();

        Runnable recargarSemana = () -> {
            try {
                LocalDate f = LocalDate.parse(pact.getTxtFecha().getText().trim());
                pact.cargarMatriz(f, servicio.getTodasReservas(), servicio.getTodosFuncionarios());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Formato de fecha invalido (YYYY-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        pact.getBtnCargar().addActionListener(e -> recargarSemana.run());
        pact.getBtnImprimir().addActionListener(e -> exportarTablaPDF(pact.getTabla(), "Calendarizacion de Actividades", "reporte_calendarizacion_actividades.pdf"));
        recargarSemana.run();
    }

    private void configurarModuloEstadisticas() {
        PanelEstadisticas pest = vistaPrincipal.getPanelEstadisticas();

        Runnable cargarRec = () -> {
            try {
                LocalDate d = LocalDate.parse(pest.getTxtDesdeRec().getText().trim());
                LocalDate h = LocalDate.parse(pest.getTxtHastaRec().getText().trim());
                validarPeriodo(d, h);
                pest.actualizarRecursos(servicio.estadisticasRecursos(d, h));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Fechas invalidas para estadisticas de recursos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        Runnable cargarAct = () -> {
            try {
                LocalDate d = LocalDate.parse(pest.getTxtDesdeAct().getText().trim());
                LocalDate h = LocalDate.parse(pest.getTxtHastaAct().getText().trim());
                validarPeriodo(d, h);
                pest.actualizarActividades(servicio.estadisticasActividades(d, h));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Fechas invalidas para estadisticas de actividades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        pest.getBtnCargarRecursos().addActionListener(e -> cargarRec.run());
        pest.getBtnCargarActividades().addActionListener(e -> cargarAct.run());
        pest.getBtnImprimirRecursos().addActionListener(e -> exportarTablaPDF(pest.getTablaRecursos(), "Estadisticas de Recursos", "reporte_estadisticas_recursos.pdf"));
        pest.getBtnImprimirActividades().addActionListener(e -> exportarTablaPDF(pest.getTablaActividades(), "Estadisticas de Actividades", "reporte_estadisticas_actividades.pdf"));

        cargarRec.run();
        cargarAct.run();
    }

    private void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("la fecha desde no puede ser posterior a la fecha hasta");
        }
    }

    private void actualizarCatalogosEnVistas() {
        if (vistaPrincipal.getPanelRecursos() != null) {
            vistaPrincipal.getPanelRecursos().actualizarCategorias(servicio.getTodasCategorias());
        }
        if (vistaPrincipal.getPanelReservas() != null) {
            vistaPrincipal.getPanelReservas().actualizarCategorias(servicio.getTodasCategorias());
        }
        if (vistaPrincipal.getPanelCalendarizacion() != null) {
            vistaPrincipal.getPanelCalendarizacion().actualizarCategorias(servicio.getTodasCategorias());
        }
    }

    private void exportarTablaPDF(JTable tabla, String titulo, String nombrePorDefecto) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(nombrePorDefecto));
        int res = fc.showSaveDialog(vistaPrincipal);
        if (res == JFileChooser.APPROVE_OPTION) {
            File archivo = fc.getSelectedFile();
            if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
                archivo = new File(archivo.getAbsolutePath() + ".pdf");
            }
            try {
                ExportadorPDF.exportarTabla(titulo, tabla.getModel(), archivo);
                JOptionPane.showMessageDialog(vistaPrincipal, "Reporte exportado exitosamente a: " + archivo.getName(), "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaPrincipal, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

