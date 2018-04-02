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

import org.junit.Rule;
import org.junit.Test;
import threegpp.charset.gsm.GSMCharset;
import threegpp.charset.junit.CharsetTestRule;
import threegpp.charset.junit.TestCharset;
import threegpp.charset.ucs2.UCS2Charset80;
import threegpp.charset.ucs2.UCS2Charset81;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.LinkedList;
import java.util.List;

public class TelecomCharsetTest extends TestCharset<CharsetEncoder, TelecomCharsetDecoder> {

    @Rule
    public CharsetTestRule charsetRule = new CharsetTestRule(new CharsetTestRule.Supplier() {
        @Override
        public Charset get() {
            return new TelecomCharset();
        }
    });

    @Override
    protected Charset getCharset() {
        return charsetRule.getCharset();
    }

    @Test
    public void testAbilityToEncode() {
        checkCanEncodeFailing();
    }

    @Test
    public void testCheckContains() {
        List<Charset> charsets = new LinkedList<>();
        charsets.add(getCharset());
        charsets.add(new GSMCharset());
        charsets.add(new UCS2Charset80());
        charsets.add(new UCS2Charset81());

        checkContainsSuccessful(charsets);
        checkContainsFailing();
    }

    @Test
    public void testDecode() {
        checkDecode(EncodedText.UCS2_80);
        checkDecode(EncodedText.UCS2_80_SINGLE);

        checkDecode(EncodedText.UCS2_81);
        checkDecode(EncodedText.UCS2_81_SINGLE);

        checkDecode(EncodedText.GSM);
        checkDecode(EncodedText.GSM_SINGLE);
        checkDecode(EncodedText.GSM_SINGLE_EXT);
    }

    @Test
    public void testDecoderType() {
        checkNewDecoder(TelecomCharsetDecoder.class);
    }
}
