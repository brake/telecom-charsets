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

package threegpp.charset.junit;

import threegpp.charset.EncodedText;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public abstract class TestCharset<E extends CharsetEncoder, D extends CharsetDecoder> {

    protected abstract Charset getCharset();

    protected void checkContainsSuccessful(Charset cs) {
        assertTrue(getCharset().contains(cs));
    }

    protected void checkContainsSuccessful(Iterable<Charset> iterable) {
        for (Charset cs : iterable) {
            checkContainsSuccessful(cs);
        }
    }

    protected void checkContainsFailing() {
        assertFalse(getCharset().contains(StandardCharsets.UTF_8));
    }

    protected void checkCanEncodeSuccess() {
        assertTrue(getCharset().canEncode());
    }

    public void checkCanEncodeFailing() {
        assertFalse(getCharset().canEncode());
    }

    protected void checkDecode(EncodedText encodedText) {
        String result = new String(encodedText.getBytes(), getCharset());
        assertEquals(encodedText.getText(), result);
    }

    protected void checkEncode(EncodedText encodedText) {
        byte [] result = encodedText.getText().getBytes(getCharset());
        assertArrayEquals(encodedText.getBytes(), result);
    }

    protected void checkNewEncoder(Class<E> encoderClass) {
        assertTrue(encoderClass.isInstance(getCharset().newEncoder()));
    }

    protected void checkNewDecoder(Class<D> decoderClass) {
        assertTrue(decoderClass.isInstance(getCharset().newDecoder()));
    }
}
