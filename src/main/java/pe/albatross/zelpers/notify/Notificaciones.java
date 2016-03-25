package pe.albatross.zelpers.notify;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class Notificaciones {

    private List<Notifica> notificaciones;
    

    public Notificaciones() {
        this.notificaciones = new ArrayList();
    }

    public void add(int tipo, String msg) {
        Notifica mensaje = new Notifica();
        mensaje.setMensaje(msg);
        mensaje.setTipoNota(tipo);
        this.notificaciones.add(mensaje);
    }

    public void add(String msg) {
        Notifica mensaje = new Notifica("", msg);
        this.notificaciones.add(mensaje);
    }

    public List<Notifica> all() {
        return notificaciones;
    }

    public static Notificaciones crearMsg(String msg, RedirectAttributes redirectAttr) {
        Notificaciones notas = new Notificaciones();
        notas.add(msg);
        redirectAttr.addFlashAttribute(Notifica.NOTIFICACIONES, notas);
        return notas;
    }

    public static Notificaciones crearMsg(String msg, Model model) {
        Notificaciones notas = new Notificaciones();
        notas.add(msg);
        model.addAttribute(Notifica.NOTIFICACIONES, notas);
        return notas;
    }

    public static Notificaciones crearMsg(int tipo, String msg, RedirectAttributes redirectAttr) {
        Notificaciones notas = new Notificaciones();
        notas.add(tipo, msg);
        redirectAttr.addFlashAttribute(Notifica.NOTIFICACIONES, notas);
        return notas;
    }

    public static Notificaciones crearMsg(int tipo, String msg, Model model) {
        Notificaciones notas = new Notificaciones();
        notas.add(tipo, msg);
        model.addAttribute(Notifica.NOTIFICACIONES, notas);
        return notas;
    }

    public static Notificaciones crearMsg(String msg) {
        Notificaciones notas = new Notificaciones();
        notas.add(msg);
        return notas;
    }

    public static Notificaciones crearMsg(int tipo, String msg) {
        Notificaciones notas = new Notificaciones();
        notas.add(tipo, msg);
        return notas;
    }
}
