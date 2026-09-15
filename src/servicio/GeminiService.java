package servicio;

import utilitarios.GeminiReservaDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiService {
    private static final Pattern TEXTO_GEMINI_PATTERN = Pattern.compile("\"text\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"", Pattern.DOTALL);
    private static final Pattern FECHA_ISO_PATTERN = Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b");
    private static final Pattern RANGO_HORAS_PATTERN = Pattern.compile("(?:\\bde\\b|\\bdesde\\b)\\s+(\\d{1,2})(?::(\\d{2}))?\\s*(?:\\ba\\b|\\bhasta\\b|-)\\s*(\\d{1,2})(?::(\\d{2}))?");
    private static final Pattern HORA_SIMPLE_PATTERN = Pattern.compile("(?:\\ba\\s+las\\b|\\bdesde\\s+las\\b|\\bdesde\\b|\\bhora\\b)\\s+(\\d{1,2})(?::(\\d{2}))?");

    private final String apiKey;
    private final HttpClient client;

    public GeminiService() {
        String envKey = System.getenv("GEMINI_API_KEY");
        this.apiKey = (envKey != null && !envKey.isBlank()) ? envKey : System.getProperty("GEMINI_API_KEY", "");
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // Envia solicitud HTTP POST a Gemini API solicitando estructura JSON estricta
    public GeminiReservaDTO extraerDatosReserva(String textoUsuario, List<String> categoriasDisponibles) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            return extraerFallbackLocal(textoUsuario, categoriasDisponibles);
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String prompt = "Extrae los datos de reserva a partir del texto: \"" + textoUsuario.replace("\"", "\\\"").replace("\n", " ") + "\". "
                + "Categorias disponibles: [" + String.join(", ", categoriasDisponibles) + "]. "
                + "Fecha de referencia actual: " + LocalDate.now() + ". "
                + "Responde UNICAMENTE en formato JSON plano sin bloques markdown con estos campos exactos: "
                + "{\"actividad\": \"nombre de la actividad\", \"fecha\": \"YYYY-MM-DD\", \"horaInicio\": \"HH:00\", \"horaFin\": \"HH:00\", \"categorias\": [\"nombre1\", \"nombre2\"]}";

        String jsonBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + escaparJson(prompt) + "\"}]}]}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parsearRespuestaGemini(response.body(), categoriasDisponibles);
            }
        } catch (Exception ex) {
            // Falla de red o timeout, pasa a fallback local
        }
        return extraerFallbackLocal(textoUsuario, categoriasDisponibles);
    }

    // Parsea los campos del JSON estricto devuelto por el LLM
    private GeminiReservaDTO parsearRespuestaGemini(String rawJson, List<String> categoriasDisponibles) {
        GeminiReservaDTO dto = new GeminiReservaDTO();
        try {
            String textoPlano = extraerTextoGemini(rawJson);
            String jsonReserva = extraerObjetoJson(textoPlano);

            dto.setActividad(extraerValor(jsonReserva, "actividad"));
            dto.setFecha(normalizarFecha(extraerValor(jsonReserva, "fecha")));
            dto.setHoraInicio(normalizarHora(extraerValor(jsonReserva, "horaInicio"), "08:00"));
            dto.setHoraFin(normalizarHora(extraerValor(jsonReserva, "horaFin"), "10:00"));

            List<String> categorias = normalizarCategorias(extraerLista(jsonReserva, "categorias"), categoriasDisponibles);
            if (categorias.isEmpty()) {
                categorias = detectarCategorias(textoPlano, categoriasDisponibles);
            }
            dto.setCategorias(categorias);
            return dto;
        } catch (Exception ex) {
            return extraerFallbackLocal(rawJson, categoriasDisponibles);
        }
    }

    private String extraerTextoGemini(String rawJson) {
        Matcher matcher = TEXTO_GEMINI_PATTERN.matcher(rawJson);
        if (matcher.find()) {
            return desescaparJson(matcher.group(1));
        }
        return rawJson;
    }

    private String extraerObjetoJson(String texto) {
        String limpio = texto.trim();
        if (limpio.startsWith("```")) {
            limpio = limpio.replaceFirst("^```[a-zA-Z]*\\s*", "").replaceFirst("\\s*```$", "");
        }

        int inicio = limpio.indexOf('{');
        int fin = limpio.lastIndexOf('}');
        if (inicio >= 0 && fin > inicio) {
            return limpio.substring(inicio, fin + 1);
        }
        return limpio;
    }

    private String extraerValor(String json, String clave) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(clave) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) return "";
        return desescaparJson(matcher.group(1)).trim();
    }

    private List<String> extraerLista(String json, String clave) {
        List<String> valores = new ArrayList<>();
        Pattern listaPattern = Pattern.compile("\"" + Pattern.quote(clave) + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher listaMatcher = listaPattern.matcher(json);
        if (!listaMatcher.find()) return valores;

        Matcher valorMatcher = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"").matcher(listaMatcher.group(1));
        while (valorMatcher.find()) {
            valores.add(desescaparJson(valorMatcher.group(1)).trim());
        }
        return valores;
    }

    private String normalizarHora(String hora, String porDefecto) {
        if (hora == null || hora.isBlank()) return porDefecto;
        hora = hora.trim();
        if (hora.length() == 4 && hora.charAt(1) == ':') {
            hora = "0" + hora;
        }
        try {
            LocalTime parsed = LocalTime.parse(hora);
            return String.format("%02d:%02d", parsed.getHour(), parsed.getMinute());
        } catch (Exception ignored) {
            return porDefecto;
        }
    }

    private String normalizarFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return LocalDate.now().plusDays(1).toString();
        fecha = fecha.trim();
        try {
            return LocalDate.parse(fecha).toString();
        } catch (Exception ignored) {
            return LocalDate.now().plusDays(1).toString();
        }
    }

    // Extractor heuristico si no hay clave de red o si ocurre error de conexion
    private GeminiReservaDTO extraerFallbackLocal(String texto, List<String> categoriasDisponibles) {
        GeminiReservaDTO dto = new GeminiReservaDTO();
        String t = texto.toLowerCase(Locale.ROOT);

        // Extraccion de actividad
        if (t.contains("reuni") || t.contains("junta")) {
            dto.setActividad("Reunion de coordinacion");
        } else if (t.contains("taller") || t.contains("capacit")) {
            dto.setActividad("Taller de capacitacion");
        } else if (t.contains("conferencia") || t.contains("charla")) {
            dto.setActividad("Conferencia institucional");
        } else if (t.contains("defensa") || t.contains("examen")) {
            dto.setActividad("Defensa de proyecto de graduacion");
        } else {
            dto.setActividad("Actividad academica institucional");
        }

        // Extraccion de fecha
        LocalDate hoy = LocalDate.now();
        Matcher fechaMatcher = FECHA_ISO_PATTERN.matcher(t);
        if (fechaMatcher.find()) {
            dto.setFecha(normalizarFecha(fechaMatcher.group()));
        } else if (t.contains("pasado manana") || t.contains("pasado ma\u00f1ana")) {
            dto.setFecha(hoy.plusDays(2).toString());
        } else if (t.contains("hoy")) {
            dto.setFecha(hoy.toString());
        } else if (t.contains("manana") || t.contains("ma\u00f1ana")) {
            dto.setFecha(hoy.plusDays(1).toString());
        } else {
            dto.setFecha(hoy.plusDays(2).toString());
        }

        // Extraccion de horas
        String[] horas = extraerHorasFallback(t);
        dto.setHoraInicio(horas[0]);
        dto.setHoraFin(horas[1]);

        // Extraccion de categorias
        dto.setCategorias(detectarCategorias(t, categoriasDisponibles));
        return dto;
    }

    private String[] extraerHorasFallback(String texto) {
        Matcher rango = RANGO_HORAS_PATTERN.matcher(texto);
        if (rango.find()) {
            String inicio = formatearHora(rango.group(1), rango.group(2));
            String fin = formatearHora(rango.group(3), rango.group(4));
            if (horaValida(inicio, fin)) {
                return new String[]{inicio, fin};
            }
        }

        Matcher simple = HORA_SIMPLE_PATTERN.matcher(texto);
        if (simple.find()) {
            String inicio = formatearHora(simple.group(1), simple.group(2));
            String fin = sumarHoras(inicio, 2);
            if (horaValida(inicio, fin)) {
                return new String[]{inicio, fin};
            }
        }

        Integer horaPorPalabra = detectarHoraPorPalabra(texto);
        if (horaPorPalabra != null) {
            String inicio = String.format("%02d:00", horaPorPalabra);
            String fin = sumarHoras(inicio, 2);
            if (horaValida(inicio, fin)) {
                return new String[]{inicio, fin};
            }
        }

        return new String[]{"08:00", "10:00"};
    }

    private Integer detectarHoraPorPalabra(String texto) {
        if (texto.contains("siete")) return 7;
        if (texto.contains("ocho")) return 8;
        if (texto.contains("nueve")) return 9;
        if (texto.contains("diez")) return 10;
        if (texto.contains("once")) return 11;
        if (texto.contains("una")) return 13;
        if (texto.contains("dos")) return 14;
        if (texto.contains("tres")) return 15;
        if (texto.contains("cuatro")) return 16;
        return null;
    }

    private String formatearHora(String hora, String minuto) {
        try {
            int h = normalizarHoraNumerica(Integer.parseInt(hora));
            int m = (minuto == null || minuto.isBlank()) ? 0 : Integer.parseInt(minuto);
            if (h < 0 || h > 23 || m < 0 || m > 59) return "08:00";
            return String.format("%02d:%02d", h, m);
        } catch (Exception ignored) {
            return "08:00";
        }
    }

    private int normalizarHoraNumerica(int hora) {
        if (hora >= 1 && hora <= 6) {
            return hora + 12;
        }
        return hora;
    }

    private String sumarHoras(String hora, int cantidad) {
        try {
            LocalTime resultado = LocalTime.parse(hora).plusHours(cantidad);
            if (resultado.isBefore(LocalTime.parse(hora))) {
                return "22:00";
            }
            return String.format("%02d:%02d", resultado.getHour(), resultado.getMinute());
        } catch (Exception ignored) {
            return "10:00";
        }
    }

    private boolean horaValida(String inicio, String fin) {
        try {
            return LocalTime.parse(inicio).isBefore(LocalTime.parse(fin));
        } catch (Exception ignored) {
            return false;
        }
    }

    private List<String> normalizarCategorias(List<String> solicitadas, List<String> disponibles) {
        List<String> seleccionadas = new ArrayList<>();
        for (String disponible : disponibles) {
            for (String solicitada : solicitadas) {
                if (coincideCategoria(solicitada, disponible) && !seleccionadas.contains(disponible)) {
                    seleccionadas.add(disponible);
                    break;
                }
            }
        }
        return seleccionadas;
    }

    private List<String> detectarCategorias(String texto, List<String> categoriasDisponibles) {
        List<String> seleccionadas = new ArrayList<>();
        String t = texto.toLowerCase(Locale.ROOT);
        for (String cat : categoriasDisponibles) {
            String cLower = cat.toLowerCase(Locale.ROOT);
            if (t.contains(cLower)) {
                seleccionadas.add(cat);
            } else if (cLower.contains("sala") && (t.contains("sala") || t.contains("reuni") || t.contains("junta"))) {
                seleccionadas.add(cat);
            } else if ((cLower.contains("laptop") || cLower.contains("portatil")) && (t.contains("laptop") || t.contains("computadora") || t.contains("portatil"))) {
                seleccionadas.add(cat);
            } else if (cLower.contains("proyector") && (t.contains("proyector") || t.contains("pantalla") || t.contains("video"))) {
                seleccionadas.add(cat);
            } else if (cLower.contains("audio") && (t.contains("microf") || t.contains("sonido") || t.contains("audio"))) {
                seleccionadas.add(cat);
            }
        }
        return seleccionadas;
    }

    private boolean coincideCategoria(String solicitada, String disponible) {
        String s = solicitada.toLowerCase(Locale.ROOT);
        String d = disponible.toLowerCase(Locale.ROOT);
        return s.equals(d) || s.contains(d) || d.contains(s);
    }

    private String escaparJson(String valor) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 32) {
                        sb.append(' ');
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    private String desescaparJson(String valor) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (c != '\\' || i + 1 >= valor.length()) {
                sb.append(c);
                continue;
            }

            char next = valor.charAt(++i);
            switch (next) {
                case '"':
                    sb.append('"');
                    break;
                case '\\':
                    sb.append('\\');
                    break;
                case '/':
                    sb.append('/');
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'u':
                    if (i + 4 < valor.length()) {
                        try {
                            String hex = valor.substring(i + 1, i + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                            break;
                        } catch (NumberFormatException ignored) {
                            sb.append("\\u");
                            break;
                        }
                    }
                    sb.append("\\u");
                    break;
                default:
                    sb.append(next);
                    break;
            }
        }
        return sb.toString();
    }
}

