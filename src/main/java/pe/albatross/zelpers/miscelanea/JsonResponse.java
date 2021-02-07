package pe.albatross.zelpers.miscelanea;

/**
 * Reemplazar por ObjectNode o ArrayNode y controlar los errores usando Axios.
 */
@Deprecated
public class JsonResponse {

    private Object data;
    private Integer total;
    private String message;
    private Boolean success;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
