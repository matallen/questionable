
# How to use the framework

Please see this [Read.ME](https://github.com/matallen/questionable/blob/master/example-app-bc/README.md) for how to use the framework 


# Presentation
[https://docs.google.com/presentation/d/1Pv2fRPEZnfCl7VaO8gFogSROtxtIHwKwkG6NX2nZq2Q](https://docs.google.com/presentation/d/1Pv2fRPEZnfCl7VaO8gFogSROtxtIHwKwkG6NX2nZq2Q)

## Openshift Environment
[https://console.d1.casl.rht-labs.com/console/project/questionable2](https://console.d1.casl.rht-labs.com/console/project/questionable2)

## Master Source Control
[https://github.com/matallen/questionable](https://github.com/matallen/questionable)

note: https://gitlab.consulting.redhat.com/mallen/dynamic-wizard-framework is the old location



# DEMO (Decision Manager Version)

* open browser to the [Business Central-driven Demo App](http://dynamic-wizard-app-questionable2.apps.d1.casl.rht-labs.com/dynamic-wizard-example-app-bc)
* Show features like:
 * validation on Given Name - can't contain numbers
 * "enabledIf" on Family Name once a value has been entered into Given Name
 * Happiness slide-switch controls the visibility of another question
 * Next/Back/Finish is disabled until page validation passes
 * optional pages - if the happiness is <5 then the "Happy Page" isn't displayed
 * custom rule-driven fields
 * calculated field values from multiple question answers


# DEMO (Spreadsheet Version)

### Steps
* open browser to the [XLS-driven Demo App](http://dynamic-wizard-app-questionable2.apps.d1.casl.rht-labs.com/dynamic-wizard-example-app-xls)
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
 * return to your [demo page and refresh](http://dynamic-wizard-app-questionable2.apps.d1.casl.rht-labs.com/dynamic-wizard-example-app-xls) to see your question changes take effect



## To Develop

### Create project & deploy Decision Manager
```
./create-app.sh <new project name>
```

One manual post-install step is that you will then need to login to Decision Manager UI (dm7Admin/dm7Admin!) and re-add the dynamic-questionnaire-model dependency to the project and "build & deploy" the project before trying the demo app.


 
