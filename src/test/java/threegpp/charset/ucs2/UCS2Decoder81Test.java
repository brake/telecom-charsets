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
import threegpp.charset.junit.DecoderTestRule;
import threegpp.charset.EncodedText;
import threegpp.charset.junit.TestDecoder;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

import static org.junit.Assert.assertEquals;
import static threegpp.charset.Common.decodeAtOnce;

public class UCS2Decoder81Test extends TestDecoder {

    @Rule
    public DecoderTestRule decoderRule = new DecoderTestRule(new UCS2Charset81());

    @Override
    protected CharsetDecoder getDecoder() {
        return decoderRule.getDecoder();
    }

    @Test
    public void testDecodeAtOnce() throws CharacterCodingException {
        tryDecodeWith(EncodedText.UCS2_81);
    }

    @Test(expected = CharacterCodingException.class)
    public void testAbilityToDecode() throws CharacterCodingException {
        ByteBuffer in = ByteBuffer.wrap(EncodedText.UCS2_INVALID.getBytes());
        decode(in);
    }

    @Test
    public void testPartialDecoding() {
        tryDecodeByPartsWith(EncodedText.UCS2_81);
    }

    @Test
    public void testDecoderReusability() throws CharacterCodingException {
        tryToRepeatDecodingReusingDecoder(EncodedText.UCS2_81);
    }

    @Test
    public void testSingleCharEncodedAsBytes() throws CharacterCodingException {
        tryDecodeWith(EncodedText.UCS2_81_SINGLE);
    }

    @Test(expected = MalformedInputException.class)
    public void testSingleCharWithInvalidLengthField() throws CharacterCodingException {
        tryDecodeWith(EncodedText.UCS2_81_SINGLE_INVALID_LENGTH);
    }

    @Test
    public void testZeroLengthInput() throws CharacterCodingException {
        byte[] source = {};
        String desiredResult = "";

        String result = decodeAtOnce(getDecoder(), source, desiredResult.length());
        assertEquals(desiredResult, result);
    }
}
