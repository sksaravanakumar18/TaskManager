terraform {
  required_version = ">= 1.7.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.30"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 5.30"
    }
  }

  # Remote state stored in GCS — replace bucket name before apply
  backend "gcs" {
    bucket = "cloudops-tfstate-dev"
    prefix = "terraform/state"
  }
}
