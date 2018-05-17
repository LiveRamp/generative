package com.liveramp.generative;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class GenerativeTestCase {

  public GenerativeTestCase(){
    ConsoleAppender appender = new ConsoleAppender();
    appender.setTarget(ConsoleAppender.SYSTEM_ERR);
    appender.setLayout(new SimpleLayout());
    appender.setFollow(true);
    appender.activateOptions();
    Logger.getRootLogger().addAppender(appender);
  }

}
