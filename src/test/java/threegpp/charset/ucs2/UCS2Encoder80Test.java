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
import threegpp.charset.junit.EncoderTestRule;
import threegpp.charset.junit.TestEncoder;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

import static org.junit.Assert.assertTrue;
import static threegpp.charset.Common.encodeAtOnce;

public class UCS2Encoder80Test extends TestEncoder {

    @Rule
    public EncoderTestRule encoderRule = new EncoderTestRule(new UCS2Charset80());

    @Override
    protected CharsetEncoder getEncoder() {
        return encoderRule.getEncoder();
    }

    @Test
    public void testEncodeAtOnce() throws CharacterCodingException {
        tryEncodeWith(EncodedText.UCS2_80);
    }

    @Test
    public void testAbilityToEncode() {
        assertTrue(getEncoder().canEncode(EncodedText.UCS2_80.getText()));
        // still can not imagine case when this encoding will unable to proceed with some text
    }

    @Test
    public void testPartialEncoding() {
        tryEncodeByPartsWith(EncodedText.UCS2_80);
    }

    @Test
    public void testEncoderReusability() throws CharacterCodingException {
        tryToRepeatEncodingReusingEncoder(EncodedText.UCS2_80);
    }

    @Test
    public void testSingleChar() throws CharacterCodingException {
        tryEncodeWith(EncodedText.UCS2_80_SINGLE);
    }

    @Test
    public void testZeroLengthInput() throws CharacterCodingException {
        String source = "";
        encodeAtOnce(getEncoder(), source, source.length());
    }
}
