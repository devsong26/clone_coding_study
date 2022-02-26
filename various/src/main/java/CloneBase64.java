import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    public static class CloneDecoder {
        private final boolean isURL;
        private final boolean isMIME;

        private CloneDecoder(boolean isURL, boolean isMIME){
            this.isURL = isURL;
            this.isMIME = isMIME;
        }

        private static final int[] fromBase64 = new int[256];
        static {
            Arrays.fill(fromBase64, -1);
            for(int i=0; i<CloneEncoder.toBase64.length; i++){
                fromBase64[CloneEncoder.toBase64[i]] = i;
            }
            fromBase64['='] = -2;
        }

        private static final int[] fromBase64URL = new int[256];

        static{
            Arrays.fill(fromBase64, -1);
            for(int i=0; i<CloneEncoder.toBase64URL.length; i++){
                fromBase64URL[CloneEncoder.toBase64URL[i]] = i;
            }
            fromBase64URL['='] = -2;
        }

        static final CloneDecoder RFC4648 = new CloneDecoder(false, false);
        static final CloneDecoder RFC4648_URLSAFE = new CloneDecoder(true, false);
        static final CloneDecoder RFC2045 = new CloneDecoder(false, true);

        public byte[] decode(byte[] src){
            byte[] dst = new byte[outLength(src, 0, src.length)];
            int ret = decode0(src, 0, src.length, dst);
            if(ret != dst.length){
                dst = Arrays.copyOf(dst, ret);
            }
            return dst;
        }

        public byte[] decode(String src){
            return decode(src.getBytes(StandardCharsets.ISO_8859_1));
        }

        public int decode(byte[] src, byte[] dst){
            int len = outLength(src, 0, src.length);
            if(dst.length < len){
                throw new IllegalArgumentException(
                    "Output byte array is too small for decoding all input bytes");
            }
            return decode0(src, 0, src.length, dst);
        }

        public ByteBuffer decode(ByteBuffer buffer){
            int pos0 = buffer.position();
            try{
                byte[] src;
                int sp, sl;
                if(buffer.hasArray()){
                    src = buffer.array();
                    sp = buffer.arrayOffset() + buffer.position();
                    sl = buffer.arrayOffset() + buffer.limit();
                    buffer.position(buffer.limit());
                }else{
                    src = new byte[buffer.remaining()];
                    buffer.get(src);
                    sp = 0;
                    sl = src.length;
                }
                byte[] dst = new byte[outLength(src, sp, sl)];
                return ByteBuffer.wrap(dst, 0, decode0(src, sp, sl, dst));
            } catch (IllegalArgumentException iae){
                buffer.position(pos0);
                throw iae;
            }
        }

        public InputStream wrap(InputStream is){
            Objects.requireNonNull(is);
            return new CloneDecInputStream(is, isURL ? fromBase64URL : fromBase64, isMIME);
        }

        private int outLength(byte[] src, int sp, int sl){
            int[] base64 = isURL ? fromBase64URL : fromBase64;
            int paddings = 0;
            int len = sl - sp;

            if(len == 0){
                return 0;
            }

            if(len < 2){
                if(isMIME && base64[0] == -1){
                    return 0;
                }
                throw new IllegalArgumentException(
                    "input byte[] should at least have 2 bytes for base64 bytes");
            }

            if(isMIME){
                //scan all bytes to fill out all non-alphabet. a performance
                // trade-off off pre-scan on Arrays.copyOf

                int n = 0;
                while(sp < sl){
                    int b = src[sp++] & 0xff;
                    if(b == '='){
                        len -= (sl - sp + 1);
                        break;
                    }

                    if((b = base64[b]) == -1){
                        n++;
                    }
                }
                len -= n;
            }else{
                if(src[sl - 1] == '='){
                    paddings++;
                    if(src[sl - 2] == '='){
                        paddings++;
                    }
                }
            }
            if(paddings == 0 && (len & 0x3) != 0){
                paddings = 4 - (len & 0x3);
            }
            return 3 * ((len + 3) / 4) - paddings;
        }

        private int decode0(byte[] src, int sp, int sl, byte[] dst){
            int[] base64 = isURL ? fromBase64URL : fromBase64;
            int dp = 0;
            int bits = 0;
            int shiftto = 18;       // pos of first byte of 4-byte atom
            while(sp < sl){
                int b = src[sp++] & 0xff;
                if((b = base64[b]) < 0){
                    if(b == -2){        // padding byte '='
                        // =    shiftto==18 unnecessary padding
                        // x=   shiftto==12 a dangling single x
                        // x    to be handled together with non-padding case
                        // xx=  shiftto==6&sp==sl missing last =
                        // xx=y shiftto==6 last is not =
                        if(shiftto == 6 && (sp == sl || src[sp++] != '=') ||
                           shiftto == 18){
                            throw new IllegalArgumentException(
                                "Input byte array has wrong 4-byte ending unit");
                        }
                        break;
                    }
                    if(isMIME){
                        continue;
                    }else{
                        throw new IllegalArgumentException(
                            "Illegal base64 character " +
                            Integer.toString(src[sp - 1], 16)
                        );
                    }
                }
                bits |= (b << shiftto);
                shiftto -= 6;
                if(shiftto < 0){
                    dst[dp++] = (byte)(bits >> 16);
                    dst[dp++] = (byte)(bits >> 8);
                    dst[dp++] = (byte)(bits);
                    shiftto = 18;
                    bits = 0;
                }
            }

            if(shiftto == 6){
                dst[dp++] = (byte)(bits >> 16);
            }else if(shiftto == 0){
                dst[dp++] = (byte)(bits >> 16);
                dst[dp++] = (byte)(bits >> 8);
            }else if(shiftto == 12){
                // dangling single "x", incorrectly encoded.
                throw new IllegalArgumentException(
                    "Last unit does not have enough valid bits");
            }

            //anything left is invalid, if is not MIME.
            // if MIME, ignore all non-base64 character
            while(sp < sl){
                if(isMIME && base64[src[sp++]] < 0){
                    continue;
                }
                throw new IllegalArgumentException(
                    "Input byte array has incorrect ending byte at " + sp
                );
            }
            return dp;
        }
    }

}
