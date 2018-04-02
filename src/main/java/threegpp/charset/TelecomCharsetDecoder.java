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

package threegpp.charset;

import threegpp.charset.gsm.GSMCharset;
import threegpp.charset.ucs2.UCS2Charset80;
import threegpp.charset.ucs2.UCS2Charset81;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import static threegpp.charset.Util.MOST_SIGNIFICANT_BIT;
import static threegpp.charset.Util.keepUnsigned;

public class TelecomCharsetDecoder extends CharsetDecoder {
    private static final float AVERAGE_CHARS_PER_BYTE = 0.5f;
    private static final float MAX_CHARS_PER_BYTE = 1.0f;

    private Charset currentCharset;
    private CharsetDecoder currentDecoder;

    TelecomCharsetDecoder(Charset cs) {
        super(cs, AVERAGE_CHARS_PER_BYTE, MAX_CHARS_PER_BYTE);
    }

    @Override
    protected CoderResult implFlush(CharBuffer out) {
        currentDecoder.decode(ByteBuffer.allocate(0), out, true);
        return currentDecoder.flush(out);
    }

    @Override
    protected void implReset() {
        currentCharset = null;
        currentDecoder = null;
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if (null == currentCharset) {
            if(!in.hasRemaining()) {
                return CoderResult.UNDERFLOW;
            } else {
                CoderResult cr = createDecoder(in);
                if (!cr.isUnderflow()) {
                    return cr;
                }
            }
        }
        return currentDecoder.decode(in, out, false);
    }

    @Override
    public boolean isAutoDetecting() {
        return true;
    }

    @Override
    public boolean isCharsetDetected() {
        return null != currentCharset;
    }

    @Override
    public Charset detectedCharset() {
        if (!isCharsetDetected()) {
            throw new IllegalStateException();
        }
        return currentCharset;
    }

    private static Charset detectCharset(int firstByte) {
        int unsignedByte = keepUnsigned(firstByte);
        if ((unsignedByte & MOST_SIGNIFICANT_BIT) == 0) {
            return new GSMCharset();
        } else if (unsignedByte == UCS2Charset80.CHARSET_TAG) {
            return new UCS2Charset80();
        } else if (unsignedByte == UCS2Charset81.CHARSET_TAG) {
            return new UCS2Charset81();
        }
        return null;
    }

    private CoderResult createDecoder(ByteBuffer in) {
        currentCharset = detectCharset(in.get());
        if (null == currentCharset) {
            return CoderResult.malformedForLength(in.remaining() + 1);
        }
        currentDecoder = currentCharset.newDecoder();

        in.position(in.position() - 1);

        return CoderResult.UNDERFLOW;
    }
}
