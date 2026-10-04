# Spring Boot Logging Guide: Using Log4j2 with SLF4J

## 1. SLF4J vs Log4j2: Concepts & Relationship

* **SLF4J (Simple Logging Facade for Java):** An abstraction layer/interface for logging frameworks. It provides a unified API (`org.slf4j.Logger`, `org.slf4j.LoggerFactory`) so application code remains decoupled from the underlying logging engine.
* **Log4j2:** An actual implementation engine responsible for writing logs to various destinations (Console, Files, Database, etc.).
* **Relationship:** SLF4J acts as the **interface**, while Log4j2 serves as the **implementation backend**. The application calls SLF4J methods, which route logging calls to Log4j2 under the hood via a binding bridge (`log4j-slf4j-impl`).

---

## 2. Spring Boot Default Logging Framework

By default, Spring Boot uses **Logback** as its logging implementation via `spring-boot-starter-logging`. If you want to use Log4j2, you must explicitly exclude `spring-boot-starter-logging` (or starters containing it) and import `spring-boot-starter-log4j2`.

---

## 3. Configuration-Driven Log4j2 Setup

Log4j2 behavior (appenders, log levels, formatting, rolling policies) is entirely driven by its configuration file (e.g., `log4j2.xml`).

### Complete `log4j2.xml` Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN"><!-- The threshold of log4j2 library's own logs -->

    <!--
    Basic hierarchy:

    Configuration
        ├── Appenders  -> WHERE logs are written
        └── Loggers    -> WHICH logs are generated and WHERE they are sent

    Logger levels determine which logging events are allowed.
    Appender filters can further restrict which events reach each destination (Using ThresholdFilter).
    -->
    <!--
    Log levels from least severe to most severe:

    TRACE < DEBUG < INFO < WARN < ERROR < FATAL (FATAL is not supported by slf4j)

    A level of INFO allows INFO, WARN, ERROR and FATAL.
    A level of DEBUG allows DEBUG, INFO, WARN, ERROR and FATAL.
    -->

    <Appenders>

        <Console name="Console" target="SYSTEM_OUT">
            <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"/>
        </Console>

        <File name="File" fileName="logs/application.log"> <!--Relative path to cwd by default, can specify absolute paths too-->
            <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"/>
        </File>

        <File name="ControllerLogs" fileName="E:/Java_dev/Springboot Learning/SimpleCrud1/Springboot-Simple-CRUD1/3_simple-crud-with-logging/logs/application_controller.log">
            <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"/>
        </File>

        <RollingFile name="ServiceLogs"
                     fileName="logs/application_service.log"
                     filePattern="logs/application_service-%d{yyyy-MM-dd}.log">

            <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"/>

            <Policies>
                <TimeBasedTriggeringPolicy interval="1"/>
            </Policies>

        </RollingFile>
        <!--
        RollingFile allows the current log file to be rotated/renamed
        according to a triggering policy (e.g. daily or when size exceeds a limit).
        -->

    </Appenders>


    <Loggers>

        <!-- Logger specifically for the Controller package -->
        <Logger name="com.shubham.simple_crud.Controller" level="DEBUG">
            <AppenderRef ref="ControllerLogs"/>
        </Logger>
        <!--
        Logger names normally correspond to Java package/class names.

        A logger for:
        com.shubham.simple_crud.Controller

        also applies to loggers beneath that package, e.g.:
        com.shubham.simple_crud.Controller.ProductController
        -->

        <Logger name="com.shubham.simple_crud.Service" level="DEBUG" additivity="false">
            <AppenderRef ref="ServiceLogs"/>
        </Logger>
        <!--
        additivity defaults to true.

        Therefore, logs from a child/package logger propagate to its parent
        logger and eventually to the Root logger.

        additivity="false" prevents this propagation.
        -->


        <!-- Default logger for everything else -->
        <Root level="INFO">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>

    </Loggers>

</Configuration>
```

---

## 4. Required Implementation Steps

### Step 1: Update `pom.xml` Dependencies
Exclude `spring-boot-starter-logging` from default starters and add `spring-boot-starter-log4j2`:

```xml
<dependencies>
    <!-- Web Starter excluding Logback -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!-- JPA Starter excluding Logback -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!-- Log4j2 Dependency -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
</dependencies>
```

---

### Step 2: Configure `application.properties`
Point Spring Boot directly to your Log4j2 configuration file located inside `src/main/resources`:

```properties
logging.config=classpath:log4j2.xml
```

---

### Step 3: Usage in Java Code (SLF4J Standard)
Always write code using SLF4J imports (`org.slf4j.Logger` and `org.slf4j.LoggerFactory`), allowing you to swap logging engines without changing business code.

#### Standard Declaration:
```java
package com.shubham.simple_crud.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/products")
    public String getProducts() {
        logger.info("Fetching all products...");
        logger.debug("Debugging controller method execution");
        return "Products List";
    }
}
```

#### Lombok Alternative (`@Slf4j`):
If using Lombok, you can replace the `LoggerFactory.getLogger(...)` line with an annotation:

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProductController {

    public void someMethod() {
        log.info("Log message using Lombok @Slf4j");
    }
}
```