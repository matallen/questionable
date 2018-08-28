package com.redhat.sso.wizard.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class FluentRulesService{
  private static final Logger log=Logger.getLogger(FluentRulesService.class);
  private String resourcePath;
//  private Collection<? extends Object> objects;
  private KieSession kieSession;
  private AgendaEventListener agendaEventListener;
  private ReleaseId releaseId;
  
  public FluentRulesService withReleaseId(String groupId, String artifactId, String version){
    releaseId=KieServices.Factory.get().newReleaseId(groupId, artifactId, version); return this;
  }
  public FluentRulesService withReleaseId(String gav){
    String[] split=gav.split(":");
    if (split.length!=3) throw new RuntimeException("gav's MUSt be in the format \"group:artifact:version\"");
    releaseId=KieServices.Factory.get().newReleaseId(split[0], split[1], split[2]); return this;
  }

  public FluentRulesService withClassPathResource(String path){
    this.resourcePath=path; return this;
  }
  
  public FluentRulesService withAgendaListener(AgendaEventListener listener){
    this.agendaEventListener=listener; return this;
  }
  
  public <T> FluentRulesService execute(T... facts) throws IOException{
    return execute(Arrays.asList(facts));
  }
  
  private static KieContainer kContainer;
  private static KieScanner kScanner;
  
  
//  public KieContainer newKieContainer(ReleaseId){
//    if (null==kContainer){
//      log.debug("Initialising kContainer ("+releaseId+")...");
//      kContainer=KieServices.Factory.get().newKieContainer(releaseId);
//      if (null!=kScanner)
//        kScanner.stop();
//      kScanner=KieServices.Factory.get().newKieScanner(kContainer);
//      kScanner.start(10000l); // poll every 10 seconds
//    }else{
//      log.debug("re-using previous kContainer");
//    }
//    log.debug("kScanner.scanNow() called");
//    kScanner.scanNow();
//  }
//  
//  public KieScanner attachScanner(KieContainer kContainer, long interval){
//    
//  }
  
//  public <T> Collection<? extends Object> execute(T fact) throws IOException{
  public <T> FluentRulesService execute(T fact) throws IOException{
    
    KieBase kieBase;
    KieServices kieServices=KieServices.Factory.get();
    if (releaseId!=null){ // maven resource
//        KieContainer kContainer=KieServices.Factory.get().newKieContainer(releaseId);
//        kBase=kContainer.getKieBase();
      
      if (null==kContainer){
        log.debug("Initialising kContainer ("+releaseId+")...");
        kContainer=kieServices.newKieContainer(releaseId);
        if (null!=kScanner)
          kScanner.stop();
        kScanner=kieServices.newKieScanner(kContainer);
        kScanner.start(10000l); // poll every 10 seconds
      }else{
        log.debug("re-using previous kContainer");
      }
      log.debug("kScanner.scanNow() called");
      kScanner.scanNow();
      kieBase=kContainer.newKieBase(kieServices.newKieBaseConfiguration());
      
    }else if (resourcePath!=null){ // file resource, build now (and dont do this in production!) 
      log.debug("execute(): resourcePath = "+resourcePath);
      String drl=IOUtils.toString(ResourceFactory.newClassPathResource(resourcePath).getInputStream());
      log.debug("execute(): drl = \n"+drl);
      KnowledgeBuilder qbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
      qbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
      if (qbuilder.hasErrors())
        log.error("ERROR:\n"+drl);
      kieBase = qbuilder.newKieBase();
      
    }else{ // assume it's on the classpath somewhere
      
      throw new RuntimeException("Must set either resourcePath (development) or releaseId (production)");
    }
      
      
//      if (null!=kieBaseName){
//        log.debug("creating KieBase with name ["+kieBaseName+"]");
//        KieBase kBase=kContainer.newKieBase(kieBaseName, kieServices.newKieBaseConfiguration());
//        session=kBase.newKieSession();
//      }else if (null!=kieSessionName){
//        session=null!=kieSessionName?kContainer.newKieSession(kieSessionName):kContainer.newKieSession();
//      }
      
    try{
      kieSession=kieBase.newKieSession();
      kieSession.addEventListener(agendaEventListener);
      sessionIntegrityChecks(kieSession);
      debugLogPackageInfo(kieSession);
      
      if (fact!=null){
        if (fact instanceof Collection){
          for(Object f:(Collection)fact){
            log.debug("execute(): inserting fact -> "+f);
            kieSession.insert(f);
          }
        }else{
          log.debug("execute(): inserting fact -> "+fact);
          kieSession.insert(fact);
        }
      }
      
      kieSession.fireAllRules();
      return this;
    }finally{
      if (kieSession!=null){
        kieSession.removeEventListener(agendaEventListener);
        kieSession.dispose();
      }
    }
  }
  
  public <T> T getResult(Class<T> firstOfType){
    for(Object o:kieSession.getObjects()){
      try{ firstOfType.cast(o);
        return firstOfType.cast(o);
      }catch(Exception e){}
    }
    return null;
  }
  
  public <T> Collection<? extends Object> getResults(){
    return kieSession.getObjects();
  }

  public FluentRulesService withRuleLoggingAgendaListener(){
    this.agendaEventListener=new AgendaEventListener(){
      public void matchCreated(MatchCreatedEvent arg0){}
      public void matchCancelled(MatchCancelledEvent arg0){}
      public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0){}
      public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0){}
      public void beforeMatchFired(BeforeMatchFiredEvent arg0){}
      public void agendaGroupPushed(AgendaGroupPushedEvent arg0){}
      public void agendaGroupPopped(AgendaGroupPoppedEvent arg0){}
      public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0){}
      public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0){}
      public void afterMatchFired(AfterMatchFiredEvent arg0){
        log.debug("Rule fired: "+arg0.getMatch().getRule().getName());
      }
    }; return this;
  }
  public interface AfterRuleFiredAgendaListener{
    public void afterRuleFired(Rule rule, List<Object> objects);
  }
  public FluentRulesService withRuleLoggingAgendaListener(final AfterRuleFiredAgendaListener logger){
    this.agendaEventListener=new AgendaEventListener(){
      public void matchCreated(MatchCreatedEvent arg0){}
      public void matchCancelled(MatchCancelledEvent arg0){}
      public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0){}
      public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0){}
      public void beforeMatchFired(BeforeMatchFiredEvent arg0){}
      public void agendaGroupPushed(AgendaGroupPushedEvent arg0){}
      public void agendaGroupPopped(AgendaGroupPoppedEvent arg0){}
      public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0){}
      public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0){}
      public void afterMatchFired(AfterMatchFiredEvent arg0){
        logger.afterRuleFired(arg0.getMatch().getRule(), arg0.getMatch().getObjects());
      }
    }; return this;
  }
  
  private void sessionIntegrityChecks(KieSession session){
    if (session==null) throw new RuntimeException("KieSession is null!");
    if (0==session.getKieBase().getKiePackages().size()) throw new RuntimeException("No Rules in kieBase!!!");
  }
  
  private void debugLogPackageInfo(KieSession session){
    if (log.isDebugEnabled()){
      log.debug("Rules in kieContainer:");
      for(KiePackage kp:session.getKieBase().getKiePackages()){
        for(Rule r:kp.getRules()){
          log.debug(" - "+kp.getName()+".\""+r.getName()+"\"");
        }
      }
    }
  }

}
