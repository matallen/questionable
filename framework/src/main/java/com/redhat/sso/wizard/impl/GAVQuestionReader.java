package com.redhat.sso.wizard.impl;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.management.GAV;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class GAVQuestionReader extends DefaultQuestionReader{
  private String groupId;
  private String artifactId;
  private String version;
  private KieContainer kContainer;
  
  public GAVQuestionReader(String groupId, String artifactId, String version){
    this.groupId=groupId;
    this.artifactId=artifactId;
    this.version=version;
  }
  
  public GAVQuestionReader execute(){
    KieServices kieServices = KieServices.Factory.get();
    ReleaseId gav=kieServices.newReleaseId(groupId, artifactId, version);
    kContainer = kieServices.newKieContainer(gav);
    return this;
  }
  
  public KieSession newKieSession(){
    return kContainer.newKieSession();
  }
}
