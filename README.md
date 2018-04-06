# telecom-charsets

[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://opensource.org/licenses/MIT) [![GitHub version](https://badge.fury.io/gh/brake%2Ftelecom-charsets.svg)](https://badge.fury.io/gh/brake%2Ftelecom-charsets) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.brake.threegpp/telecom-charsets/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.brake.threegpp/telecom-charsets) ![Java version 1.7+](https://img.shields.io/badge/Java_version-1.7+-green.svg?style=flat-square) [![Build Status](https://travis-ci.org/brake/telecom-charsets.svg?branch=master)](https://travis-ci.org/brake/telecom-charsets) [![Maintainability](https://api.codeclimate.com/v1/badges/f803b962dd656d221032/maintainability)](https://codeclimate.com/github/brake/telecom-charsets/maintainability) [![codecov](https://codecov.io/gh/brake/telecom-charsets/branch/master/graph/badge.svg)](https://codecov.io/gh/brake/telecom-charsets)

A set of Java™ [Charset](https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html) implementations supporting various codecs used in telecommunications (GSM/UCS2)

### Table of Contents

* [Purpose](#purpose)
* [Supported Character Sets](#supported-character-sets)
* [Requirements](#requirements)
* [Download](#download)
* [Usage](#usage)
* [License](#license)
* [Related links](#related-links)

## Purpose

There are several implementations of telecom text encoders and decoders over Internet. What I don't like is that you have 
to copy/past (or reinvent) some code into a static method of some additional utility class inside of application. 
Over and over, for each application. Problem I'm trying to solve is to give ability for developers of using convinient, Java™
_idiomatic_ way to encode and decode between text and bytes. Idiomatic way IMHO is 
[Charset](https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html)  implementation. 

## Supported Character Sets

Class Name | Description | Canonical Name | Aliases | Encoding Support | Decoding Support
---------- | ----------- | -------------- | ------- | ---------------- | ----------------
`GSMCharset` | **GSM 7 Bit Encoding** described in _ETSI TS 123 038_ (6.2.1, 6.2.1.1). Note that there is no support of National Language Tables (6.2.1.2, Annex A). | `X-GSM7BIT` | `GSM`, `GSM7BIT` | ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes | ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes
`GSM7BitPackedCharset` | **GSM 7 Bit Packed** based on GSM 7 bit charset with base packing as described in _ETSI TS 123 038_ (6.1.2.1 SMS Packing) | `X-GSM7BIT-PACKED` | `GSM-PACKED`, `GSM-7BIT-PACKED`, `GSM7BP` | ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes | ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes
`UCS2Charset80` | **UCS2 with tag 0x80** (AKA 80th encoding) implementation based on _ETSI TS 102 221_ (Annex A, part 1) | `X-UCS2-80` | `UCS2-80`, `UCS2x80` |  ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes | ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes
`UCS2Charset81` | **UCS2 with tag 0x81** (AKA 81th encoding) implementation based on _ETSI TS 102 221_ (Annex A, part 2) | `X-UCS2-81` | `UCS2-81`, `UCS2x81` |  ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes | ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes
`TelecomCharset` | Charset implementation which is able to decode bytes in one of following encodings: **GSM 7 Bit**, **UCS2** with tags **0x80** and **0x81** | `X-GSM-UCS2` | `ANY-TELECOM`, `TELECOM`, `GSM-OR-UCS2` |  ![#f03c15](https://placehold.it/15/f03c15/000000?text=+) No | ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) Yes

## Requirements

Java 1.7 or higher.


## Download

Direct link to [Maven Central](https://oss.sonatype.org/service/local/repositories/releases/content/com/github/brake/threegpp/telecom-charsets/1.0.0/telecom-charsets-1.0.0.jar) for case if we decide to 
use library as described in [Usage, part 1](#usage).

#### Gradle:
```gradle
compile 'com.github.brake.threegpp:telecom-charsets:1.0.0'
```

#### Maven:
```xms
<dependency>
  <groupId>com.github.brake.threegpp</groupId>
  <artifactId>telecom-charsets</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

There are two ways to use this library.

1. [Download](https://oss.sonatype.org/service/local/repositories/releases/content/com/github/brake/threegpp/telecom-charsets/1.0.0/telecom-charsets-1.0.0.jar) a precompiled `jar` file and place it to _extension_ directory of your `JRE`.

   After that you, without further configuration, can just write and run code like this:
   ```java
   import java.nio.charset.Charset;

   Charset UCS2x80 = Charset.forName("UCS2x80");
   byte [] telecomText = "My Menu Item Name".getBytes(UCS2x80);
   ```
   More information about JRE Extension Path on your system you can get from links below:
   
   - [Oracle site](https://docs.oracle.com/javase/tutorial/ext/basics/install.html)
   - [Apple site (Mac OS specific)](https://developer.apple.com/library/content/qa/qa1170/_index.html) 
   - [One more resource for Mac OS](https://www.quora.com/Where-is-the-jre-lib-ext-folder-found-in-OS-X-for-Java)
   
1. Configure a _dependency_ in your project as you are usually doing for external libraries ([gradle](#gradle) [Maven](#maven))

   ```java
   import threegpp.charset.ucs2.UCS2Charset80;

   Charset cs80 = new UCS2Charset80();
   byte [] telecomText = "Some Text".getBytes(cs80);

   ```
   

## Licence

Copyright © 2017-2018 Constantin Roganov

Distributed under the [MIT License](https://opensource.org/licenses/MIT).


## Related links

* Some charset encoding/decoding related [code](https://github.com/twitter/cloudhopper-commons/tree/master/ch-commons-charset/src/main/java/com/cloudhopper/commons/charset)
* [From code.google.com, GSM](https://code.google.com/archive/p/gsm-7-bit-encoder-decoder/downloads)
* [Useful SO answer regarding UCS2 (80th)](https://stackoverflow.com/questions/20898074/ascii-to-ucs2-encoding-java-code)
* [AOSP](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/telephony/java/com/android/internal/telephony/GsmAlphabet.java)

