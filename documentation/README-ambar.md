# Ambar

Ambar is a data streaming service using event sourcing.  For more on Ambar see [ambar.cloud](https://ambar.cloud/).

Enable Ambar via the property `ambar.enabled`.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| ambar.enabled                                   | Whether a Docker Ambar container should be started.                                                                                                                                                                                                                                                                                                                             | `false`                            |
| ambar.image.tag                                 | The image tag of the Ambar Docker container to use.                                                                                                                                                                                                                                                                                                                             | `v1.8`                             |
| ambar.config.file.path                          | The file path for the Ambar config.                                                                                                                                                                                                                                                                                                                                             | `src/test/resources/ambar-config.yaml`                                   |
| ambar.container.logging.enabled                 | Whether to output the Ambar Docker logs to the console.                                                                                                                                                                                                                                                                                                                         | `false`                            |


As of `v1.8` of Ambar, the database schema for the event store must be loaded when the Postgres instance is started.  Add a `schema.sql` file to `src/test/resources/` with the following contents:
```
CREATE TABLE public.event_store (
    id SERIAL PRIMARY KEY,
    event_id TEXT NOT NULL,
    event_name TEXT NOT NULL,
    aggregate_id TEXT NOT NULL,
    aggregate_version BIGINT NOT NULL,
    json_payload TEXT NOT NULL,
    json_metadata TEXT NOT NULL,
    recorded_on TEXT NOT NULL,
    causation_id TEXT NOT NULL,
    correlation_id TEXT NOT NULL
);
```

Add system property `postgres.schema.file.path` with value `schema.sql` to the component test run.  e.g. update the `pom.xml` with:
```
<postgres.schema.file.path>schema.sql</postgres.schema.file.path>
```

Also add the `ambar-config.yaml` that defines the config for the Ambar container to `src/test/resources/`.  See the [Ambar documentation](https://docs.ambar.cloud/) for more on this.
