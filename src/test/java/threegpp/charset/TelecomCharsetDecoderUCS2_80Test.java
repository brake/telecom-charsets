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

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import static org.junit.Assert.assertEquals;

public class TelecomCharsetDecoderUCS2_80Test extends TelecomCharsetDecoderTestBase {
    @Test
    public void testDecodeAtOnce() throws CharacterCodingException {
        tryDecodeWith(EncodedText.UCS2_80);
    }

    @Test(expected = CharacterCodingException.class)
    public void testAbilityToDecode() throws CharacterCodingException {
        ByteBuffer in = ByteBuffer.wrap(EncodedText.UCS2_INVALID.getBytes());
        decode(in);
    }

    @Test
    public void testPartialDecoding() {
        tryDecodeByPartsWith(EncodedText.UCS2_80);
    }

    @Test
    public void testDecoderReusability() throws CharacterCodingException {
        tryToRepeatDecodingReusingDecoder(EncodedText.UCS2_80);
    }

    @Test
    public void testSingleCharEncodedAsBytes() throws CharacterCodingException {
        tryDecodeWith(EncodedText.UCS2_80_SINGLE);
    }
}
