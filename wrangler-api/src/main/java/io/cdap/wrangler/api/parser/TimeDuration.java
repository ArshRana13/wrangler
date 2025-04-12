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
 public class TimeDuration implements Token {
     private static final Pattern TIME_PATTERN = 
         Pattern.compile("^(\\d+\\.?\\d*)\\s*(ns|us|ms|s|sec|min|h|hr|d|day)?$", Pattern.CASE_INSENSITIVE);
     private final String originalValue;
     private final long nanoseconds;
 
     public TimeDuration(String value) {
         this.originalValue = value;
         this.nanoseconds = parse(value);
     }
 
     @Override
     public Object value() {
         return nanoseconds;
     }
 
     @Override
     public TokenType type() {
         return TokenType.TIME_DURATION;
     }
 
     @Override
     public JsonElement toJson() {
         return new JsonPrimitive(originalValue);
     }
 
     public long getNanoseconds() {
         return nanoseconds;
     }
 
     private long parse(String str) {
         Matcher matcher = TIME_PATTERN.matcher(str.trim());
         if (!matcher.matches()) {
             throw new IllegalArgumentException(
                 String.format("'%s' is not a valid time duration format. Examples: 100ms, 2.5s", str));
         }
 
         double value = Double.parseDouble(matcher.group(1));
         String unit = matcher.group(2) == null ? "ms" : matcher.group(2).toLowerCase();
 
         switch (unit) {
             case "ns": return (long) value;
             case "us": return (long) (value * 1000);
             case "ms": return (long) (value * 1000 * 1000);
             case "s": case "sec": return (long) (value * 1000 * 1000 * 1000);
             case "min": return (long) (value * 60 * 1000 * 1000 * 1000L);
             case "h": case "hr": return (long) (value * 60 * 60 * 1000 * 1000 * 1000L);
             case "d": case "day": return (long) (value * 24 * 60 * 60 * 1000 * 1000 * 1000L);
             default:
                 throw new IllegalArgumentException("Unsupported time unit: " + unit);
         }
     }
 }