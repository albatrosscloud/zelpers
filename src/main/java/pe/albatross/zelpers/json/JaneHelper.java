package pe.albatross.zelpers.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;

public class JaneHelper {

    private Object object;
    private ObjectNode root;
    private boolean allowNullsBlanks;

    public JaneHelper() {
        throw new PhobosException("Utilizar el método 'from()'");
    }

    public JaneHelper(Object object, boolean allowNulls) {
        Assert.isNull(this.object, "El objecto inicial ya fue creado");
        this.object = object;
        this.allowNullsBlanks = allowNulls;

        this.root = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
    }

    public ObjectNode json() {
        return root;
    }

    public static JaneHelper from(Object object) {
        return JaneHelper.from(object, true);
    }

    public static JaneHelper from(Object object, boolean allowNulls) {
        return new JaneHelper(object, allowNulls);

    }

    public JaneHelper only(String attrs) {
        this.root = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, this.split(attrs));
        return this;
    }

    public JaneHelper join(String parent) {
        return join(parent, "*", false);
    }

    public JaneHelper join(String parent, String fields) {
        return join(parent, fields, false);
    }

    public JaneHelper join(String parent, boolean attachRoot) {
        return join(parent, "*", attachRoot);
    }

    public JaneHelper join(String parentString, String fields, boolean attachRoot) {
        if (this.object == null) {
            return this;
        }
        Object parent = ObjectUtil.getParentTree(this.object, parentString);
        if (parent == null) {
            return this;
        }
        int pos = parentString.indexOf(".");
        if (pos < 0) {
            if (parent instanceof List) {
                this.root.set(parentString, createArrayNode(parent, this.split(fields)));

            } else {
                this.root.set(parentString, JsonHelper.createJson(parent, JsonNodeFactory.instance, this.allowNullsBlanks, this.split(fields)));
            }
            return this;
        }

        String acum = "";
        ObjectNode nodo = this.root;

        String[] attrs = parentString.split("\\.");
        for (int i = 0; i < attrs.length; i++) {
            acum += acum.equals("") ? "" : ".";
            acum += attrs[i];

            Object parentTree = ObjectUtil.getParentTree(this.object, acum);
            if (parentTree instanceof List) {
                Assert.isTrue(i == attrs.length - 1, "No puede ingresar al array " + acum);
                nodo.set(parentString, createArrayNode(parentTree, this.split(fields)));
                return this;

            } else {
                if (i == attrs.length - 1) {
                    nodo.set(attrs[i], JsonHelper.createJson(parent, JsonNodeFactory.instance, allowNullsBlanks, this.split(fields)));
                } else {
                    nodo = nodo.with(attrs[i]);
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

    private String[] split(String attrs) {
        return attrs.replace(" ", "").split(",");
    }

}
