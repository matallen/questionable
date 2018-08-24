oc rsync ./support/.niogit `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`:/opt/eap/standalone/data/bpmsuite
oc rsync ./support/maven-repository `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`:/opt/eap/standalone/data/bpmsuite


#oc rsh `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`


