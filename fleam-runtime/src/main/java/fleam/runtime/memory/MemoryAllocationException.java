package fleam.runtime.memory;

public class MemoryAllocationException extends Exception {
    private static final long serialVersionUID = 5942828925034361667L;

    public MemoryAllocationException() {
        super();
    }

    public MemoryAllocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemoryAllocationException(String message) {
        super(message);
    }

    public MemoryAllocationException(Throwable cause) {
        super(cause);
    }
}
