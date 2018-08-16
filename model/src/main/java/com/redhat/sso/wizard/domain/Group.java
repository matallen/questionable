package com.redhat.sso.wizard.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements Serializable{
  
  public static class Builder {
    public String name;
    public Builder name(String value){
      this.name=value; return this;
    }
    public Group build(){
      Group result=new Group();
      result.setName(name);
      return result;
    }
  }
  
  
  private String name;
  private List<Question> questions;
  private Map<String, Object> properties;
  
  public String getName() {
    return name;
  }
  
  public List<Question> getQuestions(){
    if (null==questions) questions=new ArrayList<Question>();
    return questions;
  }
  
  public void setName(String value) { this.name = value;  }
  
  public Map<String, Object> getProperties(){
    if (null==properties) properties=new HashMap<String, Object>();
    return properties;
  }
  
  public Group copy(){
    Group g=new Group();
    g.name=this.name;
    for (Question q:this.questions){
      g.getQuestions().add(q.copy());
    }
    return g;
  }
}
