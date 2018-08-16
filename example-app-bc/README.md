<h1>Dynamic Questionnaire Framework</h1>





<h2>Why use this framework?</h2>

<h2>Benefits for business users</h2>

<ul>
	<li>Quick to use and develop new questionnaires</li>
	<li>Easy to change questions & pages</li>
	<li>All changes are versionable</li>
	<li>Will become more stable and featureful with uptake</li>
	<li></li>
</ul>


<h2>How to use the framework</h2>


<h3>Initial Setup</h3>


Include the dependency:
```xml
		 <dependency>
			<groupId>com.redhat.sso</groupId>
			<artifactId>dynamic-wizard-framework</artifactId>
			<version>${version}</version>
		 </dependency>
```

If you intend to use Business Central, then include the following dependency to enable maven repository integration:
```xml
		<dependency>
			<groupId>org.kie</groupId>
			<artifactId>kie-ci</artifactId>
			<version>7.3.0.Final</version>
		</dependency>
```



<h3>Create your questionnaire controller</h3>

A Default controller has been provided however it must be extended and deployed. So first create a simple controller like this, including your session management and questions reader strategies:
```java
  @Path("/simple")
  @QuestionReaderConfig(type=Type.BusinessCentral, interval=5000l, gav="com.myproject:questions:LATEST")
  public class SimpleController extends AngularController{
    public SessionManager createSessionManager(){
      return new EhCacheSessionManager();
    }
    public QuestionReader createQuestionReader(){
      new BusinessCentralQuestionReader(KieServices.Factory.get().newReleaseId("com.myteam", "questions2", "LATEST"), 10000l);
      return businessCentralQuestionReader;
    }
  }
```

Then configure it to be a rest endpoint by adding the following resteasy config to your web.xml:
```xml
	<context-param>
		<param-name>resteasy.scan</param-name>
		<param-value>true</param-value>
	</context-param>
  <context-param>
      <param-name>resteasy.resources</param-name>
      <param-value>com.redhat.sso.wizard.view.MyShinyNewController</param-value>
  </context-param>
	<context-param>
		<param-name>resteasy.servlet.mapping.prefix</param-name>
		<param-value>/api</param-value>
	</context-param>
	<listener>
		<listener-class>org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap</listener-class>
	</listener>
	<servlet>
		<servlet-name>resteasy</servlet-name>
		<servlet-class>org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>resteasy</servlet-name>
		<url-pattern>/api/*</url-pattern>
	</servlet-mapping>
```

Lastly, create a web page that includes the form framework to display the questions:
```jsp
	<div class="wrapper">
		<div style="padding-bottom:20px">
			<img style="height:65px" src="images/acme-home-loans.png"/>
		</div>
		<!-- include the dynamic questionnaire form here -->
		<div ng-app="myApp">
			<jsp:include page="qf_form.jsp">
				<jsp:param name="controllerClass" value="<%=com.redhat.sso.wizard.view.MyShinyNewController.class.getName()%>"/>
				<jsp:param name="debug" value="false"/>
			</jsp:include>
		</div>
	</div>
```



<h3></h3>
