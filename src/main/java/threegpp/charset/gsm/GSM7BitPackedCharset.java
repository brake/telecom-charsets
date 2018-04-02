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
 * A {@link Charset} based on GSM 7 bit charset with base packing as described in ETSI TS 123 038 (6.1.2.1 SMS Packing)
 * <p>
 * This {@link Charset} supports encoding and decoding.
 * <p>
 * Text charset name: X-GSM7BIT-PACKED
 * Name aliases: GSM-PACKED, GSM-7BIT-PACKED, GSM7BP
 */
public class GSM7BitPackedCharset extends Charset {

    private static final String CANONICAL_NAME = "X-GSM7BIT-PACKED";
    private static final String [] ALIASES = {"GSM-PACKED", "GSM-7BIT-PACKED", "GSM7BP"};
    static final GSMCharset underlyingCharset = new GSMCharset();


    public GSM7BitPackedCharset() {
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
        return super.canEncode();
    }

    @Override
    public boolean contains(Charset cs) {
        return cs instanceof GSM7BitPackedCharset || cs instanceof GSMCharset;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new GSM7BitPackedDecoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new GSM7BitPackedEncoder(this);
    }
}
