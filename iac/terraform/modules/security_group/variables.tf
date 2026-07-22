variable "name_prefix" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "allowed_ssh_cidrs" {
  type        = list(string)
  description = "CIDRs allowed to SSH (use your IP/32 in real labs)"
  default     = ["0.0.0.0/0"]
}

variable "allowed_http_cidrs" {
  type        = list(string)
  description = "CIDRs for HTTP(S)/app ports"
  default     = ["0.0.0.0/0"]
}

variable "tags" {
  type    = map(string)
  default = {}
}
