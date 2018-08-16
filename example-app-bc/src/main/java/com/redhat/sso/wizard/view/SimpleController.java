package com.redhat.sso.wizard.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.kie.api.KieServices;

import com.redhat.sso.wizard.domain.Question;
import com.redhat.sso.wizard.impl.BusinessCentralQuestionReader;
import com.redhat.sso.wizard.impl.QSession;
import com.redhat.sso.wizard.session.EhCacheSessionManager;
import com.redhat.sso.wizard.session.SessionManager;
import com.redhat.sso.wizard.utils.FluentRulesService;
import com.redhat.sso.wizard.utils.Json;

@Path("/simple")
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
  private BusinessCentralQuestionReader businessCentralQuestionReader;
  @Override
  public QuestionReader createQuestionReader(){
    businessCentralQuestionReader=new BusinessCentralQuestionReader(KieServices.Factory.get()
        .newReleaseId("com.myteam", "questions2", "LATEST"), 10000l);
    return businessCentralQuestionReader;
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
      .withReleaseId("com.myteam","loan-interest-rate","LATEST")
      .withRuleLoggingAgendaListener()
      .execute(fact)
      .getResult(Double.class);
    }
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(result/100)).build();
  }
}
