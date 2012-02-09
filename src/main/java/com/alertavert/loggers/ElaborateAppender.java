package com.alertavert.loggers;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Not, indeed, a very elaborate logger; but still useful for experimentation.
 *
 * @author M. Massenzio (m.massenzio@gmail.com)
 */
public class ElaborateAppender extends WriterAppender {

  /**
   * Logs the event to console (basic {{@code System.out} call
   *
   * @param evt the logging event that contains the useful info to log
   */
  @Override
  public void doAppend(LoggingEvent evt) {
    System.out.println(evt.getLevel().toString() + " <<< " + evt.getMessage());
  }

}
