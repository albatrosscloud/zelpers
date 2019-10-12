package pe.albatross.zelpers.openstack;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ObjectListOptions;
import org.openstack4j.model.storage.object.options.ObjectPutOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pe.albatross.zelpers.file.model.Inode;

@Lazy
@Service
public class SwiftServiceImp implements SwiftService {

    @Autowired
    OpenStackCredentials credentials;

    private static final String DELIMITER = "/";

    @Override
    public void uploadFileSync(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {

        File file = new File(localDirectory + fileName);

        OSClientV3 osClient = credentials.autenticate();

        Map metadata = new HashMap();

        if (publico) {
            metadata.put("cache-control", "max-age=604800, must-revalidate");
        }

        osClient.objectStorage()
                .objects()
                .put(
                        bucket,
                        fileName,
                        Payloads.create(file),
                        ObjectPutOptions.create().path(bucketDirectory)
                                .metadata(metadata)
                );
    }

    @Async
    @Override
    public void uploadFile(String bucket, String bucketDirectory, String localDirectory, String fileName, boolean publico) {

        this.uploadFileSync(bucket, bucketDirectory, localDirectory, fileName, publico);
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

        List<? extends SwiftObject> objs = osClient.objectStorage().objects().list(
                bucket,
                ObjectListOptions.create().path(path));

        DLPayload payload = objs.get(0).download();
        return payload.getInputStream();
    }

    @Override
    @Deprecated
    public boolean doesExist(String bucket, String directory, String fileName) {
        return this.doesExist(bucket, directory + fileName);
    }

    @Override
    public boolean doesExist(String bucket, String path) {
        OSClientV3 osClient = credentials.autenticate();

        List<? extends SwiftObject> objects = osClient.objectStorage().objects().list(
                bucket,
                ObjectListOptions.create().path(path)
        );

        return !CollectionUtils.isEmpty(objects);
    }

    @Override
    public boolean createDirectory(String bucket, String directory) {

        OSClientV3 osClient = credentials.autenticate();
        osClient.objectStorage().containers().createPath(bucket, directory);

        return this.doesExist(bucket, directory);
    }

    @Override
    public Inode allFile(String bucket, String directory, boolean recursive) {
        return new Inode();
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
