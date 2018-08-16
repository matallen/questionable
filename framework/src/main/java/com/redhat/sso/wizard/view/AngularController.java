package com.redhat.sso.wizard.view;

import com.redhat.sso.wizard.domain.DefaultJS;
import com.redhat.sso.wizard.domain.Group;
import com.redhat.sso.wizard.domain.Question;
import com.redhat.sso.wizard.utils.AngularTools;

public class AngularController extends DefaultController{

  /* convert the {Q} style onChange syntax to the Angular syntax of "models.<field-name>*/
  public DefaultJS clientSideTransform(DefaultJS result){
    for(Group g:result.getGroups()){
      for(Question q:g.getQuestions()){
        
//        String ocBefore=q.getOnChange();
//        String ttBefore=q.getTitle();
//        String eiBefore=q.getEnabledIf();
//        String viBefore=q.getVisibleIf();
        
        q.setTitle(AngularTools.convertInjectedExpression(q.getTitle()));
        q.setOnChange(AngularTools.convertInjectedExpression(q.getOnChange()));
//        q.setTitle(AngularTools.convertInjectedExpression(q.getTitle())); // doesn't work because text is not an ng-* attribute so it doesn't get processed! argh!
        q.setEnabledIf(AngularTools.convertInjectedExpression(q.getEnabledIf()));
        q.setVisibleIf(AngularTools.convertInjectedExpression(q.getVisibleIf()));
        
//        String ttAfter=q.getTitle();
//        String ocAfter=q.getOnChange();
//        String eiAfter=q.getEnabledIf();
//        String viAfter=q.getVisibleIf();
        
//        System.out.println("CLIENT-SIDE TRANSFORM:: ID="+q.getId()+" title=("+ttBefore+"|"+ttAfter+") onChange("+ocBefore+"|"+ocAfter+") EnabledIf("+eiBefore+"|"+eiAfter+") VisibleIf("+viBefore+"|"+viAfter+")");
      }
    }
    return result;
  }
}
