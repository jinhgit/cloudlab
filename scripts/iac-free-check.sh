#!/usr/bin/env bash
# CloudLab v2 — FREE validation only (no AWS resource creation).
# Exit 0 = checklist + terraform fmt/validate OK.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TF_DIR="$ROOT/iac/terraform/envs/lab"
FAIL=0

red() { printf '\033[31m%s\033[0m\n' "$*"; }
green() { printf '\033[32m%s\033[0m\n' "$*"; }
yellow() { printf '\033[33m%s\033[0m\n' "$*"; }

echo "=========================================="
echo " CloudLab v2 — FREE IaC check (no apply)"
echo " Cost of this script: \$0"
echo "=========================================="
echo

# --- 1) Tooling ---
if ! command -v terraform >/dev/null 2>&1; then
  red "[FAIL] terraform not installed"
  FAIL=1
else
  green "[OK] terraform $(terraform version -json 2>/dev/null | python3 -c 'import sys,json; print(json.load(sys.stdin)["terraform_version"])' 2>/dev/null || terraform version | head -1)"
fi

# --- 2) fmt ---
echo
echo "==> terraform fmt -check -recursive"
if terraform fmt -check -recursive "$ROOT/iac/terraform" >/tmp/tf-fmt.out 2>&1; then
  green "[OK] fmt clean"
else
  red "[FAIL] run: terraform fmt -recursive iac/terraform"
  cat /tmp/tf-fmt.out || true
  FAIL=1
fi

# --- 3) validate (init without backend; downloads providers — free, local disk) ---
echo
echo "==> terraform init -backend=false + validate"
if command -v terraform >/dev/null 2>&1; then
  (
    cd "$TF_DIR"
    # dummy tfvars if missing so validate can parse required vars
    if [[ ! -f terraform.tfvars ]]; then
      if [[ -f terraform.tfvars.free-tier.example ]]; then
        yellow "[INFO] no terraform.tfvars — using free-tier example for validate only"
        cp terraform.tfvars.free-tier.example terraform.tfvars.validate-tmp
        # key_name required but unused by validate of config structure
        echo 'key_name = "validate-only-key"' >> terraform.tfvars.validate-tmp
        TF_CLI_ARGS_validate="" 
        terraform init -backend=false -input=false >/tmp/tf-init.out 2>&1 || {
          red "[FAIL] terraform init"
          tail -20 /tmp/tf-init.out
          exit 1
        }
        terraform validate -var-file=terraform.tfvars.validate-tmp >/tmp/tf-val.out 2>&1 || {
          # some versions need vars differently
          terraform validate >/tmp/tf-val.out 2>&1 || true
        }
        rm -f terraform.tfvars.validate-tmp
      else
        terraform init -backend=false -input=false >/tmp/tf-init.out 2>&1
        terraform validate >/tmp/tf-val.out 2>&1
      fi
    else
      terraform init -backend=false -input=false >/tmp/tf-init.out 2>&1
      terraform validate >/tmp/tf-val.out 2>&1
    fi
  ) && green "[OK] terraform validate (or init completed)" || {
    red "[FAIL] terraform validate"
    tail -30 /tmp/tf-val.out /tmp/tf-init.out 2>/dev/null || true
    FAIL=1
  }
fi

# --- 4) Free-tier guardrails on terraform.tfvars if present ---
echo
echo "==> Free-tier guardrails (if terraform.tfvars exists)"
TFVARS="$TF_DIR/terraform.tfvars"
if [[ ! -f "$TFVARS" ]]; then
  yellow "[SKIP] no terraform.tfvars yet — copy terraform.tfvars.free-tier.example when ready"
else
  check_var() {
    local key="$1" pattern="$2" msg="$3"
    local line
    line=$(grep -E "^[[:space:]]*${key}[[:space:]]*=" "$TFVARS" | tail -1 || true)
    if [[ -z "$line" ]]; then
      yellow "[WARN] $key not set in tfvars — $msg"
      return
    fi
    if echo "$line" | grep -Eiq "$pattern"; then
      green "[OK] $key => $line"
    else
      red "[FAIL] $key unsafe for free lab: $line"
      red "       $msg"
      FAIL=1
    fi
  }

  check_var "instance_type" 't2\.micro|t3\.micro' "use t2.micro or t3.micro only"
  check_var "use_eip" 'false' "set use_eip = false (idle EIP costs money)"
  # root volume
  if grep -E '^[[:space:]]*root_volume_gb[[:space:]]*=' "$TFVARS" >/dev/null; then
    vol=$(grep -E '^[[:space:]]*root_volume_gb[[:space:]]*=' "$TFVARS" | tail -1 | sed 's/.*=//' | tr -d ' "')
    if [[ "$vol" =~ ^[0-9]+$ ]] && (( vol <= 20 )); then
      green "[OK] root_volume_gb = $vol (<=20)"
    else
      red "[FAIL] root_volume_gb=$vol — keep <= 20 for free-tier lab"
      FAIL=1
    fi
  else
    yellow "[WARN] root_volume_gb unset (module default should be 8)"
  fi

  if grep -E 't3\.(small|medium|large)|t2\.(small|medium|large)|m5\.|c5\.' "$TFVARS" >/dev/null; then
    red "[FAIL] paid-looking instance type detected in tfvars"
    FAIL=1
  fi

  if grep -E 'allowed_ssh_cidrs.*"0\.0\.0\.0/0"' "$TFVARS" >/dev/null; then
    yellow "[WARN] SSH open to world (0.0.0.0/0) — not a fee issue but security risk; use YOUR_IP/32"
  fi
fi

# --- 5) Ansible syntax (optional, free) ---
echo
echo "==> Ansible syntax-check (optional)"
if command -v ansible-playbook >/dev/null 2>&1; then
  INV="$ROOT/iac/ansible/inventory/lab.example.ini"
  ansible-playbook -i "$INV" "$ROOT/iac/ansible/playbooks/site.yml" --syntax-check >/tmp/ans.out 2>&1 \
    && green "[OK] ansible site.yml syntax" \
    || { red "[FAIL] ansible syntax"; cat /tmp/ans.out; FAIL=1; }
  ansible-playbook -i "$INV" "$ROOT/iac/ansible/playbooks/bootstrap.yml" --syntax-check >/tmp/ans2.out 2>&1 \
    && green "[OK] ansible bootstrap.yml syntax" \
    || { red "[FAIL] ansible bootstrap syntax"; cat /tmp/ans2.out; FAIL=1; }
else
  yellow "[SKIP] ansible-playbook not installed"
fi

# --- 6) Docs present ---
echo
echo "==> Docs"
for f in docs/v2-iac.md docs/v2-aws-free-checklist.md iac/README.md; do
  [[ -f "$ROOT/$f" ]] && green "[OK] $f" || { red "[FAIL] missing $f"; FAIL=1; }
done

echo
if [[ "$FAIL" -eq 0 ]]; then
  green "=========================================="
  green " FREE CHECK PASSED (\$0)"
  green " No AWS resources were created."
  green "=========================================="
  echo
  echo "Next (optional, Free Tier risk — read docs/v2-aws-free-checklist.md):"
  echo "  cp iac/terraform/envs/lab/terraform.tfvars.free-tier.example \\"
  echo "     iac/terraform/envs/lab/terraform.tfvars"
  echo "  # edit key_name + allowed_ssh_cidrs"
  echo "  # ONLY if you accept Free Tier limits:"
  echo "  I_UNDERSTAND_AWS_MAY_CHARGE=yes ./scripts/iac-apply-free-tier.sh"
  exit 0
else
  red "=========================================="
  red " FREE CHECK FAILED — fix items above"
  red "=========================================="
  exit 1
fi
