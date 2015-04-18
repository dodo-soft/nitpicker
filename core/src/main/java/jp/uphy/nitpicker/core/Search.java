package jp.uphy.nitpicker.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by che on 2015/04/18.
 */
public class Search {
  String clazz = ".stream-media-grid-items media-grid";
  public List<String> images(String keyword) throws IOException {
    List<String> result = new ArrayList<>();
    String utf8key = URLEncoder.encode(keyword, "UTF-8");
    URL url = new URL(String.format("https://twitter.com/search?q=%s&mode=photos", utf8key));
    Document doc = Jsoup.parse(url, (int) TimeUnit.MINUTES.toMillis(10));
    for(Element elem : doc.select(clazz)){
      Element img = elem.getElementById("span").getElementById("img");
      String src = img.attr("src");
      result.add(src);
    }
    return result;
  }


  public static void main(String[] args) throws IOException{
    List<String> imgs = new Search().images("背脂");
    for(String url:imgs){
      System.out.println(url);
    }
  }
}