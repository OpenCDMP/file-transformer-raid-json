# File Transformer RAiD JSON for OpenCDMP

**file-transformer-raid-json** is an implementation of the [file-transformer-base](https://github.com/OpenCDMP/file-transformer-base) package designed to handle both the **import** and **export** of JSON files based on the **[Research Activity Identifier (RAiD) Metadata Schema](https://metadata.raid.org/en/latest/)**. This microservice is built using **Spring Boot** and can be easily integrated with the OpenCDMP platform as an import/export option for RAiD-compliant JSON files.

---
## Overview

It ensures that the data is structured and exchanged in a standardized JSON format that complies with the RAiD recommendations.

- **Exports**: Supported for RAiD-compliant JSON format.
- **Imports**: Supported for RAiD-compliant JSON format.


## Features

- **JSON Export**: Export OpenCDMP plans and descriptions to RAiD-compliant JSON format.
- **JSON Import**: Import RAiD-compliant JSON files into OpenCDMP as plans and descriptions.
- **Spring Boot Microservice**: Built as a Spring Boot microservice for seamless integration with OpenCDMP.
- **Standards-Based**: Fully compliant with the **Research Activity Identifier Metadata Standard**.

**Supported operations:**
- ✅ Export plans to RDA JSON
- ✅ Import plans from RDA JSON
- ❌ Description import/export (not supported)


## Key Endpoints

This service implements the following endpoints as per `FileTransformerController`:

### Export Endpoints

- **POST `/export/plan`**: Export a plan to RAiD-compliant JSON.

```bash
POST /export/plan
{
    "planModel": { ... },
    "format": "json"
}
```

### Import Endpoints

- **POST `/import/plan`**: Import a plan from RAiD-compliant JSON.

```bash
POST /import/plan
{
    "planImportModel": { ... }
}
```

### Configuration Endpoint

- **GET `/formats`**: Returns supported formats for import/export.

---

## Integration with OpenCDMP

To integrate this service with your OpenCDMP deployment, configure the file transformer plugin in the OpenCDMP admin interface.

For detailed integration instructions, see see the [File Transformers RAiD configuration](https://opencdmp.github.io/getting-started/configuration/backend/file-transformers/#raid-file-transformer) and the [OpenCDMP File Transformers Service Authentication](https://opencdmp.github.io/getting-started/configuration/backend/#file-transformer-service-authentication).

---

## See Also

- **File Transformers Overview**: https://opencdmp.github.io/optional-services/file-transformers
- **Developer Plugin Guide**: https://opencdmp.github.io/developers/plugins/file-transformers

---

## License

This repository is licensed under the [EUPL 1.2 License](LICENSE).

### Contact

For questions, support, or feedback:

- **Email**: opencdmp at cite.gr
- **GitHub Issues**: https://github.com/OpenCDMP/file-transformer-raid-json/issues
---

*This service is part of the OpenCDMP ecosystem. For general OpenCDMP documentation, visit [opencdmp.github.io](https://opencdmp.github.io).*