

#ProjectName="dynamic-questionnaire"
if [ "x$1" == "x" ]; then
#  echo "Usage: ./create-app.sh <new-project-name>"
  ProjectName=`oc project -q`
else
  ProjectName=$1
  echo "Creating new project: $ProjectName"
  oc new-project "$ProjectName" >/dev/null
fi
echo "Deploying to project: $ProjectName"


PREFIX=rhdm71
BRANCH=$PREFIX-dev

echo "Importing Image Streams"
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/$BRANCH/$PREFIX-image-streams.yaml

echo "Importing Templates"
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/$BRANCH/templates/$PREFIX-full.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/$BRANCH/templates/$PREFIX-kieserver.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/$BRANCH/templates/$PREFIX-kieserver-basic-s2i.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/$BRANCH/templates/$PREFIX-kieserver-https-s2i.yaml

echo "Importing secrets and service account"
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/$BRANCH/kieserver-app-secret.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/$BRANCH/decisioncentral-app-secret.yaml

echo "Creating Decision Manager 7 Application config"
oc new-app --template=$PREFIX-full-persistent \
		-p APPLICATION_NAME="rhdm7-install" \
		-p IMAGE_STREAM_NAMESPACE="$ProjectName" \
		-p KIE_ADMIN_USER="dm7Admin" \
		-p KIE_ADMIN_PWD="dm7Admin!" \
		-p KIE_SERVER_CONTROLLER_USER="kieServer" \
		-p KIE_SERVER_CONTROLLER_PWD="kieServer1!" \
		-p KIE_SERVER_USER="kieServer" \
		-p KIE_SERVER_PWD="kieServer1!" \
		-p MAVEN_REPO_USERNAME="dm7Admin" \
		-p MAVEN_REPO_PASSWORD="dm7Admin!" \
		-p DECISION_CENTRAL_VOLUME_CAPACITY="512Mi"

echo "Deploying Dynamic Questionnaire Demo App"
oc process --param=APPLICATION_NAME=dynamic-wizard-app -f dynamic-wizard-app-build.yaml | oc apply -f-
oc process --param=NAMESPACE=$ProjectName --param=APPLICATION_NAME=dynamic-wizard-app -f dynamic-wizard-app-deployment.yaml | oc apply -f-

