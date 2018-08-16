package com.redhat.sso.wizard.utils;

import java.util.LinkedList;
import java.util.List;

public class HtmlSelectOptionBuilder {

  public class Option{
    private String id;
    private String value;
    public Option(String id, String value){
      this.id=id;
      this.value=value;
    }
    public String getId(){return this.id;}
    public String getValue(){return this.value;}
  }
  
  List<Option> options=new LinkedList<Option>();
  public HtmlSelectOptionBuilder add(String key, String value){
    options.add(new Option(key,value)); return this;
  }
  public List<Option> build(){
    return options;
  }
}
