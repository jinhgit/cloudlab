# CloudLab v2 lab environment — wires network + SG + compute modules.

module "network" {
  source = "../../modules/network"

  name_prefix        = var.name_prefix
  availability_zone  = var.availability_zone
  cidr               = "10.42.0.0/16"
  public_subnet_cidr = "10.42.1.0/24"
}

module "security_group" {
  source = "../../modules/security_group"

  name_prefix        = var.name_prefix
  vpc_id             = module.network.vpc_id
  allowed_ssh_cidrs  = var.allowed_ssh_cidrs
  allowed_http_cidrs = var.allowed_http_cidrs
}

module "compute" {
  count  = var.enable_compute ? 1 : 0
  source = "../../modules/compute"

  name_prefix       = var.name_prefix
  subnet_id         = module.network.public_subnet_id
  security_group_id = module.security_group.security_group_id
  key_name          = var.key_name
  instance_type     = var.instance_type
  root_volume_gb    = var.root_volume_gb
  use_eip           = var.use_eip
}
