package pe.albatross.zelpers.cloud.storage.openstack;

import pe.albatross.zelpers.cloud.credentials.OpenStackCredentials;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ObjectListOptions;
import org.openstack4j.model.storage.object.options.ObjectPutOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.model.Inode;
import pe.albatross.zelpers.miscelanea.PhobosException;

@Slf4j
@Lazy
@Service("swiftService")
@ConditionalOnSingleCandidate(OpenStackCredentials.class)
public class SwiftServiceImp implements StorageService {

    @Autowired
    OpenStackCredentials credentials;

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

        File file = new File(localDirectory + fileName);
        String swiftPath = bucketDirectory + fileName;

        if (!replace && this.doesExist(bucket, swiftPath)) {
            log.info("Already exists on Swsift (Use replace to force upload)");
            return;
        }

        log.debug("Upload Swift {}:/{} - {}", bucket, swiftPath, localDirectory);

        OSClientV3 osClient = credentials.autenticate();

        Map metadata = new HashMap();

        String mime = URLConnection.guessContentTypeFromName(file.getName());

        if (publico) {
            metadata.put("cache-control", "max-age=604800, must-revalidate");
            metadata.put("Content-Type", mime);
        }

        osClient.objectStorage()
                .objects()
                .put(
                        bucket,
                        fileName,
                        Payloads.create(file),
                        ObjectPutOptions.create().path(bucketDirectory)
                                .metadata(metadata)
                                .contentType(mime)
                );
    }

    @Override
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {
        this.uploadFile(bucket, bucketDirectory, localDirectory, fileName, publico);
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {
        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico, false);
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
    public void deleteFile(String buket, String path) {

        OSClientV3 osClient = credentials.autenticate();
        osClient.objectStorage().objects().delete(buket, path);

    }

    @Override
    @Deprecated
    public InputStream getFile(String bucket, String directory, String fileName) {

        return this.getFile(bucket, directory + fileName);
    }

    @Override
    public InputStream getFile(String bucket, String path) {

        OSClientV3 osClient = credentials.autenticate();

        SwiftObject object = osClient.objectStorage().objects().get(bucket, path);

        if (object == null) {
            throw new PhobosException("Archivo no encontrado");
        }

        DLPayload payload = object.download();

        return payload.getInputStream();
    }

    @Async
    @Override
    public void downloadFile(String bucket, String path, String pathLocal) {
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

    @Override
    @Deprecated
    public boolean doesExist(String bucket, String directory, String fileName) {
        return this.doesExist(bucket, directory + fileName);
    }

    @Override
    public boolean doesExist(String bucket, String path) {
        OSClientV3 osClient = credentials.autenticate();

        SwiftObject object = osClient.objectStorage().objects().get(bucket, path);

        return object != null;
    }

    @Override
    public boolean createDirectory(String bucket, String directory) {

        OSClientV3 osClient = credentials.autenticate();

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }

        if (directory.startsWith(DELIMITER)) {
            directory = directory.substring(1);
        }

        osClient.objectStorage().containers().createPath(bucket, directory);

        return this.doesExist(bucket, directory);
    }

    @Override
    public Inode allFile(String bucket, String directory, boolean recursive) {

        OSClientV3 osClient = credentials.autenticate();

        ObjectListOptions options = ObjectListOptions.create()
                .path(directory)
                .delimiter('/');

        if (directory.startsWith(DELIMITER)) {
            directory = directory.substring(1);
        }

        if (!directory.endsWith(DELIMITER)) {
            directory += DELIMITER;
        }

        if (directory.equals(DELIMITER)) {
            directory = "";
            options.getOptions().remove("path");
        }

        List<Inode> inodes = new ArrayList();

        List<? extends SwiftObject> result = osClient.objectStorage().objects().list(
                bucket,
                options
        );

        for (SwiftObject object : result) {

            if (object.getName().endsWith(DELIMITER)) {
                Inode inode = this.getInodeDirectory(bucket, object.getName());

                if (recursive) {
                    inode = this.allFile(bucket, object.getName(), recursive);
                }

                inodes.add(inode);

            } else {
                inodes.add(this.getInodeFile(object));
            }

        }

        Inode inode = this.getInodeDirectory(bucket, directory);
        inode.setItems(inodes);
        return inode;
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

    private Inode getInodeFile(SwiftObject summary) {

        String bucket = summary.getContainerName();
        String path = summary.getName();

        Inode inode = new Inode();
        inode.setType(Inode.Type.FILE);
        inode.setPath(path);
        inode.setBucket(bucket);
        inode.setSize(summary.getSizeInBytes());

        inode.setTitle(FilenameUtils.getBaseName(path));
        inode.setFileName(FilenameUtils.getName(path));
        inode.setExtension(FilenameUtils.getExtension(path));

        String url = String.format(credentials.getUrlBase() + "%s/%s", bucket, path);

        inode.setUrl(url);

        return inode;
    }

}
