package pe.albatross.zelpers.file.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.web.multipart.MultipartFile;

public class FileHelper {

    public static boolean saveToDisk(MultipartFile file, String fileName) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean almacenado = false;

        if (file.getSize() > 0) {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(fileName);
            int readBytes = 0;
            byte[] buffer = new byte[10000];
            while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {

                outputStream.write(buffer, 0, readBytes);
            }
            outputStream.close();
            inputStream.close();
            almacenado = true;
        }

        return almacenado;

    }

    public static boolean deleteFromDisk(String absoluteName) {
        boolean deleted = false;

        File delete = new File(absoluteName);
        deleted = delete.delete();

        return deleted;
    }

    public static void createDirectory(String ruta) {

        File folder = new File(ruta);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void renameDirectory(String oldName, String newName) {

        File folder = new File(oldName);
        if (folder.exists()) {
            folder.renameTo(new File(newName));
        }
    }

    public static boolean renameFile(String oldName, String newName) {
        boolean rtrn = false;
        File folder = new File(oldName);
        if (folder.exists()) {
            rtrn = folder.renameTo(new File(newName));
        }

        return rtrn;
    }
}
