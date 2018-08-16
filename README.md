
# How to use the framework

Please see this [Read.ME](https://gitlab.consulting.redhat.com/mallen/dynamic-wizard-framework/blob/master/example-app-bc/README.md) for how to use the framework 


# Presentation
[https://docs.google.com/presentation/d/1Pv2fRPEZnfCl7VaO8gFogSROtxtIHwKwkG6NX2nZq2Q](https://docs.google.com/presentation/d/1Pv2fRPEZnfCl7VaO8gFogSROtxtIHwKwkG6NX2nZq2Q)

## Openshift Environment
[https://console.d2.casl.rht-labs.com/console/project/dynamic-questionnaire](https://console.d2.casl.rht-labs.com/console/project/dynamic-questionnaire) (username: rmurhamm/Password123)

## Master Source Control
[https://gitlab.consulting.redhat.com/mallen/dynamic-wizard-framework](https://gitlab.consulting.redhat.com/mallen/dynamic-wizard-framework)



# DEMO (Spreadsheet Version)

### Steps
* open browser to the [XLS Demo App](http://demo-dynamic-questionnaire.apps.d2.casl.rht-labs.com/dynamic-wizard-example-app-xls/demo.jsp)
* Show features like:
 * required fields need to be completed before next button enables
 * validation on first name allows only letters, so try typing a numeric
 * if the "Do you need a mortgage" slide button is on, it takes you to a different page
 * finally, set the "Do you need a mortgage" to true and click next
 * you can show the "mortgage offer page" which demonstrates the "Your Interest rate will be" field which is a custom calculated field
* Next show changing questions configuration
 * Checkout the [source code for the demo project](https://gogs-dynamic-questionnaire.apps.d2.casl.rht-labs.com/rmurhamm/dynamic-questionnaire) (username: rmurhamm/Password123), and edit some of the questions in the file src/main/resources/questions.xls
 * check those changes back in using git commands (git add, git commit, then git push)
 * a build should automatically trigger, just [check the "demo" build and deployment status here if necessary](https://console.d2.casl.rht-labs.com/console/project/dynamic-questionnaire/overview)
 * return to your [demo page and refresh](http://demo-dynamic-questionnaire.apps.d2.casl.rht-labs.com/dynamic-wizard-example-app-xls/demo.jsp) to see your question changes take effect


## DEMO (Decision Manager Version)

... TODO

CURRENT STATUS: Awaiting fix for app being able to access DM7/maven2 location to pull maven artifacts down (the questions)


## To Develop

### Create project & deploy Decision Manager
```
./openshift-provision.sh
```

### Deploy gogs Git repo
note: this is only required if your openshift cannot see another git repo where the code is stored

```
oc create -f gogs-bxms.yaml
```

### Deploy Model
```
mvn deploy:deploy-file -DgroupId=com.redhat.sso -DartifactId=dynamic-wizard-model -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=dynamic-wizard-model-1.0-SNAPSHOT.jar -DrepositoryId=dq-rmurhamm -Durl=http://rhdm7-install-rhdmcentr-dynamic-questionnaire.apps.d2.casl.rht-labs.com/maven2/
```




# Improvements / TODO
* Create an .niogit copy for quick setup of the questions using DM7. Commit it to source control and add instructions on how to import


 
