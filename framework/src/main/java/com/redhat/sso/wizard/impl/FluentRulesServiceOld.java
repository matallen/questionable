package com.redhat.sso.wizard.impl;

import java.util.Arrays;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluentRulesServiceOld {
  private static final Logger log = LoggerFactory.getLogger(FluentRulesServiceOld.class);
  private enum Type{FileResource,Classpath,Maven}
  private String fileResourcePath;
  private Type type;
  private String kieSessionName;
  private String kieBaseName;
  private ReleaseId releaseId;
  private AgendaEventListener agendaEventListener;
  private static boolean headerPrinted=false;
  
  public FluentRulesServiceOld withStaticClasspathRules(){
    this.type=Type.Classpath; return this;
  }
  public FluentRulesServiceOld withClassPathResource(String path){
    this.type=Type.FileResource;this.fileResourcePath=path; return this;
  }
  public FluentRulesServiceOld withKieSessionName(String name){
    this.kieSessionName=name; return this;
  }
  public FluentRulesServiceOld withKieBaseName(String name){
    this.kieBaseName=name; return this;
  }
  public FluentRulesServiceOld withReleaseId(String groupId, String artifactId, String version){
    this.type=Type.Maven;
    this.releaseId=new ReleaseIdImpl(groupId, artifactId, version); return this;
  }
  public FluentRulesServiceOld withAgendaListener(AgendaEventListener listener){
    this.agendaEventListener=listener; return this;
  }
  
  
  private static KieContainer kContainer;
  private static KieScanner kScanner;
  
  public <T> int execute(T... facts){
    return execute(Arrays.asList(facts));
  }
  public <T> int execute(Iterable<T> facts){
    
    if (type==null) throw new RuntimeException("You must choose either the withReleaseId() or withStaticClasspathRules()");
    
    RulesServiceMetrics metrics=new RulesServiceMetrics();
    metrics.start();
    KieServices kieServices = KieServices.Factory.get();
    metrics.end("InitkieServices");
    
    if (type==Type.Maven){
      if (null==kContainer){
        log.debug("Initialising kContainer ("+releaseId+")...");
        kContainer=kieServices.newKieContainer(kieServices.newReleaseId(releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion()));
        if (null!=kScanner)
          kScanner.stop();
        kScanner=kieServices.newKieScanner(kContainer);
        kScanner.start(10000l); // poll every 10 seconds
      }else{
        log.debug("re-using previous kContainer");
      }
      log.debug("kScanner.scanNow() called");
      kScanner.scanNow();
    }else if(type==Type.Classpath){
      if (null==kContainer){
        kContainer=kieServices.newKieClasspathContainer();
      }
    }else if (type==Type.FileResource){
      KieFileSystem kfs = kieServices.newKieFileSystem();
      kfs.write(ResourceFactory.newFileResource(fileResourcePath));
      KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
      kContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }
    metrics.end("InitKieContainer");
    
    KieSession session=null;
    try{
      if (null!=kieBaseName){
        log.debug("creating KieBase with name ["+kieBaseName+"]");
        KieBase kBase=kContainer.newKieBase(kieBaseName, kieServices.newKieBaseConfiguration());
        session=kBase.newKieSession();
      }else if (null!=kieSessionName){
        session=null!=kieSessionName?kContainer.newKieSession(kieSessionName):kContainer.newKieSession();
      }
      metrics.end("initKieSession");
      
      sessionIntegrityChecks(session);
      debugLogPackageInfo(session);
      
      session.addEventListener(agendaEventListener);
      
      for (Object fact:facts)
        session.insert(fact);
      metrics.end("insertFacts");
      
      int numberOfRulesFired=session.fireAllRules();
      metrics.end("fireAllRules");
      
      if (log.isDebugEnabled()){
        log.debug("Drools execution metrics:");
        if (!headerPrinted){
          log.debug(String.format("%-10s | %-10s | %-10s | %-10s |", "kServ(ms)","kCont(ms)","kSess(ms)","insertFacts","fireRules"));
          headerPrinted=true;
        }
        log.debug(String.format("%-10s | %-10s | %-10s | %-10s |", metrics.get("InitkieServices"),metrics.get("InitKieContainer"),metrics.get("initKieSession"),metrics.get("insertFacts"), metrics.get("fireAllRules")));
      }
      
      return numberOfRulesFired;
    }finally{
      if (session!=null){
        session.removeEventListener(agendaEventListener);
        session.dispose();
      }
    }
  }
  
  @SuppressWarnings("unused")
  private void sessionIntegrityChecks(KieSession session){
    if (session==null) throw new RuntimeException("KieSession is null!");
    if (0==session.getKieBase().getKiePackages().size()) throw new RuntimeException("No Rules in kBase!!!");
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

