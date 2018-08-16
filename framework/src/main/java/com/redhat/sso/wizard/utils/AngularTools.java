package com.redhat.sso.wizard.utils;

public class AngularTools {
  
  public static final String ANGULAR_MODEL_NAME="models";
  
  public static String convertInjectedExpression(String toConvert){
    if (null!=toConvert){
      while(toConvert.matches(".*\\{.*\\}.*")){
        int s=toConvert.indexOf("{");
        int e=toConvert.indexOf("}")+1;
        String value=toConvert.substring(s+1, e-1);
        String newValue=ANGULAR_MODEL_NAME+"."+value;
        toConvert=new StringBuffer(toConvert).delete(s, e).insert(s, newValue).toString();
      }
    }
    return toConvert;
  }
}
