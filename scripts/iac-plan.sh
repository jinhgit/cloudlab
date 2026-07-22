#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT/iac/terraform/envs/lab"
[[ -f terraform.tfvars ]] || { echo "copy terraform.tfvars.example first"; exit 1; }
terraform init -input=false
terraform validate
terraform plan
