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

package threegpp.charset.ucs2;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import static threegpp.charset.Util.keepUnsigned;

public class UCS2Decoder80 extends CharsetDecoder {

    private static final float AVG_CHARS_PER_BYTE = 0.5f;
    private static final float MAX_CHARS_PER_BYTE = 1.0f;

    private boolean inInitialState = true;
    private CharsetDecoder decoder = UCS2Charset80.underlyingCharset.newDecoder();
    private InputStorage inputStorage;

    UCS2Decoder80(UCS2Charset80 cs) {
        super(cs, AVG_CHARS_PER_BYTE, MAX_CHARS_PER_BYTE);
    }

    @Override
    protected CoderResult implFlush(CharBuffer out) {
        if (directDecodingInProgress()) {
            return finalizeAndFlushDecoder(out);
        }
        if (!inputStorage.isClosedForInput()) {
            inputStorage.closeForInput();
        }
        if (!inputStorage.buffer().hasRemaining()) {
            return finalizeAndFlushDecoder(out);
        }
        return decoder.decode(inputStorage.buffer(), out, false);
    }

    @Override
    protected void implReset() {
        decoder.reset();
        inInitialState = true;
        inputStorage = null;
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if(inInitialState) {
            if(!isTagValid(in.get())) {
                return CoderResult.malformedForLength(in.position());
            }
            inInitialState = false;
        }
        if (directDecodingInProgress() && isCurrentInputCorrect(in)) {
            return decoder.decode(in, out, false);
        }
        initInputStorageIfEmpty(in.remaining());
        collectBytes(in);

        return CoderResult.UNDERFLOW;
    }

    private void finalizeDecoder() {
        decoder.decode(ByteBuffer.allocate(0), CharBuffer.allocate(0), true);
    }

    private static boolean isTagValid(byte tag) {
        return keepUnsigned(tag) == UCS2Charset80.CHARSET_TAG;
    }

    /**
     * There are two kinds of decoding:
     * 1. Direct (no intermediate data saved). Occurs when even length input data supplied.
     * 2. Decoding with collecting all available input data. Occurs when odd length data supplied. That data can break
     *    underlying decoder.
     */
    private boolean directDecodingInProgress() {
        return null == inputStorage;
    }

    /**
     * For underlying charset decoder input is correct only when its length is even.
     */
    private static boolean isCurrentInputCorrect(ByteBuffer in) {
        return in.remaining() % 2 == 0;
    }

    private void collectBytes(ByteBuffer in) {
        if (in.hasArray()) {
            collectBytesFromArrayBackedBuffer(in);
        } else {
            collectBytesFromBuffer(in);
        }
    }

    private void collectBytesFromArrayBackedBuffer(ByteBuffer in) {
        inputStorage.write(in.array(), in.position(), in.remaining());
        in.position(in.limit());
    }

    private void collectBytesFromBuffer(ByteBuffer in) {
        while(in.hasRemaining()) {
            inputStorage.write(in.get());
        }
    }

    private void initInputStorageIfEmpty(int initialSize) {
        if (null == inputStorage) {
            inputStorage = new InputStorage(initialSize);
        }
    }

    private CoderResult finalizeAndFlushDecoder(CharBuffer out) {
        finalizeDecoder();
        return decoder.flush(out);
    }

    private static class InputStorage {
        private ByteArrayOutputStream storage;
        private ByteBuffer result;

        InputStorage(int initialSize) {
            storage = new ByteArrayOutputStream(initialSize);
        }

        void write(int b) {
            if (isClosedForInput()) {
                throw new IllegalStateException();
            }
            storage.write(b);
        }

        void write(byte[] b, int off, int len) {
            if (isClosedForInput()) {
                throw new IllegalStateException();
            }
            storage.write(b, off, len);
        }

        void closeForInput() {
            if (isClosedForInput()) {
                throw new IllegalStateException();
            }
            result = ByteBuffer.wrap(storage.toByteArray());
        }

        boolean isClosedForInput() {
            return null != result;
        }

        ByteBuffer buffer() {
            if (!isClosedForInput()) {
                throw new IllegalStateException();
            }
            return result;
        }
    }

}
