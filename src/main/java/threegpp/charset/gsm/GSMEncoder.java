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

import threegpp.charset.Util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class GSMEncoder extends CharsetEncoder {
    private static final float AVG_BYTES_PER_CHAR = 1.0f;
    private static final float MAX_BYTES_PER_CHAR = 2.0f;

    private int nextByteToSave = -1;

    protected GSMEncoder(GSMCharset cs) {
        super(cs, AVG_BYTES_PER_CHAR, MAX_BYTES_PER_CHAR);
    }

    @Override
    protected CoderResult implFlush(ByteBuffer out) {
        if(alreadyHaveNextByte()) {
            if(!out.hasRemaining()) {
                return CoderResult.OVERFLOW;
            }
            saveNextByte(out);
        }
        return CoderResult.UNDERFLOW;
    }

    @Override
    protected void implReset() {
        nextByteToSave = -1;
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        while(in.hasRemaining()) {
            if(!out.hasRemaining()) {
                return CoderResult.OVERFLOW;
            }
            if(alreadyHaveNextByte()) {
                saveNextByte(out);
            } else {
                int code = char2GSMCode(in.get());
                if (code == -1) {
                    return CoderResult.unmappableForLength(in.position());
                }
                saveNextByte(code, out);
            }
        }
        return CoderResult.UNDERFLOW;
    }

    @Override
    public boolean canEncode(char c) {
        return char2GSMCode(c) != -1;
    }

    @Override
    public boolean canEncode(CharSequence cs) {
        for(int i = 0; i < cs.length(); i++) {
            if(!canEncode(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Try to directly map char to GSM code or GSM Extended code. When GSM Extended code mapping occur most significant
     * bit of result is set as a flag.
     * @param ch integer representation of char to examine
     * @return GSM character code or -1 in case of unmappable character
     */
    private int char2GSMCode(int ch) {
        if(ch == GSMCharset.ESCAPE_PLACE_CHAR) {
            return -1;
        }
        int result = GSMCharset.GSM_CHARACTERS.indexOf(ch);
        if(result == -1) {
            result = GSMCharset.GSM_EXT_CHARACTERS.indexOf(ch);
            if(result != -1 ) {
                result = setEscapeFlag(result);
            }
        }
        return result;
    }

    private static int setEscapeFlag(int code) {
        return code | Util.MOST_SIGNIFICANT_BIT;
    }

    private static int clearEscapeFlag(int code) {
        return code & ~Util.MOST_SIGNIFICANT_BIT;
    }

    private static boolean shouldBeEscaped(int code) {
        return (code & Util.MOST_SIGNIFICANT_BIT) != 0;
    }

    private boolean alreadyHaveNextByte() {
        return nextByteToSave != -1;
    }

    private void saveNextByte(ByteBuffer out) {
        out.put((byte)nextByteToSave);
        nextByteToSave = -1;
    }

    private void saveNextByte(int code, ByteBuffer out) {
        if (shouldBeEscaped(code)) {
            nextByteToSave = clearEscapeFlag(code);
            out.put(GSMCharset.GSM_EXTENDED_ESCAPE);
        } else {
            out.put((byte) code);
        }
    }
}
