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
**Step 1** - annotate a class with JAX-RS annotations 
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
**Step 2** - register annotated class as REST API
```java
TestRest rest = new TestRest();
Router router = RestRouter.register(vertx, rest);

vertx.createHttpServer()
		.requestHandler(router::accept)
		.listen(PORT);
```

## Paths
Each class can be annotated with a root (or base) path @Path("/rest")

Following that each public method must have a @Path annotation in order to be registered as a REST endpoint. 

### Path variables
Both class and methods support @Path variables.

```java
// RestEasy path param style
@GET
@Path("/execute/{param}")
public String execute(@PathParam("param") String parameter) {
	return parameter;
}
```

```java
// vert.x path param style
@GET
@Path("/execute/:param")
public String execute(@PathParam("param") String parameter) {
	return parameter;
}
```

### Path regular expressions
```java
// RestEasy path param style with regular expression (parameter:>regEx<)
@GET
@Path("/{one:\\w+}/{two:\\d+}/{three:\\w+}")
public String oneTwoThree(@PathParam("one") String one, @PathParam("two") int two, @PathParam("three") String three) {
	return one + two + three;
}
```

**Not recoomended** but possible are vert.x style paths with regular expressions:
```java
@GET
@Path("/\\d+")
public Response test(int one) {
    return Response.ok(one).build();
}
```
