variable "aws_region" {
  type    = string
  default = "ap-northeast-2"
}

variable "name_prefix" {
  type    = string
  default = "cloudlab-lab"
}

variable "availability_zone" {
  type        = string
  description = "e.g. ap-northeast-2a"
  default     = "ap-northeast-2a"
}

variable "key_name" {
  type        = string
  description = "Existing AWS EC2 key pair name"
}

variable "instance_type" {
  type    = string
  default = "t3.medium"
}

variable "allowed_ssh_cidrs" {
  type        = list(string)
  description = "Restrict SSH — prefer YOUR_IP/32"
  default     = ["0.0.0.0/0"]
}

variable "allowed_http_cidrs" {
  type    = list(string)
  default = ["0.0.0.0/0"]
}

variable "use_eip" {
  type    = bool
  default = true
}

variable "enable_compute" {
  type        = bool
  description = "Set false to only plan network modules without creating EC2 (dry structure)"
  default     = true
}
