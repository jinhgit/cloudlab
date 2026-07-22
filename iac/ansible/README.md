# Ansible — CloudLab v2

## Roles

| Role | Purpose |
|------|---------|
| `common` | packages, timezone, SSH harden |
| `docker_host` | Docker Engine + Compose plugin |
| `k3s_node` | optional single-node k3s |
| `cloudlab_app` | clone repo · compose/helm · health wait |

## Run

```bash
cp inventory/lab.example.ini inventory/lab.ini
# set ansible_host=

# dry-run
ansible-playbook -i inventory/lab.ini playbooks/site.yml --check

# apply
ansible-playbook -i inventory/lab.ini playbooks/bootstrap.yml
```

## Variables

See `group_vars/all.yml`. Override with `-e k3s_install=true` or host_vars.
