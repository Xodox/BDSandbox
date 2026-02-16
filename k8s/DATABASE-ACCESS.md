# Database access configuration

The application connects to the **H2** database using settings from a **ConfigMap** and a **Secret**.

## Resources

| Resource | Purpose |
|----------|--------|
| `db-configmap.yaml` | `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_DRIVER_CLASS_NAME`, `SPRING_DATASOURCE_USERNAME` |
| `db-secret.yaml` | `SPRING_DATASOURCE_PASSWORD` |

The deployment uses `envFrom` so all keys from the ConfigMap and Secret are set as environment variables in the app pod.

## Apply order

Create the database config **before** the application deployment:

```bash
kubectl apply -f namespace.yaml
kubectl apply -f db-configmap.yaml
kubectl apply -f db-secret.yaml
kubectl apply -f h2-pvc.yaml
kubectl apply -f h2-deployment.yaml
kubectl apply -f h2-service.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

## Changing the database URL

Edit `db-configmap.yaml` and set `SPRING_DATASOURCE_URL`:

- **H2 in-cluster (default):**  
  `jdbc:h2:tcp://h2-service:9092/file:/data/BDSB_H2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- **H2 file (single pod):**  
  `jdbc:h2:file:/app/data/BDSB_H2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`

Then:

```bash
kubectl apply -f db-configmap.yaml
kubectl rollout restart deployment/bdsandbox -n bdsandbox
```

## Changing the password

1. Edit `db-secret.yaml` and set `SPRING_DATASOURCE_PASSWORD` under `stringData` (plain text; Kubernetes encodes it).
2. Apply and restart:
   ```bash
   kubectl apply -f db-secret.yaml
   kubectl rollout restart deployment/bdsandbox -n bdsandbox
   ```

## Verify

```bash
kubectl get configmap bdsandbox-db-config -n bdsandbox -o yaml
kubectl get secret bdsandbox-db-secret -n bdsandbox -o yaml
kubectl exec deployment/bdsandbox -n bdsandbox -- env | grep SPRING_DATASOURCE
```
