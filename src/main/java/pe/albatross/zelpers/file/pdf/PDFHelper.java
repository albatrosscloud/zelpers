package pe.albatross.zelpers.file.pdf;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import pe.albatross.zelpers.miscelanea.PhobosException;

public class PDFHelper {

    /**
     *
     * Convierte archivo word en formato .docx a .pdf en el mismo directorio del
     * archivo con el mismo nombre del archivo. 
     *
     * @param docx File Path del archivo word
     * @return path del archivo 
     */
    public static String convertDocxToPdf(File docx) {
        String pdf = docx.getAbsolutePath().replace(".docx", ".pdf");
        return PDFHelper.convertDocxToPdf(docx, new File(pdf));
    }

    /**
     * Convierte archivo word en formato .docx a .pdf.
     *
     * @param docx File Path del archivo word
     * @param pdf File Path del archivo pdf a generar
     * @return path del archivo 
     */
    public static String convertDocxToPdf(File docx, File pdf) {
        try {
            InputStream in = new FileInputStream(docx);

            XWPFDocument document = new XWPFDocument(in);

            PdfOptions options = PdfOptions.create();

            OutputStream out = new FileOutputStream(pdf);
            PdfConverter.getInstance().convert(document, out, options);

            document.close();
            out.close();

        } catch (Exception ex) {
            throw new PhobosException(ex.getLocalizedMessage(), ex);

        } finally {
            return pdf.getAbsolutePath();
        }
    }

}
