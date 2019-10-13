package pe.albatross.zelpers.file.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.springframework.util.CollectionUtils;
import pe.albatross.zelpers.miscelanea.JsonHelper;

public class Inode {

    public enum Type {
        DIRECTORY, FILE;
    }

    private Type type;
    private String bucket;
    private String fileName;
    private String title;
    private String extension;
    private Long size;
    private String path;
    private String url;
    private String mime;
    private Inode parent;
    private List<Inode> items;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Inode getParent() {
        return parent;
    }

    public void setParent(Inode parent) {
        this.parent = parent;
    }

    public List<Inode> getItems() {
        return items;
    }

    public void setItems(List<Inode> items) {
        this.items = items;
    }

    public boolean isDirectory() {
        return this.type == Type.DIRECTORY;
    }

    public boolean isFile() {
        return this.type == Type.FILE;
    }

    public ObjectNode toJson() {
        ObjectNode json = JsonHelper.createJson(this, JsonNodeFactory.instance, true);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        if (!CollectionUtils.isEmpty(this.items)) {
            for (Inode inode : this.getItems()) {
                array.add(inode.toJson());
            }

            json.set("items", array);
        }

        json.set("parent", JsonHelper.createJson(this.getParent(), JsonNodeFactory.instance, true));

        return json;
    }

}
