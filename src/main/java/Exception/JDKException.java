package Exception;

public class JDKException extends RuntimeException{
    static final long serialVersionUID = 7818375828146090155L;

    public JDKException(String info) {
        super(info);
    }
}
