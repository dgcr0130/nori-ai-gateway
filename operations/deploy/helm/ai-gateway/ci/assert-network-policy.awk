# fixture의 URL에서 직접 정한 포트와 namespace·Pod selector를 같은 규칙 안에서 검사합니다.
function finish() {
  if (active && (!ns || !pod || !port)) exit 1
  active = 0
}
/^    # backend-(ingress|egress):/ {
  finish()
  split($0, fields, ": "); split(fields[2], idport, ":")
  id = idport[1]
  if (id !~ /^[ABC]$/) exit 1
  direction = ($0 ~ /backend-ingress/) ? "in" : "out"
  key = direction id
  if (seen[key]++) exit 1
  expectedPort = direction == "in" ? 8080 : (id == "A" ? 8081 : (id == "B" ? 80 : 443))
  expectedName = tolower(id)
  active = 1; ns = pod = port = 0
  next
}
active && /kubernetes.io\/metadata.name:/ { ns = ($2 == "\"ns-" expectedName "\"") }
active && /^              app:/ { pod = ($2 == expectedName || $2 == "\"" expectedName "\"") }
active && /protocol: TCP, port:/ {
  value = $0; sub(/^.*port: /, "", value); sub(/}.*/, "", value)
  port = (value + 0 == expectedPort); finish()
}
END {
  finish()
  for (i = 1; i <= 3; i++) {
    id = substr("ABC", i, 1)
    if (seen["in" id] != 1 || seen["out" id] != 1) exit 1
  }
}
