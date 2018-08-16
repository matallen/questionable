package com.redhat.sso.wizard.utils;

import java.io.Serializable;

public class Triple<F, S, T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private F first;private S second;private T third;
  public static <F,S,T> Triple<F,S,T> of(F first, S second, T third){
    return new Triple<F, S, T>(first, second, third);
  }
  public Triple(F first, S second, T third){
    this.first=first;
    this.second=second;
    this.third=third;
  }
  public F getFirst(){return first;}
  public S getSecond(){return second;}
  public T getThird(){return third;}
}