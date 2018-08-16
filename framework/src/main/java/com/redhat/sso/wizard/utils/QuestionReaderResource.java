package com.redhat.sso.wizard.utils;

import com.redhat.sso.wizard.impl.ClassPathQuestionReader;
import com.redhat.sso.wizard.impl.DefaultQuestionReader;
import com.redhat.sso.wizard.impl.FilePathQuestionReader;

public class QuestionReaderResource {
  
  public static DefaultQuestionReader newFilePathQuestionReader(String path, String worksheetName){
    return new FilePathQuestionReader(path, worksheetName);
  }
  public static DefaultQuestionReader newClassPathQuestionReader(String path, String worksheetName){
    return new ClassPathQuestionReader(path, worksheetName);
  }
}
