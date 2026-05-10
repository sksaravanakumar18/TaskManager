provider "google" {
  project = var.project_id
  region  = var.region
}

provider "google-beta" {
  project = var.project_id
  region  = var.region
}

# ── Networking ──────────────────────────────────────────────────────────────
module "networking" {
  source     = "./modules/networking"
  project_id = var.project_id
  region     = var.region
  env        = var.env
}

# ── GKE Cluster ─────────────────────────────────────────────────────────────
module "gke" {
  source             = "./modules/gke"
  project_id         = var.project_id
  region             = var.region
  env                = var.env
  network_self_link  = module.networking.network_self_link
  subnet_self_link   = module.networking.subnet_self_link
  pods_range_name    = module.networking.pods_range_name
  services_range_name = module.networking.services_range_name
}

# ── Pub/Sub ──────────────────────────────────────────────────────────────────
module "pubsub" {
  source     = "./modules/pubsub"
  project_id = var.project_id
  env        = var.env
}

# ── IAM & Service Accounts ──────────────────────────────────────────────────
module "iam" {
  source                    = "./modules/iam"
  project_id                = var.project_id
  env                       = var.env
  analytics_sa_email        = module.cloudrun.analytics_sa_email
  notification_sa_email     = module.cloudrun.notification_sa_email
  task_sa_email             = module.cloudrun.task_sa_email
  pubsub_topic_task_events  = module.pubsub.topic_task_events
}

# ── Secret Manager ────────────────────────────────────────────────────────────
module "secrets" {
  source     = "./modules/secrets"
  project_id = var.project_id
  env        = var.env
  jwt_secret = var.jwt_secret
}

# ── Cloud Run Services ───────────────────────────────────────────────────────
module "cloudrun" {
  source                = "./modules/cloudrun"
  project_id            = var.project_id
  region                = var.region
  env                   = var.env
  image_tag             = var.image_tag
  artifact_registry_url = var.artifact_registry_url
  pubsub_topic          = module.pubsub.topic_task_events
  jwt_secret_id         = module.secrets.jwt_secret_id
}
