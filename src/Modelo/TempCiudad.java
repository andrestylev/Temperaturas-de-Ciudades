package Modelo;

import java.time.LocalDate;

public class TempCiudad {

    private String Ciudad;
    private LocalDate Fecha;
    private double Temperatura;

    public TempCiudad(String ciudad, LocalDate fecha, double temperatura) {
        Ciudad = ciudad;
        Fecha = fecha;
        Temperatura = temperatura;
    }

    public String getCiudad() {
        return Ciudad;
    }

    public void setCiudad(String ciudad) {
        Ciudad = ciudad;
    }

    public LocalDate getFecha() {
        return Fecha;
    }

    public void setFecha(LocalDate fecha) {
        Fecha = fecha;
    }

    public double getTemperatura() {
        return Temperatura;
    }

    public void setTemperatura(double temperatura) {
        Temperatura = temperatura;
    }
}
