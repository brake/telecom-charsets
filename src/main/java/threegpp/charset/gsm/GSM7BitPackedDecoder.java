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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import static threegpp.charset.Util.keepUnsigned;

public class GSM7BitPackedDecoder extends CharsetDecoder {
    private static final float AVG_CHARS_PER_BYTE = 8.0f/7.0f;
    private static final float MAX_CHARS_PER_BYTE = AVG_CHARS_PER_BYTE;

    private Unpacker7Bit unpacker;
    private ByteBuffer unpacked7Bit;
    private CharsetDecoder decoder;

    GSM7BitPackedDecoder(GSM7BitPackedCharset cs) {
        super(cs, AVG_CHARS_PER_BYTE, MAX_CHARS_PER_BYTE);
        decoder = cs.underlyingCharset.newDecoder();
    }

    @Override
    protected CoderResult implFlush(CharBuffer out) {

        if (out.hasRemaining()) {
            if (null == unpacked7Bit) {
                unpacked7Bit = unpacker.getDecodedBytes();
            }
            return decoder.decode(unpacked7Bit, out, true);
        }
        return CoderResult.OVERFLOW;
    }

    @Override
    protected void implReset() {
        decoder.reset();
        unpacked7Bit = null;
        unpacker = null;
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        while (in.hasRemaining()) {
            if (null == unpacker) {
                unpacker = new Unpacker7Bit(in.remaining());
            }
            unpacker.decode(in);
        }
        return CoderResult.UNDERFLOW;
    }

    private static class Unpacker7Bit {

        private final ByteArrayOutputStream decodedFrom7Bit;
        private final DecodingCycleStepsIterator stepsIterator = new DecodingCycleStepsIterator();
        private int tailFromPrevStep;
        private boolean tailInitialized = false;

        Unpacker7Bit(int initialSize) {
            decodedFrom7Bit = new ByteArrayOutputStream(initialSize);
        }

        void decode(ByteBuffer in) {
            while (in.hasRemaining()) {
                DecodingStepParameters stepParameters = stepsIterator.next();
                if (stepParameters.isReinitStep()) {
                    reinit();
                } else {
                    int inputByte = keepUnsigned(in.get());
                    int head = (inputByte & stepParameters.getMask()) << stepParameters.getLeftShift();
                    int result = haveTail() ? (head | tailFromPrevStep) : head;
                    decodedFrom7Bit.write(result);

                    tailFromPrevStep = inputByte >> stepParameters.getRightShift();
                    tailInitialized = true;
                }
            }
        }

        ByteBuffer getDecodedBytes() {
            if (haveTail() && tailHaveToBeKept()) {
                decodedFrom7Bit.write(tailFromPrevStep);
            }
            return ByteBuffer.wrap(decodedFrom7Bit.toByteArray());
        }

        private void reinit() {
            if (decodedFrom7Bit.size() > 0) {
                decodedFrom7Bit.write(tailFromPrevStep);
            }
            tailInitialized = false;
        }

        private boolean haveTail() {
            return tailInitialized;
        }

        private boolean tailHaveToBeKept() {
            return tailFromPrevStep != 0;
        }
    }

    private static class DecodingStepParameters {

        private static int neutralRightShift = 0;

        private final int leftShift;
        private final int rightShift;
        private final int mask;

        DecodingStepParameters(int leftShift, int rightShift, int mask) {
            this.leftShift = leftShift;
            this.rightShift = rightShift;
            this.mask = mask;
        }

        int getLeftShift() {
            return leftShift;
        }

        int getRightShift() {
            return rightShift;
        }

        int getMask() {
            return mask;
        }

        boolean isReinitStep() {
            return rightShift == neutralRightShift;
        }
    }

    private static class DecodingCycleStepsIterator {
        private static final int[] leftShifts = {7, 0, 1, 2, 3, 4, 5, 6};
        private static final int[] rightShifts = {0, 7, 6, 5, 4, 3, 2, 1};
        private static final int[] masks = {0, 0x7F, 0x3F, 0x1F, 0xF, 0x7, 0x3, 0x1};

        private int pos = 0;

        DecodingStepParameters next() {
            if (pos == leftShifts.length) {
                pos = 0;
            }
            return new DecodingStepParameters(leftShifts[pos], rightShifts[pos], masks[pos++]);
        }
    }

}
