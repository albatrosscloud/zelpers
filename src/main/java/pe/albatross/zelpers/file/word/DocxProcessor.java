package pe.albatross.zelpers.file.word;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;

/**
 * Process a Word Template (docx) substituting data from map of variables and
 * replacing media thought md5sum from map of media.
 *
 * Based on StackOverflow Reply from "kensvebary" user.
 * https://stackoverflow.com/users/1113093/kensvebary
 */
@Slf4j
public class DocxProcessor {

    private static final String MAIN_DOCUMENT_PATH = "word/document.xml";
    private static final String MEDIA_DOCUMENT_PATH = "word/media/";

    /**
     * Generates .docx document from given template and the substitution data
     *
     * @param templateFile File template
     * @param substitutionData Hash map with the set of key-value pairs that
     * represent substitution data
     * @return path del archivo generado
     */
    public static String processTemplate(File templateFile, Map<String, String> substitutionData) {
        return processTemplate(templateFile, substitutionData, null);
    }

    /**
     * Generates .docx document from given template and the substitution data
     *
     * @param templateFile File template
     * @param substitutionData Hash map with the set of key-value pairs that
     * represent substitution data
     * @param substitutionMedia Hash map with the set of key-value pairs that
     * represent substitution media
     * @return path del archivo generado
     */
    public static String processTemplate(File templateFile, Map<String, String> substitutionData, Map<String, String> substitutionMedia) {

        String tmpDirectoryName = templateFile.getParent() + File.separator + UUID.randomUUID().toString() + File.separator;
        File tmpDirectory = new File(tmpDirectoryName);

        String generatedPath = templateFile.getParent() + File.separator + "generado-" + templateFile.getName();

        try {

            unzip(templateFile, tmpDirectory);

            changeData(new File(tmpDirectoryName + MAIN_DOCUMENT_PATH), substitutionData);

            changeMedia(new File(tmpDirectoryName + MEDIA_DOCUMENT_PATH), substitutionMedia);

            zip(tmpDirectory, new File(generatedPath));

            FileUtils.deleteDirectory(tmpDirectory);

        } catch (IOException ioe) {
            log.error("Error al Generar", ioe);

            throw new PhobosException(ioe.getMessage(), ioe);

        }

        return generatedPath;

    }

    /**
     * Substitutes keys found in target file with corresponding data
     *
     * @param targetFile Target file
     * @param substitutionData Map of key-value pairs of data
     * @throws IOException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void changeData(File targetFile, Map<String, String> substitutionData) throws IOException {

        BufferedReader br = null;
        String docxTemplate = "";
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile), "UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                docxTemplate = docxTemplate + temp;
            }
            br.close();
            targetFile.delete();
        } catch (IOException e) {
            br.close();
            throw e;
        }

        Iterator substitutionDataIterator = substitutionData.entrySet().iterator();
        while (substitutionDataIterator.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry<String, String>) substitutionDataIterator.next();

            if (docxTemplate.contains(pair.getKey())) {

                if (pair.getValue() != null) {
                    docxTemplate = docxTemplate.replace(pair.getKey(), pair.getValue());
                }
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            fos.write(docxTemplate.getBytes("UTF-8"));
            fos.close();
        } catch (IOException e) {
            fos.close();
            throw e;
        }
    }

    /**
     * Substitutes media files with new file content
     *
     * @param mediaDirectory Media Directory Unzzipped
     * @param mapMedia Map of key-value pairs of MD5 Files to Replace
     */
    public static void changeMedia(File mediaDirectory, Map<String, String> mapMedia) {
        if (mapMedia == null || !mediaDirectory.exists()) {
            return;
        }

        for (File oldFile : mediaDirectory.listFiles()) {

            String md5OldFile = TypesUtil.toMD5FromFile(oldFile.getAbsolutePath());
            String newMediaPath = mapMedia.get(md5OldFile);

            if (!StringUtils.isBlank(newMediaPath)) {

                File newMediaFile = new File(newMediaPath);
                newMediaFile.renameTo(oldFile);

            }
        }
    }

    /**
     * Unzipps specified DOCX file to specified directory
     *
     * @param zipfile Source ZIP file
     * @param directory Destination directory
     * @throws IOException sobre el template
     */
    private static void unzip(File zipfile, File directory) throws IOException {

        ZipFile zfile = new ZipFile(zipfile);
        Enumeration<? extends ZipEntry> entries = zfile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File file = new File(directory, entry.getName());
            if (entry.isDirectory()) {
                file.mkdirs();
            } else {
                file.getParentFile().mkdirs();
                InputStream in = zfile.getInputStream(entry);
                try {
                    copy(in, file);
                } finally {
                    in.close();
                }
            }
        }
    }

    /**
     * Zipps specified directory and all its subdirectories to DOCX
     *
     * @param directory Specified directory
     * @param zipfile Output ZIP file name
     * @throws IOException sobre el template
     */
    private static void zip(File directory, File zipfile) throws IOException {

        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;

        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        if (kid.getName().contains(".docx")) {
                            continue;
                        }
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {

        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    private static void copy(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            copy(in, out);
        } finally {
            out.close();
        }
    }

}
