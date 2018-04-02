/*
 * Copyright © 2017-2018 Constantin Roganov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package threegpp.charset.gsm;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class GSMDecoder extends CharsetDecoder {
    private static final float AVG_CHARS_PER_BYTE = 1.0f;
    private static final float MAX_CHARS_PER_BYTE = 1.0f;

    private boolean nextCharIsExtended = false;

    GSMDecoder(GSMCharset cs) {
        super(cs, AVG_CHARS_PER_BYTE, MAX_CHARS_PER_BYTE);
    }

    @Override
    protected void implReset() {
        nextCharIsExtended = false;
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        while(in.hasRemaining()) {
            if(!out.hasRemaining()) {
                return CoderResult.OVERFLOW;
            }
            int code = in.get();
            if(!nextCharIsExtended && isExtendedMarker(code)) {
                nextCharIsExtended = true;
            } else {
                char decoded = GSMCode2Char(code, nextCharIsExtended);
                if(decoded == GSMCharset.INVALID_CHAR) {
                    return CoderResult.unmappableForLength(in.position());
                }
                out.put(decoded);
                nextCharIsExtended = false;
            }
        }
        return CoderResult.UNDERFLOW;
    }

    private static char GSMCode2Char(int code, boolean ext) {
        try {
            if (ext) {
                return GSMCharset.GSM_EXT_CHARACTERS.charAt(code);

            } else {
                return GSMCharset.GSM_CHARACTERS.charAt(code);
            }
        } catch (IndexOutOfBoundsException whatever) {
            return GSMCharset.INVALID_CHAR;
        }
    }

    private static boolean isExtendedMarker(int code) {
        return code == GSMCharset.GSM_EXTENDED_ESCAPE;
    }
}
