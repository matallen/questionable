package com.redhat.sso.wizard.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Lists {
  public static class Builder<T>{
    private Collection<T> col=new LinkedList<T>();
    
    public Builder<T> add(T value){
      col.add(value);
      return this;
    }
    public Builder<T> addAll(Collection<T> values){
      col.addAll(values);
      return this;
    }
    public List<T> build(){
      List<T> result=new LinkedList<T>();
      for(T item : col)
        result.add(item);
      return result;
    }
  }
}
