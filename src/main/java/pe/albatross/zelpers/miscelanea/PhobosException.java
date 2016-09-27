package pe.albatross.zelpers.miscelanea;

public class PhobosException extends RuntimeException {

    public PhobosException() {
    }

    public PhobosException(String msg) {
        super(msg);
    }

    public PhobosException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
