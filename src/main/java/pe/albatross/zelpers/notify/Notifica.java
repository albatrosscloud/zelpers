package pe.albatross.zelpers.notify;

import java.io.Serializable;
import javax.servlet.http.HttpSession;

public class Notifica implements Serializable {

    private String titulo;
    private String mensaje;
    private String tipo;
    private int tipoNota;
    public static int INFO = 1;
    public static int ERROR = 2;
    public static int SUCCESS = 3;
    public static final String NOTIFICACIONES = "notificaciones";

    public Notifica() {
    }

    public Notifica(int tipo, String titulo, String mensaje) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.setTipoNota(tipo);
    }

    public Notifica(String titulo, String mensaje) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.setTipoNota(SUCCESS);
    }

    public Notifica(String mensaje) {
        this.titulo = "Mensaje de Error";
        String[] partes = mensaje.split(":::");
        if (partes.length == 1) {
            this.mensaje = mensaje;
            this.setTipoNota(SUCCESS);

        } else if (partes.length >= 2) {
            this.mensaje = partes[0];
            this.setTipoNota(Integer.valueOf(partes[1]));
        }
    }

    public static void crearMsg(HttpSession session, int tipo, String msg) {
        Notificaciones notas = (Notificaciones) session.getAttribute(NOTIFICACIONES);
        if (notas == null) {
            notas = new Notificaciones();
        }
        notas.add(tipo, msg);
        session.setAttribute(NOTIFICACIONES, notas);
    }

    public static void crearMsg(HttpSession session, String msg) {
        Notifica nota = new Notifica(msg);
        session.setAttribute(NOTIFICACIONES, nota);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public int getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(int tipoNota) {
        this.tipoNota = tipoNota;
        this.tipo = (tipoNota == SUCCESS) ? "success" : ((tipoNota == INFO) ? "info" : ((tipoNota == ERROR) ? "error" : "indefinido"));
    }
}
