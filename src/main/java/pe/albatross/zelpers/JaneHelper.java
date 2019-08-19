package pe.albatross.zelpers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;

public class JaneHelper {

    private Object object;
    private ObjectNode node;
    private boolean allowNullsBlanks = false;

    public JaneHelper() {
        this.allowNullsBlanks = false;
    }

    public JaneHelper(Object object) {
        this.allowNullsBlanks = false;
        this.object = object;
        this.node = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
    }

    public ObjectNode getNode() {
        return node;
    }

    public static JaneHelper createJsonFrom(Object obj) {
        return new JaneHelper(obj);
    }

    public static JaneHelper createJson() {
        return new JaneHelper();
    }

    public JaneHelper from(Object object) {
        Assert.isNull(this.object, "El objecto inicial ya fue creado");
        this.object = object;
        this.node = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
        return this;
    }

    public JaneHelper from(Object object, String attrs) {
        Assert.isNull(this.object, "El objecto inicial ya fue creado");
        this.object = object;
        this.node = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, attrs.split(","));
        return this;
    }

    public JaneHelper join(String attrObject) {
        return join(attrObject, "*");
    }

    public JaneHelper join(String attrObject, String fields) {
        if (this.object == null) {
            return this;
        }
        Object parent = ObjectUtil.getParentTree(this.object, attrObject);
        if (parent == null) {
            return this;
        }
        int pos = attrObject.indexOf(".");
        if (pos < 0) {
            if (parent instanceof List) {
                this.node.set(attrObject, createArrayNode(parent, fields.split(",")));

            } else {
                this.node.set(attrObject, JsonHelper.createJson(parent, JsonNodeFactory.instance, allowNullsBlanks, fields.split(",")));
            }
            return this;
        }

        String acum = "";
        ObjectNode nodo = this.node;

        String[] attrs = attrObject.split("\\.");
        for (int i = 0; i < attrs.length; i++) {
            acum += acum.equals("") ? "" : ".";
            acum += attrs[i];

            Object parentTree = ObjectUtil.getParentTree(this.object, acum);
            if (parentTree instanceof List) {
                Assert.isTrue(i == attrs.length - 1, "No puede ingresar al array " + acum);
                nodo.set(attrObject, createArrayNode(parentTree, fields.split(",")));
                return this;

            } else {
                if (i == attrs.length - 1) {
                    nodo.set(attrs[i], JsonHelper.createJson(parent, JsonNodeFactory.instance, allowNullsBlanks, fields.split(",")));
                } else {
                    nodo = nodo.with(attrs[i]);
                }
            }
        }

        return this;
    }

    public JaneHelper putJoin(String nameNode, String attrObject) {
        return putJoin(nameNode, attrObject, "*");
    }

    public JaneHelper putJoin(String nameNode, String attrObject, String fields) {
        if (this.object == null) {
            return this;
        }
        Object parent = ObjectUtil.getParentTree(this.object, attrObject);
        if (parent == null) {
            return this;
        }
        int pos = attrObject.indexOf(".");
        if (pos < 0) {
            if (parent instanceof List) {
                this.node.set(nameNode, createArrayNode(parent, fields.split(",")));

            } else {
                this.node.set(nameNode, JsonHelper.createJson(parent, JsonNodeFactory.instance, allowNullsBlanks, fields.split(",")));
            }
            return this;
        }

        String acum = "";
        String[] attrs = attrObject.split("\\.");
        for (int i = 0; i < attrs.length; i++) {
            acum += acum.equals("") ? "" : ".";
            acum += attrs[i];

            Object parentTree = ObjectUtil.getParentTree(this.object, acum);
            if (parentTree instanceof List) {
                Assert.isTrue(i == attrs.length - 1, "No puede ingresar al array " + acum);
                this.node.set(nameNode, createArrayNode(parentTree, fields.split(",")));
                return this;

            } else {
                if (i == attrs.length - 1) {
                    this.node.set(nameNode, JsonHelper.createJson(parent, JsonNodeFactory.instance, allowNullsBlanks, fields.split(",")));
                } else {
                }
            }
        }

        return this;
    }

    private ArrayNode createArrayNode(Object object, String[] fields) {
        List list = (List) object;
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Object itemList : list) {
            ObjectNode node = JsonHelper.createJson(itemList, JsonNodeFactory.instance, allowNullsBlanks, fields);
            array.add(node);
        }
        return array;
    }

}
