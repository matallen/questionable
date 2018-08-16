package com.redhat.sso.wizard.session;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.redhat.sso.wizard.impl.QSession;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhCacheSessionManager extends SessionManager{
  private static final Logger log=Logger.getLogger(EhCacheSessionManager.class);
  public static final String DEFAULT_CACHE_NAME="qSessions";
  private String cacheName;
  
  public EhCacheSessionManager(){
    this.cacheName=DEFAULT_CACHE_NAME;
  }
  public EhCacheSessionManager(String cacheName){
    this.cacheName=cacheName;
  }
  
  private Cache initCache(CacheManager manager){
    if (null==manager.getCache(cacheName)){
      log.debug("Lazy initializing cache: "+cacheName);
      manager.addCache(cacheName);
    }else{
      log.debug("Using existing cache instance");
    }
    
    Cache cache=manager.getCache(cacheName);
    log.debug("Cache size is currently: "+cache.getSize());
    if (cache.getSize()<10)
      log.debug("Cache is small so printing the key's: "+cache.getKeys());

    return cache;
  }
  
  public void saveSession(QSession session) throws IOException{
    Cache cache = initCache(CacheManager.create());
    cache.put(new Element(session.getId(), session));
  }

  public QSession retrieveSession(String sessionId) throws IOException{
    QSession session=null;
    Cache cache = initCache(CacheManager.create());
    
    if (null!=sessionId){
      if (cache.getKeys().contains(sessionId)){
        return (QSession)cache.get(sessionId).getObjectValue();
      }
    }
    
//    if(null==session && createIfNotExists){
//      session = new QuestionReaderImpl().newSession();
//      cache.put(new Element(session.getId(), session));
//      session.execute();
//    }
    
    return session;
  }

}
