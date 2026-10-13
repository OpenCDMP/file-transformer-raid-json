# OpenCDMP RAiD JSON file transformer

**file-transformer-raid-json** exports [OpenCDMP](https://github.com/OpenCDMP/OpenCDMP) plans as JSON files that follow the [Research Activity Identifier (RAiD) metadata schema](https://metadata.raid.org/en/v1.6.3/core/core.html#contents) v1.6 to v1.6.3, and imports such files as new plans. It is a Spring Boot service that implements the [file-transformer-base](https://github.com/OpenCDMP/file-transformer-base) contract; its plugin id is `raid-file-transformer`. See [File transformers](https://opencdmp.github.io/optional-services/file-transformers/) in the OpenCDMP documentation.

| Operation | Plans | Assets |
| --- | --- | --- |
| Export to RAiD JSON | Yes, including values answered in their assets | No |
| Import from RAiD JSON | Yes, as a new plan | No |

## Features

- Values are read from the plan blueprint and asset template questions that carry `raid.*` semantic keys. Values outside the RAiD vocabularies are dropped, and blocks without a valid value are left out. See the [RAiD JSON semantics reference](https://opencdmp.github.io/optional-services/file-transformers/raid-json-semantics/).
- Import creates a plan with the file's title and access, and fills the plan blueprint questions that carry the `identifier`, `date` and `access` keys.
- The service reports the keys it reads in a semantics manifest, so the plan blueprint and asset template editors of OpenCDMP can suggest them and check a structure.

## Requirements

- An OpenCDMP installation and its Keycloak realm. The service accepts only tokens that carry the `plugins` audience.
- To build from source: Java 21 and Maven. The service runs on Spring Boot 4.0.

## Run with Docker

The image is [`opencdmp/file-transformer-raid-json`](https://hub.docker.com/r/opencdmp/file-transformer-raid-json) on Docker Hub. It listens on port `8080`, reads `app-<PROFILE>.env` from `/config`, and writes its logs to `/logs`. The OpenCDMP [Docker example deployment](https://opencdmp.github.io/deployment/install/) does not include it; [Registering plugin services](https://opencdmp.github.io/deployment/configuration/plugin-services/#step-1-run-the-service) shows how to add a plugin service to it.

To run it on its own, create `config/app-docker.env`:

```properties
IDP_ISSUER_URI=https://opencdmp.example.org/auth/realms/OpenCDMP
IDP_CLAIMS_ROLES_PATH=plugins.roles
SECURITY_AUDIENCE=plugins
STORAGE_PATH=/storage
```

and start the container:

```bash
docker run -d --name file-transformer-raid-json \
  -e PROFILE=docker -e SERVER_PORT=8080 -p 8080:8080 \
  -v "$PWD/config:/config" -v "$PWD/logs:/logs" \
  opencdmp/file-transformer-raid-json:<tag>
```

At startup the service downloads the OpenID configuration from `IDP_ISSUER_URI`, so Keycloak must be reachable at that address. `/actuator/health` reports when the service is up.

## Build from source

```bash
mvn clean package
```

The build produces `web/target/file-transformer-raid-web-1.0.0-SNAPSHOT.jar`.

To run the JAR, set the required variables and start it. The realm must be reachable:

```bash
export IDP_ISSUER_URI=http://opencdmp.localhost:8180/auth/realms/OpenCDMP
export IDP_CLAIMS_ROLES_PATH=plugins.roles
export SECURITY_AUDIENCE=plugins
export STORAGE_PATH=./storage
export LOGGING_PATH=./logs
java -jar web/target/file-transformer-raid-web-1.0.0-SNAPSHOT.jar
```

Without `SERVER_PORT`, the service listens on port `8091`. [Run and test a plugin locally](https://opencdmp.github.io/developers/plugins/run-locally/) shows how to get a token, call the service, and register it with a local installation.

The `Dockerfile` builds the service from source:

```bash
docker build -t file-transformer-raid-json .
```

## Configuration

Set these as environment variables or in `app-<PROFILE>.env`:

| Variable | Purpose |
| --- | --- |
| `IDP_ISSUER_URI`, `IDP_CLAIMS_ROLES_PATH`, `SECURITY_AUDIENCE` | Keycloak realm, roles path (`plugins.roles`) and audience (`plugins`) of the tokens the service accepts. Required. |
| `STORAGE_PATH`, `RAID_FILE_TRANSFORMER_USE_SHARED_STORAGE` | Folder for temporary and shared files, and whether files are exchanged with OpenCDMP through a shared folder. |
| `PROFILE`, `SERVER_PORT`, `LOGGING_*` | Profile, port and logging, as for every plugin service. |

[File transformer services: RAiD JSON](https://opencdmp.github.io/deployment/configuration/backend/file-transformers/#raid-json-file-transformer) and [Settings common to all plugin services](https://opencdmp.github.io/deployment/configuration/plugin-services/#settings-common-to-all-plugin-services) list every variable with its default.

### Register with OpenCDMP

Register the service with its address and the plugin id `raid-file-transformer`: for the whole installation in the API service configuration, or for one organisation with **Add plugin** under **Settings > Plugins**. See [Registering plugin services](https://opencdmp.github.io/deployment/configuration/plugin-services/).

## API

The service implements these endpoints of `FileTransformerController`:

| Method | Path | Request body | Response |
| --- | --- | --- | --- |
| `POST` | `/api/file-transformer/export/plan?format=json` | `PlanModel` | `FileEnvelopeModel` with the RAiD JSON file |
| `POST` | `/api/file-transformer/import/plan` | `PlanImportModel` | `PlanModel` |
| `POST` | `/api/file-transformer/preprocessing/plan` | `FileEnvelopeModel` with the RAiD JSON file | `PreprocessingPlanModel` labelled with the file name |
| `GET` | `/api/file-transformer/formats` | - | `FileTransformerConfiguration`, with the semantics manifest |

The `description` endpoints return an error. The models come from [common-models](https://github.com/OpenCDMP/common-models) and file-transformer-base; see the [file transformer plugin guide](https://opencdmp.github.io/developers/plugins/file-transformers/).

## Related resources

- [File transformers overview](https://opencdmp.github.io/optional-services/file-transformers/)
- [RAiD JSON semantics reference](https://opencdmp.github.io/optional-services/file-transformers/raid-json-semantics/)
- [Semantics guide](https://opencdmp.github.io/administering/content-management/templates/semantics/)
- [File transformer services configuration](https://opencdmp.github.io/deployment/configuration/backend/file-transformers/#raid-json-file-transformer)
- [Registering plugin services](https://opencdmp.github.io/deployment/configuration/plugin-services/)
- [File transformer plugin guide](https://opencdmp.github.io/developers/plugins/file-transformers/)

## Contributing

Report bugs and suggest changes in [GitHub Issues](https://github.com/OpenCDMP/file-transformer-raid-json/issues). See [Contributing](https://opencdmp.github.io/developers/contributing/) for how to propose a change.

## License

file-transformer-raid-json is licensed under the [European Union Public Licence (EUPL) v1.2](LICENSE).

## Contact

Questions: email opencdmp at cite.gr.
