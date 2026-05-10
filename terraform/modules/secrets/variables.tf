variable "project_id" { type = string }
variable "env"        { type = string }
variable "jwt_secret" {
  type      = string
  sensitive = true
}
