package com.jbidwatcher.platform;
/*
 * Copyright (c) 2000-2007, CyberFOX Software, Inc. All Rights Reserved.
 *
 * Developed by mrs (Morgan Schweers)
 */

import com.jbidwatcher.util.queue.MessageQueue;
import com.jbidwatcher.util.queue.MQFactory;
import com.jbidwatcher.util.config.JConfig;
import com.jbidwatcher.util.config.ErrorManagement;
import com.jbidwatcher.util.browser.WindowsBrowserLauncher;
import com.jbidwatcher.util.browser.BrowserLauncher;

import java.io.*;

public class Browser extends JConfig implements MessageQueue.Listener {
  public Browser() {
    MQFactory.getConcrete("browse").registerListener(this);
  }

  public void messageAction(Object deQ) {
    String msg = (String)deQ;

    launchBrowser(msg);
  }

  public static String getBrowserCommand() {
    String osName = getOS();

    if(osName.equalsIgnoreCase("windows")) {
      return WindowsBrowserLauncher.getBrowser("http");
    } else {
      return "netscape";
    }
  }

  public static boolean launchBrowser(String url) {
    String rawOSName = System.getProperty("os.name");
    int spaceIndex = rawOSName.indexOf(' ');
    String osName;

    if (spaceIndex == -1) {
      osName = rawOSName;
    } else {
      osName = rawOSName.substring(0, spaceIndex);
    }

    String launchCommand = JConfig.queryConfiguration("browser.launch." + osName);

    if(launchCommand == null) {
      launchCommand = JConfig.queryConfiguration("browser.launch");
      if(launchCommand == null) {
        launchCommand = "netscape";
      }
    }

    try {
      BrowserLauncher.openURL(url, launchCommand, JConfig.queryConfiguration("browser.override","false").equals("true"));
    } catch(IOException e) {
      ErrorManagement.handleException("Launching browser", e);
      return false;
    }
    return true;
  }
}
