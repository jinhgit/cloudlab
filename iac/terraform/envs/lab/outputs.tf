output "vpc_id" {
  value = module.network.vpc_id
}

output "public_subnet_id" {
  value = module.network.public_subnet_id
}

output "security_group_id" {
  value = module.security_group.security_group_id
}

output "public_ip" {
  description = "SSH / app endpoint (null if enable_compute=false)"
  value       = var.enable_compute ? module.compute[0].public_ip : null
}

output "instance_id" {
  value = var.enable_compute ? module.compute[0].instance_id : null
}

output "ssh_command" {
  value = var.enable_compute ? "ssh -i <key.pem> ubuntu@${module.compute[0].public_ip}" : "compute disabled"
}

output "ansible_inventory_snippet" {
  description = "Paste into ansible/inventory/lab.ini"
  value = (
    var.enable_compute
    ? "[cloudlab]\nlab ansible_host=${module.compute[0].public_ip} ansible_user=ubuntu\n\n[cloudlab:vars]\nansible_python_interpreter=/usr/bin/python3\n"
    : "compute disabled"
  )
}
