package utilitarios;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.table.TableModel;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

public class ExportadorPDF {

    // Genera documento PDF corporativo con formato sobrio y datos de tabla
    public static void exportarTabla(String titulo, TableModel modelo, File archivoDestino) throws Exception {
        Document documento = new Document(PageSize.A4, 36, 36, 40, 40);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoDestino));
        documento.open();

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(44, 62, 80));
        Paragraph parrafoTitulo = new Paragraph(titulo, fuenteTitulo);
        parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
        parrafoTitulo.setSpacingAfter(18);
        documento.add(parrafoTitulo);

        int numCols = modelo.getColumnCount();
        PdfPTable tabla = new PdfPTable(numCols);
        tabla.setWidthPercentage(100);

        Font fuenteHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Color colorHeader = new Color(44, 62, 80);

        for (int c = 0; c < numCols; c++) {
            PdfPCell celdaHeader = new PdfPCell(new Phrase(modelo.getColumnName(c), fuenteHeader));
            celdaHeader.setBackgroundColor(colorHeader);
            celdaHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHeader.setPadding(6);
            tabla.addCell(celdaHeader);
        }

        Font fuenteDatos = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        int numRows = modelo.getRowCount();

        for (int r = 0; r < numRows; r++) {
            Color fondoFila = (r % 2 == 0) ? Color.WHITE : new Color(244, 246, 249);
            for (int c = 0; c < numCols; c++) {
                Object val = modelo.getValueAt(r, c);
                PdfPCell celda = new PdfPCell(new Phrase(val != null ? val.toString() : "", fuenteDatos));
                celda.setBackgroundColor(fondoFila);
                celda.setPadding(5);
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                tabla.addCell(celda);
            }
        }

        documento.add(tabla);
        documento.close();
    }
}
