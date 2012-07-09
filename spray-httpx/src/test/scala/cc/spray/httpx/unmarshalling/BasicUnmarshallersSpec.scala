/*
 * Copyright (C) 2011-2012 spray.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.spray.httpx.unmarshalling

import xml.NodeSeq
import org.specs2.mutable.Specification
import cc.spray.http._
import MediaTypes._
import HttpCharsets._


class BasicUnmarshallersSpec extends Specification {
  
  "The StringUnmarshaller" should {
    "decode `text/plain` content in ISO-8859-1 to Strings" in {
      HttpEntity("Hällö").as[String] === Right("Hällö")
    }
  }

  "The CharArrayUnmarshaller" should {
    "decode `text/plain` content in ISO-8859-1 to char arrays" in {
      HttpEntity("Hällö").as[Array[Char]].right.get.mkString === "Hällö"
    }
  }

  "The NodeSeqUnmarshaller" should {
    "decode `text/xml` content in ISO-8859-1 to NodeSeqs" in {
      HttpBody(`text/xml`, "<int>Hällö</int>").as[NodeSeq].right.get.text === "Hällö"
    }
  }

  "The FormDataUnmarshaller" should {
    "correctly unmarshal HTML form content with one element" in (
      HttpBody(ContentType(`application/x-www-form-urlencoded`, `UTF-8`), "secret=h%C3%A4ll%C3%B6").as[FormData] ===
        Right(FormData(Map("secret" -> "hällö")))
    )
    "correctly unmarshal HTML form content with three fields" in {
      HttpBody(`application/x-www-form-urlencoded`, "email=test%40there.com&password=&username=dirk").as[FormData] ===
        Right(FormData(Map("email" -> "test@there.com", "password" -> "", "username" -> "dirk")))
    }
    "be lenient on empty key/value pairs" in {
      HttpBody(`application/x-www-form-urlencoded`, "&key=value&&key2=&").as[FormData] ===
        Right(FormData(Map("key" -> "value", "key2" -> "")))
    }
    "reject illegal form content" in (
      HttpBody(`application/x-www-form-urlencoded`, "key=really=not_good").as[FormData] ===
        Left(MalformedContent("'key=really=not_good' is not a valid form content: " +
          "'key=really=not_good' does not constitute a valid key=value pair"))
    )
  }

}