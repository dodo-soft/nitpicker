/**
 * Copyright (C) 2015 uphy.jp
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
package jp.uphy.nitpicker.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Yuhi Ishikura
 */
public class DefaultContentScraper implements ContentScraper {

  @Override
  public Iterable<Content> scrape(URL url) throws IOException {
    HttpURLConnection con = (HttpURLConnection)url.openConnection();

    String name = url.getFile();
    int slashIndex = name.lastIndexOf('/');
    if (slashIndex >= 0) {
      name = name.substring(slashIndex + 1);
    }
    final List<Content> contents = new ArrayList<>();
    if (url.getHost().equals("pic.twitter.com")) {
      con.setInstanceFollowRedirects(false);
      while (con.getResponseCode() == 301) {
        final String location = con.getHeaderField("Location");
        con = (HttpURLConnection)new URL(location).openConnection();
      }
      final InputStream in = con.getInputStream();
      final Document doc = Jsoup.parse(read(in));
      in.close();

      for (Element img : doc.select("a.media-thumbnail img")) {
        final String imageSrc = img.attr("src");
        if (imageSrc.startsWith("http")) {
          contents.add(new UrlContent(ContentType.IMAGE, name + ".jpg", new URL(imageSrc)));
        }
      }
    }
    con.disconnect();
    return contents;
  }

  private static String read(InputStream in) throws IOException {
    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      final StringWriter sw = new StringWriter();
      final PrintWriter pw = new PrintWriter(sw);
      String line;
      while ((line = reader.readLine()) != null) {
        pw.println(line);
      }
      pw.close();
      return sw.toString();
    }
  }

}
