package utilitarios;

import java.util.ArrayList;
import java.util.List;

public class GeminiReservaDTO {
    private String actividad;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private List<String> categorias = new ArrayList<>();

    public GeminiReservaDTO() {
    }

    public GeminiReservaDTO(String actividad, String fecha, String horaInicio, String horaFin, List<String> categorias) {
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        if (categorias != null) {
            this.categorias = categorias;
        }
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    // Alias para compatibilidad interna
    public List<String> getCategoriasSugeridas() {
        return categorias;
    }

    public void setCategoriasSugeridas(List<String> categoriasSugeridas) {
        this.categorias = categoriasSugeridas;
    }
}
