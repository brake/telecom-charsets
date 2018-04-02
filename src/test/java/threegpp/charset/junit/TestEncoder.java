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

package threegpp.charset.junit;

import threegpp.charset.EncodedText;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

import static org.junit.Assert.assertArrayEquals;
import static threegpp.charset.Common.encodeAtOnce;
import static threegpp.charset.Common.encodeByChunks;

public abstract class TestEncoder {

    private static final int REPEATS_COUNT = 100;

    abstract protected CharsetEncoder getEncoder();

    protected ByteBuffer encode(CharBuffer in) throws CharacterCodingException {
        return getEncoder().encode(in);
    }

    protected void reset() {
        getEncoder().reset();
    }

    protected void tryEncodeWith(EncodedText encodedText) throws CharacterCodingException {
        byte [] desiredResult = encodedText.getBytes();
        byte [] result = encodeAtOnce(getEncoder(), encodedText.getText(), desiredResult.length);

        assertArrayEquals(desiredResult, result);
    }

    protected void tryEncodeByPartsWith(EncodedText encodedText) {
        byte [] desiredResult = encodedText.getBytes();

        assertArrayEquals(desiredResult, encodeByChunks(getEncoder(), encodedText.getText(), desiredResult.length));
    }

    protected void tryToRepeatEncodingReusingEncoder(EncodedText encodedText) throws CharacterCodingException {
        int i = REPEATS_COUNT;
        while (i-- > 0) {
            tryEncodeWith(encodedText);
            reset();
            tryEncodeByPartsWith(encodedText);
            reset();
        }
    }
}
