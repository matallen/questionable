package com.redhat.sso.wizard.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.redhat.sso.wizard.impl.QSession;

public class StaticSessionMapSessionManager extends SessionManager{
  private static Map<String,QSession> sessionStorage=new HashMap<String, QSession>();
  
  public void saveSession(QSession session) throws IOException{
    sessionStorage.put(session.getId(), session);
  }

  public QSession retrieveSession(String sessionId) throws IOException{
    return sessionStorage.get(sessionId);
  }
}
