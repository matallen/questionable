package com.redhat.sso.wizard.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ListToMap<T, S>{
  public Map<T,S> from(List<S> values){
    Map<T, S> result=new HashMap<T, S>();
    for(S e:values){
      result.put(getKey(e), e);
    }
    return result;
  }
  public abstract T getKey(S entity);
//  public abstract List<S> getValues();
}