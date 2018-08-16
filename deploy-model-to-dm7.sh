cd model
mvn clean package
cd -
mvn deploy:deploy-file -DgroupId=com.redhat.sso -DartifactId=dynamic-wizard-model -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=model/target/dynamic-wizard-model-1.0-SNAPSHOT.jar -DrepositoryId=dq-rmurhamm -Durl=http://rhdm7-install-rhdmcentr-dynamic-questionnaire.apps.d2.casl.rht-labs.com/maven2/
