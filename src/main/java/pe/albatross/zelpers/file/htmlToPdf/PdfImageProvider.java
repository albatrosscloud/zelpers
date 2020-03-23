package pe.albatross.zelpers.file.htmlToPdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import java.io.IOException;

public class PdfImageProvider extends AbstractImageProvider {

    @Override
    public Image retrieve(String src) {
        try {

            return Image.getInstance(this.getClass().getResource(src));

        } catch (BadElementException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {
            ex.printStackTrace();

        }
        return null;
    }

    @Override
    public String getImageRootPath() {
        return null;
    }
}
