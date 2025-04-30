package apptive.devlog.common.response.error.exception;

public class DtoBindingFailedException extends RuntimeException {

    public DtoBindingFailedException(String message) {
        super(message);
    }

    public DtoBindingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
