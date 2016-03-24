package pe.albatross.zelpers.file.system;

import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipHelper {

    public static File compressFolder(String pathFolder) throws IOException, ZipException {

        File filex = new File(pathFolder + ".zip");
        File folder = new File(pathFolder);

        ZipFile zipFile = new ZipFile(pathFolder + ".zip");

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipFile.addFolder(folder, parameters);
        return filex;

    }
}
