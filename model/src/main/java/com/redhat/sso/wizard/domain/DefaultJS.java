package com.redhat.sso.wizard.domain;

import java.util.ArrayList;
import java.util.List;

public class DefaultJS {
  public String session;
  public List<Group> groups; // Leaving this as a group because it allows an API call to provide all pages at once if that's required.
  public boolean allowBack;
  public boolean allowNext;
  public boolean allowFinish;
  
  public DefaultJS(String session, List<Group> groups, boolean allowBack, boolean allowNext, boolean allowFinish){
    this.session=session;
    this.groups=groups;
    this.allowBack=allowBack;
    this.allowNext=allowNext;
    this.allowFinish=allowFinish;
  }
  
  public String getSession(){
    return session;
  }
  
  public List<Group> getGroups() {
    if (groups==null) groups=new ArrayList<Group>();
    return groups;
  }

  public boolean isAllowBack() {
    return allowBack;
  }


  public boolean isAllowNext() {
    return allowNext;
  }


  public boolean isAllowFinish() {
    return allowFinish;
  }

}
