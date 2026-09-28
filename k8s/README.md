# Manifestos Kubernetes

Os manifestos usam Kustomize e criam namespace, ConfigMap, Secret de demonstração, bancos, RabbitMQ, aplicações, Services, Ingress, probes, limites e HPA.

Antes de publicar, substitua `OWNER` nas imagens e troque todos os valores `change-me`. Em um ambiente real, remova os valores secretos do arquivo e use o gerenciador de segredos da plataforma.

```bash
kubectl kustomize k8s
kubectl apply -k k8s
kubectl get pods,hpa,ingress -n wallet-platform
```

O HPA depende do Metrics Server e o Ingress requer um controlador NGINX. Para Minikube:

```bash
minikube addons enable metrics-server
minikube addons enable ingress
```
