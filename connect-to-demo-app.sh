oc rsh `oc get pods -l=deploymentconfig=dynamic-wizard-app --template='{{ range .items }} {{ .metadata.name }} {{ end }}' | xargs`
