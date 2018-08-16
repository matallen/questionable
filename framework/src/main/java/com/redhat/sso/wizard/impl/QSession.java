package com.redhat.sso.wizard.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.redhat.sso.wizard.domain.Group;
import com.redhat.sso.wizard.domain.Question;
import com.redhat.sso.wizard.utils.Lists;
import com.redhat.sso.wizard.utils.Triple;

public class QSession implements Serializable/*, XQSession*/{// implements QSession{
  private static final Logger log=Logger.getLogger(QSession.class);
//  private QuestionReader qr;
  private String id;
  private Map<String, Group> groups=null;
  private Map<String, String> transitions=null;
//  private Group start;
  
  public QSession(Map<String,Group> groups, Map<String,String> transitions){
    this.groups=groups;
    this.transitions=transitions;
    
    this.id=java.util.UUID.randomUUID().toString();
  }
  
  public void updateQuestionState() {
    Map<String, Object> input=new HashMap<String, Object>();
    for(Group g:groups.values()){
      for(Question q:g.getQuestions()){
        input.put(q.getId(), q);
      }
    }
//    System.out.println(MVEL.evalToString("'TRUE'", input));
//    System.out.println(MVEL.evalToString("Q1.value=='True'", input));
    
//    for(Group g:groups.values()){
//      for(Question q:g.getQuestions()){
//        if (String.valueOf(Boolean.TRUE).equalsIgnoreCase(q.getEnabledExpr()) || String.valueOf(Boolean.FALSE).equalsIgnoreCase(q.getEnabledExpr())){
//          q.setEnabled(Boolean.valueOf(q.getEnabledExpr()));
//        }else{
//          String value=MVEL.evalToString(q.getEnabledExpr(), input);
//          System.out.println("MVEL:: Evaluating [qId="+q.getId()+", enabledExpr="+q.getEnabledExpr()+", result="+value+"]");
//          q.setEnabled(Boolean.valueOf(value));
//        }
//      }
//    }
  }
  
  public String getId(){
    return id;
  }
  
  public Question getQuestion(String questionId){
    for(Group g:groups.values()){
      for(Question q:g.getQuestions()){
        if (questionId.equals(q.getId())) return q;
      }
    }
    return null;
  }
  
  
  public String firstGroup(){
    return getTransitions().keySet().iterator().next();
  }
  public String nextGroup(String currentGroup){
    return commonNextPreviousGroup(currentGroup, getTransitions().keySet().iterator());
  }
  public String previousGroup(String currentGroup){
    List<String> groupList=new Lists.Builder<String>().addAll(getTransitions().keySet()).build();
    Collections.reverse(groupList);
    return commonNextPreviousGroup(currentGroup, groupList.iterator());
  }
  public String commonNextPreviousGroup(String currentGroup, Iterator<String> it){
    while (it.hasNext()){
      String c=it.next();
//      System.out.println("NextPrevious:: Iterator.next = "+c);
      if (c.trim().equalsIgnoreCase(currentGroup.trim())){
        if (it.hasNext()){
          c=it.next();
//          System.out.println("NextPrevious:: Iterator.next = "+c);
          return c;
        }else
          return null;
      }
    }
    return null;
  }

  // splitting this from the "next/previous Group" logic is a mistake, because the code has to be soooo in sync - need to consolidate it
  public Triple<Boolean, Boolean, Boolean> getButtonStatus(String currentGroup){
    Iterator<String> it=getTransitions().keySet().iterator();
    int count=0;
    while (it.hasNext()){
      String c=it.next();
      count+=1;
      log.debug("getButtonStatus:: "+c+"["+count+"]");
      if (c.trim().equalsIgnoreCase(currentGroup.trim())){
        boolean backButtonEnabled=count>1;
        boolean nextButtonEnabled=it.hasNext();
        boolean finishButtonEnabled=!it.hasNext();
        log.debug("getButtonStatus:: found [back="+backButtonEnabled+", next="+nextButtonEnabled+", finish="+finishButtonEnabled+"]");
        return new Triple<Boolean, Boolean, Boolean>(backButtonEnabled, nextButtonEnabled, finishButtonEnabled);
      }
    }
    return new Triple<Boolean, Boolean, Boolean>(false, true, false);
  }
  
//  public void execute() throws IOException{
//    if (groups==null){
//      qr.execute();
//      groups=qr.getGroups();
//      transitions=qr.getTransitions();
//    }
//  }

  public Group getGroup(String groupName){
    updateQuestionState();
    return getGroups().get(groupName);
  }
  
  
  public Map<String, Group> getGroups(){
    updateQuestionState();
    return groups;
  }
  
  public Map<String, String> getTransitions(){
    return transitions;
  }
  
//  public String toString(){
//    return QSessionImpl.class.getSimpleName()+"[id="+getId()+", transitions.size="+getTransitions().size()+", groups.size="+getGroups().size()+"]";
//  }

}
