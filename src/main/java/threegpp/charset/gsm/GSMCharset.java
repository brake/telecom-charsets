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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Locale;

/**
 * A Java™ {@link Charset} implementation of GSM 7 Bit Encoding described in ETSI TS 123 038 (6.2.1, 6.2.1.1).
 * Note that there is no support of National Language Tables (6.2.1.2, Annex A).
 * <p>
 * This {@link Charset} supports encoding and decoding.
 * <p>
 * Text charset name: X-GSM7BIT
 * Name aliases: GSM, GSM7BIT
 */
public class GSMCharset extends Charset {
    private static final String  CANONICAL_NAME = "X-GSM7BIT";
    private static final String [] ALIASES = {"GSM", "GSM7BIT"};

    public static final String GSM_CHARACTERS = "@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞ\uFFFFÆæßÉ !\"#¤%&'()*+,-."
        + "/0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";

    public static final String GSM_EXT_CHARACTERS = "          \n         ^                   {}     \\"
        + "            [~] |                                    €                          ";

    public static final byte GSM_EXTENDED_ESCAPE = 0x1B;
    static final char ESCAPE_PLACE_CHAR = '\uFFFF';   // char at pos 0x1B, should throw error when occurred in input
    static final char INVALID_CHAR = ESCAPE_PLACE_CHAR;

    public GSMCharset() {
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
    public CharsetDecoder newDecoder() {
        return new GSMDecoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new GSMEncoder(this);
    }

    @Override
    public boolean contains(Charset cs) {
        return cs instanceof GSMCharset;
    }

    @Override
    public boolean canEncode() {
        return true;
    }
}
