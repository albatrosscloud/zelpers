package pe.albatross.zelpers.file.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipHelper {

    public static File compressFolder(String pathFolder) throws IOException, ZipException {

        if (pathFolder.endsWith(File.separator)) {
            pathFolder = pathFolder.substring(0, pathFolder.length() - 1);
        }

        File filex = new File(pathFolder + ".zip");
        File folder = new File(pathFolder);

        ZipFile zipFile = new ZipFile(pathFolder + ".zip");

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipFile.addFolder(folder, parameters);
        return filex;

    }

    public static void addToZipFile(String fileName, String fileNameToZip, ZipOutputStream zos) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);

        ZipEntry zipEntry = new ZipEntry(fileNameToZip);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }
}
