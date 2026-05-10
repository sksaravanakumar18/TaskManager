# ── VPC ─────────────────────────────────────────────────────────────────────
resource "google_compute_network" "vpc" {
  name                    = "cloudops-${var.env}-vpc"
  auto_create_subnetworks = false
  project                 = var.project_id
}

# ── Subnets ──────────────────────────────────────────────────────────────────
resource "google_compute_subnetwork" "private" {
  name                     = "cloudops-${var.env}-private-subnet"
  ip_cidr_range            = "10.0.0.0/20"
  region                   = var.region
  network                  = google_compute_network.vpc.id
  project                  = var.project_id
  private_ip_google_access = true

  secondary_ip_range {
    range_name    = "pods"
    ip_cidr_range = "10.48.0.0/14"
  }

  secondary_ip_range {
    range_name    = "services"
    ip_cidr_range = "10.52.0.0/20"
  }
}

resource "google_compute_subnetwork" "public" {
  name          = "cloudops-${var.env}-public-subnet"
  ip_cidr_range = "10.1.0.0/24"
  region        = var.region
  network       = google_compute_network.vpc.id
  project       = var.project_id
}

# ── Cloud Router & NAT ───────────────────────────────────────────────────────
resource "google_compute_router" "router" {
  name    = "cloudops-${var.env}-router"
  region  = var.region
  network = google_compute_network.vpc.id
  project = var.project_id
}

resource "google_compute_router_nat" "nat" {
  name                               = "cloudops-${var.env}-nat"
  router                             = google_compute_router.router.name
  region                             = var.region
  project                            = var.project_id
  nat_ip_allocate_option             = "AUTO_ONLY"
  source_subnetwork_ip_ranges_to_nat = "ALL_SUBNETWORKS_ALL_IP_RANGES"

  log_config {
    enable = true
    filter = "ERRORS_ONLY"
  }
}

# ── Firewall ──────────────────────────────────────────────────────────────────
resource "google_compute_firewall" "allow_internal" {
  name    = "cloudops-${var.env}-allow-internal"
  network = google_compute_network.vpc.name
  project = var.project_id

  allow {
    protocol = "tcp"
    ports    = ["0-65535"]
  }
  allow {
    protocol = "udp"
    ports    = ["0-65535"]
  }
  allow {
    protocol = "icmp"
  }

  source_ranges = ["10.0.0.0/8"]
  priority      = 1000
}

resource "google_compute_firewall" "allow_health_checks" {
  name    = "cloudops-${var.env}-allow-hc"
  network = google_compute_network.vpc.name
  project = var.project_id

  allow {
    protocol = "tcp"
  }

  # Google Load Balancer / GKE health check source ranges
  source_ranges = ["35.191.0.0/16", "130.211.0.0/22", "209.85.152.0/22", "209.85.204.0/22"]
  target_tags   = ["gke-node"]
  priority      = 1000
}
