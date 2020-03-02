# Kotlin JSON Schema Generation – Module jackson

A module for the [jsonschema-generator](https://github.com/victools/jsonschema-generator).

This module extends from [jsonschema-module-jackson](https://github.com/victools/jsonschema-module-jackson)

## Requirements

#### JavaParameters

This module requires named parameters. [Verify that javaParameters is enabled](https://stackoverflow.com/a/45577384)

## Features

Given a [data class](https://kotlinlang.org/docs/reference/data-classes.html#data-classes) this module will add the following information to the json schema:

- Default values.
- Nullable type.
- Required properties.
  - Required when a field is not nullable and doesn't have a default value.

### Example

```kotlin
data class Person(val name: String, val lastName: String, val age: Int = 28, val sex: String?)
```

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "age": {
      "type": "integer",
      "default": 28
    },
    "lastName": {
      "type": "string"
    },
    "name": {
      "type": "string"
    },
    "sex": {
      "type": ["string", "null"]
    }
  },
  "required": ["lastName", "name"]
}
```
