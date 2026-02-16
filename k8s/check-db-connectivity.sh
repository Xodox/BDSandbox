#!/bin/bash
# Check database connectivity for BDSandbox app in Kubernetes
set -e
NS="${1:-bdsandbox}"

echo "=== Database connectivity check (namespace: $NS) ==="
echo ""

echo "1. Pods status"
kubectl get pods -n "$NS" -l 'app in (bdsandbox,h2)' -o wide
echo ""

echo "2. H2 service (TCP 9092)"
kubectl get svc h2-service -n "$NS" 2>/dev/null || true
echo ""

echo "3. App environment (SPRING_DATASOURCE_*)"
kubectl exec deployment/bdsandbox -n "$NS" -- env 2>/dev/null | grep SPRING_DATASOURCE || echo "(could not read env)"
echo ""

echo "4. TCP connectivity from app pod to H2"
kubectl exec deployment/bdsandbox -n "$NS" -- sh -c "nc -zv h2-service 9092 2>&1" || echo "(nc failed or not installed)"
echo ""

echo "5. API health (GET /cars - uses DB)"
if curl -sf --connect-timeout 5 "http://localhost:30080/cars" >/dev/null 2>&1; then
  echo "   OK - API responded"
  echo "   Sample: $(curl -s --connect-timeout 3 http://localhost:30080/cars 2>/dev/null | head -c 80)"
else
  echo "   Try: kubectl port-forward svc/bdsandbox-service 30080:8080 -n $NS"
  echo "   Then: curl http://localhost:30080/cars"
fi
echo ""

echo "6. H2 server log (last line)"
kubectl logs deployment/h2 -n "$NS" --tail=1 2>/dev/null || true
echo ""
echo "Done."
