

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

RHDMCENTR_URL_DOMAIN=`oc project | awk '{print $6}' | grep -P '\.(.+)com' -o`




#PREFIX=rhdm70
#BRANCH=$PREFIX-dev

PREFIX=rhdm70
BRANCH=7.0.x



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



# wait for the dm pod to come up
while [[ `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr | grep "1/1.*Running"` != *"rhdmcentr"* ]]; do
  echo "waiting for rhdmcentr to be available..."
  sleep 3
done

# Remove the unwanted KieServer
oc delete dc rhdm7-install-kieserver


DECISION_MANAGER_URL=`oc get routes | grep rhdmcentr | grep -v secure | awk '{print $2}'`

echo "Deploying Dynamic Questionnaire Demo App"
oc process --param=APPLICATION_NAME=dynamic-wizard-app -f dynamic-wizard-app-build.yaml | oc apply -f-
#oc process --param=NAMESPACE=$ProjectName --param=APPLICATION_NAME=dynamic-wizard-app --param=RHDMCENTR_URL_DOMAIN=$RHDMCENTR_URL_DOMAIN -f dynamic-wizard-app-deployment.yaml | oc apply -f-
oc process --param=NAMESPACE=$ProjectName --param=APPLICATION_NAME=dynamic-wizard-app --param=DECISION_MANAGER_URL=$DECISION_MANAGER_URL -f dynamic-wizard-app-deployment.yaml | oc apply -f-


# deploy model
./deploy-model-to-dm7.sh $ProjectName


# deploy .niogit
./deploy-rules.sh


