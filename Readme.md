# Kotlin JSON Schema Generation – Module jackson

This module extends from https://github.com/victools/jsonschema-module-jackson

### JavaParameters
This module requires named parameters. Verify that javaParameters is enabled.
See: https://stackoverflow.com/a/45577384

## Features

A parameter is:
- default (`withDefaultCheck`) when the parameter has a default value.
- nullable (`withNullableCheck`) when the parameter is marked as nullable ('?').
- required (`withRequiredCheck`) when the parameter is not nullable or have a default value.

### Example

```kotlin
data class Person(val name: String, val lastName: String, val age: Int = 28, val sex: String?)
```

```json
{
  "$schema" : "http://json-schema.org/draft-07/schema#",
  "type" : "object",
  "properties" : {
    "age" : {
      "type" : "integer",
      "default" : 28
    },
    "lastName" : {
      "type" : "string"
    },
    "name" : {
      "type" : "string"
    },
    "sex" : {
      "type" : [ "string", "null" ]
    }
  },
  "required" : [ "lastName", "name" ]
}
```
