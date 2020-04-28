package pe.albatross.zelpers.file.text;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractTextView extends AbstractView {

    private static final String CONTENT_TYPE = "text/txt";

    private String url;

    public AbstractTextView() {
        setContentType(CONTENT_TYPE);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(getContentType());
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        buildTextDocument(model, request, response);
    }

    protected abstract void buildTextDocument(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

}
