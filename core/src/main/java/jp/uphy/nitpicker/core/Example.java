/**
 * Copyright (C) 2014 uphy.jp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.uphy.nitpicker.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


/**
 * @author Yuhi Ishikura
 */
public class Example {

  public static void main(String[] args) throws IOException {
    final Document doc = Jsoup.parse(new URL("https://news.google.co.jp/"), (int)TimeUnit.MINUTES.toMillis(1));
    for (Element e : doc.select(".esc-lead-article-title")) {
      System.out.println(e.text());
    }
  }

}
