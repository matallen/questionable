package com.redhat.sso.wizard.view;

import java.io.IOException;

public class AppProperties {
  
  private static java.util.Properties config=null;
  
  public String getProperty(String key) throws IOException{
    if (config==null){
      config=new java.util.Properties();
      config.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
    }
    return config.getProperty(key);
  }

}
