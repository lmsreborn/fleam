package fleam.runtime.jobgraph;

import fleam.util.AbstractID;

import javax.xml.bind.DatatypeConverter;

public class JobVertexID extends AbstractID {

    private static final long serialVersionUID = 1L;

    public JobVertexID() {
        super();
    }
    public JobVertexID(byte[] bytes) {
        super(bytes);
    }

    public JobVertexID(long lowerPart, long upperPart) {
        super(lowerPart, upperPart);
    }

    public static JobVertexID fromHexString(String hexString) {
        return new JobVertexID(DatatypeConverter.parseHexBinary(hexString));
    }
}
