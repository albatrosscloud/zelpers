package pe.albatross.zelpers.file.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.zelpers.miscelanea.JsonHelper;

public class Inode {

    public enum Type {
        DIRECTORY, FILE;
    }

    private Type type;
    private String fileName;
    private String title;
    private String extension;
    private Long size;
    private String path;
    private String url;
    private String mime;
    private List<Inode> items;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public List<Inode> getItems() {
        return items;
    }

    public void setItems(List<Inode> items) {
        this.items = items;
    }

    public ObjectNode toJson() {
        ObjectNode json = JsonHelper.createJson(this, JsonNodeFactory.instance);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Inode inode : this.getItems()) {
            ObjectNode item = inode.toJson();
            array.add(item);
        }
        json.set("items", array);

        return json;
    }

}
