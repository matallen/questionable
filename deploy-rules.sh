oc rsync ./niogit/.niogit `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`:/opt/eap/standalone/data/bpmsuite


#oc rsh `oc get pods -l=deploymentconfig=rhdm7-install-rhdmcentr --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`
