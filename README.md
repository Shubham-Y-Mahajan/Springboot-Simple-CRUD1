# Spring Boot Notes

Concise notes covering **Dependency Injection (DI), Spring Web / REST, and Spring Data JPA**.

---

# 1. Dependency Injection and Autowiring in Spring Boot

## 1.1 Loose Coupling and Programming to Interfaces

In Java, classes should generally **depend on abstractions (interfaces)** rather than concrete implementations. This gives **loose coupling**.

For example, a `Garage` should depend on `Vehicle`, not directly on `Honda`:

```java
interface Vehicle {
    void drive();
}

class Honda implements Vehicle {
    public void drive() {
        System.out.println("Honda driving");
    }
}
```

Without Dependency Injection, we normally create the object ourselves:

```java
class Garage {
    Vehicle car1 = new Honda();
}
```

Here, the programmer is responsible for **creating and managing the dependency**.

---

## 1.2 Inversion of Control (IoC) and Dependency Injection (DI)

**Inversion of Control (IoC)** means that object creation and lifecycle management are moved from your application code to an external framework/container.

You focus on writing the business logic instead of manually creating and managing every object.

**Dependency Injection (DI)** is one way of implementing IoC: the required dependency is **provided (injected) by the container** instead of being created inside the class.

In Spring, the **Spring container** performs this object creation and dependency management.

> **IoC = philosophy/principle**  
> **DI = a common way to implement IoC**

---

## 1.3 Spring Container and Spring Beans

In Spring, the **Spring container** is represented mainly by an `ApplicationContext`.

It lives as part of the application running inside the JVM and manages **Spring Beans** — objects whose creation and lifecycle are managed by Spring.

Traditionally, Spring could be configured using XML or Java configuration files. Spring Boot uses **auto-configuration and component scanning** to reduce the amount of explicit configuration needed.

---

## 1.4 Bean Scopes

The two basic scopes commonly used in Spring are:

### Singleton

- Default scope.
- One bean instance per Spring container.
- For a typical Spring Boot application, the singleton instance is created during application startup (when the bean is instantiated eagerly).

```java
@Component
public class Alien {
    public Alien() {
        System.out.println("Object created");
    }
}
```

```java
@SpringBootApplication
public class SimpleCrud1Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(SimpleCrud1Application.class, args);

        Alien a1 = context.getBean(Alien.class);
        Alien a2 = context.getBean(Alien.class);
    }
}
```

`Object created` is printed **once**, because both `a1` and `a2` refer to the same singleton bean.

### Prototype

- A new bean instance is created **each time the Spring container is asked for the bean**.
- Therefore, two `getBean()` calls produce two different instances.

```java
@Component
@Scope("prototype")
public class Alien {
    public Alien() {
        System.out.println("Object created");
    }
}
```

Now `Object created` is printed **twice** for the two `getBean()` calls.

> Note: Prototype scope does not mean "a new object on every method call". A new instance is created when the container resolves/requests the prototype bean.

---

## 1.5 What does `@Autowired` do?

`@Autowired` tells Spring to **find a suitable bean from the container and inject it**.

By default, autowiring is primarily resolved **by type**.

Example:

```java
@Component
public class Spaceship {
    public void fly() {
        System.out.println("flyy");
    }
}
```

### Field Injection

```java
@Component
public class Alien {
    private int aid;
    private String aname;
    private String tech;

    @Autowired
    private Spaceship ship;
}
```

### Constructor Injection

```java
@Component
public class Alien {
    private Spaceship ship;

    // With a single constructor, @Autowired is not required.
    public Alien(Spaceship ship) {
        this.ship = ship;
    }
}
```

### Setter Injection

```java
@Component
public class Alien {
    private Spaceship ship;

    @Autowired
    public void setSpaceShip(Spaceship ship) {
        this.ship = ship;
    }
}
```

The three approaches provide dependency injection, but **constructor injection is generally preferred** because the dependency is explicit and the object can be created in a valid state. Field injection is less convenient for unit testing and makes dependencies less visible.

---

## 1.6 What if Multiple Beans Have the Same Type?

Consider an interface with two implementations:

```java
public interface SpaceVehicle {
    void fly();
}
```

```java
@Component
public class Spaceship implements SpaceVehicle {
    public void fly() {
        System.out.println("Spaceship flying");
    }
}
```

```java
@Component
public class UFO implements SpaceVehicle {
    public void fly() {
        System.out.println("UFO flying");
    }
}
```

Now this is ambiguous:

```java
@Autowired
private SpaceVehicle spv;
```

Spring finds **two beans of type `SpaceVehicle`**.

### Option 1 — `@Primary`

Mark one implementation as the preferred candidate:

```java
@Component
@Primary
public class Spaceship implements SpaceVehicle {
    // ...
}

@Component
public class UFO implements SpaceVehicle {
    // ...
}
```

Then:

```java
@Autowired
private SpaceVehicle spv;
```

`spv` is injected with the `Spaceship` bean.

> `@Primary` gives a bean preference when multiple candidates of the same type exist. If multiple candidates are still equally preferred, the ambiguity must be resolved another way.

### Option 2 — `@Qualifier`

Specify the exact bean to inject:

```java
@Component
public class Spaceship implements SpaceVehicle {
    // ...
}

@Component
public class UFO implements SpaceVehicle {
    // ...
}
```

```java
@Autowired
@Qualifier("UFO")
private SpaceVehicle spv;
```

`spv` is now injected with the `UFO` bean.

By default, the bean name is usually the class name converted to lower camel case. For example:

```text
Spaceship -> spaceship
ProductService -> productService
UFO -> UFO
```

This follows Spring's bean-name generation rules; consecutive leading uppercase letters such as `UFO` are preserved.

A custom bean name can also be declared:

```java
@Component("myShip")
public class Spaceship implements SpaceVehicle {
}
```

---

## 1.7 Spring Boot and Configuration Files

Spring Boot generally does **not require explicit bean-definition configuration files** such as XML in the way traditional Spring Core applications often do.

For example, component scanning is enough in many cases:

```java
@Component
public class MyService {
}
```

Spring Boot automatically discovers and registers it as a bean.

However, Java configuration is still fully supported:

```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyService();
    }
}
```

So the important idea is:

> **Spring Boot reduces the need for explicit bean-definition configuration; it does not eliminate configuration completely.**

### `application.properties`

Spring Boot commonly uses `application.properties` for application configuration:

```properties
spring.application.name=simpleWebApp
server.port=8081
```

---

# 2. Spring Web

## 2.1 Java Applications vs Web Applications

A normal Java program runs inside the JVM, while a web application must **listen for network requests** such as HTTP requests.

Java web applications are built on the **Servlet API**. A servlet is a Java component that handles web requests and responses.

**Apache Tomcat** is a **Servlet container** that runs servlet-based applications.

Spring MVC is built on top of the Servlet API. In a Spring Boot web application, Spring MVC runs through a central servlet called the **`DispatcherServlet`**.

> Spring controllers are **not converted into separate servlets**. They are Spring-managed components whose requests are handled through the `DispatcherServlet`.

---

## 2.2 Tomcat and Spring Boot

Apache Tomcat is commonly used to run Java web applications.

Spring Boot simplifies deployment by providing an **embedded Tomcat** when using the typical Spring Web starter.

- A traditional deployment can package the application as a `.war` file and deploy it to an external servlet container such as Tomcat.
- A Spring Boot application is commonly packaged as an executable `.jar` containing the application and an embedded server.

For example:

```bash
java -jar myapp.jar
```

---

## 2.3 Typical Layers in a Spring Boot Application

A common application structure is:

```text
Client
  |
  v
Controller  -> handles HTTP requests/responses
  |
  v
Service     -> contains business logic
  |
  v
Repository  -> communicates with the database
```

### Controller
Handles incoming client requests and sends responses.

### Service
Contains business logic.

### Repository
Handles persistence/database interaction.

For example, the controller may depend on a service:

```java
@RestController
public class ProductController {

    private ProductService service;

    @Autowired
    public void setService(ProductService service) {
        this.service = service;
    }

    // request-handling methods...
}
```

The service object is injected by Spring rather than being manually created with `new`.

> The service layer should contain the business logic; the controller should mainly deal with HTTP concerns and delegate work to the service.

---

## 2.4 Spring MVC

**MVC = Model + View + Controller**

Historically, server-side MVC applications generated both the **HTML (View)** and the **data (Model)** on the server. Technologies such as JSP and Thymeleaf are examples of server-side view technologies.

In modern applications, the frontend is often a separate application. The backend commonly exposes **REST APIs** that return data (often JSON), while the frontend is responsible for rendering the UI.

The important distinction is:

- **Controller** -> handles the request
- **Model** -> represents application/domain data
- **View** -> presents the data to the user

> The **Repository is not the same thing as the MVC Model**. The repository is a persistence/data-access layer. In a typical REST backend, there may be no server-side View at all.

---

## 2.5 Creating a Spring Web Application

When creating a Spring Boot project, add the **Spring Web** dependency.

Spring Boot can also use **Spring Boot DevTools** during development for features such as automatic restart/live reload support.

---

# 3. Spring Web — Controllers and REST APIs

## 3.1 `@Controller` and `@RestController`

A controller can be defined using:

```java
@Controller
public class HomeController {

    @RequestMapping("/")
    public String homepage() {
        return "Welcome";
    }
}
```

With `@Controller`, a returned `String` is normally interpreted as a **view name**.

So Spring will try to resolve a view called `Welcome`.

If the intention is to return the string itself as the HTTP response body, use either `@RestController` or `@ResponseBody`.

### Option 1 — `@RestController`

```java
@RestController
public class HomeController {

    @RequestMapping("/")
    public String homepage() {
        return "Welcome";
    }
}
```

### Option 2 — `@ResponseBody`

```java
@Controller
public class HomeController {

    @RequestMapping("/")
    @ResponseBody
    public String homepage() {
        return "Welcome";
    }
}
```

`@RestController` is effectively a convenience annotation combining `@Controller` and `@ResponseBody`.

---

## 3.2 Front Controller — `DispatcherServlet`

You can have multiple controller classes in a Spring Boot application.

Spring MVC uses a central **Front Controller**, the `DispatcherServlet`, to receive incoming requests and dispatch them to the controller method whose mapping matches the request.

Conceptually:

```text
Client Request
      |
      v
DispatcherServlet
      |
      v
Matching Controller Method
```

---

## 3.3 Mapping Requests

`@RequestMapping` can be placed on a class or method.

```java
@RestController
public class ProductController {

    @RequestMapping("/products")
    public String products() {
        return "Products";
    }
}
```

### Important correction

`@RequestMapping` does **not** mean GET only.

If no HTTP method is specified, it can match **multiple HTTP methods**.

To explicitly specify GET:

```java
@RequestMapping(path = "/product/count", method = RequestMethod.GET)
```

Or, more commonly, use the specialized mapping:

```java
@GetMapping("/product/count")
```

Other specialized mappings are:

```java
@PostMapping
@PutMapping
@DeleteMapping
```

---

## 3.4 HTTP Methods and CRUD

A common CRUD mapping is:

| HTTP Method | CRUD Operation |
|---|---|
| `POST` | Create |
| `GET` | Read |
| `PUT` | Update |
| `DELETE` | Delete |

These are conventional REST mappings. The HTTP methods themselves do not force a CRUD meaning; the application defines how each endpoint behaves.

---

## 3.5 `@PathVariable` — Data in the URL Path

Suppose the client requests a product using its ID:

```text
GET /products/101
```

The value `101` can be captured using `@PathVariable`:

```java
@GetMapping("/products/{prodId}")
public Product getProductById(@PathVariable int prodId) {
    return service.getProductById(prodId);
}
```

Here:

```text
{prodId} -> path variable
101     -> actual value supplied by the client
```

---

## 3.6 `@RequestBody` — Data in the Request Body

When the client sends data as the request payload, use `@RequestBody`.

For example, a client can send:

```json
{
  "prodId": 101,
  "prodName": "Keyboard",
  "price": 1500
}
```

Controller:

```java
@PostMapping("/products")
public void add(@RequestBody Product prod) {
    service.addProduct(prod);
}
```

The JSON request body is converted into a Java object.

`PUT` commonly works the same way when sending an updated object:

```java
@PutMapping("/products")
public void update(@RequestBody Product prod) {
    service.updateProduct(prod);
}
```

`DELETE` commonly uses a path variable when deleting a specific resource:

```java
@DeleteMapping("/products/{prodId}")
public void delete(@PathVariable int prodId) {
    service.deleteProductById(prodId);
}
```

But this is a convention, not a strict rule: HTTP methods and parameter styles depend on the API design.

---

## 3.7 JSON Conversion — Jackson

With the usual Spring Boot **Spring Web** starter, **Jackson** is commonly included and used for JSON serialization/deserialization.

Conceptually:

```text
Java Object  <---->  JSON
   |                   |
   +---- Jackson ------+
```

- **Serialization:** Java object -> JSON
- **Deserialization:** JSON -> Java object

Spring MVC uses HTTP message converters to perform this conversion.

---

## 3.8 Complete Controller Example

```java
@RestController
public class ProductController {

    private ProductService service;

    @Autowired
    public void setService(ProductService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return service.getProducts();
    }

    @GetMapping("/products/{prodId}")
    public Product getProductById(@PathVariable int prodId) {
        return service.getProductById(prodId);
    }

    @PostMapping("/products")
    public void add(@RequestBody Product prod) {
        service.addProduct(prod);
    }

    @PutMapping("/products")
    public void update(@RequestBody Product prod) {
        service.updateProduct(prod);
    }

    @DeleteMapping("/products/{prodId}")
    public void delete(@PathVariable int prodId) {
        service.deleteProductById(prodId);
    }
}
```

The same URL path can be used for different methods when the **HTTP method differs**. For example, `GET /products` and `POST /products` are different endpoint mappings.

---

# 4. Spring Data JPA

## 4.1 ORM — Object Relational Mapping

**ORM = Object-Relational Mapping**

It maps:

```text
Java Objects  <->  Relational Database Tables
```

The ORM framework maps Java classes and fields to database tables and columns.

By convention, the class/table and field/column names are often mapped automatically, although annotations can override the defaults.

For example:

```text
Java Class     -> Database Table
Java Field     -> Database Column
Java Object    -> Database Row
```

ORM reduces the need to write basic SQL manually.

However, ORM does **not** mean that SQL completely disappears. The ORM framework generates/executes SQL behind the scenes for database operations.

**Hibernate** is one of the most widely used ORM implementations in the Java ecosystem. **EclipseLink** is another example.

---

## 4.2 JPA — Java Persistence API

**JPA = Java Persistence API**

JPA is a **standard/specification** that defines how Java applications perform ORM/persistence.

Hibernate is an **implementation of JPA**.

A useful way to think about it is:

```text
JPA        -> specification / interfaces / standard
Hibernate  -> implementation
```

Using JPA APIs allows application code to avoid being tightly coupled to one particular ORM implementation.

---

## 4.3 Spring Data JPA

**Spring Data JPA** is the Spring module that simplifies working with JPA repositories.

The database driver sits between the application and the database and provides the JDBC connectivity needed to communicate with the specific database.

Therefore, the appropriate database driver must be included as a dependency.

---

## 4.4 Database Configuration

Database connection properties can be placed in `application.properties`.

For example, with an in-memory H2 database:

```properties
spring.datasource.url=jdbc:h2:mem:shubham
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
```

The general structure of a JDBC URL is roughly:

```text
jdbc:<database>:<database-specific-location-or-options>
```

The exact URL format depends on the database.

You can also enable SQL logging:

```properties
spring.jpa.show-sql=true
```

> Spring Boot can auto-configure some datasource properties, especially for embedded databases, so explicitly specifying every property is not always required.

### In-Memory Database

An **in-memory database** stores its data in memory rather than in persistent disk storage.

For a typical H2 `mem` database:

```text
Application starts -> database is created in memory
Application stops  -> database contents are lost
```

---

# 5. Repository Layer

The **Repository layer** is responsible for communicating with the database.

## 5.1 `JpaRepository`

`JpaRepository` is a Spring Data JPA interface that already provides common CRUD and related operations.

We create our own repository interface by extending it:

```java
@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
}
```

The type parameters mean:

```text
Product -> entity/domain type
Integer -> type of the entity's primary key
```

We create **an interface, not an implementation class**.

Spring Data JPA creates the repository implementation and registers it as a Spring bean.

So you can simply inject the repository into a service.

---

# 6. Entity

An **entity** is a Java class that JPA maps to a database table.

Use `@Entity` on the class:

```java
@Entity
public class Product {

    @Id
    private int prodId;

    private String prodName;
    private int price;

    // getters and setters
}
```

Here:

```text
Product class   -> Product table (by default)
prodId          -> primary key column
prodName        -> column
price           -> column
```

`@Id` identifies the primary key field.

An entity must have an identifier, specified using `@Id` or `@EmbeddedId`.

> JPA annotations such as `@Entity` and `@Id` are not Spring annotations. They come from the JPA specification (`jakarta.persistence` in modern Spring Boot versions).

> **Managed by JPA/Hibernate != the same thing as being a Spring bean.** An entity normally does **not** need `@Component`.

Therefore, this is generally unnecessary:

```java
@Entity
@Component
public class Product {
}
```

Usually just use:

```java
@Entity
public class Product {
}
```

A typical repository is associated with one entity type:

```java
@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
}
```

---

# 7. Service + Repository Example

```java
@Service
public class ProductService {

    private ProductRepo repo;

    @Autowired
    public void setRepo(ProductRepo repo) {
        this.repo = repo;
    }

    public List<Product> getProducts() {
        return repo.findAll();
    }

    public void addProduct(Product prod) {
        repo.save(prod);
    }

    public Product getProductById(int prodId) {
        return repo.findById(prodId).orElse(null);
    }
}
```

The important flow is:

```text
HTTP Request
     |
     v
Controller
     |
     v
Service
     |
     v
Repository
     |
     v
Database
```

Common methods provided by `JpaRepository` include:

```java
repo.findAll();
repo.findById(id);
repo.save(entity);
repo.deleteById(id);
```

`save()` is commonly used for both **insert and update** operations (the exact behavior depends on the entity's persistence state/ID handling), so it is often described as an **upsert-like operation**.

---

# 8. Key Annotations at a Glance

| Annotation | Purpose |
|---|---|
| `@Component` | Registers a class as a Spring bean |
| `@Service` | Specialized `@Component` for service-layer classes |
| `@Repository` | Specialized Spring stereotype for repository/data-access classes |
| `@Controller` | Spring MVC controller; methods can return views |
| `@RestController` | Controller whose methods return response bodies by default |
| `@Autowired` | Requests dependency injection from Spring |
| `@Qualifier` | Selects a specific bean when multiple candidates exist |
| `@Primary` | Gives one candidate preference during autowiring |
| `@Entity` | Marks a class as a JPA entity |
| `@Id` | Marks the entity's primary key |
| `@RequestBody` | Binds request body data to a Java object |
| `@PathVariable` | Binds a value from the URL path |
| `@GetMapping` | Maps an HTTP GET request |
| `@PostMapping` | Maps an HTTP POST request |
| `@PutMapping` | Maps an HTTP PUT request |
| `@DeleteMapping` | Maps an HTTP DELETE request |

> `@Service`, `@Repository`, and `@Controller` are specialized Spring stereotype annotations built on top of `@Component`.
