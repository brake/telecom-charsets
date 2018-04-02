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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashSet;
import java.util.Set;

import static threegpp.charset.Util.MOST_SIGNIFICANT_BIT;
import static threegpp.charset.Util.SINGLE_BYTE_BIT_MASK;

public class UCS2Encoder81 extends CharsetEncoder {
    private static final float AVERAGE_BYTES_PER_CHAR = 1.012f;
    private static final float MAX_BYTES_PER_CHAR = 4.0f;
    private static final int MAX_TEXT_LENGTH = 0xFF;
    private static final int CHAR_CODE_MASK = SINGLE_BYTE_BIT_MASK;
    private static final int INVALID_CHARACTER = -1;
    private static final int CODE_PAGE_MASK = 0x7F80;

    private int numCharacters = 0;
    private int halfPagePointer = 0;
    private boolean onStart = true;

    UCS2Encoder81(UCS2Charset81 cs) {
        super(cs, AVERAGE_BYTES_PER_CHAR, MAX_BYTES_PER_CHAR);
    }

   @Override
    protected CoderResult implFlush(ByteBuffer out) {
        out.put(Positions.TEXT_LENGTH.get(), (byte)numCharacters)
                .put(Positions.CODE_PAGE.get(), getCodePageAsByte(halfPagePointer));

        return CoderResult.UNDERFLOW;
    }

   @Override
    protected void implReset() {
        numCharacters = 0;
        halfPagePointer = 0;
        onStart = true;
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        if(in.remaining() + numCharacters > MAX_TEXT_LENGTH) {
            return CoderResult.unmappableForLength(numCharacters);
        }

        if(onStart) {
            if (haveNoSpaceForHeader(out)) {
                return CoderResult.OVERFLOW;
            }
            prepareHeader(out);
        }
        while(in.hasRemaining()) {
            int newByte = encodeCharacter((int)in.get());
            if(newByte == INVALID_CHARACTER) {
                return CoderResult.unmappableForLength(in.position());
            }
            try {
                out.put((byte)newByte);
                numCharacters++;

            } catch (BufferOverflowException whatever) {
                in.position(in.position() - 1);
                return CoderResult.OVERFLOW;
            }
        }
        return CoderResult.UNDERFLOW;
    }

    @Override
    public boolean isLegalReplacement(byte[] replacement) {
        return true;
    }

    @Override
    public boolean canEncode(char c) {
        return true;
    }

    @Override
    public boolean canEncode(CharSequence cs) {
        Set<Integer> pages = new HashSet<>();
        int length = cs.length();
        int maxCodePages = 2;
        for (int i = 0; i < length; i++) {
            pages.add(getCharHalfPage(cs.charAt(i)));
            int size = pages.size();
            if(size > maxCodePages || (size == maxCodePages && !pages.contains(0))) {
                return false;
            }
        }
        return true;
    }

    private int encodeCharacter(int intChar) {
        int charHalfPagePointer = getCharHalfPage(intChar);
        int charCode = getCharCode(intChar);

        if(charHalfPagePointer == 0) {
            return intChar;
        }

        if(halfPagePointer == 0) {
            rememberPagePointer(charHalfPagePointer);
            
        } else if(halfPagePointer != charHalfPagePointer) {
            return INVALID_CHARACTER;
        }
        return charCode;
    }

    private static int getCharHalfPage(int ch) {
        return ch & CODE_PAGE_MASK;
    }

    /**
     * Get one byte code page ID from character for storing that in a third byte of output buffer.
     * @param character integer representation of char
     * @return byte representing "code page" of supplied character
     */
    private static byte getCodePageAsByte(int character) {
        return (byte)(character >> UCS2Charset81.CODE_PAGE_OFFSET);
    }

    private static int getCharCode(int ch) {
        return setMostSignificantBit(ch & CHAR_CODE_MASK);
    }

    private void rememberPagePointer(int pagePointer)
    {
        this.halfPagePointer = pagePointer;
    }

    /**
     * Write encoding mark and reserve places for number of chars and "half page" pointer
     * @param buffer A place for saving data
     */
    private void prepareHeader(ByteBuffer buffer) {
        buffer
            .put((byte) UCS2Charset81.CHARSET_TAG)
            .position(buffer.position() + 2);

        onStart = false;
    }

    private static int setMostSignificantBit(int ch) {
        return ch | MOST_SIGNIFICANT_BIT;
    }

    private boolean haveNoSpaceForHeader(ByteBuffer buffer) {
        return buffer.remaining() < Positions.values().length + 1;
    }

    enum Positions {
        TEXT_LENGTH(1),
        CODE_PAGE(2);

        private int position;

        Positions(int position) {
            this.position = position;
        }

        public int get() {
            return position;
        }
    }
}
