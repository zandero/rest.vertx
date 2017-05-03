# rest.vertx
A JAX-RS (RestEasy) like annotation processor for vert.x verticals
 
## Setup
```xml
<dependency>      
     <groupId>com.zandero</groupId>      
     <artifactId>rest.vertx</artifactId>      
     <version>0.1</version>      
</dependency>
```

## Example
Step 1 - annotate a class with JAX-RS annotations 
```java
@Path("/test")
public class TestRest {

	@GET
	@Path("/echo")
	@Produces(MediaType.TEXT_HTML)
	public String echo() {

		return "Hello world!";
	}
}


```
Step 2 - register annotated class as REST API
```java
TestRest rest = new TestRest();
Router router = RestRouter.register(vertx, rest);

vertx.createHttpServer()
		.requestHandler(router::accept)
		.listen(PORT);
```

