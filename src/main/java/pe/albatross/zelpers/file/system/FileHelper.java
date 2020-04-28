package pe.albatross.zelpers.file.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.file.model.Inode;
import pe.albatross.zelpers.miscelanea.PhobosException;

@Slf4j
public class FileHelper {
    
    private final static String DELIMITER = File.separator;

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

    public static boolean deleteFromDisk(String absoluteName) throws IOException {
  
        File delete = new File(absoluteName);
        FileUtils.deleteDirectory(delete);

        return true;
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

    public static boolean moveFile(String oldName, String newName) {
        try {
            Files.move(Paths.get(oldName), Paths.get(newName), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }

    }
    
    public static Inode allFile(String directory, boolean recursive) {

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }
        
        Path rootPath = Paths.get(directory);

        Inode inodeRoot = FileHelper.getInodeDirectory(rootPath, rootPath.toFile());
        List<Inode> inodes = new ArrayList();
        inodeRoot.setItems(inodes);

        try (Stream<Path> paths = Files.list(Paths.get(directory))) {

            paths.forEach(path -> {
                

                if (path.toFile().isFile()) {
                    Inode inode = FileHelper.getInodeFile(path, path.toFile());
                    inodes.add(inode);
                    
                } else {

                    Inode inode = FileHelper.getInodeDirectory(path, path.toFile());
                    
                    if (recursive) {
                        inode = FileHelper.allFile(inode.getPath(), recursive);
                    }
                    
                    inodes.add(inode);
                }
            });

        } catch (IOException ex) {
            throw new PhobosException(ex);
        }

        return inodeRoot;
    }

    private static Inode getInodeDirectory(Path path, File file) {

        Inode inode = new Inode();
        inode.setType(Inode.Type.DIRECTORY);
        inode.setPath(path.toString());

        inode.setTitle(file.getName());
        inode.setFileName(file.getName());

        if (!StringUtils.isEmpty(file.getParent())) {
            inode.setParent(FileHelper.getInodeDirectory(path.getParent(), file.getParentFile()));
        }

        return inode;
    }

    private static Inode getInodeFile(Path path, File file) {

        Inode inode = new Inode();
        inode.setType(Inode.Type.FILE);
        inode.setPath(path.toString());

        inode.setSize(file.length());

        inode.setTitle(FilenameUtils.getBaseName(path.toString()));
        inode.setFileName(FilenameUtils.getName(path.toString()));
        inode.setExtension(FilenameUtils.getExtension(path.toString()));

        return inode;
    }

    
    
}
