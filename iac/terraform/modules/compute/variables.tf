variable "name_prefix" {
  type = string
}

variable "subnet_id" {
  type = string
}

variable "security_group_id" {
  type = string
}

variable "key_name" {
  type        = string
  description = "Existing EC2 key pair name for SSH"
}

variable "instance_type" {
  type    = string
  default = "t3.medium"
}

variable "ami_id" {
  type        = string
  description = "Optional AMI override; default = Ubuntu 22.04"
  default     = null
}

variable "root_volume_gb" {
  type    = number
  default = 30
}

variable "use_eip" {
  type        = bool
  description = "Allocate and associate Elastic IP"
  default     = true
}

variable "tags" {
  type    = map(string)
  default = {}
}
