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

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;


/**
 * @author Yuhi Ishikura
 */
public class Example {

  public static void main(String[] args) throws IOException {
    final String trend = "活休";
    TreeSet<Tweet> previousTweets = new TreeSet<>();
    /*final TwitterClient client = new TwitterClient();
    while (true) {
      final Searcher searcher = client.search(trend);
      while (true) {

        if (searcher.hasNextPage()) {
          searcher.nextPage();
        } else {
          break;
        }
      }
    }*/
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

  private static void test2() throws IOException {
    final String s = read(
      new URL("https://twitter.com/i/trends?k=&pc=true&show_context=true&src=module"));
    final JSONObject obj = new JSONObject(s);
    final Document doc = Jsoup.parse(obj.getString("module_html"));
  }

  private static void test() throws IOException {
    final String s = read(
      new URL("https://twitter.com/i/profiles/show/oqpdc/timeline?contextual_tweet_id=589236007335108608&include_available_features=1&include_entities=1&max_id=589236007335108608"));
    final JSONObject obj = new JSONObject(s);
    final Document doc = Jsoup.parse(obj.getString("items_html"));
    System.out.println(doc);
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
