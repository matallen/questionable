
ProjectName="dynamic-questionnaire"

oc new-project "$ProjectName" >/dev/null

oc process --param=APPLICATION_NAME=dynamic-wizard-app -f dynamic-wizard-app-build.yaml | oc apply -f-
oc process --param=NAMESPACE=$ProjectName --param=APPLICATION_NAME=dynamic-wizard-app -f dynamic-wizard-app-deployment.yaml | oc apply -f-

