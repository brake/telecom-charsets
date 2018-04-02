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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * A {@link Charset} implementation which is able to decode bytes in one of encodings listed below:
 * <ul>
 *     <li>GSM 7 Bit
 *     <li>UCS2 with tag 0x80
 *     <li>UCS2 with tag 0x81
 * </ul>
 * <p>
 * This {@link Charset} supports decoding only.
 * <p>
 * Text charset name: X-GSM-UCS2
 * Name aliases: ANY-TELECOM, TELECOM, GSM-OR-UCS2
 */
public class TelecomCharset extends Charset {
    private  static final String CANONICAL_NAME = "X-GSM-UCS2";
    private static final String [] ALIASES = {"ANY-TELECOM", "TELECOM", "GSM-OR-UCS2"};

    public TelecomCharset() {
        super(CANONICAL_NAME, ALIASES);
    }

    @Override
    public boolean contains(Charset cs) {
        return cs instanceof GSMCharset || cs instanceof UCS2Charset80 ||
                cs instanceof UCS2Charset81 || cs instanceof TelecomCharset;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new TelecomCharsetDecoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canEncode() {
        return false;
    }
}
