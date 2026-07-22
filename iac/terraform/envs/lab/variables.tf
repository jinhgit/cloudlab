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
  type        = string
  description = "Use t3.micro/t2.micro for Free Tier lab; larger types may incur charges"
  default     = "t3.micro"
}

variable "root_volume_gb" {
  type        = number
  description = "Root EBS size (GiB). Free Tier lab: keep <= 20"
  default     = 8
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
  type        = bool
  description = "Elastic IP — free only while attached to running instance; free-tier lab default false"
  default     = false
}

variable "enable_compute" {
  type        = bool
  description = "Set false to only plan network modules without creating EC2 (dry structure)"
  default     = true
}
