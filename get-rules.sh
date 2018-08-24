oc rsync `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`:/opt/eap/standalone/data/bpmsuite/.niogit ./support
oc rsync `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`:/opt/eap/standalone/data/bpmsuite/maven-repository ./support
