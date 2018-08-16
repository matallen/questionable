package com.redhat.sso.wizard.impl;

import java.io.File;
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

public class FilePathQuestionReader extends DefaultQuestionReader {
  private static final Logger log=Logger.getLogger(FilePathQuestionReader.class);
  private File file;
  private String sheetName;
  private KieBase kBase;
  
  public FilePathQuestionReader(File file, String sheetName){
    this.file=file;
    this.sheetName=sheetName;
  }
  public FilePathQuestionReader(String filePath, String sheetName){
    this.file=new File(filePath);
    this.sheetName=sheetName;
  }
  
  private String compileDroolsTemplate(InputStream xls, String template, String sheetName, int startingRow, int startingCol) throws IOException {
    InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream(template);
    return new ExternalSpreadsheetCompiler().compile(xls, sheetName, templateStream, startingRow, startingCol);
  }
  
  public KieSession newKieSession(){
    try{
      int startingRow = 2;
      int startingCol = 1;
      String questionsDrl = compileDroolsTemplate(ResourceFactory.newFileResource(file).getInputStream(), "questionsTemplate.drl", sheetName, startingRow, startingCol);
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
