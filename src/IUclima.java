import java.util.List;
import java.util.Map;

import javax.swing.border.Border;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Calendar;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

import Modelo.TempCiudad;
import Servicios.HerramientaTemp;
import datechooser.beans.DateChooserCombo;

public class IUclima extends JFrame {

    private JTabbedPane panelGrafica;
    private DateChooserCombo dccDesde;
    private DateChooserCombo dccHasta;
    private Calendar calDesde;
    private Calendar calHasta; 

    private List<TempCiudad> datosTemperatura;
    private List<String> Climas;

    public IUclima() {
        setTitle("Temperatura En Ciudades");
        setSize(990, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JToolBar barraHerramientas = new JToolBar();
        IUclima.this.add(barraHerramientas);
        barraHerramientas.setBackground(Color.LIGHT_GRAY);
        // Configurar layout con espaciado horizontal (15) y vertical (10)
        barraHerramientas.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 27));
        barraHerramientas.setPreferredSize(new Dimension(850, 110));
        barraHerramientas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        barraHerramientas.setFloatable(false);
        
        // Panel para agrupar botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 5));
        panelBotones.setOpaque(false ); // Hacer transparente para que se vea el fondo del toolbar
        
        // Configurar botón de promedios con texto centrado
        JButton botonPromedio = new JButton("<html><center>Graficar Barras<br>De<br>Promedios</center></html>");
        botonPromedio.setToolTipText("Muestra la grafica de promedios");
        botonPromedio.setPreferredSize(new Dimension(150, 60));
        botonPromedio.setFont(new Font("Arial", Font.BOLD, 12));
        botonPromedio.setHorizontalTextPosition(SwingConstants.CENTER);
        botonPromedio.setVerticalTextPosition(SwingConstants.CENTER);
        botonPromedio.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonPromedio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        botonPromedio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Limpiar pestañas anteriores
                panelGrafica.removeAll();

                // Validar fechas
                if (dccDesde.getSelectedDate() == null || dccHasta.getSelectedDate() == null) {
                    JOptionPane.showMessageDialog(IUclima.this,
                            "Por favor seleccione ambas fechas",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convertir fechas
                calDesde = dccDesde.getSelectedDate();
                calHasta = dccHasta.getSelectedDate();

                LocalDate fechaInicio = LocalDate.of(
                        calDesde.get(Calendar.YEAR),
                        calDesde.get(Calendar.MONTH) + 1,
                        calDesde.get(Calendar.DAY_OF_MONTH));

                LocalDate fechaFin = LocalDate.of(
                        calHasta.get(Calendar.YEAR),
                        calHasta.get(Calendar.MONTH) + 1,
                        calHasta.get(Calendar.DAY_OF_MONTH));

                // Calcular promedios
                Map<String, Double> promedios = HerramientaTemp.calcularPromediosPorCiudad(
                        datosTemperatura, fechaInicio, fechaFin);
         
                // Crear panel personalizado con paintComponent
                JPanel panelBarras = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        dibujarGraficaBarras(g, promedios, fechaInicio, fechaFin);
                    }
                };
                panelBarras.setPreferredSize(new Dimension(800, 500));
                panelBarras.setBackground(Color.WHITE);

                // Agregar a pestaña
                panelGrafica.addTab("Gráfica de Promedios", panelBarras);
                panelGrafica.setSelectedIndex(0);

                panelGrafica.revalidate();
                panelGrafica.repaint();
            }
        });

        barraHerramientas.add(botonPromedio);

        // Configurar botón máximos y mínimos
        // Añadir botones al panel de botones
        panelBotones.add(botonPromedio);

        JButton botonmaxmin = new JButton("<html><center>Máximos y Mínimos<br>de<br> Temperaturas</center></html>");
        botonmaxmin.setToolTipText("Ciudades con máximos y mínimos");
        botonmaxmin.setPreferredSize(new Dimension(150, 60));
        botonmaxmin.setFont(new Font("Arial", Font.BOLD, 12));
        botonmaxmin.setHorizontalTextPosition(SwingConstants.CENTER);
        botonmaxmin.setVerticalTextPosition(SwingConstants.CENTER);
        botonmaxmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonmaxmin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        botonmaxmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Limpiar pestañas
                panelGrafica.removeAll();

                // Validar fecha
                if (dccDesde.getSelectedDate() == null) {
                    JOptionPane.showMessageDialog(IUclima.this,
                            "Por favor seleccione una fecha",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convertir fecha
                Calendar calFecha = dccDesde.getSelectedDate();
                LocalDate fecha = LocalDate.of(
                        calFecha.get(Calendar.YEAR),
                        calFecha.get(Calendar.MONTH) + 1,
                        calFecha.get(Calendar.DAY_OF_MONTH));

                // Obtener extremas
                Map<String, String> extremas = HerramientaTemp.obtenerCiudadesExtremas(
                        datosTemperatura, fecha);

                // Crear panel
                JPanel panelMaxMin = crearPanelMaxMin(extremas, fecha);

                // Agregar a pestaña
                panelGrafica.addTab("Máximos y Mínimos - " + fecha, panelMaxMin);
                panelGrafica.setSelectedIndex(0);

                panelGrafica.revalidate();
                panelGrafica.repaint();
            }
        });

        panelBotones.add(botonmaxmin);
        barraHerramientas.add(panelBotones);

        // Panel para agrupar fechas
        JPanel panelFechas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelFechas.setOpaque(true);

        // Configurar etiquetas y selectores de fecha con alineación
        JLabel etiquetaFechaDesde = new JLabel("Fecha Desde:", SwingConstants.CENTER);
        etiquetaFechaDesde.setFont(new Font("Arial", Font.BOLD, 14));
        etiquetaFechaDesde.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaFechaDesde.setVerticalAlignment(SwingConstants.CENTER);
        panelFechas.add(etiquetaFechaDesde);

        // Configurar DateChooserCombo Desde
        dccDesde = new DateChooserCombo();
        dccDesde.setPreferredSize(new Dimension(120, 30));
        panelFechas.add(dccDesde);

        // Espacio entre fechas
        panelFechas.add(Box.createHorizontalStrut(20));

        JLabel etiquetaFechaHasta = new JLabel("Fecha Hasta:", SwingConstants.CENTER);
        etiquetaFechaHasta.setFont(new Font("Arial", Font.BOLD, 14));
        etiquetaFechaHasta.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaFechaHasta.setVerticalAlignment(SwingConstants.CENTER);
        panelFechas.add(etiquetaFechaHasta);

        // Configurar DateChooserCombo Hasta
        dccHasta = new DateChooserCombo();
        dccHasta.setPreferredSize(new Dimension(120, 30));
        panelFechas.add(dccHasta);

        barraHerramientas.add(panelFechas);
        

        JPanel contenedorGrafica = new JPanel();
        contenedorGrafica.setLayout(new BoxLayout(contenedorGrafica, BoxLayout.Y_AXIS));
        contenedorGrafica.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelGrafica = new JTabbedPane();
        panelGrafica.setBorder(BorderFactory.createTitledBorder("Graficas de temperatura"));

        contenedorGrafica.add(panelGrafica);

        getContentPane().add(barraHerramientas, BorderLayout.NORTH);
        getContentPane().add(contenedorGrafica, BorderLayout.CENTER);

        cargarDatos();
    }

    public void cargarDatos() {
        String nombreArchivo = System.getProperty("user.dir") + "/src/Datos/temperaturas_ciudades.csv";

        java.io.File file = new java.io.File(nombreArchivo);
        System.out.println(" Existe el archivo? " + file.exists());

        datosTemperatura = HerramientaTemp.getDatos(nombreArchivo);
        Climas = HerramientaTemp.getClimas(datosTemperatura);
    }

    private void dibujarGraficaBarras(Graphics g, Map<String, Double> datos,
            LocalDate inicio, LocalDate fin) {
        if (datos == null || datos.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 16));         
            g.setColor(Color.RED);
            g.drawString("No hay datos para mostrar", 330, 35);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Título
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.BLACK);
        String titulo = String.format("Promedios de Temperatura (%s a %s)", inicio, fin);
        g2d.drawString(titulo, 20, 30);

        // Encontrar temperatura máxima
        double maxTemp = datos.values().stream()
                .max(Double::compare)
                .orElse(1.0);

        // Configuración
        int margenIzq = 100;
        int margenTop = 80;
        int anchoBarra = 80;
        int espacioEntre = 50;
        int alturaMaxima = 400; // Altura fija para las barras

        int x = margenIzq;
        int index = 0;

        // Colores
        Color[] colores = {
                new Color(52, 152, 219), // Azul
                new Color(231, 76, 60), // Rojo
                new Color(46, 204, 113) // Verde
        };

        // Dibujar cada barra
        for (Map.Entry<String, Double> entrada : datos.entrySet()) {
            String ciudad = entrada.getKey();
            double temperatura = entrada.getValue();

            // Calcular altura de la barra
            int alturaBarra = (int) ((temperatura / maxTemp) * alturaMaxima);
            int y = 500 - alturaBarra; // 500 es la línea base

            // Dibujar barra
            Color colorBarra = colores[index % colores.length];
            g2d.setColor(colorBarra);
            g2d.fillRect(x, y, anchoBarra, alturaBarra);

            // Borde
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, anchoBarra, alturaBarra);

            // Valor encima
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String tempStr = String.format("%.2f°C", temperatura);
            FontMetrics fm = g2d.getFontMetrics();
            int anchoTexto = fm.stringWidth(tempStr);
            g2d.drawString(tempStr, x + (anchoBarra - anchoTexto) / 2, y - 5);

            // Nombre de ciudad
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            fm = g2d.getFontMetrics();
            anchoTexto = fm.stringWidth(ciudad);
            g2d.drawString(ciudad, x + (anchoBarra - anchoTexto) / 2, 520);

            x += anchoBarra + espacioEntre;
            index++;
        }

        // Línea base
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(margenIzq - 10, 500, 700, 500);
    }

    private JPanel crearPanelMaxMin(Map<String, String> datos, LocalDate fecha) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (datos.containsKey("error")) {
            JLabel lblError = new JLabel(datos.get("error"));
            lblError.setFont(new Font("Arial", Font.BOLD, 16));
            lblError.setForeground(Color.RED);
            lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblError);
            return panel;
        }

        // Título
        JLabel titulo = new JLabel("Temperaturas Extremas - " + fecha);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);

        panel.add(Box.createVerticalStrut(40));

        // Ciudad más calurosa
        JPanel panelCalurosa = new JPanel();
        panelCalurosa.setLayout(new BoxLayout(panelCalurosa, BoxLayout.Y_AXIS));
        panelCalurosa.setBackground(new Color(255, 200, 200));
        panelCalurosa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 76, 60), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        panelCalurosa.setMaximumSize(new Dimension(600, 120));

        JLabel lblCalTitulo = new JLabel("🔥 CIUDAD MÁS CALUROSA");
        lblCalTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblCalTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCalInfo = new JLabel(datos.get("masCalurosa"));
        lblCalInfo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblCalInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCalurosa.add(lblCalTitulo);
        panelCalurosa.add(Box.createVerticalStrut(10));
        panelCalurosa.add(lblCalInfo);

        panel.add(panelCalurosa);
        panel.add(Box.createVerticalStrut(30));

        // Ciudad menos calurosa
        JPanel panelFria = new JPanel();
        panelFria.setLayout(new BoxLayout(panelFria, BoxLayout.Y_AXIS));
        panelFria.setBackground(new Color(200, 220, 255));
        panelFria.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        panelFria.setMaximumSize(new Dimension(600, 120));

        JLabel lblFriaTitulo = new JLabel("❄️ CIUDAD MENOS CALUROSA");
        lblFriaTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblFriaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblFriaInfo = new JLabel(datos.get("menosCalurosa"));
        lblFriaInfo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblFriaInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelFria.add(lblFriaTitulo);
        panelFria.add(Box.createVerticalStrut(10));
        panelFria.add(lblFriaInfo);

        panel.add(panelFria);

        return panel;
    }

}
