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

import org.junit.Rule;
import org.junit.Test;
import threegpp.charset.EncodedText;
import threegpp.charset.junit.CharsetTestRule;
import threegpp.charset.junit.TestCharset;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GSM7BitPackedCharsetTest extends TestCharset<GSM7BitPackedEncoder, GSM7BitPackedDecoder> {

    @Rule
    public CharsetTestRule charsetRule = new CharsetTestRule(new CharsetTestRule.Supplier() {
        @Override
        public Charset get() {
            return new GSM7BitPackedCharset();
        }
    });

    @Override
    protected Charset getCharset() {
        return charsetRule.getCharset();
    }

    @Test
    public void testCheckContains() {
        List<Charset> charsets = new LinkedList<>();
        charsets.add(new GSMCharset());
        charsets.add(getCharset());

        checkContainsSuccessful(charsets);
        checkContainsFailing();
    }

    @Test
    public void testAbilityToEncode() {
        checkCanEncodeSuccess();
    }

    @Test
    public void testDecode() {
        checkDecode(EncodedText.GSM_7BP);
        checkDecode(EncodedText.GSM_7BP_AT);
        checkDecode(EncodedText.GSM_SINGLE);
        checkDecode(EncodedText.GSM_7BP_SINGLE_EXT);
    }

    @Test
    public void testEncode() {
        checkEncode(EncodedText.GSM_7BP);
        checkEncode(EncodedText.GSM_7BP_AT);
        checkEncode(EncodedText.GSM_SINGLE);
        checkEncode(EncodedText.GSM_7BP_SINGLE_EXT);
    }

    @Test
    public void testEncoderType() {
        checkNewEncoder(GSM7BitPackedEncoder.class);
    }

    @Test
    public void testDecoderType() {
        checkNewDecoder(GSM7BitPackedDecoder.class);
    }
}
