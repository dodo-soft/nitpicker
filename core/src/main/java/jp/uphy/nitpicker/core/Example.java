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
package jp.uphy.nitpicker.core;

import jp.uphy.nitpicker.scraper.Content;
import jp.uphy.nitpicker.scraper.ContentScraper;
import jp.uphy.nitpicker.scraper.DefaultContentScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Yuhi Ishikura
 */
public class Example {

  public static void main(String[] args) throws IOException, InterruptedException {
    imageDownloaderExample("背脂");
  }

  private static void imageDownloaderExample(String keyword) throws IOException, InterruptedException {
    final Searcher searcher = new Searcher(keyword);
    final Pattern p = Pattern.compile("^.*?(pic\\.twitter\\.com/[a-zA-Z0-9]+?)$");
    final Path downloadDirectory = Paths.get("target/downloads");

    if (Files.exists(downloadDirectory) == false) {
      Files.createDirectory(downloadDirectory);
    }
    final ContentScraper scraper = new DefaultContentScraper();
    while (searcher.hasNextPage()) {
      searcher.nextPage();
      for (Tweet t : searcher.getResults()) {
        final Matcher m = p.matcher(t.getMessage());
        if (m.matches()) {
          final URL url = new URL("https://" + m.group(1));
          for (Content content : scraper.scrape(url)) {
            downloadContent(downloadDirectory, content);
          }
        }
      }
      Thread.sleep(5000);
    }
  }

  private static void downloadContent(Path dest, Content content) throws IOException {
    final Path file = dest.resolve(content.getName());

    try (final BufferedInputStream bis = new BufferedInputStream(content.openStream());
      final OutputStream out = Files.newOutputStream(file)
    ) {
      final byte[] buf = new byte[0x1000];
      int size;
      while ((size = bis.read(buf)) != -1) {
        out.write(buf, 0, size);
      }
    }
  }

  private static void detectDeletion() throws IOException {
    final String trend = "活休";
    TreeSet<Tweet> previousTweets = new TreeSet<>();
    while (true) {
      final TreeSet<Tweet> currentTweets = new TreeSet<>();
      try {
        final Document doc = Jsoup.parse(new URL(String.format("https://twitter.com/search?q=%s&src=tren", URLEncoder.encode(trend), "UTF-8")), (int)TimeUnit.MINUTES.toMillis(1));
        for (Element e : doc.select(".js-stream-tweet")) {
          final Tweet t = new Tweet();
          t.user = e.attr("data-name");
          t.userId = e.attr("data-user-id");
          t.screenName = e.attr("data-screen-name");
          t.tweetId = e.attr("data-tweet-id");
          t.message = e.select(".js-tweet-text").text();
          t.time = Long.parseLong(e.select(".js-short-timestamp").attr("data-time-ms"));
          if (previousTweets.contains(t) == false) {
            System.out.println("New: " + t);
          }
          currentTweets.add(t);
        }

        // この時間以前のツイートは削除検出できない
        long lastTime = currentTweets.last().time;
        previousTweets.stream().filter(t -> currentTweets.contains(t) == false).forEach(t -> {
          if (t.time >= lastTime) {
            System.out.println("Deleted: " + t);
          }
        });
        previousTweets = currentTweets;
        Thread.sleep(10 * 1000);
      } catch (InterruptedException e) {
        break;
      }
    }
  }

  private static String read(URL url) throws IOException {
    final HttpURLConnection con = (HttpURLConnection)url.openConnection();
    final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
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
