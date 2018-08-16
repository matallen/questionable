package com.redhat.sso.wizard.domain;

import java.io.Serializable;
import java.util.Map;

public class Transition implements Serializable{
  private String from;
  private String to; // TODO allow multiple?
  private int order;
  
  public Transition(Map<String, Object> x){
    from=(String)x.get("From");
    to=(String)x.get("To");
    order=(Integer.parseInt((String)x.get("Order")));
  }
  
  public String getFrom() {
    return from;
  }
  
  public int getOrder() {
    return order;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

}
