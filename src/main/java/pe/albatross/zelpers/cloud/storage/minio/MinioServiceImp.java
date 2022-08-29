package pe.albatross.zelpers.cloud.storage.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.cloud.credentials.MinioCredentials;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.model.Inode;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;

@Slf4j
@Lazy
@Service("minioService")
@ConditionalOnSingleCandidate(MinioCredentials.class)
public class MinioServiceImp implements StorageService {

    @Autowired
    MinioCredentials credentials;

    private static final String DELIMITER = File.separator;

    @Override
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico, boolean replace) {

        if (!localDirectory.endsWith(DELIMITER)) {
            localDirectory += DELIMITER;
        }

        if (!bucketDirectory.endsWith(DELIMITER)) {
            bucketDirectory += DELIMITER;
        }
        if (bucketDirectory.startsWith(DELIMITER)) {
            bucketDirectory = bucketDirectory.substring(1);
        }

        String remotePath = bucketDirectory + fileName;
        String localPath = localDirectory + fileName;

        File file = new File(localPath);
        Assert.isTrue(file.exists(), "Archivo no existente: " + file.getPath());

        if (!replace && this.doesExist(bucket, remotePath)) {
            log.info("Already exists on Swsift (Use replace to force upload)");
            return;
        }

        log.debug("Upload Minio {}:/{} - {}", bucket, remotePath, localDirectory);

        String mime = MimeTypes.OCTET_STREAM;

        try {
            Tika tika = new Tika();
            mime = tika.detect(file);

        } catch (IOException ex) {
            log.error("Error al Detectar Tipo", ex);
        }

        try {

            MinioClient minioClient = credentials.autenticate();

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(remotePath)
                            .filename(localPath)
                            .contentType(mime)
                            .build());

        } catch (Exception e) {
            log.error("Error al subir archivos", e);
            throw new PhobosException("Error al subir archivo a minio.");
        }
    }

    @Override
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {
        this.uploadFile(bucket, bucketDirectory, localDirectory, fileName, publico, true);
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {
        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico, true);
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico, boolean replace) {
        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico, replace);
    }

    @Async
    @Override
    @Deprecated
    public void deleteFile(String buket, String directory, String fileName) {

        this.deleteFile(buket, directory + fileName);
    }

    @Async
    @Override
    public void deleteFile(String bucket, String path) {
        try {

            MinioClient minioClient = credentials.autenticate();

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build());

        } catch (Exception e) {
            log.error(String.format("No se pudo eliminar el archivo %s %s", bucket, path), e);
        }

    }

    @Override
    @Deprecated
    public InputStream getFile(String bucket, String directory, String fileName) {

        return this.getFile(bucket, directory + fileName);
    }

    @Override
    public InputStream getFile(String bucket, String path) {
        MinioClient minioClient = credentials.autenticate();

        try ( InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .build())) {

            return stream;

        } catch (Exception e) {
            log.error(String.format("No se pudo descargar el archivo %s %s", bucket, path), e);
        }

        return null;
    }

    @Override
    public void downloadFileSync(String bucket, String path, String pathLocal) {
        log.debug("Download Swift {} {}", bucket, path);

        InputStream in = this.getFile(bucket, path);

        File targetFile = new File(pathLocal);
        try {
            FileUtils.copyInputStreamToFile(in, targetFile);

        } catch (Exception e) {
            log.debug(e.getLocalizedMessage(), e);
            e.printStackTrace();
        }
    }

    @Async
    @Override
    public void downloadFile(String bucket, String path, String pathLocal) {
        this.downloadFileSync(bucket, path, pathLocal);
    }

    @Override
    @Deprecated
    public boolean doesExist(String bucket, String directory, String fileName) {
        return this.doesExist(bucket, directory + fileName);
    }

    @Override
    public boolean doesExist(String bucket, String path) {
        MinioClient minioClient = credentials.autenticate();

        try ( InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .build())) {

            return stream.available() > 0;

        } catch (Exception e) {
            log.error(String.format("No se pudo validar existencia del archivo %s %s", bucket, path), e);
            throw new PhobosException("Error al validar existencia en minio.");
        }
    }

    @Override
    public boolean createDirectory(String bucket, String directory) {

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }

        if (directory.startsWith(DELIMITER)) {
            directory = directory.substring(1);
        }

        try {

            MinioClient minioClient = credentials.autenticate();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(directory)
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());

            return true;

        } catch (Exception e) {
            log.error("Error al subir archivos", e);
            throw new PhobosException("Error al crear directorio en minio.");
        }

    }

    @Override
    public Inode allFile(String bucket, String directory, boolean recursive) {

        throw new PhobosException("Pendiente de implementar, revisar aws o swift como idea.");
    }

}
