# Certbot Concourse Resource

Wraps the Certbot CLI, storing the `/etc/letsencrypt` dir in a versioned bucket (currently only GCS).
Currently supports Google Cloud DNS verification.

This resource assumes you're using service accounts for authentication. It could be extended to support static credentials.

## Source configuration

* `email`: *Required.* The email that identifies your Letsencrypt account, which is created automatically.
* `bucket`: *Required.* The name of the GCS bucket in which to store the Letsencrypt directory.
* `versioned_file`: *Required.* The name of the object in the bucket to store the Letsencrypt directory.

  It's a tar file, so something like `letsencrypt.tar` makes sense.
* `acme_server_url`: *Required.* In production, set this to `https://acme-v02.api.letsencrypt.org/directory`.
  When testing the service and to avoid rate limits, use `https://acme-staging-v02.api.letsencrypt.org/directory`.

## Example

```yaml
jobs:

- name: issue
  plan:
  - put: certificates
    params:
      domains:
      - some.domain.you.own

resources:

- name: certificates
  type: certbot
  source:
    email: accounts@domain.you.own
    bucket: my-great-bucket
    versioned_file: letsencrypt.tar
    acme_server_url: https://acme-staging-v02.api.letsencrypt.org/directory

resource_types:

- name: certbot
  type: registry-image
  source:
    repository: whatever-repo/certbot-concourse-resource # we haven't got a repo yet
    tag: # set latest version from Docker hub

```

## Behaviour

### `check`: Check for new versions of the directory tar

### `out`: Run `certbot certonly --expand`

### `in`: Download and unpack directory tar.

Your etc/letsencrypt directory becomes available in the directory you chose as your resource name.

You can use `get` steps to grab the current certificate(s), for install into load balancers etc.

