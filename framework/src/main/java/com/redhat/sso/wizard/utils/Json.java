package com.redhat.sso.wizard.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class Json {

  public static ObjectMapper newObjectMapper(boolean pretty){
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,pretty);
    mapper.setSerializationInclusion(Inclusion.NON_NULL);
    return mapper;
  }
}
