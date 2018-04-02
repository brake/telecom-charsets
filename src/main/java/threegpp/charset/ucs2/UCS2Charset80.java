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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import static java.nio.charset.StandardCharsets.*;
import java.util.Locale;

/**
 * A {@link Charset} implementation based on ETSI TS 102 221 (Annex A, part 1, tag 0x80).
 * <p>
 * This {@link Charset} supports encoding and decoding.
 * <p>
 * Text charset name: X-UCS2-80
 * Name aliases: UCS2-80, UCS2x80
 */
public class UCS2Charset80 extends Charset {
    private static final String CANONICAL_NAME = "X-UCS2-80";
    private static final String [] ALIASES = {"UCS2-80", "UCS2x80"};

    static final Charset underlyingCharset = UTF_16LE;
    public static int CHARSET_TAG = 0x80;

    public UCS2Charset80() {
        super(CANONICAL_NAME, ALIASES);
    }

    @Override
    public String displayName() {
        return super.displayName();
    }

    @Override
    public String displayName(Locale locale) {
        return super.displayName(locale);
    }

    @Override
    public boolean canEncode() {
        return true;
    }

    @Override
    public boolean contains(Charset cs) {
        return cs instanceof UCS2Charset80 || underlyingCharset.contains(cs);
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new UCS2Decoder80(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new UCS2Encoder80(this);
    }
}
