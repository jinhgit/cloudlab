# iac/ — CloudLab v2 Infrastructure as Code (Sketch)

Terraform (provision) + Ansible (configure) scaffold for **one-node lab** bootstrap.

> **Not a full multi-cloud product yet.** Structure and contracts are ready; fill credentials and run against a real AWS (or adapt modules) account.

## Quick map

| Tool | Path | Job |
|------|------|-----|
| Terraform | `terraform/envs/lab` | VPC · SG · EC2 · EIP |
| Ansible | `ansible/playbooks/bootstrap.yml` | OS · Docker · k3s · CloudLab |
| Orchestrator | `../scripts/iac-bootstrap.sh` | plan → apply → configure |

## Docs

- Design: [docs/v2-iac.md](../docs/v2-iac.md)
- v1 stack still lives at repo root (`docker-compose*.yml`, `kubernetes/`)

## Minimal usage

```bash
# Terraform
cd terraform/envs/lab
cp terraform.tfvars.example terraform.tfvars
# edit key_name, region, allowed_ssh_cidrs
terraform init && terraform plan

# Ansible (after you have a host IP)
cd ../../ansible
cp inventory/lab.example.ini inventory/lab.ini
# set ansible_host=
ansible-playbook -i inventory/lab.ini playbooks/site.yml --check
```

## Safety

- Do **not** commit `terraform.tfvars`, `*.tfstate`, `inventory/lab.ini` with real IPs/keys.
- Default sketch uses **AWS provider** APIs; charges may apply if you `apply`.
