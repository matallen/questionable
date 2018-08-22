package com.redhat.sso.wizard.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.kie.api.event.kiescanner.KieScannerStatusChangeEvent;
import org.kie.api.event.kiescanner.KieScannerUpdateResultsEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class BusinessCentralQuestionReader extends DefaultQuestionReader {
  
  private static final Logger log=Logger.getLogger(BusinessCentralQuestionReader.class);
  private ReleaseId releaseId;
  
  private static KieScanner kScanner;
  private long pollInterval=1000l;
  private static KieContainer kContainer;
  
  public BusinessCentralQuestionReader(ReleaseId releaseId, long pollInterval){
    this.releaseId=releaseId;
  }
  
  public KieSession newKieSession(){
    KieServices ks=KieServices.Factory.get();
    
    if (null!=System.getenv("DECISION_MANAGER_URL") &&
        null!=System.getenv("DECISION_MANAGER_USERNAME") &&
        null!=System.getenv("DECISION_MANAGER_PASSWORD")
        ){
      try{
        String userHome=System.getProperty("user.home");
        String settingsXml=IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("client-settings-template.xml"));
        settingsXml=settingsXml.replaceAll("\\$SERVER_URL", System.getenv("DECISION_MANAGER_URL"));
        settingsXml=settingsXml.replaceAll("\\$USERNAME", System.getenv("DECISION_MANAGER_USERNAME"));
        settingsXml=settingsXml.replaceAll("\\$PASSWORD", System.getenv("DECISION_MANAGER_PASSWORD"));
        String dir="/home/jboss/target/";
        boolean x=new File(dir).mkdirs();
        log.debug("settings.xml template converted:\n"+settingsXml);
        log.debug("mkdirs success? = "+x);
        log.debug("Writing "+dir+"client-settings.xml with URL: "+System.getenv("DECISION_MANAGER_URL"));
        
        File dest=new File(dir,"client-settings.xml");
        if (!dest.exists()) dest.createNewFile();
        IOUtils.write(settingsXml, new FileOutputStream(dest));
        log.debug("Setting 'kie.maven.settings.custom' to '"+dir+"client-settings.xml'");
        System.setProperty("kie.maven.settings.custom", dir+"client-settings.xml");
      }catch(IOException e){
        e.printStackTrace();
      }
    }
    
//    if (null!=System.getenv("KIE_MAVEN_SETTINGS_CUSTOM")){
//      log.debug("Settings 'kie.maven.settings.custom' to '"+System.getenv("KIE_MAVEN_SETTINGS_CUSTOM")+"'");
//      System.setProperty("kie.maven.settings.custom", System.getenv("KIE_MAVEN_SETTINGS_CUSTOM"));
//    }
    
    log.debug("Initialising kContainer ("+releaseId+")...");
    kContainer=ks.newKieContainer(releaseId);
    KieBase kieBase=kContainer.newKieBase(ks.newKieBaseConfiguration());
    
    if (null!=kScanner)
      kScanner.stop();
    kScanner=ks.newKieScanner(kContainer);
    kScanner.start(pollInterval);
    kScanner.scanNow();
    kScanner.addListener(new KieScannerEventListener(){
      public void onKieScannerUpdateResultsEvent(KieScannerUpdateResultsEvent arg0){log.debug("KieScanner.onUpdateResultsEvent("+arg0.getResults().getMessages()+")");}
      public void onKieScannerStatusChangeEvent(KieScannerStatusChangeEvent arg0){}
    });
    
    return kieBase.newKieSession();
  }

}
