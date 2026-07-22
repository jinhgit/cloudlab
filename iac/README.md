# iac/ — CloudLab v2 Infrastructure as Code (Sketch)

Terraform (provision) + Ansible (configure) scaffold for **one-node lab** bootstrap.

## Cost policy

| Path | Cost | Command |
|------|------|---------|
| **Default validation** | **$0** | `./scripts/iac-free-check.sh` |
| AWS Free Tier apply | Not guaranteed $0 | opt-in only, see free checklist |
| Paid instance types | **Blocked** | free-check fails |

> AWS EC2 is **not permanently free**. Free Tier has account/time limits.  
> Full checklist: [docs/v2-aws-free-checklist.md](../docs/v2-aws-free-checklist.md)

## Quick map

| Tool | Path | Job |
|------|------|-----|
| Terraform | `terraform/envs/lab` | VPC · SG · EC2 (micro) |
| Ansible | `ansible/playbooks/bootstrap.yml` | OS · Docker · k3s · CloudLab |
| Free check | `../scripts/iac-free-check.sh` | fmt · validate · free-tier rules |

## Always free ($0)

```bash
# repo root
./scripts/iac-free-check.sh
```

## Optional Free Tier apply (risk)

```bash
cd terraform/envs/lab
cp terraform.tfvars.free-tier.example terraform.tfvars
# edit key_name + allowed_ssh_cidrs = YOUR_IP/32

# refused unless you set the flag:
I_UNDERSTAND_AWS_MAY_CHARGE=yes ./scripts/iac-apply-free-tier.sh

# same day:
terraform destroy
```

## Ansible only

```bash
cd ansible
cp inventory/lab.example.ini inventory/lab.ini
ansible-playbook -i inventory/lab.ini playbooks/site.yml --check
```

## Safety

- Do **not** commit `terraform.tfvars`, `*.tfstate`, real inventory hosts.
- Free lab defaults: `t3.micro`, `use_eip=false`, `root_volume_gb=8`.
- Design: [docs/v2-iac.md](../docs/v2-iac.md)
