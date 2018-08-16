package com.redhat.sso.wizard.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListSelectionEvent;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Question implements Serializable{
  private String group;
//  private String column;
  private String id;
  private String type;
  private String title;
  private String validation;
  private Object value;
  private boolean enabled;
  private String enabledIf;
  private List<String> choices;
//  private String choices;
  private String originalCustom;
  private String custom;
  private boolean visible;
  private String visibleIf;
  private boolean required;
  private String onChange;
  private String placeholder;
  private String errorMessage;
  private Integer order;
  
  public Question(Map<String, Object> x){
    group=(String)x.get("Group");
    id=(String)x.get("QuestionID");
    type=(String)x.get("Type");
    title=(String)x.get("Title");
    validation=(String)x.get("Validation");
    visibleIf=((String)x.get("VisibleIf"));
    enabledIf=((String)x.get("EnabledIf"));
    required="true".equalsIgnoreCase(((String)x.get("Required")));
    onChange=(String)x.get("OnChange");
    errorMessage=(String)x.get("ErrorMessage");
    placeholder=(String)x.get("Placeholder");
    custom=(String)x.get("Custom");
    originalCustom=(String)x.get("Custom");
    
    String toTest=(String)x.get("Value");
    
    // is value numeric?
    if (null!=toTest){
      if (toTest.matches("[0-9]*")) value=(Integer.parseInt((String)x.get("Value")));
      
      // is value boolean?
//      Pattern p=Pattern.compile("(true|false|yes|no)", Pattern.CASE_INSENSITIVE);
//      if (p.matcher(toTest).matches()){
//        value=Boolean.parseBoolean(toTest.toLowerCase());
//      }
      
      //else it's likely a string
      if (value==null)
        value=toTest;
    }
    
    order=(Integer)x.get("#");
    
//    choices=(String)x.get("Choices");
    if (null!=x.get("Choices")){
      choices=new ArrayList<String>();
      for(String v:((String)x.get("Choices")).split(","))
        if (v!=null && v.trim().length()>0)
          choices.add(v);
    }
    
    if (null==id) throw new RuntimeException("Question.id ["+id+"] is invalid");
    if (null==type) throw new RuntimeException("Question.type ["+type+"] is invalid");
    if (null==title) throw new RuntimeException("Question.title ["+title+"] is invalid");
  }
  
  public String getGroup() {         return group;         }
  public String getId() {            return id;            }
  public String getType() {          return type;          }
  public String getTitle() {         return title;         }
  public String getValidation() {    return validation;    }
  public Object getValue() {         return value;         }
  public String getEnabledIf() {     return enabledIf;     }
  public List<String> getChoices() {
//    System.out.println("getChoices called. choices = "+choices);
//    if (choices!=null){
//      System.out.println("getChoices called. choices.size = "+choices.size());
//    }
    return choices;       }
//  public List<String> getChoices() {
//    List<String> result=new ArrayList<String>();
//    if (null!=choices)
//      for(String s:choices.split(","))
//        result.add(s.trim());
//    return result;
//  }
  public String getCustom() {        return custom;        }
  @JsonIgnore 
  public String getOriginalCustom() {return originalCustom;}
  public String getVisibleIf() {     return visibleIf;     }
  public boolean isRequired() {      return required;      }
  public String getPlaceholder() {   return placeholder;   }
  public String getErrorMessage() {  return errorMessage;  }
  @JsonIgnore                                              
  public Integer getOrder() {        return order;         }
  public String getOnChange() {      return onChange;      }
  
  
  public void setValue(Object value)             { this.value = value;             }
  public void setTitle(String value)             { this.title = value;             }
  public void setEnabled(boolean value)          { this.enabled = value;           }
  public void setCustom(String value)            { this.custom = value;            }
  public void setOnChange(String value)          { this.onChange = value;          }
  public void setVisibleIf(String value)         { this.visibleIf = value;         }
  public void setEnabledIf(String value)         { this.enabledIf = value;         }
  
  // Fields added just for the Business Central Guided DT editor
  public Question(){}
  public void setGroup(String value)             { this.group = value;                             }
  public void setId(String value)                { this.id = value;                                }
  public void setType(String value)              { this.type = value;                              }
  public void setRequired(boolean value)         { this.required = value;                          }
  public void setValue(String value)             { this.value = value;                             }
  public void setValidation(String value)        { this.validation = value;                        }
  public void setChoices(String value)           {
    this.choices=new ArrayList<String>();
    for(String v:value.split(","))
      this.choices.add(v.trim());
  }
  public void setPlaceholder(String value)       { this.placeholder = value;                       }
  public void setErrorMessage(String value)      { this.errorMessage= value;                       }
  public void setOrder(Integer value)            { this.order= value;                              } // due to a frustratingly restricted DT implementation in BC, we have to set this too
  // of Fields for Business Central
  
  public String toString(){
    return "Question["
      +";group="+getGroup()
      +";id="+getId()
      +";title="+getTitle()
      +";type="+getType()
      +";value="+getValue()
      +";enabledIf="+getEnabledIf()
      +";visibleIf="+getVisibleIf()
      +";placeholder="+getPlaceholder()
      +";choices="+getChoices()
      +";errorMessage="+getErrorMessage()
      +"]";
  }
  
  public Question copy(){
    Question q=new Question();
    q.group=this.group;
    q.id=this.id;
    q.type=this.type;
    q.title=this.title;
    q.validation=this.validation;
    q.value=this.value;
    q.enabled=this.enabled;
    q.enabledIf=this.enabledIf;
    q.custom=this.custom;
    q.originalCustom=this.originalCustom;
    q.choices=this.choices;
    q.visible=this.visible;
    q.visibleIf=this.visibleIf;
    q.required=this.required;
    q.onChange=this.onChange;
    q.placeholder=this.placeholder;
    q.errorMessage=this.errorMessage;
    q.order=this.order;
    return q;
  }

}
