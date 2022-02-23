import java.util.Base64;
import java.util.Objects;

public class CloneBase64 {

    private CloneBase64() {}

    public static CloneEncoder getEncoder(){
        return CloneEncoder.RFC4648;
    }

    public static CloneEncoder getUrlEncoder(){
        return CloneEncoder.RFC4648_URLSAFE;
    }

    public static CloneEncoder getMimeEncoder(){
        return CloneEncoder.RFC2045;
    }

    public static CloneEncoder getMimeEncoder(int lineLength, byte[] lineSeparator){
        Objects.requireNonNull(lineSeparator);
        int[] base64 = CloneDecoder.fromBase64;
        for(byte b : lineSeparator){
            if(base64[b & 0xff] != -1){
                throw new IllegalArgumentException(
                    "Illegal base64 line separator 0x" + Integer.toString(b, 16)
                );
            }
        }
        if(lineLength <= 0){
            return CloneEncoder.RFC4648;
        }
        return new CloneEncoder(false, lineSeparator, lineLength >> 2 << 2, true);
    }

}
