package utilitarios;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.util.Map;

public class GeneradorGraficas {

    // Construye panel de barras con colores corporativos sobrios
    public static ChartPanel crearGraficoBarras(String titulo, String ejeX, String ejeY, Map<String, Integer> datos, Color colorBarra) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            dataset.addValue(entry.getValue(), "Cantidad", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                titulo,
                ejeX,
                ejeY,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(244, 246, 249));
        plot.setDomainGridlinePaint(new Color(220, 224, 230));
        plot.setRangeGridlinePaint(new Color(220, 224, 230));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, colorBarra != null ? colorBarra : new Color(44, 62, 80));
        renderer.setMaximumBarWidth(0.12);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(360, 220));
        return chartPanel;
    }
}
