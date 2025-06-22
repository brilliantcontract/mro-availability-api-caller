# MRO Availability API Caller

This project provides a small Java application that can be deployed as a WAR and exposes REST endpoints for fetching product availability and pricing information from MRO Supply.

## Features

- **Java 8 / JAX-RS** implementation using Jersey client libraries.
- Exposes REST endpoints under `/resources` via `ApplicationConfig`.
- Multiple callers for different access modes:
  - `ApiCallerBackEnd` – calls the administrative proxy API.
  - `ApiCallerFrontEnd` – calls the public API when logged in.
  - `ApiCallerFrontEndLoggedOut` – calls the public API without session cookies.
- Test utilities under `src/test` showcase basic calls to these APIs.

## Building

The project uses Maven and targets Java 8. Build the WAR with:

```bash
mvn clean package
```


## Usage

Deploy the generated WAR to any Java EE 7 compatible servlet container. The following resources become available:

- `POST /resources/product-availability-and-price-on-back-end`
- `POST /resources/product-availability-and-price-on-front-end`
- `POST /resources/product-availability-and-price-on-front-end-logged-out`

Each endpoint expects form fields `productId` and `cookies` (if required) and proxies the request to the corresponding MRO Supply API. Responses include the remote status and body.

Example curl usage is displayed when accessing each resource with a `GET` request.

