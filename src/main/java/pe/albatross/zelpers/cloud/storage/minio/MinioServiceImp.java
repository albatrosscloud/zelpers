package pe.albatross.zelpers.cloud.storage.minio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.BucketExistsArgs;
import io.minio.GetBucketPolicyArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.UploadObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
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
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName,
            boolean publico, boolean replace) {

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
            minioClient.ignoreCertCheck();

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
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName,
            boolean publico) {
        this.uploadFile(bucket, bucketDirectory, localDirectory, fileName, publico, true);
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName,
            boolean publico) {
        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico, true);
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName,
            boolean publico, boolean replace) {
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

        try {

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build());

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

        try (InputStream stream = minioClient.getObject(
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
                            .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());

            return true;

        } catch (Exception e) {
            log.error("Error al subir archivos", e);
            throw new PhobosException("Error al crear directorio en minio.");
        }

    }

    @Override
    public Inode allFile(String bucket, String directory, boolean recursive) {

        if (directory.startsWith(DELIMITER)) {
            directory = directory.substring(1);
        }

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }

        if (directory.equals(DELIMITER)) {
            directory = "";
        }

        try {

            MinioClient minioClient = credentials.autenticate();

            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucket)
                    .prefix(directory)
                    .delimiter(DELIMITER)
                    .build();

            Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            Iterator<Result<Item>> iterator = results.iterator();

            List<Inode> inodes = new ArrayList();

            while (iterator.hasNext()) {
                Item item = iterator.next().get();

                if (item.isDir()) {

                    Inode inode = this.getInodeDirectory(bucket, item.objectName());

                    if (recursive) {
                        inode = this.allFile(bucket, item.objectName(), recursive);
                    }

                    inodes.add(inode);

                } else {
                    inodes.add(this.getInodeFile(bucket, item));
                }
            }

            Inode inode = this.getInodeDirectory(bucket, directory);
            inode.setItems(inodes);
            return inode;

        } catch (Exception e) {
            log.error("Error al listar el bucket/directorio", e);
            throw new PhobosException("Error al listar el directorio en minio.");
        }

    }

    private Inode getInodeDirectory(String bucket, String pathDirectory) {

        Inode inode = new Inode();
        inode.setType(Inode.Type.DIRECTORY);
        inode.setPath(pathDirectory);
        inode.setBucket(bucket);

        File file = new File(pathDirectory);
        inode.setTitle(file.getName());
        inode.setFileName(file.getName());

        if (!StringUtils.isEmpty(file.getParent())) {
            inode.setParent(this.getInodeDirectory(bucket, file.getParent()));
        }

        return inode;
    }

    private Inode getInodeFile(String bucket, Item item) {

        String path = item.objectName();

        Inode inode = new Inode();
        inode.setType(Inode.Type.FILE);
        inode.setPath(path);
        inode.setBucket(bucket);
        inode.setSize(item.size());

        inode.setTitle(FilenameUtils.getBaseName(path));
        inode.setFileName(FilenameUtils.getName(path));
        inode.setExtension(FilenameUtils.getExtension(path));

        String url = String.format(credentials.getUrlBase() + "%s/%s", bucket, path);

        inode.setUrl(url);

        return inode;
    }

@Override
    public void createBucket(String bucket) {
        try {
            MinioClient minioClient = credentials.autenticate();

            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucket).build());
            } else {
                log.error("El bucket '{}' ya existe.", bucket);
                throw new PhobosException(String.format("El bucket '%s' ya existe.", bucket));
            }
        } catch (Exception e) {
            log.error("Error al crear el bucket '{}'", bucket, e);
            throw new PhobosException("No se pudo crear el bucket en MinIO.");
        }
    }

    @Override
    public void makeFilePublic(String bucket, String objectPath) {
        try {
            MinioClient minioClient = credentials.autenticate();
            ObjectMapper mapper = new ObjectMapper();

            String currentPolicy = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucket).build());
            Map<String, Object> policy = currentPolicy.isEmpty() ? new HashMap<>()
                    : mapper.readValue(currentPolicy, Map.class);
            List<Map<String, Object>> statements = (List<Map<String, Object>>) policy.getOrDefault("Statement",
                    new ArrayList<>());

            String resourceArn = "arn:aws:s3:::" + bucket + "/" + objectPath;
            boolean alreadyExists = statements.stream().anyMatch(statement -> {
                Object resource = statement.get("Resource");
                return resource instanceof String ? resource.equals(resourceArn)
                        : resource instanceof List && ((List<?>) resource).contains(resourceArn);
            });

            if (alreadyExists) {
                log.info("El objeto '{}' ya es público.", objectPath);
                return;
            }

            Map<String, Object> newStatement = new HashMap<>();
            newStatement.put("Effect", "Allow");
            newStatement.put("Principal", "*");
            newStatement.put("Action", "s3:GetObject");
            newStatement.put("Resource", resourceArn);
            
            statements.add(newStatement);

            policy.put("Version", "2012-10-17");
            policy.put("Statement", statements);

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucket)
                    .config(mapper.writeValueAsString(policy))
                    .build());

        } catch (Exception e) {
            log.error("No se pudo hacer público el archivo '{}'", objectPath, e);
            throw new PhobosException("Error al hacer público el archivo.");
        }
    }

    @Override
    public String generateTemporaryAccessUrl(String bucket, String objectPath, int expirationInSeconds) {
        try {
            MinioClient minioClient = credentials.autenticate();

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET) 
                            .bucket(bucket)
                            .object(objectPath)
                            .expiry(expirationInSeconds) 
                            .build());

        } catch (Exception e) {
            log.error("No se pudo generar URL temporal para '{}'", objectPath, e);
            throw new PhobosException("Error al generar acceso temporal al archivo.");
        }
    }
}
