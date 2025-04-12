/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonPrimitive;
 import io.cdap.wrangler.api.annotations.PublicEvolving;
 
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 @PublicEvolving
 public class ByteSize implements Token {
     private static final Pattern BYTE_PATTERN = 
         Pattern.compile("^(\\d+\\.?\\d*)\\s*([KMGTP]?i?B?)$", Pattern.CASE_INSENSITIVE);
     private final String originalValue;
     private final long bytes;
 
     public ByteSize(String value) {
         this.originalValue = value;
         this.bytes = parse(value);
     }
 
     @Override
     public Object value() {
         return bytes;
     }
 
     @Override
     public TokenType type() {
         return TokenType.BYTE_SIZE;
     }
 
     @Override
     public JsonElement toJson() {
         return new JsonPrimitive(originalValue);
     }
 
     public long getBytes() {
         return bytes;
     }
 
     private long parse(String str) {
         Matcher matcher = BYTE_PATTERN.matcher(str.trim());
         if (!matcher.matches()) {
             throw new IllegalArgumentException(
                 String.format("'%s' is not a valid byte size format. Examples: 10KB, 1.5MB", str));
         }
 
         double size = Double.parseDouble(matcher.group(1));
         String unit = matcher.group(2).toUpperCase();
 
         switch (unit) {
             case "B": case "": return (long) size;
             case "KB": return (long) (size * 1000);
             case "KIB": return (long) (size * 1024);
             case "MB": return (long) (size * 1000 * 1000);
             case "MIB": return (long) (size * 1024 * 1024);
             case "GB": return (long) (size * 1000 * 1000 * 1000);
             case "GIB": return (long) (size * 1024 * 1024 * 1024);
             case "TB": return (long) (size * 1000L * 1000 * 1000 * 1000);
             case "TIB": return (long) (size * 1024L * 1024 * 1024 * 1024);
             default:
                 throw new IllegalArgumentException("Unsupported byte unit: " + unit);
         }
     }
 }