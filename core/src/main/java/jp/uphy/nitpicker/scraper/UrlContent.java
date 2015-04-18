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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * @author Yuhi Ishikura
 */
public final class UrlContent implements Content {

  private ContentType type;
  private String name;
  private URL url;

  UrlContent(ContentType type, String name, URL url) {
    this.type = type;
    this.name = name;
    this.url = url;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ContentType getType() {
    return type;
  }

  @Override
  public InputStream openStream() throws IOException {
    return this.url.openStream();
  }

  @Override
  public String toString() {
    return "Content{" +
      "name='" + name + '\'' +
      ", type=" + type +
      '}';
  }
}
