package com.redhat.sso.wizard.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.redhat.sso.wizard.domain.Question;
import com.redhat.sso.wizard.impl.BusinessCentralQuestionReader;
import com.redhat.sso.wizard.impl.QSession;
import com.redhat.sso.wizard.session.EhCacheSessionManager;
import com.redhat.sso.wizard.session.SessionManager;
import com.redhat.sso.wizard.utils.FluentRulesService;
import com.redhat.sso.wizard.utils.Json;

@Path("/simple")
//@QuestionReaderConfig(type=Type.BusinessCentral, gav="com.myteam:questions:LATEST")
public class SimpleController extends AngularController{
  
  // ###################################
  // ## Define the session management ##
  // ###################################
  public SessionManager createSessionManager(){
    return new EhCacheSessionManager();
  }
  

  // ######################
  // ## Define the rules ##
  // ######################
  @Override
  public QuestionReader createQuestionReader(){
    return new BusinessCentralQuestionReader(getReleaseId("com.myteam:questions:LATEST"), 10000l);
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
    
    String questionId=request.getParameter("id");
    String controlType=request.getParameter("type");
    Question fact=session.getQuestion(questionId);
    
    if (loanInterestRateRulesService==null){
      Double result=new FluentRulesService()
      .withReleaseId("com.myteam:loan-interest-rate:LATEST")
      .withRuleLoggingAgendaListener()
      .execute(fact)
      .getResult(Double.class);
      return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(result/100)).build();
    }else{
      return Response.status(500).build();
    }
  }
}
