package com.redhat.sso.wizard.view;

import java.io.IOException;

import com.redhat.sso.wizard.impl.QSession;
import com.redhat.sso.wizard.session.SessionManager;

public interface Controller {
  public Object start() throws IOException;
  public Object next(QSession session, String payload) throws IOException;
  public Object back(QSession session, String payload) throws IOException;
  public Object finish(QSession session, String payload) throws IOException;
  public Object result(QSession session) throws IOException;
  
  public QuestionReader createQuestionReader();
  public SessionManager createSessionManager();
}
