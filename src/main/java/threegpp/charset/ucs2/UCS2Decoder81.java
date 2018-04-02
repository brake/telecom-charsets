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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import static threegpp.charset.Util.MOST_SIGNIFICANT_BIT;
import static threegpp.charset.Util.keepUnsigned;

public class UCS2Decoder81 extends CharsetDecoder {
    private static float MAX_CHARS_PER_BYTE = 1.0f;
    private static float AVERAGE_CHARS_PER_BYTE = 1.0f;
    private static int MIN_VALID_INPUT_BUFFER_LENGTH = 4;  // 0x81 + length + half-page + at least one character
    private static int MIN_CHAR_COUNT = 1;

    private int numCharactersExpected = 0;
    private int charNum = 0;
    private int halfPagePointer = 0;

    UCS2Decoder81(UCS2Charset81 cs) {
        super(cs, AVERAGE_CHARS_PER_BYTE, MAX_CHARS_PER_BYTE);
    }

    @Override
    public CodingErrorAction malformedInputAction() {
        return CodingErrorAction.REPORT;
    }

    @Override
    public CodingErrorAction unmappableCharacterAction() {
        return CodingErrorAction.REPORT;
    }

    @Override
    protected void implReset() {
        numCharactersExpected = 0;
        charNum = 0;
        halfPagePointer = 0;
    }

    @Override
    protected CoderResult implFlush(CharBuffer out) {
        if (charNum == numCharactersExpected) {
            return CoderResult.UNDERFLOW;
        }
        return CoderResult.malformedForLength(numCharactersExpected);
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {

        if(onStartOfDecoding()) {
            if(!isInputBufferInitiallyValid(in)) {
                return CoderResult.malformedForLength(1);
            }
            setUpDecoderAdvancingBuffer(in);
        }

        while(in.hasRemaining() && charNum <= numCharactersExpected) {
            if(out.hasRemaining()) {
                out.put(inputByteToChar(in.get()));
                charNum++;
            } else {
                return CoderResult.OVERFLOW;
            }
        }
        return CoderResult.UNDERFLOW;
    }

    private static boolean isInputBufferInitiallyValid(ByteBuffer in) {
        if(in.limit() >= MIN_VALID_INPUT_BUFFER_LENGTH && in.get() == (byte) UCS2Charset81.CHARSET_TAG) {
            int length = in.get();
            in.position(in.position() - 1);

            return length >= MIN_CHAR_COUNT;
        }
        return false;
    }

    private static int getCodePageAsInt(byte codePage) {
        return (int)codePage << UCS2Charset81.CODE_PAGE_OFFSET;
    }

    private boolean isHalfPageOffsetRequired(int value) {
        return (value & MOST_SIGNIFICANT_BIT) != 0;
    }

    private int remove7thBit(int value) {
        return value ^ MOST_SIGNIFICANT_BIT;
    }

    private char makeCharWithPageOffset(int value) {
        return (char)(halfPagePointer | remove7thBit(value));
    }

    private char makeCharWithNoPage(int value) {
        return (char)value;
    }

    private char inputByteToChar(byte value) {
        int unsignedValue = keepUnsigned(value);
        return isHalfPageOffsetRequired(unsignedValue) ? makeCharWithPageOffset(unsignedValue) : makeCharWithNoPage(unsignedValue);
    }

    private boolean onStartOfDecoding() {
        return numCharactersExpected == 0;
    }

    private void setUpDecoderAdvancingBuffer(ByteBuffer in) {
        numCharactersExpected = in.get();
        halfPagePointer = getCodePageAsInt(in.get());
    }
}
