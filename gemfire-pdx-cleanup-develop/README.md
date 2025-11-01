### CleanupPdxTypes Function

This function is intended to find region entries that do NOT have a
corresponding PDX type defined. This situation may manifest as an
`IllegalStateException` when performing a query. For example:

```
java.lang.IllegalStateException: Unknown pdx type=42728320
  at gemfire//org.apache.geode.internal.InternalDataSerializer.readPdxSerializable(InternalDataSerializer.java:3125)
  at gemfire//org.apache.geode.internal.InternalDataSerializer.basicReadObject(InternalDataSerializer.java:2874)
  at gemfire//org.apache.geode.DataSerializer.readObject(DataSerializer.java:3189)
  at gemfire//org.apache.geode.internal.util.BlobHelper.deserializeBlob(BlobHelper.java:110)
  at gemfire//org.apache.geode.internal.cache.EntryEventImpl.deserialize(EntryEventImpl.java:2067)
  ...
```

### Building and Running

Before building, ensure that you have access to Broadcom's commercial package
repository. Follow the instructions [here](https://gemfire.dev/quickstart/java/)
in order to set up your account and obtain a token.

You will need to set the following environment variables before building:
```
COMMERCIAL_MAVEN_USERNAME
COMMERCIAL_MAVEN_PASSWORD
```

To build a jar, run `./gradlew build -x test`. The resulting jar can be found in `build/libs/`

Once compiled into a jar, the function can be deployed using `gfsh deploy --jar`
The function should be executed using gfsh as follows:
```
gfsh execute function --id=pdx-cleanup-function --arguments=SIMULATE,REGION
```

The required arguments are:

- SIMULATE ("true" or "false") - When "true", the function will only log those
  entries it finds that do not have an associated PDX type. When set to "false",
  these entries will be removed.
- REGION - The name of the region to process.

Note: When executing the function against a REPLICATE region, it is recommended
to ALSO use the `--member` option to only target a single member.

The returned value is the number of entries that have been identified as missing
an associated PDX type. These entries will also be logged with messages like:
```
PDX_DEBUG: Removed entry=124 with missing pdxType=15339912
```

### Note
Clients should be stopped before running the function.

Clients should be started with the Java system property
`gemfire.ON_DISCONNECT_CLEAR_PDXTYPEIDS`. This will ensure that PDX types are
synchronized with the server.