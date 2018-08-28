package com.redhat.sso.wizard.view;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.mvel2.MVEL;

import com.redhat.sso.wizard.domain.DefaultJS;
import com.redhat.sso.wizard.domain.Group;
import com.redhat.sso.wizard.domain.Question;
import com.redhat.sso.wizard.impl.BusinessCentralQuestionReader;
import com.redhat.sso.wizard.impl.ClassPathQuestionReader;
import com.redhat.sso.wizard.impl.QSession;
import com.redhat.sso.wizard.impl.QuestionReaderConfig;
import com.redhat.sso.wizard.session.EhCacheSessionManager;
import com.redhat.sso.wizard.session.SessionManager;
import com.redhat.sso.wizard.utils.Json;
import com.redhat.sso.wizard.utils.ListToMap;
import com.redhat.sso.wizard.utils.Lists;
import com.redhat.sso.wizard.utils.MapBuilder;
import com.redhat.sso.wizard.utils.Triple;

@Path("/default")
public class DefaultController implements Controller{
  private static final Logger log=Logger.getLogger(DefaultController.class);
  private QuestionReader questionReader=null;
  private SessionManager sessionManager=null;
  
  
  //Overridable
  public SessionManager createSessionManager(){
    log.debug("DefaultController:: creating default EhCacheSessionManager");
    return new EhCacheSessionManager("qSessions");
  }
  
  //Overridable
  public QuestionReader createQuestionReader(){
    log.debug("createQuestionReader() called");
    log.debug("classname = "+getClass().getSimpleName());
    for(Annotation a:getClass().getAnnotations()){
      System.out.println("Annotation: "+a.toString());
    }
    if (null!=getClass().getAnnotation(QuestionReaderConfig.class)){
      QuestionReaderConfig annotation=this.getClass().getAnnotation(QuestionReaderConfig.class);
      log.debug("QuestionReaderConfig annotation found");
      if ("".equals(annotation.path())){
        // business central
        if(!validateGavString(annotation.gav())){
          throw new RuntimeException("ReleaseId's MUST have 3 elements, ie. 'com.parent:artifactId:version'");
        }
        String[] releaseId=annotation.gav().split(":");
        log.debug("QuestionReader annotation specified type 'BusinessCentral' using releaseId of "+annotation.gav());
        questionReader=new BusinessCentralQuestionReader(KieServices.Factory.get()
            .newReleaseId(releaseId[0], releaseId[1], releaseId[2]), annotation.interval());
        return questionReader;
      }else{
        // classpath
        log.debug("QuestionReaderConfig annotation specified type 'ClassPath' of "+annotation.path() +", and worksheet name '"+annotation.worksheetName()+"'");
        new ClassPathQuestionReader(annotation.path(), annotation.worksheetName());
      }
      
    }
    log.debug("no QuestionReaderConfig annotation found, defaulting to 'questions.xls' on the classpath");
    return new ClassPathQuestionReader("questions.xls", "Sheet1");
  }
  
  public boolean validateGavString(String gav){
    String[] releaseId=gav.split(":");
    if (releaseId.length!=3)
      return false;
    return true;
  }
  
  public ReleaseId getReleaseId(String defaultGAVIfNotFound){
    String[] releaseIdParts;
    if (null!=System.getenv("QUESTIONS_GAV_OVERRIDE") && validateGavString(System.getenv("QUESTIONS_GAV_OVERRIDE"))){
      releaseIdParts=System.getenv("QUESTIONS_GAV_OVERRIDE").split(":");
    }else if (validateGavString(defaultGAVIfNotFound)){
      releaseIdParts=defaultGAVIfNotFound.split(":");
    }else{
      throw new RuntimeException("Release Id MUST be in the format \"group:artifact:version\"");
    }
    return KieServices.Factory.get().newReleaseId(releaseIdParts[0], releaseIdParts[1], releaseIdParts[2]);
  }
  
  /*private*/ public SessionManager getSessionManager(){
    if (sessionManager==null) sessionManager=createSessionManager();
    return sessionManager;
  }
  public QSession getSession(String sessionId) throws IOException{
    return getSessionManager().retrieveSession(sessionId);
  }
  
  public void saveSession(QSession session) throws IOException{
    getSessionManager().saveSession(session);
  }
  
  public QuestionReader getQuestionReader(){
    if (null==questionReader) questionReader=createQuestionReader();
    log.debug("Initialized QuestionReader: "+questionReader.getClass().getSimpleName() +" - from class "+this.getClass().getSimpleName());
    return questionReader;
  }
  
  public DefaultJS clientSideTransform(DefaultJS result){
    return result;
  }
  
  // Injects values into the title text from the value of other questions
  private String serverSideReplaceField(String toConvert, QSession session){
//    log.debug("Convert (before): "+toConvert);
    String before=toConvert;
    if (null!=toConvert){
      while(toConvert.matches(".*\\{.*\\}.*")){
        int s=toConvert.indexOf("{");
        int e=toConvert.indexOf("}")+1;
        String reference=toConvert.substring(s+1, e-1);
        String newValue="";
        for(Group gRef:session.getGroups().values()){
          
//          if (gRef.getProperties().containsKey(reference))
//          gRef.getProperties().en
          for (Question qRef:gRef.getQuestions()){
            if (qRef.getId().equals(reference)){
              if (qRef.getValue() instanceof String){
                newValue=(String)qRef.getValue();
              }else{
                newValue=String.valueOf(qRef.getValue());
              }
              break;
            }
          }
        }
        toConvert=new StringBuffer(toConvert).delete(s, e).insert(s, newValue).toString();
      }
    }
    if (!before.equals(toConvert))
      log.debug("Convert: before=["+before+"], after=["+toConvert+"]");
    return toConvert;
  }
  
  /* inject values from one field into a field (title, value etc..) of another */
  
  // TODO: DEFECT! this rewrites back to the original field, so it can never be re-initialised if a user goes back and corrects the original dependent value
  public Group serverSideValueReplace(QSession session, String groupNameToDisplay){
    log.debug("serverSideValueReplace(): groupNameToDisplay = "+groupNameToDisplay);
    Group result=session.getGroup(groupNameToDisplay);
    log.debug("serverSideValueReplace(): group found? "+(result!=null?"Yes":"No"));
    for(Question q:result.getQuestions()){
      
      // TODO: This server-side logic doesnt work. we need to store the "original" config, and replace the actual field value on page load each time, otherwise it only happens the first time through
      
      // Title
//      q.setTitle(serverSideReplaceField(q.getTitle(), session));
      
      // Value
//      if (q.getValue() instanceof String)
//        q.setValue(serverSideReplaceField((String)q.getValue(), session));
      
      // Choices / Custom config
//      if (q.getChoices()!=null && q.getChoices().size()>0){
//        List<String> newChoices=new ArrayList<String>();
//        for(String choice:q.getChoices()){
//          choice=serverSideReplaceField(choice, session);
//          newChoices.add(choice);
//        }
//        q.getChoices().clear();
//        q.getChoices().addAll(newChoices);
//      }
      
      // Custom
      if (StringUtils.isNotEmpty(q.getOriginalCustom())){
        q.setCustom(serverSideReplaceField(q.getOriginalCustom(), session));
      }
      
      
//      q.setOnChange(convert(q.getOnChange(), session));
    }
    return result;
  }
  
  /**
   * Stores your new session for you also
   */
  public QSession newSession() throws IOException{
    QSession session=getQuestionReader().newSession();
    getSessionManager().saveSession(session);
    return session;
  }

  @GET
  @Path("/pages/start")
  public Response start(@Context HttpServletRequest request) throws IOException{
    log.debug("Start:: Called");
    String result=Json.newObjectMapper(true).writeValueAsString(start());
    log.debug("Start:: Returning: "+result);
    return Response.status(200).entity(result).build();
  }
  public Object start() throws IOException{
    QSession session=getQuestionReader().newSession();
    getSessionManager().saveSession(session);
    
    String firstGroupName=session.firstGroup();
    Group groupToDisplay=session.getGroup(firstGroupName);
    Triple<Boolean, Boolean, Boolean> buttonStatus = session.getButtonStatus(groupToDisplay.getName());
    DefaultJS result=new DefaultJS(session.getId(), new Lists.Builder<Group>().add(groupToDisplay).build(), buttonStatus.getFirst(), buttonStatus.getSecond(), buttonStatus.getThird());
    return clientSideTransform(result);
  }
  
  @POST
  @Path("/pages/next")
  public Response next(@Context HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException{
    log.debug("Next:: Called");
    String sessionId=request.getParameter("sessionId");
    if (null==sessionId) return Response.status(500).entity("No SessionId parameter provided").build();
    QSession session=getSession(sessionId);
    if (session==null) return Response.status(500).entity("Unable to find existing session for Id: "+sessionId).build();
    String payload=IOUtils.toString(request.getInputStream());
    String result=Json.newObjectMapper(true).writeValueAsString(next(session, payload));
    log.debug("Next:: Returning -> "+result);
    return Response.status(200).entity(result).build();
  }
  public Object next(QSession session, String payload) throws IOException{
    return backOrNext(session, payload, true);
  }
  
  @POST
  @Path("/pages/back")
  public Response back(@Context HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException{
    log.debug("Back:: Called");
    String sessionId=request.getParameter("sessionId");
    if (null==sessionId) return Response.status(500).entity("No SessionId parameter provided").build();
    QSession session=getSession(sessionId);
    if (session==null) return Response.status(500).entity("Unable to find existing session for Id: "+sessionId).build();
    String payload=IOUtils.toString(request.getInputStream());
    String result=Json.newObjectMapper(true).writeValueAsString(back(session, payload));
    log.debug("Back:: Returning -> "+result);
    return Response.status(200).entity(result).build();
  }
  public Object back(QSession session, String payload) throws IOException{
    return backOrNext(session, payload, false);
  }
  
  @POST
  @Path("/pages/finish")
  public Response finish(@Context HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException{
    log.debug("Finish:: Called");
    String sessionId=request.getParameter("sessionId");
    if (null==sessionId) return Response.status(500).entity("No SessionId parameter provided").build();
    QSession session=getSession(sessionId);
    if (session==null) return Response.status(500).entity("Unable to find existing session for Id: "+sessionId).build();
    String payload=IOUtils.toString(request.getInputStream());
    String result=Json.newObjectMapper(true).writeValueAsString(finish(session, payload));

    log.debug("Finish:: Returning -> "+result);
    return Response.status(200).entity(result).build();
  }
  public Object finish(QSession session, String payload) throws IOException{
    return new MapBuilder<String, Object>().put("sessionId", session.getId()).build();
//    return backOrNext(session, payload, true);
  }

  @GET
  @Path("/pages/result")
  public Response result(@Context HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException{
    log.debug("Result:: Called");
    String sessionId=request.getParameter("sessionId");
    if (null==sessionId) return Response.status(500).entity("No SessionId parameter provided").build();
    QSession session=getSession(sessionId);
    if (session==null) return Response.status(500).entity("Unable to find existing session for Id: "+sessionId).build();
    
    ObjectMapper mapper = Json.newObjectMapper(true);
    String result = mapper.writeValueAsString(result(session));
    
    log.debug("Result:: Returning -> "+result);
    return Response.status(200).entity(result).build();
  }
  
  public Object result(QSession session) throws IOException {
    
    //TODO: It may be nicer to create a new map and set only question id, title and value to remove all the "structural" aspects of asking the questions rather than just displaying the information
    
    List<Map<String,Object>> groups=new ArrayList<Map<String,Object>>();
    for(Group g:session.getGroups().values()){
      Map<String,Object> group=new HashMap<String, Object>();
      group.put("name", g.getName());
      List<Map<String,Object>> questions=new ArrayList<Map<String,Object>>();
      for(Question q:g.getQuestions()){
        Map<String,Object> question=new HashMap<String, Object>();
        question.put("group", g.getName());
        question.put("id",    q.getId());
        question.put("type",  q.getType());
        question.put("title", q.getTitle());
        question.put("value", q.getValue());
        questions.add(question);
      }
      group.put("questions", questions);
      groups.add(group);
    }
    
    return groups;
  }

  
  // TODO: Mat - Attempt to reduce duplicated code - come back to this later when you have more time
//  private String checkSessionAndGetPayload(HttpServletRequest request) throws IOException{
//    String sessionId=request.getParameter("sessionId");
//    if (null==sessionId) throw new IOException("No SessionId parameter provided");// return Response.status(500).entity("No SessionId parameter provided").build();
//    QSession session=getSession(sessionId);
//    if (session==null) throw new IOException("Unable to find existing session for Id: "+sessionId);//return Response.status(500).entity("Unable to find existing session for Id: "+sessionId).build();
//    return IOUtils.toString(request.getInputStream());
//  }
  
  
//  private Map<String, Tuple<String, Question>> getAllQuestions(QSession session){
//    Map<String, Tuple<String, Question>> result=new HashMap<String, Tuple<String, Question>>();
//    for(Group g:session.getGroups().values()){
//      for(Question q:g.getQuestions()){
//        result.put(q.getId(), new Tuple<String, Question>(g.getName(), q));
//      }
//    }
//    return result;
//  }
  private Map<String, Object> allQuestionsAsKeyValueMap(QSession session){
    Map<String, Object> result=new HashMap<String, Object>();
    for(Group g:session.getGroups().values()){
      for(Question q:g.getQuestions()){
        result.put(q.getId(), q.getValue());
      }
    }
    return result;
  }
  
  
  private DefaultJS backOrNext(QSession session, String payload, boolean next) throws JsonGenerationException, JsonMappingException, IOException{
    
    // save data
    String currentGroupName=save(payload, session); //returning the group name is a bit of a hack but it's ok for now, saves double parsing the payload to keep encapsulation
    
    // find next group/page
    log.debug("BackOrNext:: Finding "+(next?"next":"previous")+" group from ["+currentGroupName+"]");
    boolean canLeaveCurrentPage=!next || checkAnswers(session.getGroup(currentGroupName));
    session.getGroup(currentGroupName).getProperties().put("Valid", true);
    log.debug("BackOrNext:: Are all questions valid & able to move to next page?: "+canLeaveCurrentPage);
    String groupNameToDisplay=currentGroupName;
    if (canLeaveCurrentPage){
      groupNameToDisplay=next?session.nextGroup(currentGroupName):session.previousGroup(currentGroupName);
    }
    log.debug("BackOrNext:: looking for group name: "+groupNameToDisplay);
    
    
    if (canLeaveCurrentPage){
      String nextGroupName=next?session.nextGroup(currentGroupName):session.previousGroup(currentGroupName);
      log.debug("BackOrNext:: canLeaveCurrentPage=True; currentGroup="+currentGroupName+"; nextGroup="+nextGroupName);
      Map<String, Object> allQuestions = allQuestionsAsKeyValueMap(session);
      
      // loop until we find a page with at least one visible control
      while (nextGroupName!=null && !atLeastOneQuestionIsVisible(nextGroupName, allQuestions, session)){
        nextGroupName=next?session.nextGroup(nextGroupName):session.previousGroup(nextGroupName);
      }
      groupNameToDisplay=nextGroupName;
    }else{
      groupNameToDisplay=currentGroupName;
    }
    
    DefaultJS result;
    if (groupNameToDisplay!=null){
      // process any fields that reference other question values
      Group groupToDisplay=serverSideValueReplace(session, groupNameToDisplay).copy(); // clone the object so any server-side "transforming" doesn't affect the source objects
      log.debug("BackOrNext:: found. Displaying group \""+groupToDisplay.getName()+"\"");
      
      
      Triple<Boolean, Boolean, Boolean> buttonStatus = session.getButtonStatus(groupNameToDisplay);
      
      log.debug("BackOrNext:: Button status (next:"+buttonStatus.getFirst()+", back:"+buttonStatus.getSecond()+", finish:"+buttonStatus.getThird()+")");
      
      result=new DefaultJS(session.getId(), new Lists.Builder<Group>().add(groupToDisplay).build(), buttonStatus.getFirst(), buttonStatus.getSecond(), buttonStatus.getThird());
    }else{
      // then there's no groups to display, goto FINISH
      //TODO: This has not been implemented correctly yet. If the last page is skipped due to non-visible controls then there is no way of handling this yet, so this line is considered temporary and only to prevent an NPE
      result=new DefaultJS(session.getId(), null, false, false, false);
    }
    
    return clientSideTransform(result);
  }
  
  private boolean atLeastOneQuestionIsVisible(String nextGroupName, Map<String, Object> allQuestions, QSession session){
    log.debug("atLeastOneQuestionIsVisible:: nextGroupName = "+nextGroupName);
    Group nextGroup=session.getGroup(nextGroupName);
//    Group nextGroup=serverSideValueReplace(session, nextGroupName);
    
    // if next page has at least one field visible then display
    for(Question q:nextGroup.getQuestions()){
      
      //if it's true then ignore it (default to true)
      if (q.getVisibleIf()==null || "true".equals(q.getVisibleIf().toLowerCase().trim()))
        return true;
      
      Map<String, Object> data=new MapBuilder<String, Object>().putAll(allQuestions).build();
      
      //convert the expression to an MVEL one. MVEL can eval a lot of the javascript expressions, but the object referencing is different, it doesn't need any wrapping characters
      String visibleIfExpression=q.getVisibleIf().replaceAll("\\{", "").replaceAll("\\}", "");
      
      if ((Boolean)MVEL.eval(visibleIfExpression, data)) return true;
    }
    return false;
  }
  
  
  /** ######## CHECK ANSWERS ######## */
  public boolean checkAnswers(Group group){
    log.debug("Next:: Checking answers for group ["+group.getName()+"]");
    boolean allQuestionsValid=true;
    for(Question q:group.getQuestions()){
      
      if (null==q.getValidation() && null!=q.getValue()){
        allQuestionsValid=allQuestionsValid && true;
      }else if (null!=q.getValidation() && null!=q.getValue()){
        allQuestionsValid=allQuestionsValid && String.valueOf(q.getValue()).matches(q.getValidation());
      }
      if(q.isRequired() && q.getValue()==null)
        allQuestionsValid=false;
      
      log.debug("CheckAnswers:: Checking Question "+q.getId()+":: value="+q.getValue()+", validation="+q.getValidation()+", allQuestionsValid after check="+allQuestionsValid);
    }
    return allQuestionsValid;
  }
  
  public HashMap<String,Object> parsePayload(String payload) throws JsonParseException, JsonMappingException, IOException{
    return Json.newObjectMapper(false).readValue(payload, new TypeReference<Map<String, Object>>() {});
  }
  
  
  /** ######## SAVE ########## **/
  private String save(String payload, QSession session) throws IOException{
    log.debug("Save:: "+payload);
    
    HashMap<String,Object> mapOfValuesToSave = parsePayload(payload);
    
    // TODO: Argh! we dont know what page/group these answers are on!
    String groupName=(String)mapOfValuesToSave.get("group");
    log.debug("Save:: saving items for group: "+ groupName);
    
    mapOfValuesToSave.remove("sessionId");
    mapOfValuesToSave.remove("group");
    
    // save values
    Group group=session.getGroup(groupName);
    log.debug("Save:: Searched for group '"+groupName+"', found -> "+group);
    
    
    Map<String,Question> questionMap=new ListToMap<String, Question>() {
      @Override
      public String getKey(Question entity) {
        return entity.getId();
      }
    }.from(group.getQuestions());
    
    for(Entry<String, Object> e:mapOfValuesToSave.entrySet()){
      Question q=questionMap.get(e.getKey());
      if (q==null) continue;
      q.setValue(e.getValue());
    }
    
    // save in storage
    saveSession(session);
    
    return groupName;
  }

  
//  private QSession generateTESTSession(){
//    Group group1=new Group();
//    group1.getQuestions().add(
//        new Question(new MapBuilder<String, Object>()
//        .put("QuestionID", "X")
//        .put("Group", "Page1")
//        .put("Type", "text")
//        .put("Title", "X")
//        .put("Choices", "gatewayId={Dependent}")
//        .put("Value", "True")
//        .put("VisibleIf", "true")
//        .put("#", com.redhat.sso.wizard.impl.DefaultQuestionReader.counter+=1)
//        .build()));
//    
//    Group group2=new Group();
//    group2.getQuestions().add(
//        new Question(new MapBuilder<String, Object>()
//        .put("QuestionID", "Dependent")
//        .put("Group", "Page2")
//        .put("Type", "text")
//        .put("Title", "Dependent")
//        .put("VisibleIf", "{X}==true")
//        .put("Value", "Fred")
//        .put("#", com.redhat.sso.wizard.impl.DefaultQuestionReader.counter+=1)
//        .build()));
//    group2.getQuestions().add(
//        new Question(new MapBuilder<String, Object>()
//        .put("QuestionID", "GivenName")
//        .put("Group", "Page2")
//        .put("Type", "text")
//        .put("Title", "Given Name")
//        .put("Validation", "^[a-zA-Z]*$")
//        .put("EnabledIf", "true")
//        .put("VisibleIf", "{Dependent}=='Fred'")
//        .put("ErrorMessage", "Must contain only letters")
//        .put("#", com.redhat.sso.wizard.impl.DefaultQuestionReader.counter+=1)
//        .build())
//        );
//    Map<String,Group> groups=new MapBuilder<String,Group>().put("Page1", group1).put("Page2", group2).build();
//    Map<String,String> transitions=new MapBuilder<String,String>().put("Page1","").put("Page2","").build();
//    
//    QSession session=new QSession(groups, transitions);
//    return session;
//  }

}

