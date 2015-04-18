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

import java.io.IOException;


/**
 * @author Yuhi Ishikura
 */
public final class TwitterClient {

  public Searcher search(String keyword) throws IOException {
    final Searcher searcher = new Searcher(keyword);
    searcher.nextPage();
    return searcher;
  }

}
