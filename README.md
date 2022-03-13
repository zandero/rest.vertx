# Rest.Vertx

- Lightweight JAX-RS (RestEasy) like annotation processor for vert.x verticles.
- You are highly encouraged to participate and improve upon the existing code.
- Please report any [issues](https://github.com/zandero/rest.vertx/issues) discovered.

## Donations are welcome

**If this project help you reduce time to develop?**

Keep it running and
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=R6SBEEJNP97MC&lc=SI&item_name=Zandero&item_number=Rest%2eVertX&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted)
for cookies and coffee or become a **[sponsor](https://github.com/sponsors/zandero)**.

## Setup

```xml

<dependency>
    <groupId>com.zandero</groupId>
    <artifactId>rest.vertx</artifactId>
    <version>1.0.6.1</version>
</dependency>
```

See also: [change log](https://github.com/zandero/rest.vertx/releases)

## Important note - breaking changes - when migrating to vertx 4.*

> If you are using **vert.x** version **3.*** then use version **0.9.*** of **Rest.vertx**
>
> Version **1.0.4** and higher are only compatible with **vert.x** version **4** and higher and introduce some **breaking changes:**
> - **@RolesAllowed** authorization throws **403 Forbidden** exception where before it was a **401 Unauthorized** exception
> - Authentication and authorization flow is now aligned with **vert.x** and **@RolesAllowed** processing is done by an **AuthorizationProvider** implementation
> - **@RolesAllowed** annotations still work but should be replaced with **@Authenticate** and **@Authorize** annotations instead

## Acknowledgments

This project uses:

* the superb [IntelliJ Idea](https://www.jetbrains.com/idea/)
* the
  excellent <img src="https://www.yourkit.com/images/yklogo.png" width="80"> [Java Profiler](https://www.yourkit.com/java/profiler/)

## Request/Response rest.vertx lifecycle

A high level overview of how **rest.vertx** works:

![Request lifecycle](./docs/request_lifecycle.svg)

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
TestRest rest=new TestRest();
    Router router=RestRouter.register(vertx,rest);

    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());
```

or alternatively

```java
Router router=Router.router(vertx);

    TestRest rest=new TestRest();
    RestRouter.register(router,rest);

    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());
```

or alternatively use _RestBuilder_ helper to build up endpoints.

### Registering by class type

> version 0.5 or later

Alternatively RESTs can be registered by class type only.

```java
Router router=RestRouter.register(vertx,TestRest.class);

    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());
```

## RestBuilder

> version 0.7 or later

Rest endpoints, error handlers, writers and readers can be bound in one go using the RestBuilder.

```java
Router router=new RestBuilder(vertx)
    .register(RestApi.class,OtherRestApi.class)
    .reader(MyClass.class,MyBodyReader.class)
    .writer(MediaType.APPLICATION_JSON,CustomWriter.class)
    .errorHandler(IllegalArgumentExceptionHandler.class)
    .errorHandler(MyExceptionHandler.class)
    .build();
```

or

```java
router=new RestBuilder(router)
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
> GET /api/execute/ 
``` 

> **NOTE:** multiple identical paths can be registered - if response is not terminated (ended) the next method is executed.
> However this should be avoided whenever possible.

### Path variables

Both class and methods support **@Path** variables.

```java
// RestEasy path param style
@GET
@Path("/execute/{param}")
public String execute(@PathParam("param") String parameter){
    return parameter;
    }
```

```
> GET /execute/that -> that
```

```java
// vert.x path param style
@GET
@Path("/execute/:param")
public String execute(@PathParam("param") String parameter){
    return parameter;
    }
```

```
> GET /execute/this -> this
```

### Path regular expressions

```java
// RestEasy path param style with regular expression {parameter:>regEx<}
@GET
@Path("/{one:\\w+}/{two:\\d+}/{three:\\w+}")
public String oneTwoThree(@PathParam("one") String one,@PathParam("two") int two,@PathParam("three") String three){
    return one+two+three;
    }
```

```
> GET /test/4/you -> test4you
```

> since version 0.8.7 or later

Also possible are Vert.x style paths with regular expressions.

```java
// VertX style path :parameter:regEx   
@GET
@Path("/:one:\\d+/minus/:two:\\d+")
public Response test(int one,int two){
    return Response.ok(one-two).build();
    }
```

```
> GET /12/minus/3 -> 9
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
> GET /calculate/add?two=2&one=1 -> 3
```

In case needed a request reader can be assigned to provide the correct variable:

```java
    @GET
public int getDummyValue(@QueryParam("dummy") @RequestReader(DummyReader.class) Dummy dummy){

    return dummy.value;
```

#### Decoding of query variables

> since version 0.8.7 or later

Query variables are decoded by default   
If the original (non decoded) value is desired, we can use the @Raw annotation.

```java
@GET
@Path("/decode")
public String echoGetQuery(@QueryParam("decoded") String decodedQuery,
@QueryParam("raw") @Raw String rawQuery){
```

```
> GET /decode?decoded=hello+world -> decoded = "hello world"
> GET /decode?raw=hello+world     -> raw = "hello+world"
```

### Matrix parameters

Matrix parameters are defined using the @MatrixParam annotation.

```java
@GET
@Path("{operation}")
public int calculate(@PathParam("operation") String operation,@MatrixParam("one") int one,@MatrixParam("two") int two){

    switch(operation){
    case"add":
    return one+two;

    case"multiply":
    return one*two;

default:
    return 0;
    }
    }
```

```
> GET /add;one=1;two=2 -> 3
```

### Conversion of path, query, ... variables to Java objects

Rest.Vertx tries to convert path, query, cookie, header and other variables to their corresponding Java types.

Basic (primitive) types are converted from string to given type - if conversion is not possible a **400 bad request**
response follows.

Complex java objects are converted according to **@Consumes** annotation or **@RequestReader** _request body reader_
associated.

Complex java object annotated with **@BeanParam** annotation holding fields annotated with @PathParam, @QueryParam ...

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

**Option 2** - The **@RequestReader** annotation defines a _ValueReader_ to convert a String to a specific class,
converting:

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
RestRouter.getReaders().register(SomeClass.class,SomeClassReader.class);
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
RestRouter.getReaders().register("application/json",SomeClassReader.class);
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

**Option 5** - **@BeanParam** argument is constructed via vert.x RoutingContext.
> since version 0.9.0 or later

```java
@POST
@Path("/read/{param}")
public String read(@BeanParam BeanClazz bean){
    ...
    }
```  

```java
public class BeanClazz {
    @PathParam("param")
    private String path;

    @QueryParam("query")
    @Raw
    private String query;

    @HeaderParam("x-token")
    private String token;

    @CookieParam("chocolate")
    private String cookie;

    @MatrixParam("enum")
    private MyEnum enumValue;

    @FormParam("form")
    private String form;

    @BodyParam
    @DefaultValue("empty")
    private String body;
}
```

OR via constructor

```java
public BeanClazz(@PathParam("param") String path,
@HeaderParam("x-token") boolean xToken,
@QueryParam("query") @Raw int query,
@CookieParam("chocolate") String cookie){
    ...
    }
```

#### Missing ValueReader?

If no specific ValueReader is assigned to a given class type, **rest.vertx** tries to instantiate the class:

* converting String to primitive type if class is a String or primitive type
* using a single String constructor
* using a single primitive type constructor if given String can be converted to the specific type
* using static methods _fromString(String value)_ or _valueOf(String value)_ (in that order)

## @SuppressCheck annotation

Rest.vertx tries to be smart and checks all readers and writers type compatibility.   
Meaning if a REST method returns a _String_ then a _String_ compatible writer is expected.

In case the check is to strong (preventing some fancy Java generics or inheritance) the @SuppressCheck annotation can be
applied to skip the check.

> NOTE: **This will not prevent a writer/reader runtime exception in case of type incompatibility!**

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
> GET /user -> "User is: unknown
   
> GET /user?username=Foo -> "User is: Foo
```

## [](#RequestContext) Request context

Additional request bound variables can be provided as method arguments using the @Context annotation.

Following types are by default supported:

* **@Context HttpServerRequest** - vert.x current request
* **@Context HttpServerResponse** - vert.x response (of current request)
* **@Context Vertx** - vert.x instance
* **@Context EventBus** - vert.x EventBus instance
* **@Context RoutingContext** - vert.x routing context (of current request)
* **@Context User** - vert.x user entity (if set)
* **@Context RouteDefinition** - vertx.rest route definition (reflection of **Rest.Vertx** route annotation data)

```java
@GET
@Path("/context")
public String createdResponse(@Context HttpServerResponse response,@Context HttpServerRequest request){
    response.setStatusCode(201);
    return request.uri();
    }
```

### Registering a context provider

If desired a custom context provider can be implemented to extract information from _request_ into a object.  
The context provider is only invoked in when the context object type is needed. Use _addProvider()_ method on **
RestRouter** or **RestBuilder** to register a context provider.

```java
public class TokenProvider implements ContextProvider<Token> {

    @Override
    public Token provide(HttpServerRequest request) throws Throwable {
        String token = request.getHeader("X-Token");
        if (token != null) {
            return new Token(token);
        }

        return null;
    }
}

RestRouter.addProvider(Token.class,TokenProvider.class);
```

or

```java
RestRouter.addProvider(Token.class,request->{
    String token=request.getHeader("X-Token");
    if(token!=null){
    return new Token(token);
    }

    return null;
    });
```

or

```java
public class Token {

    public String token;

    public Token(HttpServerRequest request) {
        token = request.getHeader("X-Token");
    }
}

RestRouter.addProvider(Token.class,Token::new)
```

```java
@GET
@Path("/token")
public String readToken(@Context Token token){
    return token.getToken();
    }
```

If **@Context** for given class **can not** be provided than a **400** _@Context can not be provided_ exception is
thrown

### Pushing a custom context

While processing a request a custom context can be pushed into the vert.x routing context data storage.  
This context data can than be utilized as a method argument. The pushed context is thread safe for the current request.

> The main difference between a context push and a context provider is that the context push is executed on every request, while the registered provider is only invoked when needed!

In order to achieve this we need to create a custom handler that pushes the context before the REST endpoint is called:

```java
Router router=Router.router(vertx);
    router.route().handler(pushContextHandler());

    router=RestRouter.register(router,new CustomContextRest());
    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());

private Handler<RoutingContext> pushContextHandler(){

    return context->{
    RestRouter.pushContext(context,new MyCustomContext("push this into storage"));
    context.next();
    };
    }
```

or

```java
RestRouter.provide(TokenProvider.class); // push of context provider 
```

> A pushed context is handy in case we wan't to make sure some context related object is always present (on every request), ie. session / user ...

Then the context object can than be used as a method argument

```java

@Path("custom")
public class CustomContextRest {

    @GET
    @Path("/context")
    public String createdResponse(@Context MyCustomContext context) {
    }
}
```

### Context reader

> version 0.8.6 or later

A custom context reader can be applied to a @Context annotated variable to override the global context providers.

```java
@GET
@Path("/token")
@ContextReader(TokenProvider.class)
public String createdResponse(@Context Token token){
    return token.token;
    }

// or

@GET
@Path("/token")
public String createdResponse(@ContextReader(TokenProvider.class) @Context Token token){
    return token.token;
    }
```

## Body handler

> version 0.9.1 or later

In case needed a custom body handler can be provided for all body handling requests.

```java
BodyHandler bodyHandler=BodyHandler.create("my_upload_folder");
    RestRouter.setBodyHandler(bodyHandler);

    Router router=RestRouter.register(vertx,UploadFileRest.class);
```

or

```java
BodyHandler handler=BodyHandler.create("my_upload_folder");

    Router router=new RestBuilder(vertx)
    .bodyHandler(handler)
    .register(UploadFileRest.class)
    .build();
```

## Response building

### Response writers

Metod results are converted using response writers.  
Response writers take the method result and produce a vert.x response.

Example of a simple response writer:

```java

@Produces("application/xml")        // content-type header
@Header("X-Status: I'm a dummy")    // additional static headers
public class DummyWriter implements HttpResponseWriter<Dummy> {

    @Override
    public void write(Dummy data, HttpServerRequest request, HttpServerResponse response) {

        response.status(200); // for illustration ... needed only when overriding 200

        String out = data.name + "=" + data.value;
        response.end("<custom>" + out + "</custom>");
    }
}
```

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
    @ResponseWriter(SomeClassWriter.class) // writer will be used for this REST call only
    public SomeClass write() {

        return new SomeClass();
    }
}
```

> Global writers are used in case no other writer is specified for given type or content-type!

**Option 3** - An ResponseWriter is globally assigned to a specific class type.

```java
RestRouter.getWriters().register(SomeClass.class,SomeClassWriter.class);
    RestRouter.getWriters().register("application/json",SomeClassWriter.class);
    RestRouter.getWriters().register(SomeClassWriter.class); // where SomeClassWriter is annotated with @Produces("application/json")
```

**Option 4** - An ResponseWriter is globally assigned to a specific mime type.

```java
RestRouter.getWriters().register(MyClass.class,MyJsonWriter.class);
    RestRouter.getWriters().register("application/json",MyJsonWriter.class);
    RestRouter.getWriters().register(MyJsonWriter.class); // where MyJsonWriter is annotated with @Produces("application/json") 
```

```java

@Path("produces")
public class ConsumeJSON {

    @GET
    @Path("write")
    @Produces("application/json") // appropriate content-type writer will be looked up
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
public HttpServerResponse vertx(@Context HttpServerResponse response){

    response.setStatusCode(201);
    response.putHeader("X-MySessionHeader",sessionId);
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
public Response jax(){

    return Response
    .accepted("Hello world!!")
    .header("X-MySessionHeader",sessionId)
    .build();
    }
```

## Authentication and Authorization

> version 1.0.4 or later

Authentication and Authorization steps:

1. **RestAuthorizationProvider** reads the request and provides the appropriate **Credentials**
   for the **AuthorizationProvider**

1. **AuthorizationProvider** uses the supplied **Credentials** to identify the **User** accessing the REST endpoint
    - if the user is not identified a **401 Unauthorized** exception is thrown

1. the **User** entity is then provided to the **AuthorizationProvider** to check if user is allowed accessing the REST
   endpoint
    - User entity should return a list of allowed Authorizations to be matched against the provider authorization
    - if the user is not allowed to access the REST endpoint a **403 Forbidden** exception is thrown

### Authentication

In order to authenticate a user we can annotate a REST interface/method with the **@Authenticate** annotation.  
The **@Authenticate** annotation needs **RestAuthenticationProvider** implementation.

```java
import com.zandero.rest.annotation.*;

@Path("secure")
@Authenticate(MyAuthenticator.class)
public class ConsumeJSON {

    @GET
    @Path("path")
    @Authorize(MyAuthorizationProvider.class)
    public String OnlyAccessibleToCreditedUsers() {
        return "I'm VIP";
    }
}
```

**Example of RestAuthenticationProvider implementation:**

```java
public class MyAuthenticator implements RestAuthenticationProvider {

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {

        // process credentials if provided
        String token = credentials != null ? credentials.getString("token") : null;  // expecting credentials to be instance of TokenCredentials

        // search for the user with session expressed in token
        User user = null;
        if (token != null) {
            user = getUserWithSession(token);
        }

        if (user != null) {
            resultHandler.handle(Future.succeededFuture(user));
        } else {
            resultHandler.handle(Future.failedFuture("Missing authentication token"));
        }
    }

    @Override
    public void authenticate(Credentials credentials, Handler<AsyncResult<User>> resultHandler) {
        if (credentials instanceof TokenCredentials) {
            TokenCredentials token = (TokenCredentials) credentials;
            authenticate(token.toJson(), resultHandler);
        } else {
            resultHandler.handle(Future.failedFuture(new BadRequestException("Missing authentication token")));
        }
    }

    @Override
    public Credentials provideCredentials(RoutingContext context) {
        String token = context.request().getHeader("X-Token");
        return token != null ? new TokenCredentials(token) : null; // token might be null
    }
}
```

### Authorization

```java
public class MyAuthorizationProvider implements AuthorizationProvider {

    @Override
    public String getId() {
        return "MyAuthorizationProvider";
    }

    @Override
    public void getAuthorizations(User user, Handler<AsyncResult<Void>> handler) {
        if (PermissionBasedAuthorization.create("HasPermission").match(user)) {
            handler.handle(Future.succeededFuture());
        } else {
            handler.handle(Future.failedFuture("You are not allowed in!"));
        }
    }
}
```

### User

Example of a simple **User** entity with _PermissionBasedAuthorization_ to be matched in **MyAuthorizationProvider**

```java
public class MyUser extends UserImpl {

    private final String role;

    public MyUser(String role) {
        this.role = role;
    }

    /**
     * @return all authorizations that user can be matched against
     */
    @Override
    public Authorizations authorizations() {
        return new AuthorizationsImpl().add("MyAuthorizationProvider",
                                            PermissionBasedAuthorization.create(role));
    }
}
```

### Throwing custom exceptions on Authorization / Authentication problems

_Authorization_ and _Authentication_ throw **Unauthorized** and **Forbidden** exceptions respectively.  
But we can customize Authorization/Authentication provider by simply providing a custom exception when handling failure.
> handler.handle(Future.failedFuture(new MyException("custom")));

The thrown exception is taken over and can be further processed with an Exception handler to produce the desired output.

## Global Authenticaton / Authorization providers

We can define global authentication/authorization providers for all routes.

```java
RestBuilder builder=new RestBuilder(vertx)
    .authenticateWith(MyAuthenticator.class)
    .authorizeWith(new MyAuthorizationProvider())
    .register(EchoRest.class);

    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());
```

> Route based authentication/authorization providers override globally defined providers.

## User roles & authorization

> Up until version: **0.9.***  
> Since version **1.0.*** one should use **@Authenticate** and **@Authorize** annotations instead!  
> **@RollesAllowed**, **@PermitAll** and **@DenyAll** will still work.

User access is checked in case REST API is annotated with:

* **@RolesAllowed(role)**, **@RolesAllowed(role_1, role_2, ..., role_N)** - check if user is in any given role
* **@PermitAll** - allow everyone
* **@DenyAll** - deny everyone

User access is checked against the vert.x _User_ entity stored in _RoutingContext_, calling the _User.isAuthorised(role,
handler)_ method.

In order to make this work, we need to fill up the RoutingContext with a User entity.

> **Depricated: vert.x 3 example**

```java
public void init(){

    // 1. register handler to initialize User
    Router router=Router.router(vertx);
    router.route().handler(getUserHandler());

    // 2. REST with @RolesAllowed annotations
    TestAuthorizationRest testRest=new TestAuthorizationRest();
    RestRouter.register(router,testRest);

    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());
    }

// simple hanler to push a User entity into the vert.x RoutingContext
public Handler<RoutingContext> getUserHandler(){

    return context->{

    // read header ... if present ... create user with given value
    String token=context.request().getHeader("X-Token");

    // set user ...
    if(token!=null){
    context.setUser(new SimulatedUser(token)); // push User into context
    }

    context.next();
    };
    }
```

> **Depricated: vert.x 3 example**

```java
@GET
@Path("/info")
@RolesAllowed("User")
public String info(@Context User user){

    if(user instanceof SimulatedUser){
    SimulatedUser theUser=(SimulatedUser)user;
    return theUser.name;
    }

    return"hello logged in "+user.principal();
    }
```

**Example of User implementation:**
> **Depricated: vert.x 3 example**

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

**Using a value reader is simple:**

Register as global reader:
> Global readers are used in case no other reader is specified for given type or content-type!

```java
RestRouter.getReaders().register(MyNewObject.class,MyCustomReader.class);
    RestRouter.getReaders().register("application/json",MyCustomReader.class);
    RestRouter.getReaders().register(MyCustomReader.class); // if reader is annotated with @Consumes("application/json")

// or
    new RestBuilder(vertx).reader(MyNewObject.class,MyCustomReader.class);
    new RestBuilder(vertx).reader("appplication/json",MyCustomReader.class);
    new RestBuilder(vertx).reader(MyCustomReader.class); // if reader is annotated with @Consumes("application/json")
```

Use only local on specific REST endpoint:

```java

@Path("read")
public class ReadMyNewObject {

    @POST
    @Path("object")
    @RequestReader(MyCustomReader.class) // MyCustomReader will provide the MyNewObject to REST API
    public String add(MyNewObject item) {
        return "OK";
    }

    // or

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

**Using a response writer is simple:**  
Register as global writer:

```java
RestRouter.getWriters().register(MyObject.class,MyCustomResponseWriter.class);
// or
    new RestBuilder(vertx).writer(MyObject.class,MyCustomResponseWriter.class);
```

Use only local on specific REST endpoint:

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

### Consuming / Producing JSONs

By default **Rest.Vertx** binds _application/json_ mime type to internal _JsonValueReader_ and _JsonResponseWriter_
to read and write JSONs. This reader/writer utilizes Jackson with Vert.x internal _io.vertx.core.json.Json.mapper_
ObjectMapper.  
In order to change serialization/deserialization of JSON via Jackson the internal _io.vertx.core.json.Json.mapper_
should be altered.

## Ordering routes

By default routes area added to the Router in the order they are listed as methods in the class when registered. One can
manually change the route REST order with the **@RouteOrder** annotation.

By default each route has the order of 0.  
If route order is != 0 then vertx.route order is set. The higher the order - the later each route is listed in _Router_.
Order can also be negative, e.g. if you want to ensure a route is evaluated before route number 0.

**Example:** despite multiple identical paths the route order determines the one being executed.

```java
@RouteOrder(20)
@GET
@Path("/test")
public String third(){
    return"third";
    }

@RouteOrder(10)
@GET
@Path("/test")
public String first(){
    return"first";
    }

@RouteOrder(15)
@GET
@Path("/test")
public String second(){
    return"second";
    }
```

```java
>GET/test->"first" 
```

# Rest events

> version 0.8.6 or later

Rest events are a useful when some additional work/action must be performed based on the response produced.  
For instance, we want to send out a registration confirmation e-mail on a 200 response (a successful registration).

Rest events are triggered after the response has been generated, but before the REST has ended.  
One or more events are executed **synchronously** after the REST execution.  
The order of events triggered is not defined, nor should one event rely on the execution of another event.

Rest events can be bound to:

* http response code
* thrown exception
* or both

This is the place to trigger some async operation via event bus, or some other response based operation.

A RestEvent processor must implement the RestEvent interface (similar to ResponseWriters). The event input is either the
produced response entity or the exception thrown.  
If the event/entity pair does not match, the event is **not triggered**.

#### Example

```java
@GET
@Path("trigger/{status}")
@Events({@Event(SimpleEvent.class), // triggered on OK respons >=200 <300
    @Event(value = FailureEvent.class, exception = IllegalArgumentException.class), // triggered via exception thrown
    @Event(value = SimpleEvent.class, response = 301)}) // triggered on response code 301
public Dummy returnOrFail(@PathParam("status") int status){

    if(status>=200&&status< 300){
    return new Dummy("one","event");
    }

    if(status>=300&&status< 400){
    response.setStatusCode(301);
    return new Dummy("two","failed");
    }

    throw new IllegalArgumentException("Failed: "+status);
    }  
```

```java
public class SimpleEvent implements RestEvent<Dummy> {

    @Override
    public void execute(Dummy entity, RoutingContext context) throws Throwable {

        System.out.println("Event triggered: " + entity.name + ": " + entity.value);
        context.vertx().eventBus().send("rest.vertx.testing", JsonUtils.toJson(entity)); // send as JSON to event bus ...
    }
}
```

```java
public class FailureEvent implements RestEvent<Exception> {

    @Override
    public void execute(Exception entity, RoutingContext context) throws Throwable {
        log.error("Error: ", entity);
    }
}
```

# Enabling CORS requests

> version 0.7.4 or later

```java
Router router=new RestBuilder(vertx)
    .enableCors("*",true,1728000,allowedHeaders,HttpMethod.OPTIONS,HttpMethod.GET)
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

If no designated exception handler is provided, a default exception handler kicks in trying to match the exception type
with a build in exception handler.

## Bind exception handler to specific exception

Exception handlers are bound to an exception type - first matching exception / handler pair is used.

### Example

```java
public class MyExceptionClass extends Throwable {

    private final String error;
    private final int status;

    public MyExceptionClass(String message, int code) {
        error = message;
        status = code;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }
}

// bind exception handler to exception type
public class MyExceptionHandler implements ExceptionHandler<MyExceptionClass> {
    @Override
    public void write(MyExceptionClass result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(result.getCode());
        response.end(result.getError());
    }
}

...

// throw your exception
@GET
@Path("/throw")
@CatchWith(MyExceptionHandler.class)
public String fail(){

    throw new MyExceptionClass("Not implemented.",404);
    }
```

```
> GET /throw -> 404 Not implemented
```

## Path / Method error handler

Both class and methods support **@CatchWith** annotation.

**@CatchWith** annotation must provide an _ExceptionHandler_ implementation that handles the thrown exception:

```java
@GET
@Path("/test")
@CatchWith(MyExceptionHandler.class)
public String fail(){

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
@CatchWith({IllegalArgumentExceptionHandler.class, MyExceptionHandler.class})
public String fail(){

    throw new IllegalArgumentException("Bang!");
    }
```

```java
public class IllegalArgumentExceptionHandler implements ExceptionHandler<IllegalArgumentException> {

    @Override
    public void write(IllegalArgumentException result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(400);
        response.end("Invalid parameters '" + result.getMessage() + "'");
    }
}
```

```java
public class MyExceptionHandler implements ExceptionHandler<MyExceptionClass> {

    @Override
    public void write(MyExceptionClass result, HttpServerRequest request, HttpServerResponse response) {

        response.setStatusCode(result.getStatus());
        response.end(result.getError());
    }
}
```

## Global error handler(s)

The global error handler is invoked in case no other error handler is provided or no other exception type maches given
handlers.  
In case no global error handler is associated a default (generic) error handler is invoked.

```java
Router router=RestRouter.register(vertx,SomeRest.class);
    RestRouter.getExceptionHandlers().register(MyExceptionHandler.class);

    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());
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

* handle a subpath (regular expression pattern) where a handler was not found
* handle all not matching requests

```java
Router router=new RestBuilder(vertx)
    .register(MyRest.class)
    .notFound(".*\\/other/?.*",OtherNotFoundHandler.class) // handle all calls to an /other request
    .notFound("/rest/.*",RestNotFoundHandler.class) // handle all calls to /rest subpath
    .notFound(NotFoundHandler.class) // handle all other not found requests
    .build();
```

or

```java
RestRouter.notFound(router,"rest",RestNotFoundHandler.class);
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

Rest.vertx simplifies serving of static resource files. All you need to do is to create a REST endpoint that returns the
relative path of the desired resource file, bound with _FileResponseWriter_ writer.

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

## Uploading files

> version 0.9.1 or later

Example of a REST endpoint handling file upload.

```java
// 1. provide a BodyHandler 
BodyHandler bodyHandler = BodyHandler.create("my_upload_folder");
RestBuilder builder = new RestBuilder(vertx)
        .bodyHandler(bodyHandler)
        .register(UploadFileRest.class);
```

```java
// 2. implement File upload rest to handle incoming files
@Path("/upload")
public class UploadFileRest {

    @POST
    @Path("/file")
    public String upload(@Context RoutingContext context) {

        Set<FileUpload> fileUploadSet = context.fileUploads();
        if (fileUploadSet == null || fileUploadSet.isEmpty()) {
            return "missing upload file!";
        }

        Iterator<FileUpload> fileUploadIterator = fileUploadSet.iterator();
        List<String> urlList = new ArrayList<>();
        while (fileUploadIterator.hasNext()) {
            FileUpload fileUpload = fileUploadIterator.next();
            urlList.add(fileUpload.uploadedFileName());
        }

        return StringUtils.join(urlList, ", ");
    }
}
```

## Blocking and Async RESTs

> version 0.8.1 or later

### Default

By default all REST utilize _vertx().executeBlocking()_ call. Therefore the vertx event loop is not blocked. It will
utilize the default vertx thread pool:

```java
DeploymentOptions options=new DeploymentOptions();
    options.setWorkerPoolSize(poolSize);
    options.setMaxWorkerExecuteTime(maxExecuteTime);
    options.setWorkerPoolName("rest.vertx.example.worker.pool");

    vertx.deployVerticle(new RestVertxVerticle(settings),options);
```

Responses are always terminated (ended).

### Async

If desired a REST endpoint can return io.vertx.core._Future_ and will be executed asynchronously waiting for the future
object to finish. If used with non default (provided) _HttpResponseWriter_ the response must be terminated manually.

This should be used in case we need to use a specific vertx worker pool   
... thus we can manually execute the Future<> with that specific worker pool.

The output writer is determined upon the Future<Object> type returned. If returned future object is _null_ then due to
Java generics limitations, the object type **can not** be determined. Therefore the response will be produced by the
best matching response writer instead.

> **suggestion:** wrap null responses to object instances

#### Simple async example

> Deprecated example for **vert.x 3**, applicable to versions prior **1.0.4**   
> since **vert.x 4** _Promise_ and _CompletableFuture_ should be used instead

```java
WorkerExecutor executor=Vertx.vertx().createSharedWorkerExecutor("SlowServiceExecutor",20);
```

```java
@GET
@Path("async")
public Future<Dummy> create(@Context Vertx vertx)throws InterruptedException{

    Future<Dummy> res=Future.future();
    asyncCall(executor,res);
    return res;
    }
```

```java
public void asyncCall(WorkerExecutor executor,Future<Dummy> value)throws InterruptedException{

    executor.executeBlocking(
    fut->{
    try{
    Thread.sleep(1000);
    }
    catch(InterruptedException e){
    value.fail("Fail");
    }

    value.complete(new Dummy("async","called"));
    fut.complete();
    },
    false,
    fut->{}
    );
    }
```

## Injection

> version 8.0 or later

Allows @Inject (JSR330) injection of RESTs, writers and readers.

To provide injection an _InjectionProvider_ interface needs to be implemented.

### Binding injection provider

```java
Router router=new RestBuilder(vertx)
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
public class GuiceInjectionProvider implements InjectionProvider {

    private final Injector injector;

    public GuiceInjectionProvider(Module[] modules) {
        injector = Guice.createInjector(modules);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getInstance(Class clazz) {
        return injector.getInstance(clazz);
    }
}
```

```java
Router router=new RestBuilder(vertx).injectWith(new GuiceInjectionProvider(getModules())).build();
    vertx.createHttpServer()
    .requestHandler(router)
    .listen(PORT.get());

private Module[]getModules(){
    return new Module[]{
    new ServiceModule(),
    new SecurityModule()...
    };
    }
```

### Implement service (use @Inject if needed)

```java
import javax.ws.rs.core.Context;

public MyServiceImpl implements MyService{

private final OtherService other;

@Inject
public MyServiceImpl(OtherService service){
    other=service;
    }

public String call(){
    return"something";
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

> since version 0.8.1 or later

Rest api classes **can not** use @Context fields, @Context is provided via method parameters instead.

In case needed a RequestReader, ResponseWriter or ExceptionHandler can use a @Context annotated field,
see [Request context](#RequestContext) for details.

Use _@Context_ fields only when really necessary, as the readers, writers and handlers are not cached but initialized on
the fly on every request when needed.

This is done in order to ensure thread safety, so one context does not jump into another thread.

```java
public class StringWriter implements HttpResponseWriter<String> {

    @Context
    RoutingContext context;

    @Override
    public void write(String path, HttpServerRequest request, HttpServerResponse response) throws FileNotFoundException {

        if (context.data().get("myData") == null) {
			...
        } else { ...}
    }
```

## Internal caching

> since version 0.8.1 or later

### Caching and singletons

* All registered REST classes are singletons by default, no need to annotate them with _@Singleton_ annotation.
* By default, all _HttpResponseWriter_, _ValueReader_, _ExceptionHandler_, _RestAuthenticationProviders_ and
  _AuthoriziationProvider_
  classes are singletons that are cached once initialized.
* In case _HttpResponseWriter_, _ValueReader_, _ExceptionHandler_, _RestAuthenticationProviders_ and
  _AuthoriziationProvider_
  are utilizing a **@Context** field they are initialized on **every request** for thread safety

### Disabling caching

> since version 0.8.6 or later

To disabled caching use the @NoCache annotation.

```java

@NoCache
public class NotCachedClass() {
}
```

# Validation

> since version 0.8.4 or later

Rest.vertx can utilize any JSR 380 validation implementation, we only need to provide the appropriate validator
implementation.  
For instance we can use Hibernate implementation:

```java
HibernateValidatorConfiguration configuration=Validation.byProvider(HibernateValidator.class)
    .configure();

    Validator validator=configuration.buildValidatorFactory().getValidator();
```

Link validator with **rest.vertx**:

```java
Router router=new RestBuilder(vertx)
    .validateWith(validator)
    .register(Rest.class)
    .build();
```

or

```java
RestRouter.validateWith(validator);
```

and annotate REST calls:

```java
@POST("valid")
public int sum(@Max(10) @QueryParam("one") int one,
@Min(1) @QueryParam("two") int two,
@Valid Dummy body){
    return one+two+body.value;
    }
```

In case of a violation a _400 Bad request_ response will be generated using _ConstraintExceptionHandler_.

# Static data annotations

## @Produces on response writers

Additional to REST endpoints @Produces can also be applied to response writers.  
This will add the appropriate content-type header to the output, plus will register writer to the given content-type if
no other association is given.

Example:

```java

@Produces("application/json")
public class JsonExceptionHandler implements ExceptionHandler<String> {

    @Override
    public void write(String result, HttpServerRequest request, HttpServerResponse response) {
		...
    }
}
```

## @Header annotation

> since version 0.8.4 or later

The @Header annotation adds one or multiple static header to the response. It can be applied either to REST endpoints or
to response writers.

Example:

```java

@Header("X-Status-Reason: Validation failed")
public class ConstraintExceptionHandler implements ExceptionHandler<ConstraintException> {

    @Override
    public void write(ConstraintException result, HttpServerRequest request, HttpServerResponse response) {
        ...
    }
}
```

> since version 1.0.4 or later

The @Header annotation can be used instead of @Consumes and @Produces annotation directly on the REST endpoint.
**Rest.vertx** assigns 'Accept' headers to readers and 'Content-Type' and all other response headers to writers.

Example:

```java
    @GET
@Path("/produce")
@Header({"Accept: application/json", "Content-Type: application/json"})
public String getAsJson(){
    return"I'm Johnson";
    }

// is the same as
@GET
@Path("/produce")
@Consumes("application/json")
@Produces("application/json")
public String getAsJson(){
    return"I'm Johns son!";
    }
```

# Best practices

## Don't do this

### Don't use vert.x @Context if not necessary 

On a request @Context must be dynamically provided by the method using it. 
This means that no caching is possible and every class or chain of classes must be created on each and every request.  
This might cause unecessary processing overhead - avoid it if you can.

### Don't manipulate the response inside the REST method body

The intended workflow is as follows:
* request gets read by __RequestReader__ and deserialized into a Java Object
* REST method uses the request input to call a service and produce a result
* REST method returns the result as a Java Object
* __RequestWriter__ or __ExceptionHandler__ take the produced result and write the vertx response

# Logging

Rest.vertx uses [Slf4j](https://www.slf4j.org/) logging API. In order to see all messages produced by Rest.vertx use a
Slf4j compatible logging implementation.

## Logback logging settings example

```xml

<logger name="com.zandero.rest" level="DEBUG"/>
```

## Shorthands for method, path, consumes and produces

Instead of the following:

```java
@GET
@Path("/test")
@Consumes("application/json")
@Produces("application/json")
public String method(){...}
```

a shorthand form can be used combining all into one

```java
@Get("/test")
@Consumes("application/json")
@Produces("application/json")
public String method(){...}
```

or even:

```java
@Get(value = "/test", consumes = "application/json", produces = "application/json")
public String method(){...}
```

# Writing Unit tests

Tips and tricks to writing unit tests for your REST endpoints.

## Example

### REST endpoint

For instance, we have the following REST endpoint

```java

import javax.ws.rs.Produces;

@Path("test")
public class EchoRest {

    @GET
    @Path("echo")
    @Produces("application/json")
    public String echo() {
        return "echo";
    }
}
```

### JUnit test

We can test the REST endpoint like this:

> NOTE: once a _ExceptionHandler_, _Writer_, _Reader_ ... etc. is registered
> it is **cached**. Therefore before each test it is recommended to call
> **RestRouter.clearCache()** to make sure you have a clean set up.

```java

@ExtendWith(VertxExtension.class)
class EchoTest {

    public static final String API_ROOT = "/";
    protected static final int PORT = 4444;
    public static final String HOST = "localhost";

    protected static Vertx vertx = null;
    protected static VertxTestContext vertxTestContext;
    protected static WebClient client;

    public static void before() {

        vertx = Vertx.vertx();
        vertxTestContext = new VertxTestContext();

        // Important ... this clears any registered writers/readers/exception handlers ... 
        // and provides a clean slate for the next test
        RestRouter.clearCache();

        client = WebClient.create(vertx);
    }

    @BeforeAll
    static void start() {

        before();

        Router router = Router.router(vertx);
        RestRouter.register(router, EchoRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT.get());
    }

    @AfterEach
    void lastChecks(Vertx vertx) {
        vertx.close(vertxTestContext.succeedingThenComplete());
    }

    @AfterAll
    static void close() {
        vertx.close();
    }

    @Test
    void getEcho(VertxTestContext context) {

        client.get(PORT.get(), HOST, "/test/echo").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("\"echo\"", response.body());
                context.completeNow();
            })));
    }
}
```

The **@BeforeAll**, **@AfterEach** and **@AfterAll** methods should be moved into a helper class from there all test
classes can be extended.