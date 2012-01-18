package com.alertavert.hbase.protobufs;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
  private static final String BUNDLE_NAME = "com.alertavert.hbase.protobufs.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {
  }

  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  public static String getString(String key, String... args) {
    return MessageFormat.format(getString(key), (Object[]) args);
  }
}
