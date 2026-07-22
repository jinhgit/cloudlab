# AWS Lab Apply 체크리스트 — **무료 우선**

> **결론부터:**  
> - **완전 무료·영구 보장**으로 AWS에 EC2를 올리는 방법은 **없습니다.**  
> - **과금 0원으로 가능한 것** = 로컬 검증 (`fmt` / `validate` / `plan` 일부) + 체크리스트 자동화.  
> - AWS **Free Tier**는 “신규 계정·한도 안·기간 내”만 무료에 가깝고, **설정 실수 시 과금**됩니다.  
> - 이 레포 기본 정책: **`apply`는 기본 비활성**, Free Tier 제약을 강제하는 프로필만 제공.

---

## 1. 무료로 해도 되는 것 (권장 · 비용 $0)

| 단계 | 명령 | 비용 |
|------|------|------|
| Terraform 포맷/검증 | `./scripts/iac-free-check.sh` | $0 |
| Terraform init + validate | 스크립트 포함 | $0 |
| Ansible 문법 검사 | `ansible-playbook ... --syntax-check` | $0 |
| v1 Compose 로컬 스택 | `docker compose up` | $0 (로컬 리소스만) |
| 문서·체크리스트 통과 | 본 문서 + 스크립트 exit 0 | $0 |

```bash
# repo root — 과금 없음
./scripts/iac-free-check.sh
```

이 경로만으로도 포트폴리오에  
“IaC 구조 + 검증 파이프라인 + Free Tier 위험 인식”을 설명할 수 있습니다.

---

## 2. AWS Free Tier를 쓸 때의 현실

| 사실 | 설명 |
|------|------|
| 대상 | 보통 **신규 계정 12개월** Free Tier (정책은 AWS 고지 변경 가능) |
| EC2 | **t2.micro / t3.micro** 월 750시간급 (리전·계정 조건 확인) |
| EBS | Free Tier 한도 내 (초과 시 과금) |
| EIP | **실행 중 인스턴스에 연결 안 된 EIP = 과금** → lab 프로필은 **EIP 끔** |
| 데이터 전송 | 아웃바운드 대량 시 과금 |
| 영구 무료 아님 | Free Tier 종료 후 **반드시 destroy** |

**유료 리소스 (이 레포 lab 프로필에서 금지):**

- `t3.medium` 이상  
- NAT Gateway, ALB, RDS, EKS  
- 미사용 Elastic IP  
- 대용량 EBS (lab은 **8–20GB 이하** 권장)

---

## 3. Free Tier lab 프로필 (레포 제공)

| 파일 | 용도 |
|------|------|
| `iac/terraform/envs/lab/terraform.tfvars.free-tier.example` | 무료 한도 지향 값 |
| `scripts/iac-free-check.sh` | 무료 검증 + 위험 설정 차단 |
| `scripts/iac-apply-free-tier.sh` | **명시적 확인 후에만** apply (기본 거부) |

Free Tier 값 요약:

```hcl
instance_type   = "t3.micro"   # Free Tier 후보
root_volume_gb  = 8            # 최소 디스크
use_eip         = false        # 유휴 EIP 과금 방지
enable_compute  = true
# allowed_ssh_cidrs = ["YOUR_PUBLIC_IP/32"]  # 필수 권장
```

---

## 4. 적용 전 체크리스트 (인쇄/면접용)

### A. 계정 · 비용 방어 ($0 유지 핵심)

- [ ] AWS 콘솔 **Billing → Free Tier / Budgets** 확인  
- [ ] **예산 $0~1 알림** + 임계 시 메일 (권장)  
- [ ] 루트 계정 MFA  
- [ ] IAM 사용자로만 작업 (액세스 키 최소 권한)  
- [ ] 작업 후 **당일 `terraform destroy`** 약속  

### B. 설정 파일

- [ ] `terraform.tfvars` 가 **free-tier example 기반**  
- [ ] `instance_type` ∈ `{t2.micro, t3.micro}`  
- [ ] `use_eip = false`  
- [ ] `root_volume_gb` ≤ 20  
- [ ] `allowed_ssh_cidrs` = **내 IP/32** (0.0.0.0/0 지양)  
- [ ] `key_name` 이 계정에 실존  
- [ ] 리전이 Free Tier 지원 리전인지 확인  

### C. 무료 검증 (apply 전 필수)

```bash
./scripts/iac-free-check.sh
# exit 0 이어야 다음 단계
```

- [ ] `terraform fmt -check`  
- [ ] `terraform validate`  
- [ ] free-tier 규칙 스크립트 통과  
- [ ] (선택) `terraform plan` — **API 호출은 보통 무료**, 리소스 생성 없음  

### D. apply 할 때만 (Free Tier 계정 + 본인 책임)

```bash
# 기본은 막혀 있음. 의도적으로만:
I_UNDERSTAND_AWS_MAY_CHARGE=yes ./scripts/iac-apply-free-tier.sh
```

- [ ] plan 출력에 **NAT/ALB/RDS 없음** 확인  
- [ ] 생성 리소스: VPC, subnet, SG, **micro EC2 1대**, EBS 소용량  
- [ ] SSH 접속 확인  
- [ ] (선택) Ansible `bootstrap` — 트래픽·시간 최소화  
- [ ] **즉시 또는 당일** `terraform destroy -auto-approve`  
- [ ] 콘솔에서 인스턴스/EIP/볼륨 **잔여 0** 확인  

### E. destroy 후 최종

- [ ] EC2 terminated  
- [ ] EBS 볼륨 삭제됨  
- [ ] EIP 없음  
- [ ] Billing 탐색에 예상치 못한 항목 없음  

---

## 5. 과금이 나오는 대표 실수

| 실수 | 결과 |
|------|------|
| `t3.medium` 유지 | Free Tier 밖 → **유료** |
| EIP 만들고 인스턴스 종료 | **EIP 과금** |
| destroy 깜빡 | 시간당 과금 누적 |
| 0.0.0.0/0 + 방치 | 보안 사고 + 트래픽 비용 위험 |
| 다른 리전에 리소스 방치 | 안 보이는 과금 |

---

## 6. 포트폴리오 설명 문장 (추천)

> “v2 IaC는 Terraform/Ansible 골격과 **무료 검증 파이프라인**까지 구현했습니다.  
> AWS 실 apply는 Free Tier 제약 프로필과 destroy 체크리스트를 두고,  
> 기본 워크플로는 **과금 0인 plan/validate**로 고정했습니다.”

유료 실습을 안 해도 **설계 능력 + 비용 의식**을 보여줄 수 있습니다.

---

## 7. 완전 무료 대안 (AWS 없이 “apply 경험”)

| 대안 | 비고 |
|------|------|
| **로컬 Docker Compose (v1)** | 이미 구현, $0 |
| **Multipass / 로컬 VM** | 무료, Ansible만 대상 호스트로 |
| **Oracle Cloud Always Free** 등 | AWS 아님, 별도 약관 |

이 레포 v2 스케치의 기본 검증은 **`./scripts/iac-free-check.sh` = $0** 입니다.
