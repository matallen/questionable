package com.redhat.sso.wizard.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.BeanSerializer;
import org.codehaus.jackson.map.ser.BeanSerializerModifier;
import org.codehaus.jackson.map.ser.std.BeanSerializerBase;

public class FilteredModule extends SimpleModule {
  private static final long serialVersionUID = 1L;
  
  public FilteredModule(String name, Version version) {
    super(name, version);
  }
  
  @Override
  public void setupModule(SetupContext context) {
      super.setupModule(context);

      context.addBeanSerializerModifier(new BeanSerializerModifier() {
          @SuppressWarnings("unused")
          public JsonSerializer<?> modifySerializer(
                  SerializationConfig config,
                  BeanDescription beanDesc,
                  JsonSerializer<?> serializer) {
              if (serializer instanceof BeanSerializerBase) { 
                  return new FilteredBeanSerializer((BeanSerializerBase) serializer);
              } 
              return serializer; 

          }                   
      });
  }

  private class FilteredBeanSerializer extends BeanSerializer {

      public FilteredBeanSerializer(BeanSerializerBase source) {
          super(source);
      }

      @Override
      protected void serializeFields(Object arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException, JsonGenerationException {
          super.serializeFieldsFiltered(arg0, arg1, arg2);
      }

  }
}