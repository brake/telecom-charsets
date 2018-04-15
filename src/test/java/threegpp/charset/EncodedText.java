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

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

public enum EncodedText {

    GSM("[Lorem ipsum dolor sit amet], {consectetur adipiscing elit.} Quisque sagittis ~.",
            "1B3C4C6F72656D20697073756D20646F6C6F722073697420616D65741B3E2C201B28636F6E73656374657475722061646970697363"
                    + "696E6720656C69742E1B292051756973717565207361676974746973201B3D2E"),

    GSM_SINGLE("L", "4C"),

    GSM_SINGLE_EXT("[", "1B3C"),

    GSM_7BP_SINGLE_EXT("[", "1B1E"),

    // edge case when string ends with 00 byte - @ (at) character
    GSM_7BP_AT("Lorem ipsum dolor sit amet@", "CCB7BCDC06A5E1F37A1B447EB3DF72D03C4D0785DB653A00"),

    GSM_7BP("[Lorem ipsum dolor sit amet], {consectetur adipiscing elit.} Quisque sagittis ~.",
            "1B1EF32D2FB74169F8BCDE0691DFECB71C344FD341E17699BEF1B1401BD4F8ED9E97C7F432BD2E0785C969787A3E4EBBCFA0323B4D"
                    + "776D52A0683D3D8FD7CBA079F89CA6D3D373D0A6E702"),

    UCS2_INVALID("", "ADBCFD"),

    UCS2_80("Valid Text. Корректный текст. सही पाठ",
        "8000560061006C0069006400200054006500780074002E0020041A043E044004400435043A0442043D044B0439002004420435043A0441"
                + "0442002E00200938093909400020092A093E0920"),

    UCS2_80_SINGLE("V", "800056"),

    UCS2_81("Valid Text. Корректный текст.", "811D0856616C696420546578742E209ABEC0C0B5BAC2BDCBB920C2B5BAC1C22E"),
    UCS2_81_SINGLE("V", "81010056"),
    UCS2_81_SINGLE_INVALID_LENGTH("V", "811D0856");


    private String text;
    private byte [] encodedBytes;

    EncodedText(String plainText, String hexBytes) {
        text = plainText;
        encodedBytes = parseHexBinary(hexBytes);
    }

    EncodedText(String plainText, byte[] bytes) {
        text = plainText;
        encodedBytes = bytes;
    }

    public String getText() {
        return text;
    }

    public byte[] getBytes() {
        return encodedBytes;
    }
}
