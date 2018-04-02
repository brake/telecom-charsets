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

import org.junit.Rule;
import org.junit.Test;
import threegpp.charset.EncodedText;
import threegpp.charset.junit.CharsetTestRule;
import threegpp.charset.junit.TestCharset;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class UCS2Charset81Test extends TestCharset<UCS2Encoder81, UCS2Decoder81> {

    @Rule
    public CharsetTestRule charsetRule = new CharsetTestRule(new CharsetTestRule.Supplier() {
        @Override
        public Charset get() {
            return new UCS2Charset81();
        }
    });

    @Override
    protected Charset getCharset() {
        return charsetRule.getCharset();
    }

    @Test
    public void testCheckContains() {
        List<Charset> charsets = new LinkedList<>();
        charsets.add(getCharset());
        charsets.add(StandardCharsets.UTF_16LE);
        charsets.add(StandardCharsets.US_ASCII);

        checkContainsSuccessful(charsets);
    }

    @Test
    public void testAbilityToEncode() {
        checkCanEncodeSuccess();
    }

    @Test
    public void testDecode() {
        checkDecode(EncodedText.UCS2_81);
        checkDecode(EncodedText.UCS2_81_SINGLE);
    }

    @Test
    public void testEncode() {
        checkEncode(EncodedText.UCS2_81);
        checkEncode(EncodedText.UCS2_81_SINGLE);
    }

    @Test
    public void testEncoderType() {
        checkNewEncoder(UCS2Encoder81.class);
    }

    @Test
    public void testDecoderType() {
        checkNewDecoder(UCS2Decoder81.class);
    }
}
