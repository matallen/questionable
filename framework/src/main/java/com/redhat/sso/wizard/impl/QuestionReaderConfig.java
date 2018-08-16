package com.redhat.sso.wizard.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QuestionReaderConfig {
  public Type type();
  public long interval() default 10000l;
  public String gav() default "";
  public enum Type{BusinessCentral, XlsDecisionTable}
  
  public String path() default "";
  public String worksheetName() default "Sheet1";
  
}
