# edge-erm
Copyright (C) 2024 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0. See the file "LICENSE" for more information.

# Introduction
Provides an ability to retrieve license term information from FOLIO to external systems. The data flow is
`edge-erm <-> FOLIO <-> mod-agreements`.

## API Details
| Method | URL                                        | Description |
|--------|--------------------------------------------|---|
| GET    | /erm/license-terms/{id} | Retrieve license terms by package/title identifier |
| POST   | /erm/license-terms/batch | Retrieve license terms by package/title identifier in batch |

# Security
See [edge-common-spring](https://github.com/folio-org/edge-common-spring)

# Installation/Deployment

## Configuration

* See [edge-common](https://github.com/folio-org/edge-common) for a description of how configuration works.

## Required permissions for system user
`licenses.licenses.item.get`

***System properties***
Property | Default     | Description
------------------------- | ----------- | -------------
`port`                    | `8081`      | Server port to listen on
`secure_store`            | `Ephemeral` | Type of secure store to use.  Valid: `Ephemeral`, `AwsSsm`, `Vault`
`secure_store_props`      | `src/main/resources/ephemeral.properties`        | Path to a properties file specifying secure store configuration
`token_cache_ttl_ms`      | `3600000`   | How long to cache token, in milliseconds (ms)
`null_token_cache_ttl_ms` | `30000`     | How long to cache token failure (null JWTs), in milliseconds (ms)
`token_cache_capacity `   | `100`       | Max token cache size
`JAVA_OPTIONS`            | `-XX:MaxRAMPercentage=66.0`              | Java options, default - maximum heap size as a percentage of total memory)

### Secure Stores

Three secure stores currently implemented for safe retrieval of encrypted credentials:

#### EphemeralStore ####

Only intended for _development purposes_.  Credentials are defined in plain text in a specified properties file.  See `src/main/resources/ephemeral.properties`

For example:
```
secureStore.type=Ephemeral
# a comma separated list of tenants
tenants=mytenant
#######################################################
# For each tenant, the institutional user password...
#
# Note: this is intended for development purposes only
#######################################################
# format: tenant=username,password
mytenant=edgeuser,password
```

## Set up opac-yml-utils submodule

To install opac-yml-utils submodule run git command: 'git submodule update --init' or 'git submodule update --remote'  

### Issue tracker
See project [EDGERM](https://issues.folio.org/browse/EDGERM)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).