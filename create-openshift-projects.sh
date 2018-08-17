
ProjectName="dynamic-questionnaire"

echo "Creating Project: $ProjectName"
oc new-project "$ProjectName" >/dev/null

echo "Importing Image Streams"
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/rhdm70-dev/rhdm70-image-streams.yaml

echo "Importing Templates"
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/rhdm70-dev/templates/rhdm70-full.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/rhdm70-dev/templates/rhdm70-kieserver.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/rhdm70-dev/templates/rhdm70-kieserver-basic-s2i.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/rhdm70-dev/templates/rhdm70-kieserver-https-s2i.yaml

echo "Importing secrets and service account"
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/rhdm70-dev/kieserver-app-secret.yaml
oc create -f https://raw.githubusercontent.com/jboss-container-images/rhdm-7-openshift-image/rhdm70-dev/decisioncentral-app-secret.yaml

echo "Creating Decision Manager 7 Application config"
oc new-app --template=rhdm70-full-persistent \
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


echo "Deploying Gogs"
oc process --param=GOGS_VOLUME_CAPACITY=1Gi --param=DB_VOLUME_CAPACITY=1Gi -f gogs-bxms.yaml | oc apply -f-


echo "Deploying app"
oc process --param=APPLICATION_NAME=dynamic-wizard-app -f dynamic-wizard-app-build.yaml | oc apply -f-
oc process --param=NAMESPACE=$ProjectName --param=APPLICATION_NAME=dynamic-wizard-app -f dynamic-wizard-app-deployment.yaml | oc apply -f-



#echo "Exporting/Importing repo - Getting "
#mkdir tempdeleteme
#cd tempdeleteme
#git clone https://gitlab.consulting.redhat.com/mallen/dynamic-wizard-framework

#cd ..
#rm -rf tempdeleteme
