package com.redhat.sso.wizard.view;

import java.io.IOException;

import org.kie.api.runtime.KieSession;

import com.redhat.sso.wizard.impl.QSession;

public interface QuestionReader {

  public QSession newSession();
//  public Map<String, Group> getGroups();
//  public Map<String, String> getTransitions();
//  public QuestionReader execute() throws IOException;
  
  public KieSession newKieSession();
}
