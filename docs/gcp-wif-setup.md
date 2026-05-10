# Google Cloud Workload Identity Federation Setup

This guide helps you set up Workload Identity Federation (WIF) for secure GitHub Actions authentication to Google Cloud.

## Prerequisites

- Google Cloud Project with billing enabled
- `gcloud` CLI installed and configured
- GitHub repository with admin access

## Step 1: Enable Required APIs

```bash
gcloud services enable iamcredentials.googleapis.com
gcloud services enable cloudresourcemanager.googleapis.com
gcloud services enable sts.googleapis.com
gcloud services enable serviceusage.googleapis.com
```

## Step 2: Create a Workload Identity Pool

```bash
PROJECT_ID="your-gcp-project-id"
POOL_ID="github-pool"
POOL_DISPLAY_NAME="GitHub Actions"

gcloud iam workload-identity-pools create ${POOL_ID} \
  --project=${PROJECT_ID} \
  --location=global \
  --display-name="${POOL_DISPLAY_NAME}"
```

## Step 3: Create a Workload Identity Provider

```bash
PROVIDER_ID="github-provider"
PROVIDER_DISPLAY_NAME="GitHub Provider"
GITHUB_REPO="your-github-org/your-github-repo"

gcloud iam workload-identity-pools providers create-oidc ${PROVIDER_ID} \
  --project=${PROJECT_ID} \
  --location=global \
  --workload-identity-pool=${POOL_ID} \
  --display-name="${PROVIDER_DISPLAY_NAME}" \
  --attribute-mapping="google.subject=assertion.sub,assertion.aud=assertion.aud,assertion.repository=assertion.repository" \
  --issuer-uri="https://token.actions.githubusercontent.com"
```

## Step 4: Create a Service Account

```bash
SERVICE_ACCOUNT_NAME="github-actions"
SERVICE_ACCOUNT_EMAIL="${SERVICE_ACCOUNT_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"

gcloud iam service-accounts create ${SERVICE_ACCOUNT_NAME} \
  --project=${PROJECT_ID} \
  --display-name="GitHub Actions Service Account"
```

## Step 5: Grant IAM Roles to Service Account

For Terraform infrastructure:

```bash
# Grant roles needed for Terraform
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/compute.admin"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/storage.admin"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/iam.securityAdmin"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/container.admin"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
  --role="roles/artifactregistry.admin"
```

## Step 6: Set up Workload Identity Binding

```bash
# Allow GitHub to impersonate the service account
gcloud iam service-accounts add-iam-policy-binding ${SERVICE_ACCOUNT_EMAIL} \
  --project=${PROJECT_ID} \
  --role="roles/iam.workloadIdentityUser" \
  --principal="principalSet://iam.googleapis.com/projects/${PROJECT_ID}/locations/global/workloadIdentityPools/${POOL_ID}/attribute.repository/${GITHUB_REPO}"
```

## Step 7: Get the Workload Identity Provider Resource Name

```bash
gcloud iam workload-identity-pools providers describe ${PROVIDER_ID} \
  --project=${PROJECT_ID} \
  --location=global \
  --workload-identity-pool=${POOL_ID} \
  --format='value(name)'
```

This will output something like:
```
projects/123456789/locations/global/workloadIdentityPools/github-pool/providers/github-provider
```

## Step 8: Add GitHub Secrets

In your GitHub repository, add these secrets (Settings → Secrets and variables → Actions):

1. **GCP_WIF_PROVIDER**: The full resource name from Step 7
   ```
   projects/123456789/locations/global/workloadIdentityPools/github-pool/providers/github-provider
   ```

2. **GCP_SERVICE_ACCOUNT**: The service account email
   ```
   github-actions@your-project-id.iam.gserviceaccount.com
   ```

3. **TF_STATE_BUCKET**: GCS bucket for Terraform state
   ```
   your-project-id-tfstate
   ```

4. **JWT_SECRET**: JWT secret for your application (generate randomly)
   ```
   your-random-jwt-secret
   ```

## Step 9: Verify Setup

Run a GitHub Actions workflow to test authentication:

```yaml
- name: Verify GCP Authentication
  run: gcloud auth list
```

## Troubleshooting

### Invalid Audience Error

If you get: `Invalid value for "audience". This value should be the full resource name of the Identity Provider`

**Solution**: Ensure `GCP_WIF_PROVIDER` secret contains the complete resource name:
```
projects/{PROJECT_ID}/locations/global/workloadIdentityPools/{POOL_ID}/providers/{PROVIDER_ID}
```

### Permission Denied Errors

- Verify service account has required IAM roles
- Check Workload Identity binding is correctly configured
- Ensure repository path matches in the binding (format: `owner/repo`)

### Token Generation Fails

- Verify issuer URI is exactly: `https://token.actions.githubusercontent.com`
- Check attribute mappings include: `assertion.repository`, `assertion.sub`, `assertion.aud`

## References

- [Google Cloud Workload Identity Federation](https://cloud.google.com/iam/docs/workload-identity-federation)
- [GitHub Actions GCP Authentication](https://github.com/google-github-actions/auth)
- [Setup WIF for GitHub Actions](https://cloud.google.com/iam/docs/workload-identity-federation-with-github-actions)
