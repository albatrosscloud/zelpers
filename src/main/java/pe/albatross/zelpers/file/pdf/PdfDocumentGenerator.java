package pe.albatross.zelpers.file.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang3.StringUtils;
import pe.albatross.zelpers.enums.HeaderTypeEnum;
import pe.albatross.zelpers.miscelanea.OSValidator;

public class PdfDocumentGenerator {

    public final static Float PADDING_DEFAULT = 3F;
    public final static Float PADDING_MD = 5F;

    private final String PDF_SAVE_PATH = OSValidator.isWindows() ? "c:/tmp/" : "/tmp/";

    public static Font FUENTE_TITULO = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_TITULO_TABLA = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC, BaseColor.WHITE);
    public static Font FUENTE_CABECERA_TABLA = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.WHITE);

    public static Font FUENTE_5 = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
    public static Font FUENTE_6 = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
    public static Font FUENTE_7 = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    public static Font FUENTE_8 = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
    public static Font FUENTE_9 = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
    public static Font FUENTE_10 = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    public static Font FUENTE_11 = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    public static Font FUENTE_12 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

    public static Font FUENTE_5_NEGRITA = new Font(Font.FontFamily.HELVETICA, 5, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_6_NEGRITA = new Font(Font.FontFamily.HELVETICA, 5, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_7_NEGRITA = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_8_NEGRITA = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_11_NEGRITA = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
    public static Font FUENTE_12_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_10_NEGRITA = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_16_NEGRITA = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_18_NEGRITA = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_19_NEGRITA = new Font(Font.FontFamily.HELVETICA, 19, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_20_NEGRITA = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_22_NEGRITA = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.BLACK);
    public static Font FUENTE_25_NEGRITA = new Font(Font.FontFamily.HELVETICA, 25, Font.BOLD, BaseColor.BLACK);

    public Document documentPageVertical(FileOutputStream baos, UEventoPaginaPdf eventoPaginaPdf) throws Exception {
        float relativeAditionalMargin = 0;
        HeaderTypeEnum headerTypeEnum = eventoPaginaPdf.getHeaderTypeEnum();

        if (headerTypeEnum != null) {
            if (headerTypeEnum.equals(HeaderTypeEnum.HEADER2)) {
                int cont = 0;
                if (StringUtils.isNotBlank(eventoPaginaPdf.getTitulo1())) {
                    cont++;
                }
                if (StringUtils.isNotBlank(eventoPaginaPdf.getTitulo2())) {
                    cont++;
                }
                if (cont > 0) {
                    relativeAditionalMargin += headerTypeEnum.getRelativeMarginTop() * cont;
                }
            } else {
                relativeAditionalMargin += headerTypeEnum.getRelativeMarginTop();
            }
        }
        Document document = new Document(PageSize.A4, 36, 36, 20 + relativeAditionalMargin, 36);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document = this.generarPlantillaAgrariaPdf(document, writer, eventoPaginaPdf);
        document.open();

        return document;
    }

    public Document documentPageHorizontal(FileOutputStream baos, UEventoPaginaPdf eventoPaginaPdf) throws Exception {
        //     Document document = new Document(PageSize.A4.rotate(), 46, 46, 20, 36);
        Document document = new Document(PageSize.A4.rotate(), 46, 46, 36, 36);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document = this.generarPlantillaAgrariaPdf(document, writer, eventoPaginaPdf);
        document.open();

        return document;
    }

    public PdfPTable lineHeader(PdfPTable table, PdfPCell cell, Document document, int ancho) throws Exception {
        table.getDefaultCell().setBorder(1);
        table.setWidthPercentage(ancho / 5.23f);//480
        cell = new PdfPCell(new Paragraph(" "));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setColspan(table.getNumberOfColumns());
        table.addCell(cell);
        document.add(table);

        return table;
    }

    public PdfPCell addTitleCellTable(String strTituloCabecera, PdfPTable table, PdfPCell celdaTablaReporte, int icolspan, int ialin) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, FUENTE_6_NEGRITA);
        celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);

        celdaTablaReporte.setHorizontalAlignment(ialin);
        celdaTablaReporte.setColspan(icolspan);

        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
    }

    public PdfPCell addTitleCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin) {
        PdfPCell pdfPCell = PdfDocumentGenerator.this.addTitleCellTable(strTituloCabecera, table, icolspan, ialin, PdfDocumentGenerator.PADDING_DEFAULT, FUENTE_CABECERA_TABLA);
        return pdfPCell;
    }

    public PdfPCell addTitleCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin, Float padding) {
        PdfPCell pdfPCell = PdfDocumentGenerator.this.addTitleCellTable(strTituloCabecera, table, icolspan, ialin, padding, FUENTE_CABECERA_TABLA);
        return pdfPCell;
    }

    public PdfPCell addTitleCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin, Font font) {
        PdfPCell pdfPCell = PdfDocumentGenerator.this.addTitleCellTable(strTituloCabecera, table, icolspan, ialin, PdfDocumentGenerator.PADDING_DEFAULT, font);
        return pdfPCell;
    }

    public PdfPCell addTitleCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin, Float padding, Font font) {
        PdfPCell celdaTablaReporte = generarCeldaCabeceraTabla(strTituloCabecera, font);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);
        if (padding != null) {
            celdaTablaReporte.setPadding(padding);
        }
        celdaTablaReporte.setHorizontalAlignment(ialin);
        celdaTablaReporte.setColspan(icolspan);

        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, PdfPCell celdaTablaReporte, int icolspan, int ialin) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, FUENTE_6);
        celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);

        celdaTablaReporte.setHorizontalAlignment(ialin);
        celdaTablaReporte.setColspan(icolspan);

        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin) {
        PdfPCell celdaTablaReporte = PdfDocumentGenerator.this.addBodyCellTable(strTituloCabecera, table, icolspan, null, ialin, FUENTE_8, null, null);
        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int rowspan, int ialin) {
        PdfPCell celdaTablaReporte = PdfDocumentGenerator.this.addBodyCellTable(strTituloCabecera, table, icolspan, rowspan, ialin, FUENTE_8, null, null);
        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int rowspan, int ialin, Font font) {
        PdfPCell celdaTablaReporte = PdfDocumentGenerator.this.addBodyCellTable(strTituloCabecera, table, icolspan, rowspan, ialin, font, null, null);
        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin, Float paddiFloat) {
        PdfPCell celdaTablaReporte = PdfDocumentGenerator.this.addBodyCellTable(strTituloCabecera, table, icolspan, null, ialin, FUENTE_8, null, paddiFloat);
        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin, Font fuente) {
        PdfPCell celdaTablaReporte = PdfDocumentGenerator.this.addBodyCellTable(strTituloCabecera, table, icolspan, null, ialin, fuente, null, null);
        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin, Font fuente, Float paddiFloat) {
        PdfPCell celdaTablaReporte = PdfDocumentGenerator.this.addBodyCellTable(strTituloCabecera, table, icolspan, null, ialin, fuente, null, paddiFloat);
        return celdaTablaReporte;
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, int ialin, Font fuente, Integer border, Float padding) {
        return PdfDocumentGenerator.this.addBodyCellTable(strTituloCabecera, table, icolspan, null, ialin, fuente, border, padding);
    }

    public PdfPCell addBodyCellTable(String strTituloCabecera, PdfPTable table, int icolspan, Integer irowspan, int ialin, Font fuente, Integer border, Float padding) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, fuente);
        PdfPCell celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);
        if (border != null) {
            celdaTablaReporte.setBorder(border);
        } else {
            celdaTablaReporte.setBorder(table.getDefaultCell().getBorder());
        }
        if (padding != null) {
            celdaTablaReporte.setPadding(padding);
        }
        celdaTablaReporte.setHorizontalAlignment(ialin);
        celdaTablaReporte.setColspan(icolspan);
        if (irowspan != null) {
            celdaTablaReporte.setRowspan(irowspan);
        }
        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
    }

    public PdfPTable createTableDefault(PdfPTable table, Document document, int ancho) throws Exception {
        table.setWidthPercentage(ancho / 5.23f);
        document.add(table);

        return table;
    }

    public PdfPCell fontBodyExpenseTable(String strTituloCabecera, PdfPTable table, PdfPCell celdaTablaReporte, int icolspan, int ialin) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, FUENTE_9);
        celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTablaReporte.setBorder(0);

        celdaTablaReporte.setHorizontalAlignment(ialin);
        celdaTablaReporte.setColspan(icolspan);

        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
    }

    public PdfPCell fontBodySizeSevenTable(String strTituloCabecera, PdfPTable table, PdfPCell celdaTablaReporte, int icolspan, int ialin) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, FUENTE_7);
        celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTablaReporte.setBorder(0);

        celdaTablaReporte.setHorizontalAlignment(ialin);
        celdaTablaReporte.setColspan(icolspan);

        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
    }

    public PdfPCell fontBodySizeTwelveBoldTable(String strTituloCabecera, PdfPTable table, PdfPCell celdaTablaReporte, int icolspan, int ialin) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, FUENTE_12_NEGRITA);
        celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTablaReporte.setBorder(0);

        celdaTablaReporte.setHorizontalAlignment(ialin);
        celdaTablaReporte.setColspan(icolspan);

        table.addCell(celdaTablaReporte);

        return celdaTablaReporte;
    }

    public Document generarPlantillaAgrariaPdf(Document documentoPDF, PdfWriter escritor, UEventoPaginaPdf eventoPagina) throws Exception {
        Rectangle rct = new Rectangle(36, 54, 559, 788);
        escritor.setBoxSize("art", rct);
        escritor.setPageEvent(eventoPagina);
        return documentoPDF;
    }

    public PdfPCell generarCeldaCabeceraTabla(String strTituloCabecera, Font fuente) {
        Paragraph parrafoCeldaReporte = new Paragraph(strTituloCabecera, FUENTE_CABECERA_TABLA);
        if (fuente != null) {
            parrafoCeldaReporte = new Paragraph(strTituloCabecera, fuente);
        }
        PdfPCell celdaTablaReporte = new PdfPCell(parrafoCeldaReporte);
        celdaTablaReporte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTablaReporte.setHorizontalAlignment(Element.ALIGN_CENTER);

        celdaTablaReporte.setBackgroundColor(new BaseColor(0, 50, 100));
        return celdaTablaReporte;
    }

    public Paragraph agregarEnter(int numLineas) {
        StringBuffer cadena = new StringBuffer("");
        for (int ctaLineas = 0; ctaLineas < numLineas; ctaLineas++) {
            cadena.append("\n");
        }
        Paragraph lineaBlanco = new Paragraph(cadena.toString(), FUENTE_6);
        lineaBlanco.setSpacingAfter(0);
        lineaBlanco.setSpacingBefore(0);
        return lineaBlanco;
    }

    public Chunk agregarTab(int numTab) {
        StringBuffer cadena = new StringBuffer("");
        for (int ctaTab = 0; ctaTab < numTab; ctaTab++) {
            cadena.append("\t");
        }
        Chunk tab = new Chunk(cadena.toString(), FUENTE_6);
        return tab;
    }

    public String documentPdfAdmisionFilename(String nombreArchivo, String codigo, String subFolder) {
        File folder = new File(PDF_SAVE_PATH + (StringUtils.isEmpty(subFolder) ? "" : File.separator + subFolder));
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder + File.separator + nombreArchivo.replace(' ', '_') + "_" + codigo + ".pdf";
    }
}
