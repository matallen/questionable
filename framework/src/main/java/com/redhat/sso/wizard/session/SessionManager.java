package com.redhat.sso.wizard.session;

import java.io.IOException;

import com.redhat.sso.wizard.impl.QSession;
import com.redhat.sso.wizard.view.AppProperties;

public abstract class SessionManager {
  
//  private static SessionManager instance;
//  
//  public static SessionManager get(){
//    if (null==instance){
//      String clazzName="";
//      try{
//        clazzName=new AppProperties().getProperty("sessionManager");
////        System.out.println("Initializing "+Controller.class.getSimpleName()+": "+clazzName);
//        instance=(SessionManager)Class.forName(clazzName).newInstance();
//        System.out.println("Initialized "+SessionManager.class.getSimpleName()+": "+clazzName);
//      }catch(IOException e){
//        e.printStackTrace();
//      } catch (ClassNotFoundException e) {
//        e.printStackTrace();
//      } catch (InstantiationException e) {
//        e.printStackTrace();
//      } catch (IllegalAccessException e) {
//        e.printStackTrace();
//      }
//      if (null==instance)
//        throw new RuntimeException("ERROR: No "+SessionManager.class.getSimpleName()+" configured/able to instantiate [specified class was: "+clazzName+"]");
//    }
//    return instance;
//  }
  public abstract void saveSession(QSession session) throws IOException;
  public abstract QSession retrieveSession(String sessionId) throws IOException;
}
