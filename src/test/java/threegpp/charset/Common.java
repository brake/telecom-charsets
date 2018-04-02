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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Common {
    private static final int LENGTH_OF_CHUNK = 10;

    private static String[] chunks(final String source, int chunkLength) {
        int sourceLength = source.length();

        if (chunkLength >= sourceLength) {
            return new String[]{source};
        }
        int numChunks = sourceLength / chunkLength;
        if (sourceLength % chunkLength > 0) {
            numChunks++;
        }
        String[] result = new String[numChunks];

        int lastChunkIndex = numChunks - 1;

        for (int i = 0, startOffset = 0, endOffset = 0; i < numChunks; i++) {

            if (i == lastChunkIndex) {
                result[i] = source.substring(startOffset);

            } else {
                endOffset = startOffset + chunkLength;
                result[i] = source.substring(startOffset, endOffset);
            }
            startOffset = endOffset;
        }
        return result;
    }

    private static byte[][] chunks(final byte[] source, int chunkLength) {
        if (chunkLength >= source.length) {
            return new byte[][]{source};
        }

        int numChunks = source.length / chunkLength;
        if (source.length % chunkLength > 0) {
            numChunks++;
        }
        byte[][] result = new byte[numChunks][];

        int lastChunkIndex = numChunks - 1;

        for (int i = 0, startOffset = 0, endOffset = 0; i < numChunks; i++) {

            if (i == lastChunkIndex) {
                endOffset = source.length;

            } else {
                endOffset = startOffset + chunkLength;
            }
            result[i] = copyOfRange(source, startOffset, endOffset);

            startOffset = endOffset;
        }
        return result;
    }

    public static String decodeAtOnce(CharsetDecoder decoder, byte[] source, int desiredTextLength)
            throws CharacterCodingException {
        ByteBuffer in = ByteBuffer.wrap(source);
        CharBuffer result = decoder.decode(in);

        assertTrue(result.hasArray());
        assertEquals(desiredTextLength, result.limit());

        return result.rewind().toString();
    }

    public static String decodeByParts(CharsetDecoder decoder, byte[] source, int desiredTextLength) {
        byte[][] parts = chunks(source, LENGTH_OF_CHUNK);
        int lastPartIndex = parts.length - 1;

        CharBuffer resultBuffer = CharBuffer.allocate(desiredTextLength);

        for (int i = 0; i < parts.length; i++) {
            ByteBuffer in = ByteBuffer.allocate(LENGTH_OF_CHUNK).put(parts[i]);
            in.rewind().limit(parts[i].length);

            CoderResult coderResult = decoder.decode(in, resultBuffer, i == lastPartIndex);
            assertEquals(CoderResult.UNDERFLOW, coderResult);
        }
        assertEquals(CoderResult.UNDERFLOW, decoder.flush(resultBuffer));

        return resultBuffer.rewind().toString();
    }

    public static byte[] encodeByChunks(CharsetEncoder encoder, String source, int desiredResultLength) {
        String[] parts = chunks(source, LENGTH_OF_CHUNK);
        int lastPartIndex = parts.length - 1;

        ByteBuffer resultBuffer = ByteBuffer.allocate(desiredResultLength);

        for (int i = 0; i < parts.length; i++) {
            CharBuffer in = CharBuffer.wrap(parts[i]);

            CoderResult coderResult = encoder.encode(in, resultBuffer, i == lastPartIndex);
            assertEquals(CoderResult.UNDERFLOW, coderResult);
        }
        assertEquals(CoderResult.UNDERFLOW, encoder.flush(resultBuffer));

        return resultBuffer.array();
    }

    public static byte[] encodeAtOnce(CharsetEncoder encoder, String source, int desiredResultLength)
            throws CharacterCodingException {
        CharBuffer in = CharBuffer.wrap(source);
        ByteBuffer result = encoder.encode(in);

        assertTrue(result.hasArray());
        assertEquals(desiredResultLength, result.limit());

        return Arrays.copyOf(result.array(), result.limit());
    }
}
