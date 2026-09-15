package servicio;

import datos.XmlManager;
import modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

public class ServicioReservas {
    private final XmlManager xmlManager;
    private final GeminiService geminiService;

    private List<Usuario> cacheUsuarios;
    private List<Funcionario> cacheFuncionarios;
    private List<Categoria> cacheCategorias;
    private List<Recurso> cacheRecursos;
    private List<Reserva> cacheReservas;

    public ServicioReservas(XmlManager xmlManager) {
        this.xmlManager = xmlManager;
        this.geminiService = new GeminiService();
        recargarDatos();
    }

    public synchronized void recargarDatos() {
        this.cacheUsuarios = xmlManager.leerUsuarios();
        this.cacheFuncionarios = xmlManager.leerFuncionarios();
        this.cacheCategorias = xmlManager.leerCategorias();
        this.cacheRecursos = xmlManager.leerRecursos();
        this.cacheReservas = xmlManager.leerReservas();
    }

    public GeminiService getGeminiService() {
        return geminiService;
    }

    public Usuario autenticar(String id, String clave) {
        for (Usuario u : cacheUsuarios) {
            if (u.getId().equalsIgnoreCase(id) && u.getClave().equals(clave)) {
                return u;
            }
        }
        for (Funcionario f : cacheFuncionarios) {
            if (f.getId().equalsIgnoreCase(id) && f.getClave().equals(clave)) {
                return f;
            }
        }
        return null;
    }

    public boolean cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        boolean modificado = false;
        for (Usuario u : cacheUsuarios) {
            if (u.getId().equalsIgnoreCase(id) && u.getClave().equals(claveActual)) {
                u.setClave(claveNueva);
                modificado = true;
                break;
            }
        }
        if (modificado) {
            xmlManager.guardarUsuarios(cacheUsuarios);
            return true;
        }
        for (Funcionario f : cacheFuncionarios) {
            if (f.getId().equalsIgnoreCase(id) && f.getClave().equals(claveActual)) {
                f.setClave(claveNueva);
                modificado = true;
                break;
            }
        }
        if (modificado) {
            xmlManager.guardarFuncionarios(cacheFuncionarios);
            return true;
        }
        return false;
    }

    // Procesa registro asignando el primer recurso disponible por categoria solicitada
    public synchronized Reserva crearReserva(String funcionarioId, String actividad, LocalDate fecha, LocalTime horaIni, LocalTime horaFin, List<String> categoriasIds) throws Exception {
        validarDatosReserva(funcionarioId, actividad, fecha, horaIni, horaFin, categoriasIds);

        List<String> noDisponibles = new ArrayList<>();
        List<String> recursosAsignar = new ArrayList<>();
        Set<String> categoriasUnicas = new LinkedHashSet<>(categoriasIds);

        for (String catId : categoriasUnicas) {
            Categoria cat = buscarCategoriaPorId(catId);
            if (cat == null) {
                throw new IllegalArgumentException("Categoria inexistente: " + catId);
            }

            Recurso disponible = obtenerPrimerRecursoDisponible(catId, fecha, horaIni, horaFin);
            if (disponible == null) {
                noDisponibles.add(cat.getDescripcion());
            } else {
                recursosAsignar.add(disponible.getId());
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new IllegalStateException("Categorias sin disponibilidad horaria: " + String.join(", ", noDisponibles));
        }

        String nuevoId = generarIdReserva();
        Reserva nueva = new Reserva(nuevoId, funcionarioId, actividad, fecha, horaIni, horaFin, recursosAsignar, EstadoReserva.ACTIVA);
        cacheReservas.add(nueva);
        xmlManager.guardarReservas(cacheReservas);
        return nueva;
    }

    private void validarDatosReserva(String funcionarioId, String actividad, LocalDate fecha, LocalTime horaIni, LocalTime horaFin, List<String> categoriasIds) {
        if (!existeFuncionario(funcionarioId)) {
            throw new IllegalArgumentException("Funcionario inexistente: " + funcionarioId);
        }
        if (actividad == null || actividad.isBlank()) {
            throw new IllegalArgumentException("La actividad es obligatoria");
        }
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de reserva no puede estar en el pasado");
        }
        if (horaIni == null || horaFin == null || !horaIni.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
        if (categoriasIds == null || categoriasIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una categoria");
        }
        for (String catId : categoriasIds) {
            if (catId == null || catId.isBlank()) {
                throw new IllegalArgumentException("Categoria invalida en la solicitud");
            }
        }
    }

    public synchronized boolean cancelarReserva(String reservaId, String funcionarioId) throws Exception {
        for (Reserva r : cacheReservas) {
            if (r.getId().equalsIgnoreCase(reservaId) && r.getFuncionarioId().equalsIgnoreCase(funcionarioId)) {
                if (r.getFecha().isBefore(LocalDate.now())) {
                    throw new IllegalStateException("No se puede cancelar una reserva pasada");
                }
                r.setEstado(EstadoReserva.CANCELADA);
                xmlManager.guardarReservas(cacheReservas);
                return true;
            }
        }
        return false;
    }

    public Recurso obtenerPrimerRecursoDisponible(String categoriaId, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Recurso r : cacheRecursos) {
            if (r.getCategoriaId().equalsIgnoreCase(categoriaId)) {
                if (!recursoOcupado(r.getId(), fecha, inicio, fin)) {
                    return r;
                }
            }
        }
        return null;
    }

    public boolean recursoOcupado(String recursoId, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Reserva res : cacheReservas) {
            if (res.getEstado() == EstadoReserva.ACTIVA && res.getFecha().equals(fecha)) {
                if (res.getRecursosIds().contains(recursoId)) {
                    if (horariosSeSolapan(inicio, fin, res.getHoraInicio(), res.getHoraFin())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Determina solapamiento estricto entre dos intervalos de horas
    private boolean horariosSeSolapan(LocalTime iniA, LocalTime finA, LocalTime iniB, LocalTime finB) {
        return iniA.isBefore(finB) && finA.isAfter(iniB);
    }

    private String generarIdReserva() {
        int max = 0;
        for (Reserva r : cacheReservas) {
            try {
                int num = Integer.parseInt(r.getId().replace("RES-", ""));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("RES-%06d", max + 1);
    }

    public List<Funcionario> buscarFuncionarios(String termino) {
        if (termino == null || termino.isBlank()) return new ArrayList<>(cacheFuncionarios);
        String t = termino.toLowerCase();
        List<Funcionario> res = new ArrayList<>();
        for (Funcionario f : cacheFuncionarios) {
            if (f.getId().toLowerCase().contains(t) || f.getNombre().toLowerCase().contains(t)) {
                res.add(f);
            }
        }
        return res;
    }

    public void guardarFuncionario(Funcionario f) throws Exception {
        if (f == null || f.getId() == null || f.getId().isBlank() || f.getNombre() == null || f.getNombre().isBlank()) {
            throw new IllegalArgumentException("ID y nombre del funcionario son obligatorios");
        }
        if (existeUsuarioAdministrador(f.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario administrador con ese ID");
        }

        boolean existe = false;
        for (int i = 0; i < cacheFuncionarios.size(); i++) {
            if (cacheFuncionarios.get(i).getId().equalsIgnoreCase(f.getId())) {
                f.setClave(cacheFuncionarios.get(i).getClave());
                cacheFuncionarios.set(i, f);
                existe = true;
                break;
            }
        }
        if (!existe) {
            f.setClave(f.getId());
            cacheFuncionarios.add(f);
        }
        xmlManager.guardarFuncionarios(cacheFuncionarios);
    }

    public void borrarFuncionario(String id) throws Exception {
        if (tieneReservasFuncionario(id)) {
            throw new IllegalStateException("No se puede borrar un funcionario con reservas registradas");
        }
        boolean eliminado = cacheFuncionarios.removeIf(f -> f.getId().equalsIgnoreCase(id));
        if (!eliminado) {
            throw new IllegalArgumentException("Funcionario no encontrado: " + id);
        }
        xmlManager.guardarFuncionarios(cacheFuncionarios);
    }

    public List<Categoria> buscarCategorias(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) return new ArrayList<>(cacheCategorias);
        String d = descripcion.toLowerCase();
        List<Categoria> res = new ArrayList<>();
        for (Categoria c : cacheCategorias) {
            if (c.getDescripcion().toLowerCase().contains(d)) {
                res.add(c);
            }
        }
        return res;
    }

    public void guardarCategoria(Categoria c) throws Exception {
        if (c == null || c.getDescripcion() == null || c.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripcion de la categoria es obligatoria");
        }
        if (c.getId() == null || c.getId().isBlank()) {
            c.setId(generarIdCategoria());
            cacheCategorias.add(c);
        } else {
            boolean existe = false;
            for (int i = 0; i < cacheCategorias.size(); i++) {
                if (cacheCategorias.get(i).getId().equalsIgnoreCase(c.getId())) {
                    cacheCategorias.set(i, c);
                    existe = true;
                    break;
                }
            }
            if (!existe) cacheCategorias.add(c);
        }
        xmlManager.guardarCategorias(cacheCategorias);
    }

    private String generarIdCategoria() {
        int max = 0;
        for (Categoria c : cacheCategorias) {
            try {
                int num = Integer.parseInt(c.getId().replace("CAT-", ""));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("CAT-%06d", max + 1);
    }

    public void borrarCategoria(String id) throws Exception {
        if (tieneRecursosCategoria(id)) {
            throw new IllegalStateException("No se puede borrar una categoria con recursos asociados");
        }
        boolean eliminado = cacheCategorias.removeIf(c -> c.getId().equalsIgnoreCase(id));
        if (!eliminado) {
            throw new IllegalArgumentException("Categoria no encontrada: " + id);
        }
        xmlManager.guardarCategorias(cacheCategorias);
    }

    public Categoria buscarCategoriaPorId(String id) {
        for (Categoria c : cacheCategorias) {
            if (c.getId().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    public List<Recurso> filtrarRecursos(String categoriaId, String descripcion) {
        List<Recurso> res = new ArrayList<>();
        for (Recurso r : cacheRecursos) {
            boolean coincideCat = (categoriaId == null || categoriaId.isBlank() || categoriaId.equals("TODAS") || r.getCategoriaId().equalsIgnoreCase(categoriaId));
            boolean coincideDesc = (descripcion == null || descripcion.isBlank() || r.getDescripcion().toLowerCase().contains(descripcion.toLowerCase()));
            if (coincideCat && coincideDesc) {
                res.add(r);
            }
        }
        return res;
    }

    public void guardarRecurso(Recurso r) throws Exception {
        if (r == null || r.getId() == null || r.getId().isBlank() || r.getDescripcion() == null || r.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("ID y descripcion del recurso son obligatorios");
        }
        if (buscarCategoriaPorId(r.getCategoriaId()) == null) {
            throw new IllegalArgumentException("Categoria inexistente para el recurso: " + r.getCategoriaId());
        }

        boolean existe = false;
        for (int i = 0; i < cacheRecursos.size(); i++) {
            if (cacheRecursos.get(i).getId().equalsIgnoreCase(r.getId())) {
                cacheRecursos.set(i, r);
                existe = true;
                break;
            }
        }
        if (!existe) cacheRecursos.add(r);
        xmlManager.guardarRecursos(cacheRecursos);
    }

    public void borrarRecurso(String id) throws Exception {
        if (tieneReservasRecurso(id)) {
            throw new IllegalStateException("No se puede borrar un recurso usado en reservas registradas");
        }
        boolean eliminado = cacheRecursos.removeIf(r -> r.getId().equalsIgnoreCase(id));
        if (!eliminado) {
            throw new IllegalArgumentException("Recurso no encontrado: " + id);
        }
        xmlManager.guardarRecursos(cacheRecursos);
    }

    private boolean existeUsuarioAdministrador(String id) {
        for (Usuario u : cacheUsuarios) {
            if (u.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeFuncionario(String id) {
        if (id == null || id.isBlank()) return false;
        for (Funcionario f : cacheFuncionarios) {
            if (f.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneReservasFuncionario(String funcionarioId) {
        for (Reserva r : cacheReservas) {
            if (r.getFuncionarioId().equalsIgnoreCase(funcionarioId)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneRecursosCategoria(String categoriaId) {
        for (Recurso r : cacheRecursos) {
            if (r.getCategoriaId().equalsIgnoreCase(categoriaId)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneReservasRecurso(String recursoId) {
        for (Reserva r : cacheReservas) {
            if (r.getRecursosIds().contains(recursoId)) {
                return true;
            }
        }
        return false;
    }

    public List<Reserva> obtenerReservasFuncionario(String funcionarioId) {
        List<Reserva> res = new ArrayList<>();
        for (Reserva r : cacheReservas) {
            if (r.getFuncionarioId().equalsIgnoreCase(funcionarioId)) {
                res.add(r);
            }
        }
        return res;
    }

    public List<Reserva> getTodasReservas() {
        return new ArrayList<>(cacheReservas);
    }

    public List<Recurso> getTodosRecursos() {
        return new ArrayList<>(cacheRecursos);
    }

    public List<Categoria> getTodasCategorias() {
        return new ArrayList<>(cacheCategorias);
    }

    public List<Funcionario> getTodosFuncionarios() {
        return new ArrayList<>(cacheFuncionarios);
    }

    // Calcula total de recursos reservados por categoria en un periodo
    public Map<String, Integer> estadisticasRecursos(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        for (Categoria cat : cacheCategorias) {
            mapa.put(cat.getDescripcion(), 0);
        }

        for (Reserva res : cacheReservas) {
            if (res.getEstado() == EstadoReserva.ACTIVA && !res.getFecha().isBefore(desde) && !res.getFecha().isAfter(hasta)) {
                for (String recId : res.getRecursosIds()) {
                    for (Recurso r : cacheRecursos) {
                        if (r.getId().equalsIgnoreCase(recId)) {
                            Categoria c = buscarCategoriaPorId(r.getCategoriaId());
                            if (c != null) {
                                mapa.put(c.getDescripcion(), mapa.getOrDefault(c.getDescripcion(), 0) + 1);
                            }
                        }
                    }
                }
            }
        }
        return mapa;
    }

    // Agrupa y contabiliza actividades realizadas por semana calendario en un periodo
    public Map<String, Integer> estadisticasActividades(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> mapa = new TreeMap<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Reserva res : cacheReservas) {
            if (res.getEstado() == EstadoReserva.ACTIVA && !res.getFecha().isBefore(desde) && !res.getFecha().isAfter(hasta)) {
                LocalDate fechaLunes = res.getFecha().with(weekFields.dayOfWeek(), 1);
                String claveSemana = fechaLunes.toString();
                mapa.put(claveSemana, mapa.getOrDefault(claveSemana, 0) + 1);
            }
        }
        return mapa;
    }
}

