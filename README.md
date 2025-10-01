# File Transformer RAiD JSON for OpenCDMP

**file-transformer-raid-json** is an implementation of the `file-transformer-base` package designed to handle both the **import** and **export** of JSON files based on the **[Research Activity Identifier (RAiD) Metadata Schema](https://metadata.raid.org/en/latest/)**. This microservice is built using **Spring Boot** and can be easily integrated with the OpenCDMP platform as an import/export option for RAiD-compliant JSON files.

## Overview

It ensures that the data is structured and exchanged in a standardized JSON format that complies with the RAiD recommendations.

- **Exports**: Supported for RAiD-compliant JSON format.
- **Imports**: Supported for RAiD-compliant JSON format.

## Features

- **JSON Export**: Export OpenCDMP plans and descriptions to RAiD-compliant JSON format.
- **JSON Import**: Import RAiD-compliant JSON files into OpenCDMP as plans and descriptions.
- **Spring Boot Microservice**: Built as a Spring Boot microservice for seamless integration with OpenCDMP.
- **Standards-Based**: Fully compliant with the **Research Activity Identifier Metadata Standard**.

## Key Endpoints

This service implements the following endpoints as per `FileTransformerController`:

### Export Endpoints

- **POST `/export/plan`**: Export a plan to RAiD-compliant JSON.
- **POST `/export/description`**: Export a description to RAiD-compliant JSON.

```bash
POST /export/plan
{
    "planModel": { ... },
    "format": "json"
}
```

```bash
POST /export/description
{
    "descriptionModel": { ... },
    "format": "json"
}
```

### Import Endpoints

- **POST `/import/plan`**: Import a plan from RAiD-compliant JSON.
- **POST `/import/description`**: Import a description from RAiD-compliant JSON.

```bash
POST /import/plan
{
    "planImportModel": { ... }
}
```

```bash
POST /import/description
{
    "descriptionImportModel": { ... }
}
```

### Configuration Endpoint

- **GET `/formats`**: Returns supported formats for import/export.

## Example

To export a plan into RAiD-compliant JSON format:

```bash
POST /export/plan
{
    "planModel": { ... },
    "format": "json"
}
```

To import a plan from RAiD-compliant JSON format:

```bash
POST /import/plan
{
    "planImportModel": { ... }
}
```

## License

This repository is licensed under the [EUPL 1.2 License](LICENSE).

## Contact

For questions or support regarding this implementation, please contact:

- **Email**: opencdmp at cite.gr
