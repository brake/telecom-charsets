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

import threegpp.charset.gsm.GSM7BitPackedCharset;
import threegpp.charset.gsm.GSMCharset;
import threegpp.charset.ucs2.UCS2Charset80;
import threegpp.charset.ucs2.UCS2Charset81;

import java.nio.charset.Charset;
import java.util.*;

public class CharsetProvider extends java.nio.charset.spi.CharsetProvider {

    private static Charset [] allCharsets = {
            new GSM7BitPackedCharset(), new GSMCharset(),
            new UCS2Charset80(), new UCS2Charset81(), new TelecomCharset()
    };

    private Set<Charset> set = new HashSet<>();
    private Map<String, Charset> namesMap = new HashMap<>();

    public CharsetProvider() {
        super();

        Collections.addAll(set, allCharsets);

        for (Charset cs : allCharsets) {
            namesMap.put(cs.displayName(), cs);

            for(String alias: cs.aliases()) {
                namesMap.put(alias, cs);
            }
        }
    }

    @Override
    public Iterator<Charset> charsets() {
        return set.iterator();
    }

    @Override
    public Charset charsetForName(String charsetName) {
        return namesMap.get(charsetName);
    }
}
