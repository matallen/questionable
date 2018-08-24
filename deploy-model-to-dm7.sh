#SERVER=http://rhdm7-install-rhdmcentr-questionable2.apps.d1.casl.rht-labs.com

if [ "x$1" == "x" ]; then
  echo "Usage: ./deploy-model-to-dm7.sh <project-name>"
  exit 0
fi


SERVER="http://rhdm7-install-rhdmcentr-$1.apps.d1.casl.rht-labs.com"
echo "Deploying to: $SERVER"

cd model
mvn clean package
cd -
mvn deploy:deploy-file -DgroupId=com.redhat.sso -DartifactId=dynamic-wizard-model -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=model/target/dynamic-wizard-model-1.0-SNAPSHOT.jar -DrepositoryId=dm7 -Durl=$SERVER/maven2/


echo "If you just got a 401:Not Authorized response, then please add the following to your maven ~/.m2/settings.xml file and try again"
echo ""
echo "<servers>"
echo "  <server>"
echo "    <id>dm7</id>"
echo "    <username>dm7Admin</username>"
echo "    <password>dm7Admin!</password>"
echo "  </server>"
echo "</servers>"
echo ""
