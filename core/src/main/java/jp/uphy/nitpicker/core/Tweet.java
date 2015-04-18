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

/**
 * @author Yuhi Ishikura
 */
public final class Tweet implements Comparable {

  String userId;
  String user;
  String screenName;
  String tweetId;
  String message;
  long time;

  public String getUser() {
    return user;
  }

  public String getUserId() {
    return userId;
  }

  public String getScreenName() {
    return screenName;
  }

  public String getTweetId() {
    return tweetId;
  }

  public String getMessage() {
    return message;
  }

  public long getTime() {
    return time;
  }

  @Override
  public String toString() {
    return "Tweet{" +
      "message='" + message + '\'' +
      ", userId='" + userId + '\'' +
      ", user='" + user + '\'' +
      ", screenName='" + screenName + '\'' +
      ", tweetId='" + tweetId + '\'' +
      ", time='" + time + '\'' +
      '}';
  }

  @Override
  public int compareTo(Object o) {
    return (int)(time - ((Tweet)o).time);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tweet tweet = (Tweet)o;

    if (time != tweet.time) return false;
    if (message != null ? !message.equals(tweet.message) : tweet.message != null) return false;
    if (screenName != null ? !screenName.equals(tweet.screenName) : tweet.screenName != null) return false;
    if (tweetId != null ? !tweetId.equals(tweet.tweetId) : tweet.tweetId != null) return false;
    if (user != null ? !user.equals(tweet.user) : tweet.user != null) return false;
    if (userId != null ? !userId.equals(tweet.userId) : tweet.userId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = userId != null ? userId.hashCode() : 0;
    result = 31 * result + (user != null ? user.hashCode() : 0);
    result = 31 * result + (screenName != null ? screenName.hashCode() : 0);
    result = 31 * result + (tweetId != null ? tweetId.hashCode() : 0);
    result = 31 * result + (message != null ? message.hashCode() : 0);
    result = 31 * result + (int)(time ^ (time >>> 32));
    return result;
  }
}
