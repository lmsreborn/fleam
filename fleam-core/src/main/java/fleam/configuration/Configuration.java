package fleam.configuration;

import fleam.core.io.IOReadableWritable;
import fleam.core.memory.DataInputView;
import fleam.core.memory.DataOutputView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Configuration implements IOReadableWritable, java.io.Serializable, Cloneable {

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    protected final HashMap<String, Object> configData;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public Configuration(){
        this.configData = new HashMap<String, Object>();
    }

    public Configuration(Configuration otherConfig){
        this.configData = new HashMap<String, Object>(otherConfig.configData);
    }



    @Override
    public void write(final DataOutputView out) throws IOException{
        synchronized (this.configData){
            out.writeInt(this.configData.size());

            for (Map.Entry<String, Object> entry : this.configData.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();



            }
        }

    }

    @Override
    public void read(final DataInputView in) throws IOException{

    }

    //---------------------------------------------


    @Override
    public int hashCode() {
        int hash = 0;
        for (String s : this.configData.keySet()) {
            hash ^= s.hashCode();
        }
        return hash;
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        else if (obj instanceof Configuration) {
            Map<String, Object> otherConf = ((Configuration) obj).configData;

            for (Map.Entry<String, Object> e : this.configData.entrySet()) {
                Object thisVal = e.getValue();
                Object otherVal = otherConf.get(e.getKey());

                if (!thisVal.getClass().equals(byte[].class)) {
                    if (!thisVal.equals(otherVal)) {
                        return false;
                    }
                } else if (otherVal.getClass().equals(byte[].class)) {
                    if (!Arrays.equals((byte[]) thisVal, (byte[]) otherVal)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.configData.toString();
    }

}
