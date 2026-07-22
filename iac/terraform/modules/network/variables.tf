variable "name_prefix" {
  type        = string
  description = "Resource name prefix (e.g. cloudlab-lab)"
}

variable "cidr" {
  type        = string
  description = "VPC CIDR"
  default     = "10.42.0.0/16"
}

variable "public_subnet_cidr" {
  type        = string
  description = "Public subnet CIDR"
  default     = "10.42.1.0/24"
}

variable "availability_zone" {
  type        = string
  description = "AZ for the single public subnet"
}

variable "tags" {
  type        = map(string)
  description = "Common tags"
  default     = {}
}
