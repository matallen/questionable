package com.redhat.sso.wizard.utils;

import java.io.Serializable;

public class Tuple<F, S> implements Serializable {
  private static final long serialVersionUID = 1L;
  private F first;private S second;
  public static <F,S> Tuple<F,S> of(F first, S second){
    return new Tuple<F, S>(first, second);
  }
  public Tuple(F first, S second){
    this.first=first;
    this.second=second;
  }
  public F getFirst(){return first;}
  public S getSecond(){return second;}
}