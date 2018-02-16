# Rest.Vertx
Lightweight JAX-RS (RestEasy) like annotation processor for vert.x verticals

---  

If this project help you reduce time to develop? Keep it running and donate for cookies and coffee.
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=R6SBEEJNP97MC&lc=SI&item_name=Zandero&item_number=Rest%2eVertX&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted)
---  

 
## Setup
```xml
<dependency>      
     <groupId>com.zandero</groupId>      
     <artifactId>rest.vertx</artifactId>      
     <version>0.8.1</version>      
</dependency>
```
See also: [older versions](https://github.com/zandero/rest.vertx/releases)

**Rest.Vertx** is still in beta, so please report any [issues](https://github.com/zandero/rest.vertx/issues) discovered.  
You are highly encouraged to participate and improve upon the existing code.

## Acknowledgments
This project uses:
 * the superb [IntelliJ Idea](https://www.jetbrains.com/idea/) 
 * the excellent <img src="https://www.yourkit.com/images/yklogo.png" width="80"> [Java Profiler](https://www.yourkit.com/java/profiler/)

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

or alternatively
```java
Router router = Router.router(vertx);

TestRest rest = new TestRest();
RestRouter.register(router, rest);

vertx.createHttpServer()
	.requestHandler(router::accept)
	.listen(PORT);
```

or alternatively use _RestBuilder_ helper to build up endpoints.

### Registering by class type
> version 0.5 or later

Alternatively RESTs can be registered by class type only.  

```java
Router router = RestRouter.register(vertx, TestRest.class);

vertx.createHttpServer()
	.requestHandler(router::accept)
	.listen(PORT);
```

## RestBuilder
> since version 0.7

Rest endpoints, error handlers, writers and readers can be bound in one go using the RestBuilder.

```java
Router router = new RestBuilder(vertx)
    .register(RestApi.class, OtherRestApi.class)
    .reader(MyClass.class, MyBodyReader.class)
    .writer(MediaType.APPLICATION_JSON, CustomWriter.class)
    .errorHandler(IllegalArgumentExceptionHandler.class)
    .errorHandler(MyExceptionHandler.class)
    .build();
```

or
 
```java
router = new RestBuilder(router)
    .register(AdditionalApi.class)		                
    .build();
```

## Paths
Each class can be annotated with a root (or base) path @Path("/rest").  
In order to be registered as a REST API endpoint the class public method must have a **@Path** annotation.  


 ```java
@Path("/api")
public class SomeApi {
	
   @GET
   @Path("/execute")
   public String execute() {
 	  return "OK";
   }
}
```

**OR** - if class is not annotated the method @Path is taken as the full REST API path.

```java
public class SomeApi {
	
   @GET
   @Path("/api/execute")
   public String execute() {
 	    return "OK";
   }
}
```

```
GET /api/execute/ 
``` 
 

> **NOTE:** multiple identical paths can be registered - if response is not terminated (ended) the next method is executed.
> However this should be avoided whenever possible.

### Path variables
Both class and methods support **@Path** variables.

```java
// RestEasy path param style
@GET
@Path("/execute/{param}")
public String execute(@PathParam("param") String parameter) {
	return parameter;
}
```

```
GET /execute/that -> that
```

```java
// vert.x path param style
@GET
@Path("/execute/:param")
public String execute(@PathParam("param") String parameter) {
	return parameter;
}
```

```
GET /execute/this -> this
```

### Path regular expressions
```java
// RestEasy path param style with regular expression {parameter:>regEx<}
@GET
@Path("/{one:\\w+}/{two:\\d+}/{three:\\w+}")
public String oneTwoThree(@PathParam("one") String one, @PathParam("two") int two, @PathParam("three") String three) {
	return one + two + three;
}
```

```
GET /test/4/you -> test4you
```

**Not recommended** but possible are vert.x style paths with regular expressions.  
In this case method parameters correspond to path expressions by index. 
```java
@GET
@Path("/\\d+/minus/\\d+")
public Response test(int one, int two) {
    return Response.ok(one - two).build();
}
```

```
GET /12/minus/3 -> 9
```

### Query variables
Query variables are defined using the @QueryParam annotation.  
In case method arguments are not _nullable_ they must be provided or a **400 bad request** response follows. 

```java
@Path("calculate")
public class CalculateRest {

	@GET
	@Path("add")
	public int add(@QueryParam("one") int one, @QueryParam("two") int two) {

		return one + two;
	}
}
```

```
GET /calculate/add?two=2&one=1 -> 3
```

### Matrix parameters
Matrix parameters are defined using the @MatrixParam annotation.

```java
@GET
@Path("{operation}")
public int calculate(@PathParam("operation") String operation, @MatrixParam("one") int one, @MatrixParam("two") int two) {
    
  switch (operation) {
    case "add":
      return one + two;
      
	case "multiply" :
	  return one * two;
	
	  default:
	    return 0;
    }
}
```

```
GET /add;one=1;two=2 -> 3
```


### Conversion of path, query, ... variables to Java objects 
Rest.Vertx tries to convert path, query, cookie, header and other variables to their corresponding Java types.
    
Basic (primitive) types are converted from string to given type - if conversion is not possible a **400 bad request** response follows.
 
Complex java objects are converted according to **@Consumes** annotation or **@RequestReader** _request body reader_ associated.

**Option 1** - The **@Consumes** annotation **mime/type** defines the reader to be used when converting request body.  
In this case a build in JSON converter is applied.
```java
@Path("consume")
public class ConsumeJSON {

	@POST
	@Path("read")
	@Consumes("application/json")
	public String add(SomeClass item) {

		return "OK";
	}
}
```  

**Option 2** - The **@RequestReader** annotation defines a _ValueReader_ to convert a String to a specific class, converting:  
 * request body
 * path
 * query
 * cookie
 * header
 
```java
@Path("consume")
public class ConsumeJSON {

	@POST
	@Path("read")
	@Consumes("application/json")
	@RequestReader(SomeClassReader.class)
	public String add(SomeClass item) {

		return "OK";
	}
}
```

**Option 3** - An RequestReader is globally assigned to a specific class type.

```java
RestRouter.getReaders().register(SomeClass.class, SomeClassReader.class);
```

```java
@Path("consume")
public class ConsumeJSON {

	@POST
	@Path("read")
	public String add(SomeClass item) {

		return "OK";
	}
}
```

**Option 4** - An RequestReader is globally assigned to a specific mime type.

```java
RestRouter.getReaders().register("application/json", SomeClassReader.class);
```

```java
@Path("consume")
public class ConsumeJSON {

	@POST
	@Path("read")
	@Consumes("application/json")
	public String add(SomeClass item) {

		return "OK";
	}
}
```

First appropriate reader is assigned searching in following order:
1. use parameter ValueReader
1. use method ValueReader
1. use class type specific ValueReader
1. use mime type assigned ValueReader
1. use general purpose ValueReader

#### Missing ValueReader?

If no specific ValueReader is assigned to a given class type, **rest.vertx** tries to instantiate the class:
* converting String to primitive type if class is a String or primitive type
* using a single String constructor
* using a single primitive type constructor if given String can be converted to the specific type  
* using static method _fromString(String value)_ or _valueOf(String value)_


## @SuppressCheck annotation
Rest.vertx tries to be smart and checks all readers and writers type compatibility.   
Meaning if a REST method returns a _String_ then a _String_ compatible writer is expected.
 
In case the check is to strong (preventing some fancy Java generics or inheritance) the @SuppressCheck annotation can be applied to skip the check.

>NOTE: **This will not prevent a writer/reader runtime exception in case of type incompatibility!** 

```java
@SuppressCheck
public class TestSuppressedWriter implements HttpResponseWriter<Dummy> {

	@Override
	public void write(Dummy result, HttpServerRequest request, HttpServerResponse response) {
		response.end(result.name);
	}
}
```

### Cookies, forms and headers ...
Cookies, HTTP form and headers can also be read via **@CookieParam**, **@HeaderParam** and **@FormParam** annotations.  

```java
@Path("read")
public class TestRest {

	@GET
	@Path("cookie")
	public String readCookie(@CookieParam("SomeCookie") String cookie) {

		return cookie;
	}
}
```

```java
@Path("read")
public class TestRest {

	@GET
	@Path("header")
	public String readHeader(@HeaderParam("X-SomeHeader") String header) {

		return header;
	}
}
```

```java
@Path("read")
public class TestRest {

	@POST
	@Path("form")
	public String readForm(@FormParam("username") String user, @FormParam("password") String password) {

		return "User: " + user + ", is logged in!";
	}
}
```

## @DefaultValue annotation
We can provide default values in case parameter values are not present with @DefaultValue annotation.

@DefaultValue annotation can be used on:
* @PathParam
* @QueryParam
* @FormParam
* @CookieParam
* @HeaderParam 
* @Context

```java
public class TestRest {

	@GET
	@Path("user")
	public String read(@QueryParam("username") @DefaultValue("unknown") String user) {

		return "User is: " + user;
	}
}
```

```
GET /user -> "User is: unknown
   
GET /user?username=Foo -> "User is: Foo
```


## [](#RequestContext) Request context 
Additional request bound variables can be provided as method arguments using the @Context annotation.
 
Following types are by default supported:
* **@Context HttpServerRequest** - vert.x current request 
* **@Context HttpServerResponse** - vert.x response (of current request)
* **@Context Vertx** - vert.x instance
* **@Context RoutingContext** - vert.x routing context (of current request)
* **@Context User** - vert.x user entity (if set)
* **@Context RouteDefinition** - vertx.rest route definition (reflection of **Rest.Vertx** route annotation data)

```java
@GET
@Path("/context")
public String createdResponse(@Context HttpServerResponse response, @Context HttpServerRequest request) {

	response.setStatusCode(201);
	return request.uri();
}
```

### Registering a context provider
If desired a custom context provider can be implemented to extract information from _request_ into a object.  
The context provider is only invoked in when the context object type is needed. 

```java
RestRouter.addContextProvider(Token.class, request -> {
		String token = request.getHeader("X-Token");
		if (token != null) {
			return new Token(token);
		}
			
		return null;
	});
```


```java
@GET
@Path("/token")
public String readToken(@Context Token token) {

	return token.getToken();
}
```

If **@Context** for given class **can not** be provided than a **400** _@Context can not be provided_ exception is thrown 


### Pushing a custom context
While processing a request a custom context can be pushed into the vert.x routing context data storage.  
This context data can than be utilized as a method argument. The pushed context is thread safe for the current request.

> The main difference between a context push and a context provider is that the context push is executed on every request, 
while the registered provider is only invoked when needed!

In order to achieve this we need to create a custom handler that pushes the context before the REST endpoint is called:
```java
Router router = Router.router(vertx);
router.route().handler(pushContextHandler());

router = RestRouter.register(router, new CustomContextRest());
vertx.createHttpServer()
		.requestHandler(router::accept)
		.listen(PORT);

private Handler<RoutingContext> pushContextHandler() {

	return context -> {
		RestRouter.pushContext(context, new MyCustomContext("push this into storage"));
		context.next();
	};
}
```

Then the context object can than be used as a method argument 
```java
@Path("custom")
public class CustomContextRest {
	

    @GET
    @Path("/context")
    public String createdResponse(@Context MyCustomContext context) {
    
    }
```

## Response building

### Response writers
Metod results are converted using response writers.  
Response writers take the method result and produce a vert.x response.

**Option 1** - The **@Produces** annotation **mime/type** defines the writer to be used when converting response.  
In this case a build in JSON writer is applied.
```java
@Path("produces")
public class ConsumeJSON {

	@GET
	@Path("write")
	@Produces("application/json")
	public SomeClass write() {

		return new SomeClass();
	}
}
```

**Option 2** - The **@ResponseWriter** annotation defines a specific writer to be used.
```java
@Path("produces")
public class ConsumeJSON {

	@GET
	@Path("write")
	@Produces("application/json")
	@ResponseWriter(SomeClassWriter.class)
	public SomeClass write() {

		return new SomeClass();
	}
}
```

**Option 3** - An ResponseWriter is globally assigned to a specific class type.

```java
RestRouter.getWriters().register(SomeClass.class, SomeClassWriter.class);
```


**Option 4** - An ResponseWriter is globally assigned to a specific mime type.

```java
RestRouter.getWriters().register("application/json", MyJsonWriter.class);
```

```java
@Path("produces")
public class ConsumeJSON {

	@GET
	@Path("write")
	@Produces("application/json")
	public SomeClass write() {

		return new SomeClass();
	}
}
```

First appropriate writer is assigned searching in following order:
1. use assigned method ResponseWriter
1. use class type specific writer
1. use mime type assigned writer
1. use general purpose writer (call to _.toString()_ method of returned object)

### vert.x response builder
In order to manipulate returned response, we can utilize the **@Context HttpServerResponse**.
 
```java
@GET
@Path("/login")
public HttpServerResponse vertx(@Context HttpServerResponse response) {

    response.setStatusCode(201);
    response.putHeader("X-MySessionHeader", sessionId);
    response.end("Hello world!");
    return reponse;
}
```

### JAX-RS response builder
> **NOTE** in order to utilize the JAX Response.builder() an existing JAX-RS implementation must be provided.  
> Vertx.rest uses the Glassfish Jersey implementation for testing: 

```xml
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-common</artifactId>
    <version>2.22.2</version>
</dependency>
```

```java
@GET
@Path("/login")
public Response jax() {

    return Response
        .accepted("Hello world!!")
        .header("X-MySessionHeader", sessionId)
        .build();
}
```

## User roles & authorization
User access is checked in case REST API is annotated with:
* **@RolesAllowed(role)**, **@RolesAllowed(role_1, role_2, ..., role_N)** - check if user is in any given role
* **@PermitAll** - allow everyone
* **@DenyAll** - deny everyone

User access is checked against the vert.x _User_ entity stored in _RoutingContext_, calling the _User.isAuthorised(role, handler)_ method.

In order to make this work, we need to fill up the RoutingContext with a User entity.

```java
public void init() {
	
    // 1. register handler to initialize User
    Router router = Router.router(vertx);
    router.route().handler(getUserHandler());

    // 2. REST with @RolesAllowed annotations
    TestAuthorizationRest testRest = new TestAuthorizationRest();
    RestRouter.register(router, testRest);

    vertx.createHttpServer()
        .requestHandler(router::accept)
        .listen(PORT);
}

// simple hanler to push a User entity into the vert.x RoutingContext
public Handler<RoutingContext> getUserHandler() {

    return context -> {

        // read header ... if present ... create user with given value
        String token = context.request().getHeader("X-Token");

        // set user ...
        if (token != null) {
            context.setUser(new SimulatedUser(token)); // push User into context
        }

        context.next();
    };
}
```

```java
@GET
@Path("/info")
@RolesAllowed("User")
public String info(@Context User user) {

    if (user instanceof SimulatedUser) {
    	SimulatedUser theUser = (SimulatedUser)user;
    	return theUser.name;
    }

    return "hello logged in " + user.principal();
}
```

**Example of User implementation:**
```java
public class SimulatedUser extends AbstractUser {

  private final String role; // role and role in one
	
  private final String name;

  public SimulatedUser(String name, String role) {
    this.name = name;
    this.role = role;
  }
  
  /**
   * permission has the value of @RolesAllowed annotation
   */
  @Override
  protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {

    resultHandler.handle(Future.succeededFuture(role != null && role.equals(permission)));
  }

  /**
   * serialization of User entity
   */  
  @Override
  public JsonObject principal() {

    JsonObject json = new JsonObject();
    json.put("role", role);
    json.put("name", name);
    return json;  
  }

  @Override
  public void setAuthProvider(AuthProvider authProvider) {
    // not utilized by Rest.vertx  
  }
}
```

## Implementing a custom value reader
In case needed we can implement a custom value reader.  
A value reader must:
 * implement _ValueReader_ interface
 * linked to a class type, mime type or _@RequestReader_ 
 
**Example of RequestReader:**
```java
/**
 * Converts request body to JSON
 */
public class MyCustomReader implements ValueReader<MyNewObject> {

	@Override
	public MyNewObject read(String value, Class<MyNewObject> type) {

		if (value != null && value.length() > 0) {
			
		    return new MyNewObject(value);
		}
		
		return null;
	}
}
```

Using a value reader is simple:
```java
@Path("read")
public class ReadMyNewObject {

  @POST
  @Path("object")
  @RequestReader(MyCustomReader.class) // MyCustomReader will provide the MyNewObject to REST API
  public String add(MyNewObject item) {
    return "OK";
  }
  
  // OR
  
  @PUT
  @Path("object")
  public String add(@RequestReader(MyCustomReader.class) MyNewObject item) {
      return "OK";
  }
}
```

We can utilize request readers also on queries, headers and cookies:
```java
@Path("read")
public class ReadMyNewObject {
 
   @GET
   @Path("query")
   public String add(@QueryParam("value") @RequestReader(MyCustomReader.class) MyNewObject item) {
     return item.getName();
   }
}
```

## Implementing a custom response writer
In case needed we can implement a custom response writer.  
A request writer must:
 * implement _HttpResponseWriter_ interface
 * linked to a class type, mime type or _@ResponseWriter_
 
**Example of ResponseWriter:**
```java
/**
 * Converts request body to JSON
 */
public class MyCustomResponseWriter implements HttpResponseWriter<MyObject> {

  /**
   * result is the output of the corresponding REST API endpoint associated 
   */  
  @Override
  public void write(MyObject data, HttpServerRequest request, HttpServerResponse response) {
    
    response.putHeader("X-ObjectId", data.id);
    response.end(data.value);
  }
}
```

Using a response writer is simple:
```java
@Path("write")
public class WriteMyObject {
  
  @GET
  @Path("object")
  @ResponseWriter(MyCustomResponseWriter.class) // MyCustomResponseWriter will take output and fill up response 
  public MyObject output() {
    
  	return new MyObject("test", "me");
  }
}
```

## Ordering routes
By default routes area added to the Router in the order they are listed as methods in the class when registered.
One can manually change the route REST order with the **@RouteOrder** annotation.

By default each route has the order of 0.  
If route order is != 0 then vertx.route order is set. The higher the order - the later each route is listed in _Router_.
Order can also be negative, e.g. if you want to ensure a route is evaluated before route number 0.


**Example:** despite multiple identical paths the route order determines the one being executed. 
```java
@RouteOrder(20)
@GET
@Path("/test")
public String third() {
  return "third";
}

@RouteOrder(10)
@GET
@Path("/test")
public String first() {
  return "first";
}

@RouteOrder(15)
@GET
@Path("/test")
public String second() {
  return "second";
}
```

```java
GET /test -> "first" 
```

# Enabling CORS requests
> version 0.7.4 or later 

```java
Router router = new RestBuilder(vertx)
    .enableCors("*", true, 1728000, allowedHeaders, HttpMethod.OPTIONS, HttpMethod.GET)
    .register(apiRest) // /api endpoint
    .notFound(RestNotFoundHandler.class) // rest not found (last resort)
    .build();
```

or
```java
RestRouter.enableCors(router,            // to bind handler to
	                  allowedOriginPattern, // origin pattern
	                  allowCredentials,     // alowed credentials (true/false)
	                  maxAge,               // max age in seconds
	                  allowedHeaders,       // set of allowed headers
	                  methods)              // list of methods or empty for all
```

# Error handling
Unhandled exceptions can be addressed via a designated _ExceptionHandler_:
1. for a given method path
1. for a given root path
1. globally assigned to the RestRouter

> NOTE: An exception handler is a designated response writer bound to a Throwable class

If no designated exception handler is provided, a default exception handler kicks
in trying to match the exception type with a build in exception handler.

## Path / Method error handler
Both class and methods support **@CatchWith** annotation.  

**@CatchWith** annotation must provide an _ExceptionHandler_ implementation that handles the thrown exception: 

```java
@GET
@Path("/test")
@CatchWith(MyExceptionHandler.class)
public String fail() {

  throw new IllegalArgumentExcetion("Bang!"); 
}
```
```java
public class MyExceptionHandler implements ExceptionHandler<Throwable> {
    @Override
    public void write(Throwable result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(406);
        response.end("I got this ... : '" + result.getMessage() + "'");
    }
}
```

## Multiple exception handlers
Alternatively multiple handlers can be bound to a method / class, serving different exceptions.  
Handlers are considered in order given, first matching handler is used.

```java
@GET
@Path("/test")
@CatchWith({HandleRestException.class, WebApplicationExceptionHandler.class})
public String fail() {

    throw new IllegalArgumentExcetion("Bang!"); 
}
```

## Global error handler(s)
The global error handler is invoked in case no other error handler is provided or no other exception type maches given handlers.  
In case no global error handler is associated a default (generic) error handler is invoked.

```java
  Router router = RestRouter.register(vertx, SomeRest.class);
  RestRouter.getExceptionHandlers().register(MyExceptionHandler.class);  
    
  vertx.createHttpServer()
    .requestHandler(router::accept)
    .listen(PORT);
```

or alternatively we bind multiple exception handlers.  
Handlers are considered in order given, first matching handler is used.
  
```java  
  Router router = RestRouter.register(vertx, SomeRest.class);
  RestRouter.getExceptionHandlers().register(MyExceptionHandler.class, GeneralExceptionHandler.class);  
```

## Page not found helper
> version 0.7.4 or later  

To ease page/resource not found handling a special _notFound()_ handler can be be utilized.

We can
* handle a subpath / pattern where a handler was not found
* handle all not matching requests 

```java
Router router = new RestBuilder(vertx)
    .register(MyRest.class)
    .notFound(".*\\/other", OtherNotFoundHandler.class) // handle all calls to a /other request
    .notFound("rest", RestNotFoundHandler.class) // handle all calls to /rest subpath
    .notFound(NotFoundHandler.class) // handle all other not found requests
    .build();
```

or
```java
RestRouter.notFound(router, "rest", RestNotFoundHandler.class);
```

The not found handler must extend _NotFoundResponseWriter_:
```java
public class NotFoundHandler extends NotFoundResponseWriter {
                                  
    @Override
    public void write(HttpServerRequest request, HttpServerResponse response) {
    
        response.end("404 HTTP Resource: '" + request.path() + "' not found!");
    }
}
 ```
 
## Serving static/resource files
> version 0.8 or later

Rest.vertx simplifies serving of static resource files. 
All you need to do is to create a REST endpoint that returns the relative path of the desired resource file,
bound with _FileResponseWriter_ writer.

For example:
```java
@Path("docs")
public class StaticFileRest {

	@GET
	@Path("/{path:.*}")
	@ResponseWriter(FileResponseWriter.class)
	public String serveDocFile(@PathParam("path") String path) {

		return "html/" + path;
	}
}
```

will load resource file in _html/{path}_ and return it's content.


```
> GET docs/page.html -> returns page.html content via FileResponseWriter
```
 
 
## Blocking and Async RESTs
> version 0.8.1 or later

By default all REST utilize _vertx().executeBlocking()_ call. Therefore the vertx event loop is not blocked. 
Responses are always terminated (ended).

If desired a REST endpoint can return _Future_ and will be executed asynchronously waiting for the future object to finish.
If used with non default (provided) _HttpResponseWriter_ the response must be terminated manually.

The output writer is determined upon the Future<Object> type returned. If returned future object is _null_ then 
due to Java generics limitations the object type **can not** be determinied.
Therefore the response will be produced by the best matching response writer instead.  

> **suggestion:** wrap null responses to object instances 

#### Simple async example
```java
@GET
@Path("async")
public Future<Dummy> create(@Context Vertx vertx) throws InterruptedException {

    Future<Dummy> res = Future.future();
    asyncCall(vertx, res);
    return res;
}
```

```java
public void asyncCall(Vertx vertx, Future<Dummy> value) throws InterruptedException {

    vertx.executeBlocking(
            fut -> {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    value.fail("Fail");
                }
                value.complete(new Dummy("async", "called"));
                fut.complete();
            },
            false,
            fut -> {}
        );
}
```


## Injection
> version 8.0 or later

Allows @Inject (JSR330) injection of RESTs, writers and readers. 

To provide injection an _InjectionProvider_ interface needs to be implemented.

### Binding injection provider
```java
Router router = new RestBuilder(vertx)
		                .injectWith(GuiceInjectionProvider.class)
		                .register(GuicedRest.class)
		                .build();
```

or

```java
RestRouter.injectWith(GuiceInjectionProvider.class);
```

### Implement injection provider
Following is a simple implementation of a Guice injection provider.

```java
public class GuiceInjectionProvider extends AbstractModule implements InjectionProvider  {

	private Injector injector;

	public GuiceInjectionProvider() {
		injector = Guice.createInjector(this);
	}

	@Override
	protected void configure() {
		bind(MyService.class).to(MyServiceImpl.class);
		bind(OtherService.class).to(MyOtherServiceImpl.class);
	}

	@Override
	public Object getInstance(Class clazz) {
		return injector.getInstance(clazz);
	}
}
```

### Implement service (use @Inject if needed)
```java
public MyServiceImpl implements MyService {
	
	private final OtherService other;
	
	@Inject
	public MyServiceImpl(OtherService service) {
		other = service;
	}
	
	public String call() {
		return "something";
	}
}
```

### Use @Inject in RESTs
```java
@Path("rest")
public class GuicedRest {

	private final MyService service;

	@Inject
	public GuicedRest(MyService someService) {

		service = someService;
	}

	@GET
	@Path("test")
	public String get() {
		return service.call();
	}
}
```
Injection can also be used od _RequestReader_, _ResponseWriters_ or _ExceptionHandler_ if needed.

### @Context fields
>since version 8.1 or later

Rest api classes **can not** use @Context fields, @Context is provided via method parameters instead. 
 
In case needed a RequestReader, ResponseWriter or ExceptionHandler can use a @Context annotated field, see [Request context](#RequestContext) for details.
 
Use _@Context_ fields only when really necessary, as the readers, writers and handlers are not cached but initialized on the fly on every request when needed.  
  
This is done in order to ensure thread safety, so one context does not jump into another thread.

## Internal caching
>since version 8.1 or later

### Caching and singletons

* All registered REST classes are singletons by default, no need to annotate them with _@Singleton_ annotation.  
* By default all _HttpResponseWriter_, _ValueReader_ and _ExceptionHandler_ classes are singletons that are cached once initialized.
* In case _HttpResponseWriter_, _ValueReader_ or _ExceptionHandler_ are utilizing a **@Context** field they are initialized on **every request** for thread safety 