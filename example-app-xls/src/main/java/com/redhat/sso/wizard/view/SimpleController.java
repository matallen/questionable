package com.redhat.sso.wizard.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.redhat.sso.wizard.domain.Question;
import com.redhat.sso.wizard.impl.QSession;
import com.redhat.sso.wizard.session.SessionManager;
import com.redhat.sso.wizard.utils.FluentRulesService;
import com.redhat.sso.wizard.utils.Json;
import com.redhat.sso.wizard.utils.QuestionReaderResource;

@Path("/simple")
public class SimpleController extends AngularController{
  
  // ###################################
  // ## Define the session management ##
  // ###################################
  private static Map<String,QSession> sessionStorage=new HashMap<String, QSession>();
  public SessionManager createSessionManager(){
    return new SessionManager() {
      @Override public QSession retrieveSession(String sessionId) throws IOException {
        return sessionStorage.get(sessionId);
      }
      @Override public void saveSession(QSession session) throws IOException {
        sessionStorage.put(session.getId(), session);
      }
    };
  }
  
  // ######################
  // ## Define the rules ##
  // ######################
  @Override
  public QuestionReader createQuestionReader(){
//    return QuestionReaderResource.newFilePathQuestionReader("src/main/resources/questions.xls", "Sheet1");
    return QuestionReaderResource.newClassPathQuestionReader("questions.xls", "Sheet1");
  }
  
  
  // ###################
  // ## Custom Fields ##
  // ###################
  private static FluentRulesService loanInterestRateRulesService;
  @GET
  @Path("/controls/InterestRate")
  public Response InterestRateControl(@Context HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException{
    String sessionId=request.getParameter("sessionId");
    QSession session=getSession(sessionId);
    
    Question fact=session.getQuestion("Age");
    
    if (fact.getValue()==null) fact.setValue(0); //default
    
    Double result=0d;
    if (loanInterestRateRulesService==null){
      result=new FluentRulesService()
//      .withReleaseId("com.myteam","loan-interest-rate","LATEST")
      .withClassPathResource("InterestRate.drl")
      .withRuleLoggingAgendaListener()
      .execute(fact)
      .getResult(Double.class);
    }
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(result/100)).build();
  }
}
