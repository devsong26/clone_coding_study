import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
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

    public static CloneDecoder getDecoder(){
        return CloneDecoder.RFC4648;
    }

    public static CloneDecoder getUrlDecoder(){
        return CloneDecoder.RFC4648_URLSAFE;
    }

    public static CloneDecoder getMimeDecoder(){
        return CloneDecoder.RFC2045;
    }

    public static class CloneEncoder {

        private final byte[] newline;
        private final int linemax;
        private final boolean isURL;
        private final boolean doPadding;

        private CloneEncoder(boolean isURL, byte[] newline, int linemax, boolean doPadding){
            this.isURL = isURL;
            this.newline = newline;
            this.linemax = linemax;
            this.doPadding = doPadding;
        }

        private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };

        private static final char[] toBase64URL = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
        };

        private static final int MIMELINEMAX = 76;
        private static final byte[] CRLF = new byte[]{'\r', '\n'};

        static final CloneEncoder RFC4648 = new CloneEncoder(false, null, -1, true);
        static final CloneEncoder RFC4648_URLSAFE = new CloneEncoder(true, null, -1, true);
        static final CloneEncoder RFC2045 = new CloneEncoder(false, CRLF, MIMELINEMAX, true);

        private final int outLength(int srclen){
            int len = 0;
            if (doPadding) {
                len = 4 * ((srclen + 2) / 3);
            }else{
                int n = srclen % 3;
                len = 4 * (srclen / 3) + (n == 0 ? 0 : n + 1);
            }

            if(linemax > 0){
                len += (len - 1) / linemax * newline.length;
            }
            return len;
        }

        public byte[] encode(byte[] src){
            int len = outLength(src.length);
            byte[] dst = new byte[len];
            int ret = encode0(src, 0, src.length, dst);
            if(ret != dst.length){
                return Arrays.copyOf(dst, ret);
            }

            return dst;
        }

        public int encode(byte[] src, byte[] dst){
            int len = outLength(src.length);
            if(dst.length < len){
                throw new IllegalArgumentException(
                    "Output byte array is too small for encoding all input bytes");
            }
            return encode0(src, 0, src.length, dst);
        }

        @SuppressWarnings("deprecation")
        public String encodeToString(byte[] src){
            byte[] encoded = encode(src);
            return new String(encoded, 0, 0, encoded.length);
        }

        public ByteBuffer encode(ByteBuffer buffer){
            int len = outLength(buffer.remaining());
            byte[] dst = new byte[len];
            int ret = 0;
            if(buffer.hasArray()){
                ret = encode0(buffer.array(),
                            buffer.arrayOffset() + buffer.position(),
                            buffer.arrayOffset() + buffer.limit(),
                            dst);
                buffer.position(buffer.limit());
            }else{
                byte[] src = new byte[buffer.remaining()];
                buffer.get(src);
                ret = encode0(src, 0, src.length, dst);
            }

            if(ret != dst.length){
                dst = Arrays.copyOf(dst, ret);
            }

            return ByteBuffer.wrap(dst);
        }

        public OutputStream wrap(OutputStream os){
            Objects.requireNonNull(os);
            return new CloneEncOutputStream(os, isURL ? toBase64URL : toBase64,
                    newline, linemax, doPadding);
        }

        public CloneEncoder withoutPadding(){
            if(!doPadding){
                return this;
            }
            return new CloneEncoder(isURL, newline, linemax, false);
        }

        private int encode0(byte[] src, int off, int end, byte[] dst){
            char[] base64 = isURL ? toBase64URL : toBase64;
            int sp = off;
            int slen = (end - off) / 3 * 3;
            int sl = off + slen;
            if(linemax > 0 && slen > linemax / 4 * 3){
                slen = linemax / 4 * 3;
            }

            int dp = 0;

            while(sp < sl){
                int sl0 = Math.min(sp + slen, sl);
                for(int sp0 = sp, dp0 = dp; sp0 < sl0; ){
                    int bits = (src[sp0++] & 0xff) << 16 |
                               (src[sp0++] & 0xff) <<  8 |
                               (src[sp0++] & 0xff);
                    dst[dp0++] = (byte)base64[(bits >>> 18) & 0x3f];
                    dst[dp0++] = (byte)base64[(bits >>> 12) & 0x3f];
                    dst[dp0++] = (byte)base64[bits & 0x3f];
                }
                int dlen = (sl0 - sp) / 3 * 4;
                dp += dlen;
                sp = sl0;
                if(dlen == linemax && sp < end){
                    for(byte b : newline){
                        dst[dp++] = b;
                    }
                }
            }
            if(sp < end) {
                int b0 = src[sp++] & 0xff;
                dst[dp++] = (byte)base64[b0 >> 2];
                if (sp == end){
                    dst[dp++] = (byte)base64[(b0 << 4) & 0x3f];
                    if(doPadding){
                        dst[dp++] = '=';
                        dst[dp++] = '=';
                    }
                }else{
                    int b1 = src[sp++] & 0xff;
                    dst[dp++] = (byte)base64[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[dp++] = (byte)base64[(b1 << 2) & 0x3f];
                    if(doPadding){
                        dst[dp++] = '=';
                    }
                }
            }
            return dp;
        }

    }

}
