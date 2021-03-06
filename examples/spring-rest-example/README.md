# Spring automatic MDC-logging
This example show how to automatically populate MDC values from the `RequestMapping` expression of a Spring REST service. In other words, a log definition file `document` containing a key

```yaml
keys
  - id:
      name: Document id
      type: string
      description: The internal id of the document
      example: abc
```

and a REST service with a method  

```java
@GET
@Path("/{id}/hello")
@Logged(DocumentStoreMarker.class)
public String message(@PathParam("id") String id) {
	logger.info("Say hello");
	
	return "Hello " + id;
}
```

will automatically populate the MDC with the `id`. An HTTP request to `/123/hello` will result in log output.

```json
{
  "message" : "Say hello",
  "document" : {
    "id" : "123"
  }
}
```


## Constraints
The path identifiers must match keys used in the log definition. In addition, `Date` types are not supported.

## TODO
Identify and/or populate filters as via bean [instance] or [definition] post processors. 

[instance]:				https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-factory-extension-bpp
[definition]:			https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-factory-extension-factory-postprocessors
