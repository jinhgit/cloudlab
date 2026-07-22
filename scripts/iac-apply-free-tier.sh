#!/usr/bin/env bash
# OPTIONAL: Free Tier–constrained terraform apply.
# DEFAULT: refuses to run. Paid/unsafe types are blocked.
#
# This is NOT a guarantee of $0 charges. AWS Free Tier has account/time limits.
# Prefer: ./scripts/iac-free-check.sh  (always $0)
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TF_DIR="$ROOT/iac/terraform/envs/lab"

if [[ "${I_UNDERSTAND_AWS_MAY_CHARGE:-}" != "yes" ]]; then
  cat <<'EOF'
Refusing to apply.

AWS EC2 is not permanently free. Free Tier has limits; mistakes can cost money.

Always-free path:
  ./scripts/iac-free-check.sh

If you have Free Tier eligibility AND accept residual risk:
  1) Use terraform.tfvars.free-tier.example
  2) Restrict allowed_ssh_cidrs to YOUR_IP/32
  3) Set budget alerts in AWS Billing
  4) Re-run:
       I_UNDERSTAND_AWS_MAY_CHARGE=yes ./scripts/iac-apply-free-tier.sh
  5) Destroy the same day:
       cd iac/terraform/envs/lab && terraform destroy
EOF
  exit 2
fi

# Must pass free check first
"$ROOT/scripts/iac-free-check.sh"

if [[ ! -f "$TF_DIR/terraform.tfvars" ]]; then
  echo "Create terraform.tfvars from terraform.tfvars.free-tier.example first." >&2
  exit 1
fi

# Hard block paid instance types
if grep -Eiq 'instance_type\s*=\s*"(t3\.(small|medium|large)|t2\.(small|medium|large)|m5\.|c5\.|r5\.)' \
  "$TF_DIR/terraform.tfvars"; then
  echo "Blocked: non-micro instance_type in terraform.tfvars" >&2
  exit 1
fi

if grep -Eiq 'use_eip\s*=\s*true' "$TF_DIR/terraform.tfvars"; then
  echo "Blocked: use_eip=true (idle EIP can be billed). Set use_eip=false." >&2
  exit 1
fi

cd "$TF_DIR"
terraform init -input=false
terraform plan -out=tfplan-free
echo
echo "Plan saved to tfplan-free. Review carefully."
echo "To apply:  terraform apply tfplan-free"
echo "To destroy when done:  terraform destroy"
echo
if [[ "${AUTO_APPROVE:-}" == "true" ]]; then
  echo "AUTO_APPROVE=true — applying..."
  terraform apply -input=false tfplan-free
  echo "Remember: terraform destroy before Free Tier limits / overnight."
else
  echo "Apply not auto-run. Inspect plan, then apply manually if you still want."
fi
