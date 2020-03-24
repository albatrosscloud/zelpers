package pe.albatross.zelpers.miscelanea;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.slf4j.LoggerFactory.getLogger;
import pe.albatross.zelpers.notify.Notifica;
import pe.albatross.zelpers.notify.Notificaciones;

/**
 * EVITAR USAR
 * 
 * Preferentemente no usar try-catch en los controllers, porque los nuevos 
 * proyetos poseen el WebsiteAdvice y este los atrapará y responderá adecuandamente.
 */
@Deprecated
public class ExceptionHandler {

    private static final Logger logger = getLogger(ExceptionHandler.class);

    static String MENSAJE_ERROR_GRAL = "Lo sentimos, su petición no pudo ser atendida. Comuníquese con Mesa de Ayuda";

    private static void showError(PhobosException ex) {
        int level = 2;
        String fullClassName = Thread.currentThread().getStackTrace()[level].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[level].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[level].getLineNumber();

        ex.printStackTrace();
        logger.info(ex.getMessage());
        logger.info(className + "." + methodName + "():" + lineNumber);
    }

    public static void handlePhobosEx(PhobosException ex, JsonResponse json) {

        json.setSuccess(false);
        json.setMessage(ex.getLocalizedMessage());

        // momentaneamente
        //ex.printStackTrace();
        showError(ex);
    }

    public static void handlePhobosEx(PhobosException ex, RedirectAttributes redirect) {

        Notificaciones notas = new Notificaciones();
        String msg = ex.getLocalizedMessage();
        notas.add(Notifica.ERROR, msg);
        redirect.addFlashAttribute(Notifica.NOTIFICACIONES, notas);
        // momentaneamente
        //ex.printStackTrace();
        showError(ex);
    }

    public static void handlePhobosEx(PhobosException ex, Model model) {

        Notificaciones notas = Notificaciones.crearMsg(Notifica.ERROR, ex.getLocalizedMessage(), model);

        // momentaneamente
        //ex.printStackTrace();
        showError(ex);
    }

    public static void handleSpecial(RuntimeException ex, JsonResponse json, String mensaje) {

        json.setSuccess(false);
        json.setMessage(mensaje);
        logger.warn(mensaje);
        logger.warn(ex.getMessage());
        // momentaneamente
        ex.printStackTrace();
    }

    public static void handleSpecial(RuntimeException ex, RedirectAttributes redirect, String mensaje) {

        Notificaciones notas = new Notificaciones();
        notas.add(Notifica.ERROR, mensaje);
        redirect.addFlashAttribute(Notifica.NOTIFICACIONES, notas);
        logger.warn(mensaje);
        logger.warn(ex.getMessage());
        // momentaneamente
        ex.printStackTrace();
    }

    public static void handleSpecial(RuntimeException ex, Model model, String mensaje) {

        Notificaciones notas = Notificaciones.crearMsg(Notifica.ERROR, mensaje, model);
        logger.warn(mensaje);
        logger.warn(ex.getMessage());
        // momentaneamente
        ex.printStackTrace();
    }

    public static void handleException(Exception ex, JsonResponse json) {

        json.setSuccess(false);
        json.setMessage(MENSAJE_ERROR_GRAL);
        // solo para este caso se necesita ver el error que se genera ya que se desconce su origen
        logger.error(ex.getMessage());
        ex.printStackTrace();
    }

    public static void handleException(Exception ex, RedirectAttributes redirect) {

        Notificaciones notas = new Notificaciones();
        notas.add(Notifica.ERROR, MENSAJE_ERROR_GRAL);
        redirect.addFlashAttribute(Notifica.NOTIFICACIONES, notas);
        // solo para este caso se necesita ver el error que se genera ya que se desconce su origen
        logger.error(ex.getMessage());
        ex.printStackTrace();
    }

    public static void handleException(Exception ex, Model model) {

        Notificaciones notas = Notificaciones.crearMsg(Notifica.ERROR, MENSAJE_ERROR_GRAL, model);
        // solo para este caso se necesita ver el error que se genera ya que se desconce su origen
        logger.error(ex.getMessage());
        ex.printStackTrace();
    }

    public static String exceptionOnStringMedium(Exception e) {
        String error = "";
        try {
            String[] ss = ExceptionUtils.getRootCauseStackTrace(e);
            error = StringUtils.join(ss, ", ");
            if (error != null && !error.isEmpty()) {
                if (error.length() > 500) {
                    error = error.substring(0, 500);
                }
            }
        } catch (Exception ex) {
            logger.debug("Error exceptionOnStringMedium", ex);
        }
        return error;
    }
}
