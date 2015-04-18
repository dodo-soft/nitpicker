package jp.uphy.nitpicker.scraper;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author Yuhi Ishikura
 */
public interface Content {

  String getName() throws IOException;

  ContentType getType() throws IOException;

  InputStream openStream() throws IOException;
}
