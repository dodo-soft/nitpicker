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
public final class Searcher {

  private final String keyword;
  private TreeSet<Tweet> tweets = new TreeSet<>();
  private boolean isFirstQuery = true;
  private boolean hasNext = true;
  private String cursor;

  Searcher(String keyword) {
    this.keyword = keyword;
  }

  public Iterable<Tweet> getResults() {
    return tweets;
  }

  public void nextPage() throws IOException {
    if (isFirstQuery) {
      final Document doc = Jsoup.parse(new URL(String.format("https://twitter.com/search?q=%s", URLEncoder.encode(this.keyword, "UTF-8"))), (int)TimeUnit.MINUTES.toMillis(1));
      this.tweets = parseDocument(doc);
      this.isFirstQuery = false;
      this.cursor = doc.select("div.stream-container").attr("data-scroll-cursor");
      this.hasNext = this.cursor != null;
    } else {
      final URL url = new URL(
        String.format("https://twitter.com/i/search/timeline?q=%s&include_available_features=1&include_entities=1&scroll_cursor=%s", URLEncoder.encode(this.keyword, "UTF-8"), this.cursor));
      final JSONObject json = parseJson(url);
      this.hasNext = json.has("scroll_cursor");
      if (this.hasNext) {
        this.cursor = json.getString("scroll_cursor");
      }
      final String html = json.getString("items_html");
      final Document doc = Jsoup.parse(html);
      this.tweets = parseDocument(doc);
    }
  }

  private static TreeSet<Tweet> parseDocument(Document doc) {
    final TreeSet<Tweet> newTweets = new TreeSet<>();
    for (Element e : doc.select(".js-stream-tweet")) {
      final Tweet t = new Tweet();
      t.user = e.attr("data-name");
      t.userId = e.attr("data-user-id");
      t.screenName = e.attr("data-screen-name");
      t.tweetId = e.attr("data-tweet-id");
      t.message = e.select(".js-tweet-text").text();
      t.time = Long.parseLong(e.select(".js-short-timestamp").attr("data-time-ms"));
      newTweets.add(t);
    }
    return newTweets;
  }

  private static JSONObject parseJson(URL url) throws IOException {
    final HttpURLConnection con = (HttpURLConnection)url.openConnection();
    final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    String line;
    while ((line = reader.readLine()) != null) {
      pw.println(line);
    }
    pw.close();
    return new JSONObject(sw.toString());
  }

  public boolean hasNextPage() {
    return isFirstQuery || this.hasNext;
  }

}
