package datos;

import modelo.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class XmlManager {
    private final String rutaArchivo;

    public XmlManager(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        inicializarArchivo();
    }

    // Asegura existencia de la estructura base XML si el archivo no existe
    private void inicializarArchivo() {
        try {
            File archivo = new File(rutaArchivo);
            File directorio = archivo.getParentFile();
            if (directorio != null && !directorio.exists()) {
                directorio.mkdirs();
            }
            if (!archivo.exists()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();

                Element root = doc.createElement("sistema");
                doc.appendChild(root);

                root.appendChild(doc.createElement("usuarios"));
                root.appendChild(doc.createElement("funcionarios"));
                root.appendChild(doc.createElement("categorias"));
                root.appendChild(doc.createElement("recursos"));
                root.appendChild(doc.createElement("reservas"));

                guardarDocumento(doc);
                sembrarDatosPredeterminados();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document cargarDocumento() throws Exception {
        File archivo = new File(rutaArchivo);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(archivo);
    }

    private void guardarDocumento(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(rutaArchivo));
        transformer.transform(source, result);
    }

    // Inicializa registros requeridos segun especificaciones del sistema
    private void sembrarDatosPredeterminados() {
        try {
            List<Usuario> usuarios = new ArrayList<>();
            usuarios.add(new Usuario("admin", "admin", Rol.ADMINISTRADOR));
            guardarUsuarios(usuarios);

            List<Funcionario> funcionarios = new ArrayList<>();
            funcionarios.add(new Funcionario("111", "111", "Juan Perez", "3323-8899"));
            funcionarios.add(new Funcionario("222", "222", "Maria Perez", "2222-3344"));
            funcionarios.add(new Funcionario("333", "333", "Carlos Rodriguez", "8888-1122"));
            guardarFuncionarios(funcionarios);

            List<Categoria> categorias = new ArrayList<>();
            categorias.add(new Categoria("CAT-000001", "Sala para 10 personas"));
            categorias.add(new Categoria("CAT-000002", "Laptop windows"));
            categorias.add(new Categoria("CAT-000003", "Sala de Juntas"));
            categorias.add(new Categoria("CAT-000004", "Proyector Multimedia"));
            guardarCategorias(categorias);

            List<Recurso> recursos = new ArrayList<>();
            recursos.add(new Recurso("238715", "CAT-000002", "Laptop #238715"));
            recursos.add(new Recurso("34343", "CAT-000001", "Sala 1 primer piso"));
            recursos.add(new Recurso("45238", "CAT-000002", "Laptop #45238"));
            recursos.add(new Recurso("452784", "CAT-000003", "Sala de Juntas 1"));
            recursos.add(new Recurso("562100", "CAT-000004", "Proyector Epson HDMI"));
            guardarRecursos(recursos);

            List<String> recs1 = new ArrayList<>();
            recs1.add("238715");
            recs1.add("34343");
            Reserva res1 = new Reserva("RES-000001", "111", "Reunion clientes", LocalDate.of(2026, 7, 31), LocalTime.of(8, 0), LocalTime.of(10, 0), recs1, EstadoReserva.ACTIVA);

            List<String> recs2 = new ArrayList<>();
            recs2.add("238715");
            recs2.add("452784");
            Reserva res2 = new Reserva("RES-000003", "111", "Sesion de Junta", LocalDate.of(2026, 8, 5), LocalTime.of(9, 0), LocalTime.of(11, 0), recs2, EstadoReserva.ACTIVA);

            List<String> recs3 = new ArrayList<>();
            recs3.add("452784");
            Reserva res3 = new Reserva("RES-000005", "111", "Reunion de Control", LocalDate.of(2026, 8, 12), LocalTime.of(8, 0), LocalTime.of(9, 0), recs3, EstadoReserva.ACTIVA);

            List<Reserva> reservas = new ArrayList<>();
            reservas.add(res1);
            reservas.add(res2);
            reservas.add(res3);
            guardarReservas(reservas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Usuario> leerUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        try {
            Document doc = cargarDocumento();
            NodeList nodos = doc.getElementsByTagName("usuario");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elem = (Element) nodos.item(i);
                String id = elem.getElementsByTagName("id").item(0).getTextContent();
                String clave = elem.getElementsByTagName("clave").item(0).getTextContent();
                String rol = elem.getElementsByTagName("rol").item(0).getTextContent();
                lista.add(new Usuario(id, clave, Rol.valueOf(rol)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardarUsuarios(List<Usuario> lista) throws Exception {
        Document doc = cargarDocumento();
        Element root = doc.getDocumentElement();
        Node nodoContenedor = root.getElementsByTagName("usuarios").item(0);

        while (nodoContenedor.hasChildNodes()) {
            nodoContenedor.removeChild(nodoContenedor.getFirstChild());
        }

        for (Usuario u : lista) {
            Element el = doc.createElement("usuario");
            Element id = doc.createElement("id");
            id.setTextContent(u.getId());
            Element clave = doc.createElement("clave");
            clave.setTextContent(u.getClave());
            Element rol = doc.createElement("rol");
            rol.setTextContent(u.getRol().name());

            el.appendChild(id);
            el.appendChild(clave);
            el.appendChild(rol);
            nodoContenedor.appendChild(el);
        }
        guardarDocumento(doc);
    }

    public List<Funcionario> leerFuncionarios() {
        List<Funcionario> lista = new ArrayList<>();
        try {
            Document doc = cargarDocumento();
            NodeList nodos = doc.getElementsByTagName("funcionario");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elem = (Element) nodos.item(i);
                String id = elem.getElementsByTagName("id").item(0).getTextContent();
                String clave = elem.getElementsByTagName("clave").item(0).getTextContent();
                String nombre = elem.getElementsByTagName("nombre").item(0).getTextContent();
                String telefono = elem.getElementsByTagName("telefono").item(0).getTextContent();
                lista.add(new Funcionario(id, clave, nombre, telefono));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardarFuncionarios(List<Funcionario> lista) throws Exception {
        Document doc = cargarDocumento();
        Element root = doc.getDocumentElement();
        NodeList nodoContenedor = root.getElementsByTagName("funcionarios");
        Element funcionariosElem;
        if (nodoContenedor.getLength() == 0) {
            funcionariosElem = doc.createElement("funcionarios");
            root.appendChild(funcionariosElem);
        } else {
            funcionariosElem = (Element) nodoContenedor.item(0);
        }

        while (funcionariosElem.hasChildNodes()) {
            funcionariosElem.removeChild(funcionariosElem.getFirstChild());
        }

        for (Funcionario f : lista) {
            Element el = doc.createElement("funcionario");
            Element id = doc.createElement("id");
            id.setTextContent(f.getId());
            Element clave = doc.createElement("clave");
            clave.setTextContent(f.getClave());
            Element nombre = doc.createElement("nombre");
            nombre.setTextContent(f.getNombre());
            Element tel = doc.createElement("telefono");
            tel.setTextContent(f.getTelefono());

            el.appendChild(id);
            el.appendChild(clave);
            el.appendChild(nombre);
            el.appendChild(tel);
            funcionariosElem.appendChild(el);
        }
        guardarDocumento(doc);
    }

    public List<Categoria> leerCategorias() {
        List<Categoria> lista = new ArrayList<>();
        try {
            Document doc = cargarDocumento();
            NodeList nodos = doc.getElementsByTagName("categoria");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elem = (Element) nodos.item(i);
                String id = elem.getElementsByTagName("id").item(0).getTextContent();
                String descripcion = elem.getElementsByTagName("descripcion").item(0).getTextContent();
                lista.add(new Categoria(id, descripcion));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardarCategorias(List<Categoria> lista) throws Exception {
        Document doc = cargarDocumento();
        Element root = doc.getDocumentElement();
        Node nodoContenedor = root.getElementsByTagName("categorias").item(0);

        while (nodoContenedor.hasChildNodes()) {
            nodoContenedor.removeChild(nodoContenedor.getFirstChild());
        }

        for (Categoria c : lista) {
            Element el = doc.createElement("categoria");
            Element id = doc.createElement("id");
            id.setTextContent(c.getId());
            Element desc = doc.createElement("descripcion");
            desc.setTextContent(c.getDescripcion());

            el.appendChild(id);
            el.appendChild(desc);
            nodoContenedor.appendChild(el);
        }
        guardarDocumento(doc);
    }

    public List<Recurso> leerRecursos() {
        List<Recurso> lista = new ArrayList<>();
        try {
            Document doc = cargarDocumento();
            NodeList nodos = doc.getElementsByTagName("recurso");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elem = (Element) nodos.item(i);
                String id = elem.getElementsByTagName("id").item(0).getTextContent();
                String catId = elem.getElementsByTagName("categoriaId").item(0).getTextContent();
                String descripcion = elem.getElementsByTagName("descripcion").item(0).getTextContent();
                lista.add(new Recurso(id, catId, descripcion));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardarRecursos(List<Recurso> lista) throws Exception {
        Document doc = cargarDocumento();
        Element root = doc.getDocumentElement();
        Node nodoContenedor = root.getElementsByTagName("recursos").item(0);

        while (nodoContenedor.hasChildNodes()) {
            nodoContenedor.removeChild(nodoContenedor.getFirstChild());
        }

        for (Recurso r : lista) {
            Element el = doc.createElement("recurso");
            Element id = doc.createElement("id");
            id.setTextContent(r.getId());
            Element catId = doc.createElement("categoriaId");
            catId.setTextContent(r.getCategoriaId());
            Element desc = doc.createElement("descripcion");
            desc.setTextContent(r.getDescripcion());

            el.appendChild(id);
            el.appendChild(catId);
            el.appendChild(desc);
            nodoContenedor.appendChild(el);
        }
        guardarDocumento(doc);
    }

    public List<Reserva> leerReservas() {
        List<Reserva> lista = new ArrayList<>();
        try {
            Document doc = cargarDocumento();
            NodeList nodos = doc.getElementsByTagName("reserva");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elem = (Element) nodos.item(i);
                String id = elem.getElementsByTagName("id").item(0).getTextContent();
                String funcId = elem.getElementsByTagName("funcionarioId").item(0).getTextContent();
                String act = elem.getElementsByTagName("actividad").item(0).getTextContent();
                String fechaStr = elem.getElementsByTagName("fecha").item(0).getTextContent();
                String horaIniStr = elem.getElementsByTagName("horaInicio").item(0).getTextContent();
                String horaFinStr = elem.getElementsByTagName("horaFin").item(0).getTextContent();
                String estStr = elem.getElementsByTagName("estado").item(0).getTextContent();

                List<String> recs = new ArrayList<>();
                NodeList nodosRecs = elem.getElementsByTagName("recursoId");
                for (int j = 0; j < nodosRecs.getLength(); j++) {
                    recs.add(nodosRecs.item(j).getTextContent());
                }

                Reserva r = new Reserva(
                        id,
                        funcId,
                        act,
                        LocalDate.parse(fechaStr),
                        LocalTime.parse(horaIniStr),
                        LocalTime.parse(horaFinStr),
                        recs,
                        EstadoReserva.valueOf(estStr)
                );
                lista.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardarReservas(List<Reserva> lista) throws Exception {
        Document doc = cargarDocumento();
        Element root = doc.getDocumentElement();
        Node nodoContenedor = root.getElementsByTagName("reservas").item(0);

        while (nodoContenedor.hasChildNodes()) {
            nodoContenedor.removeChild(nodoContenedor.getFirstChild());
        }

        for (Reserva r : lista) {
            Element el = doc.createElement("reserva");
            Element id = doc.createElement("id");
            id.setTextContent(r.getId());
            Element funcId = doc.createElement("funcionarioId");
            funcId.setTextContent(r.getFuncionarioId());
            Element act = doc.createElement("actividad");
            act.setTextContent(r.getActividad());
            Element fecha = doc.createElement("fecha");
            fecha.setTextContent(r.getFecha().toString());
            Element horaIni = doc.createElement("horaInicio");
            horaIni.setTextContent(r.getHoraInicio().toString());
            Element horaFin = doc.createElement("horaFin");
            horaFin.setTextContent(r.getHoraFin().toString());
            Element est = doc.createElement("estado");
            est.setTextContent(r.getEstado().name());

            Element recsNode = doc.createElement("recursosAsignados");
            for (String rid : r.getRecursosIds()) {
                Element re = doc.createElement("recursoId");
                re.setTextContent(rid);
                recsNode.appendChild(re);
            }

            el.appendChild(id);
            el.appendChild(funcId);
            el.appendChild(act);
            el.appendChild(fecha);
            el.appendChild(horaIni);
            el.appendChild(horaFin);
            el.appendChild(est);
            el.appendChild(recsNode);
            nodoContenedor.appendChild(el);
        }
        guardarDocumento(doc);
    }
}
