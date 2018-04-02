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

public class GSMCharsetTest extends TestCharset<GSMEncoder, GSMDecoder> {

    @Rule
    public CharsetTestRule charsetRule = new CharsetTestRule(new CharsetTestRule.Supplier() {
        @Override
        public Charset get() {
            return new GSMCharset();
        }
    });

    @Override
    protected Charset getCharset() {
        return charsetRule.getCharset();
    }

    @Test
    public void testCheckContains() {
        checkContainsSuccessful(getCharset());
        checkContainsFailing();
    }

    @Test
    public void testAbilityToEncode() {
        checkCanEncodeSuccess();
    }

    @Test
    public void testDecode() {
        checkDecode(EncodedText.GSM);
        checkDecode(EncodedText.GSM_SINGLE);
        checkDecode(EncodedText.GSM_SINGLE_EXT);
    }

    @Test
    public void testEncode() {
        checkEncode(EncodedText.GSM);
        checkEncode(EncodedText.GSM_SINGLE);
        checkEncode(EncodedText.GSM_SINGLE_EXT);
    }

    @Test
    public void testEncoderType() {
        checkNewEncoder(GSMEncoder.class);
    }

    @Test
    public void testDecoderType() {
        checkNewDecoder(GSMDecoder.class);
    }
}
