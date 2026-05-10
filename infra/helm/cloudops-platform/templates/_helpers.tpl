{{- define "cloudops-platform.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "cloudops-platform.fullname" -}}
{{- printf "%s-%s" .Release.Name (include "cloudops-platform.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "cloudops-platform.namespace" -}}
{{- default .Release.Namespace .Values.namespaceOverride -}}
{{- end -}}

{{- define "cloudops-platform.labels" -}}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
app.kubernetes.io/part-of: cloudops-platform
{{- end -}}

{{- define "cloudops-platform.serviceName" -}}
{{- .name -}}
{{- end -}}

{{- define "cloudops-platform.serviceAccountName" -}}
{{- if .config.serviceAccount.create -}}
{{- .name -}}
{{- else -}}
default
{{- end -}}
{{- end -}}

{{- define "cloudops-platform.image" -}}
{{- printf "%s/%s:%s" $.Values.imageDefaults.registry .config.image.repository $.Values.imageDefaults.tag -}}
{{- end -}}
