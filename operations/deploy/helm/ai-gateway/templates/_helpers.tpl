{{- define "aigw.labels" -}}
app: {{ .Values.app.name }}
app.kubernetes.io/name: ai-gateway
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- /* URL 포트는 설정 원문에서만 계산합니다. */ -}}
{{- define "aigw.endpoint" -}}
{{- $url := required "주소가 필요합니다" .url -}}
{{- if not (regexMatch "^https?://[^/:@?#[:space:]]+(:[0-9]+)?(/[^?#[:space:]]*)?$" $url) }}{{ fail "HTTP 주소 형식이 잘못되었습니다" }}{{ end -}}
{{- $authority := regexReplaceAll "^https?://" (regexFind "^https?://[^/]+" $url) "" -}}
{{- $parts := splitList ":" $authority -}}
{{- $host := first $parts -}}
{{- $port := ternary 443 80 (hasPrefix "https://" $url) -}}
{{- if eq (len $parts) 2 -}}
  {{- $port = int (last $parts) -}}
{{- else if .explicit -}}
  {{- fail "명시적 포트가 필요합니다" -}}
{{- end -}}
{{- if or (lt (int $port) 1) (gt (int $port) 65535) }}{{ fail "포트 범위가 잘못되었습니다" }}{{ end -}}
{{- if .ipv4 -}}
  {{- if not (regexMatch "^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$" $host) }}{{ fail "IPv4 숫자 주소가 필요합니다" }}{{ end -}}
  {{- range splitList "." $host -}}{{- if or (gt (int .) 255) (gt (len .) 3) }}{{ fail "IPv4 주소가 잘못되었습니다" }}{{ end -}}{{- end -}}
{{- end -}}
{{- dict "host" $host "port" $port | toJson -}}
{{- end -}}

{{- define "aigw.guard" -}}
{{- $config := required "appConfig가 필요합니다" .Values.appConfig | fromYaml -}}
{{- $gateway := required "ai.gateway 설정이 필요합니다" (dig "ai" "gateway" dict $config) -}}
{{- $_ := required "이미지 tag가 필요합니다" .Values.app.imageTag -}}
{{- $backends := list -}}
{{- $gitlab := false -}}
{{- range $id, $system := (default dict $gateway.systems) -}}
  {{- if $system.backendUrl -}}
    {{- $backends = append $backends $id -}}
    {{- $_ := include "aigw.endpoint" (dict "url" $system.backendUrl "explicit" false "ipv4" false) -}}
  {{- end -}}
  {{- if $system.repositories }}{{ $gitlab = true }}{{ end -}}
{{- end -}}
{{- if ne (toJson (sortAlpha $backends)) (toJson (keys .Values.systems | sortAlpha)) }}{{ fail "backend 시스템과 selector 키 집합이 다릅니다" }}{{ end -}}
{{- range $id, $system := .Values.systems -}}
  {{- $_ := required "backend namespace가 필요합니다" $system.backend.namespace -}}
  {{- $_ := required "backend podSelector가 필요합니다" $system.backend.podSelector -}}
{{- end -}}
{{- $targets := required "netpol.targets가 필요합니다" .Values.netpol.targets -}}
{{- range $key := list "db" "dns" "loki" "tempo" "otlp" -}}
  {{- $_ := required (printf "공용 대상이 필요합니다: %s" $key) (index $targets $key) -}}
{{- end -}}
{{- if and $gitlab (not $targets.gitlab) }}{{ fail "등록 저장소의 GitLab 대상이 필요합니다" }}{{ end -}}
{{- $_ := include "aigw.endpoint" (dict "url" $gateway.llm.baseUrl "explicit" true "ipv4" true) -}}
{{- $_ := include "aigw.endpoint" (dict "url" $gateway.documents.s3Endpoint "explicit" true "ipv4" true) -}}
{{- end -}}

{{- define "aigw.peer" -}}
{{- if .cidr }}
- ipBlock:
    cidr: {{ .cidr | quote }}
{{- else }}
- namespaceSelector:
    matchLabels:
      kubernetes.io/metadata.name: {{ .namespace | quote }}
  podSelector:
    matchLabels:
      {{- toYaml .podSelector | nindent 6 }}
{{- end }}
{{- end -}}
