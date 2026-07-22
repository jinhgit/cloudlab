#!/usr/bin/env bash
# CloudLab v2 orchestrator (sketch)
# 1) terraform plan/apply (optional)
# 2) write ansible inventory from terraform output
# 3) ansible-playbook bootstrap
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TF_DIR="$ROOT/iac/terraform/envs/lab"
ANSIBLE_DIR="$ROOT/iac/ansible"
SKIP_TF="${SKIP_TF:-false}"
AUTO_APPROVE="${AUTO_APPROVE:-false}"

echo "==> CloudLab v2 IaC bootstrap (sketch)"

if [[ "$SKIP_TF" != "true" ]]; then
  if ! command -v terraform >/dev/null 2>&1; then
    echo "terraform not found — set SKIP_TF=true to only run Ansible" >&2
    exit 1
  fi
  if [[ ! -f "$TF_DIR/terraform.tfvars" ]]; then
    echo "Missing $TF_DIR/terraform.tfvars"
    echo "Copy terraform.tfvars.example and edit key_name / SSH CIDRs."
    exit 1
  fi
  cd "$TF_DIR"
  terraform init -input=false
  terraform plan -out=tfplan
  if [[ "$AUTO_APPROVE" == "true" ]]; then
    terraform apply -input=false tfplan
  else
    echo "Review plan, then: (cd $TF_DIR && terraform apply tfplan)"
    echo "Or re-run with AUTO_APPROVE=true"
    # still try to generate inventory if state already has outputs
  fi

  if terraform output -raw public_ip >/dev/null 2>&1; then
    IP="$(terraform output -raw public_ip)"
    mkdir -p "$ANSIBLE_DIR/inventory"
    cat > "$ANSIBLE_DIR/inventory/lab.ini" <<EOF
[cloudlab]
lab ansible_host=${IP} ansible_user=ubuntu

[cloudlab:vars]
ansible_python_interpreter=/usr/bin/python3
EOF
    echo "==> Wrote inventory with ${IP}"
  fi
fi

if ! command -v ansible-playbook >/dev/null 2>&1; then
  echo "ansible-playbook not found — install Ansible to continue configure phase" >&2
  exit 1
fi

if [[ ! -f "$ANSIBLE_DIR/inventory/lab.ini" ]]; then
  echo "Missing ansible inventory. Copy inventory/lab.example.ini → lab.ini" >&2
  exit 1
fi

cd "$ANSIBLE_DIR"
ansible-playbook -i inventory/lab.ini playbooks/bootstrap.yml

echo "==> Bootstrap finished. Open http://<public_ip>:3000"
