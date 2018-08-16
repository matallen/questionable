package com.redhat.sso.wizard.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSession;

import com.redhat.sso.wizard.domain.Group;
import com.redhat.sso.wizard.domain.Question;


public class DefaultQuestionReaderTest{
  
  @Test
  public void testFilePathQuestionReader(){
    
    DefaultQuestionReader test=new DefaultQuestionReader(){
      @Override
      public KieSession newKieSession(){
        return new FilePathQuestionReader("src/test/resources/questions-test.xls", "Sheet1").newKieSession();
      }
    };
    
    QSession session=test.newSession();
    
    Collection<Group> groups=session.getGroups().values();
    assertEquals(groups.size(), 2);
    
    System.out.println(session.getTransitions());
    
    // Transition structure
    assertEquals(2, session.getTransitions().size());
    
    // Group structure
    Group p1=session.getGroup("P1");
    Group p2=session.getGroup("P2");
    
    assertEquals("P1", p1.getName());
    assertEquals(1, p1.getQuestions().size());
    
    assertEquals("P2", p2.getName());
    assertEquals(1, p2.getQuestions().size());
    
    // Question structure
    Question q1=session.getQuestion("Q1");
    assertEquals("Field on page 1", q1.getTitle());
    
    Question q2=session.getQuestion("Q2");
    assertEquals("Field on page 2", q2.getTitle());
  }
  
  @Test
  public void testBusinessCentralQuestionReader(){
    
    DefaultQuestionReader test=new DefaultQuestionReader(){
      @Override
      public KieSession newKieSession(){
//        return new FilePathQuestionReader("src/test/resources/questions-test.xls").newKieSession();
        
        // drools 6 business central
//        ReleaseId r=KieServices.Factory.get().newReleaseId("org.kie.example","project1","1.0.0-SNAPSHOT");
        
        // drools 7
        ReleaseId r=KieServices.Factory.get().newReleaseId("com.myteam","questions2","1.0.0");
        
        return new BusinessCentralQuestionReader(r, 10000l).newKieSession();
      }
    };
    
    QSession session=test.newSession();
    
    Collection<Group> groups=session.getGroups().values();
    assertEquals(groups.size(), 2);
    
    System.out.println(session.getTransitions());
    
    // Transition structure
    assertEquals(2, session.getTransitions().size());
    
    // Group structure
    Group p1=session.getGroup("P1");
    Group p2=session.getGroup("P2");
    
    assertEquals("P1", p1.getName());
    assertEquals(1, p1.getQuestions().size());
    
    assertEquals("P2", p2.getName());
    assertEquals(1, p2.getQuestions().size());
    
    // Question structure
    Question q1=session.getQuestion("Q1");
    assertEquals("Q1", q1.getTitle());
    
    Question q2=session.getQuestion("Q2");
    assertEquals("Q2", q2.getTitle());
    
    String firstGroup=session.firstGroup();
    assertEquals("P1", firstGroup);
    assertEquals("P1", session.getGroup(firstGroup).getName()); // just check the map works too
    
    String nextGroup=session.nextGroup(firstGroup);
    assertEquals("P2", nextGroup);
    assertEquals("P2", session.getGroup(nextGroup).getName()); // just check the map works too

    String previousGroup=session.previousGroup(nextGroup);
    assertEquals("P1", previousGroup);

    
  }
  
  @Test
  public void testXXX(){
    
  }

}
