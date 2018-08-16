package com.redhat.sso.wizard.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class ClassPathQuestionReader extends DefaultQuestionReader {
  private static final Logger log=Logger.getLogger(ClassPathQuestionReader.class);
  private String path;
  private String worksheetName;
  private KieBase kBase;
  
  public ClassPathQuestionReader(String path, String worksheetName){
    this.path=path;
    this.worksheetName=worksheetName;
  }
  
  private String compileDroolsTemplate(InputStream xls, String template, String sheetName, int startingRow, int startingCol) throws IOException {
    InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream(template);
    return new ExternalSpreadsheetCompiler().compile(xls, sheetName, templateStream, startingRow, startingCol);
  }
  
  public KieSession newKieSession(){
    try{
      int startingRow = 2;
      int startingCol = 1;
      String questionsDrl=compileDroolsTemplate(ResourceFactory.newClassPathResource(path).getInputStream() , "questionsTemplate.drl", worksheetName, startingRow, startingCol);
      log.debug(questionsDrl);
      KnowledgeBuilder qbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
      qbuilder.add(ResourceFactory.newByteArrayResource(questionsDrl.getBytes()), ResourceType.DRL);
      if (qbuilder.hasErrors())
        log.error(questionsDrl);
      kBase = qbuilder.newKieBase();
      
      return kBase.newKieSession();
    }catch(IOException e){
      throw new RuntimeException(e);
    }
  }

}
