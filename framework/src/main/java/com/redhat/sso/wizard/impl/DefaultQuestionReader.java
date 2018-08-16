package com.redhat.sso.wizard.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kie.api.runtime.KieSession;

import com.redhat.sso.wizard.domain.Group;
import com.redhat.sso.wizard.domain.Question;
import com.redhat.sso.wizard.view.QuestionReader;

public abstract class DefaultQuestionReader implements QuestionReader{
  public static final Logger log=Logger.getLogger(DefaultQuestionReader.class);
  
  public static Integer counter=0; //used in Xls rules to set the order of the rule firing so we can order the questions
  public abstract KieSession newKieSession();
  
  public QSession newSession(){
    KieSession kSession=newKieSession();
    
    Map<String,String> transitions=new LinkedHashMap<String, String>();
    List<Question> questions=new LinkedList<Question>();
    
    kSession.fireAllRules();
    Map<String,Group> groups=new HashMap<String, Group>();
    for(Object o:kSession.getObjects()){
      if (o instanceof Question)
        questions.add((Question)o);
    }
    
    
    Collections.sort(questions, new Comparator<Question>() {
      public int compare(Question lhs, Question rhs) {
          // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
          return lhs.getOrder() < rhs.getOrder() ? -1 : (lhs.getOrder() > rhs.getOrder()) ? 1 : 0;
      }
    });
    
    for(Question q:questions){
      log.debug("Question found: "+q);
      
      if (!transitions.containsKey(q.getGroup()))
        transitions.put(q.getGroup(), "");
      
      Group group=groups.containsKey(q.getGroup())?groups.get(q.getGroup()):new Group.Builder().name(q.getGroup()).build();
      
      group.getQuestions().add(q);
      groups.put(group.getName(), group);
    }
    
    // TODO: Need to do some thinking about rules and transition determination here
    
    QSession result=new QSession(groups, transitions);
    
    return result;
  }

}
