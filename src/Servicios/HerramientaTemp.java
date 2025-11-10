package Servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Modelo.TempCiudad;

public class HerramientaTemp {

    // metodo para obtener datos desde un archivo CSV
    public static List<TempCiudad> getDatos(String nombreArchivo) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try (Stream<String> lineas = Files.lines(Paths.get(nombreArchivo))) {

            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(texto -> new TempCiudad(texto[0], LocalDate.parse(texto[1], formatoFecha),
                            Double.parseDouble(texto[2])))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }

    }

    // metodo para obtener lista de ciudades sin repeticiones
    public static List<String> getClimas(List<TempCiudad> datosTemperatura) {
        return datosTemperatura.stream()
                .map(TempCiudad::getCiudad)
                .distinct()
                .collect(Collectors.toList());
    }

    // metodo filtro por ciudad y rango de fechas
    public static List<TempCiudad> filtro(List<TempCiudad> rangos, String ciudad, LocalDate inicio, LocalDate fin) {
        return rangos.stream()
                .filter(item -> item.getCiudad().equals(ciudad) &&
                        !(item.getFecha().isBefore(inicio) ||
                                item.getFecha().isAfter(fin)))
                .collect(Collectors.toList());
    }

    // calculador de promedios por ciudad en un rango de fechas
    public static Map<String, Double> calcularPromediosPorCiudad(List<TempCiudad> datos,
            LocalDate inicio,
            LocalDate fin) {
        // Filtrar datos para el rango de fechas
        List<TempCiudad> datosFiltrados = datos.stream()
                .filter(temp -> !temp.getFecha().isBefore(inicio) &&
                        !temp.getFecha().isAfter(fin))
                .collect(Collectors.toList());

        // Verificar si hay datos para ese rango de fechas
        if (datosFiltrados.isEmpty()) {
            return Collections.emptyMap(); // IUclima ya maneja el caso de mapa vacio
        }

        // Calcular promedios si hay datos
        return datosFiltrados.stream()
                .collect(Collectors.groupingBy(
                        TempCiudad::getCiudad,
                        Collectors.averagingDouble(TempCiudad::getTemperatura)));
    }

    // metodo para obtener ciudades con maxima y minima temperatura en una fecha
    // dada
    public static Map<String, String> obtenerCiudadesExtremas(List<TempCiudad> datos,
            LocalDate fecha) {
        // filtrar solo los registros de esa fecha
        List<TempCiudad> datosFecha = datos.stream()
                .filter(temp -> temp.getFecha().isEqual(fecha))
                .collect(Collectors.toList());

        // verificar si hay datos para esa fecha
        if (datosFecha.isEmpty()) {
            return Map.of("error", "No hay datos disponibles para esa fecha");
        }

        // encontrar la temperatura máxima y ciudad correspondiente
        TempCiudad masCalurosa = datosFecha.stream()
                .max(Comparator.comparingDouble(TempCiudad::getTemperatura))
                .orElse(null);

        // encontrar la temperatura mínima y ciudad correspondiente
        TempCiudad menosCalurosa = datosFecha.stream()
                .min(Comparator.comparingDouble(TempCiudad::getTemperatura))
                .orElse(null);

        // retornar resultados
        return Map.of(
                "masCalurosa", masCalurosa.getCiudad() + " (" + masCalurosa.getTemperatura() + "°C)",
                "menosCalurosa", menosCalurosa.getCiudad() + " (" + menosCalurosa.getTemperatura() + "°C)");
    }
}
