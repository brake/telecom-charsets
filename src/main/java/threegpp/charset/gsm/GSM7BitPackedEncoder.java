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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.NoSuchElementException;

import static threegpp.charset.Util.keepUnsigned;

public class GSM7BitPackedEncoder extends CharsetEncoder {
    private static final float AVERAGE_BYTES_PER_CHAR = 0.875f;
    private static final float MAX_BYTES_PER_CHAR = 2.0f;

    private CharsetEncoder encoder;
    private ByteSaver byteSaver = null;
    private BytePacker bytePacker = null;

    GSM7BitPackedEncoder(GSM7BitPackedCharset cs) {
        super(cs, AVERAGE_BYTES_PER_CHAR, MAX_BYTES_PER_CHAR);
        encoder = GSM7BitPackedCharset.underlyingCharset.newEncoder();
    }

    /**
     * Do actual packing and put result data to output buffer.
     */
    @Override
    protected CoderResult implFlush(ByteBuffer out) {
        if(null == bytePacker) {
            CoderResult result = byteSaver.flush(encoder);

            if (!result.isUnderflow()) {
                return result;
            }
            bytePacker = new BytePacker(byteSaver.iterator());
        }
        return bytePacker.packBytes(out);
    }

    @Override
    protected void implReset() {
        encoder.reset();
        byteSaver = null;
        bytePacker = null;
    }

    /**
     * Here we can only encode chars to GSM codes and can't return data back to caller.
     * All packing will be done on flush because we're sure input data are over.
     */
    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        if(null == byteSaver) {
            byteSaver = new ByteSaver(in.remaining());
        }
        return byteSaver.encode(in, encoder);
    }

    @Override
    public boolean canEncode(char c) {
        return encoder.canEncode(c);
    }

    @Override
    public boolean canEncode(CharSequence cs) {
        return encoder.canEncode(cs);
    }

    private static class ByteSaver {
        private static final float INCREASE_THRESHOLD = 1.1f;

        private ByteStore store;

        ByteSaver(int size) {
            store = new ByteStore(withThreshold(size));
        }

        ShiftedIntPairIterator iterator() {
            return new ShiftedIntPairIterator(new IntIterator(store.getResultArray()));
        }

        CoderResult encode(CharBuffer in, CharsetEncoder encoder) {
            CoderResult result = encoder.encode(in, store.asByteBuffer(), false);
            store.flush();

            while(result.isOverflow()) {
                result = encoder.encode(in, store.asByteBuffer(), false);
                store.flush();
            }
            return result;
        }

        CoderResult flush(CharsetEncoder encoder) {
            CoderResult result = encoder.encode(CharBuffer.wrap(""), store.asByteBuffer(), true);
            store.flush();

            if (!result.isUnderflow()) {
                return result;
            }
            result = encoder.flush(store.asByteBuffer());
            store.flush();

            while(result.isOverflow()) {
                result = encoder.flush(store.asByteBuffer());
                store.flush();
            }
            return result;
        }

        private static int withThreshold(int val) {
            return (int)(val * INCREASE_THRESHOLD);
        }
    }

    private static class ByteStore {
        private ByteArrayOutputStream store;
        private byte [] bufferBackend;
        private ByteBuffer buffer;

        ByteStore(int size) {
            store = new ByteArrayOutputStream(size);
            bufferBackend = new byte[size];
            buffer = ByteBuffer.wrap(bufferBackend);
        }

        ByteBuffer asByteBuffer() {
            return buffer;
        }

        void flush() {
            store.write(bufferBackend, 0, buffer.position());
            buffer.rewind();
        }

        byte[] getResultArray() {
            return store.toByteArray();
        }
    }

    private static class BytePacker {

        private ShiftedIntPairIterator iterator;
        private EncodingCycleStepsIterator stepsIterator = new EncodingCycleStepsIterator();
        private EncodingStepParameters currentStep;

        BytePacker(ShiftedIntPairIterator iterator) {
            this.iterator = iterator;
        }

        CoderResult packBytes(ByteBuffer out) {
            while(iterator.hasNext()) {
                if(!out.hasRemaining()) {
                    return CoderResult.OVERFLOW;
                }
                IntPair pair = iterator.next();
                currentStep = stepsIterator.next();
                if(currentStep.shouldBeDone()) {
                    out.put(pack(pair, currentStep));
                }
            }
            return CoderResult.UNDERFLOW;
        }

        int getNumberOfUsedBitsInLastByte() {
            return currentStep.getLeftShift();
        }

        private byte pack(IntPair bytes, EncodingStepParameters parameters) {
            int half1 = bytes.getFirst() >> parameters.getRightShift();
            int half2 = bytes.getSecond() << parameters.getLeftShift();

            return (byte)(keepUnsigned(half1 | half2));
        }
    }

    private static class IntPair {
        private int first;
        private int second;

        IntPair(int f, int s) {
            first = f;
            second = s;
        }

        int getFirst() {
            return first;
        }

        int getSecond() {
            return second;
        }
    }

    private static class ShiftedIntPairIterator {
        private IntIterator iterator;
        private int first;
        private int second;
        private boolean initial = true;
        private boolean stop = false;

        ShiftedIntPairIterator(IntIterator iterator) {
            this.iterator = iterator;
        }

        boolean hasNext() {
            return !stop;
        }

        IntPair next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (initial) {
                first = iterator.next();
                initial = false;
            } else {
                first = second;
            }
            if (iterator.hasNext()) {
                second = iterator.next();
            } else {
                second = 0;
                stop = true;
            }
            return new IntPair(first, second);
        }
    }

    private static class EncodingStepParameters {
        private final int SKIP_STEP_NUM = 7;

        int rightShift;
        int leftShift;

        EncodingStepParameters(int rShift, int lShift) {
            rightShift = rShift;
            leftShift = lShift;
        }

        int getRightShift() {
            return rightShift;
        }

        int getLeftShift() {
            return leftShift;
        }

        boolean shouldBeDone() {
            return rightShift != SKIP_STEP_NUM;
        }
    }

    private static class EncodingCycleStepsIterator {
        private final int[] rShifts = {0, 1, 2, 3, 4, 5, 6, 7};
        private final int[] lShifts = {7, 6, 5, 4, 3, 2, 1, 0};

        int pos = 0;

        EncodingStepParameters next() {
            if (pos == rShifts.length) {
                pos = 0;
            }
            return new EncodingStepParameters(rShifts[pos], lShifts[pos++]);
        }
    }

    private static class IntIterator {
        private byte [] bytes;
        private int pos = 0;

        IntIterator(final byte [] bytes) {
            if (null == bytes) {
                throw new IllegalArgumentException();
            }
            this.bytes = bytes;
        }

        boolean hasNext() {
            return bytes.length != 0 && pos != bytes.length;
       }

        int next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            return (int)bytes[pos++];
        }
    }
}
