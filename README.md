# jackson-extension

[Overview](#1-project-overview) | [Features](#2-features--status) | [Requirements](#3-requirements--compatibility) | [Architecture](#4-architecture--modules) | [Installation](#5-installation) | [Quick Start](#6-quick-start) | [Configuration](#7-configuration) | [Core Usage](#8-core-usage--api) | [Testing & Build](#9-testing--build) | [Versioning](#10-versioning--branches) | [License](#11-contributing--license)

> **Status**: maintained on the `feature/2.0.x` line (JDK 17). Artifacts are not yet published to Maven Central; they are distributed through the project's private repository and GitHub Releases.

## 1. Project Overview

`jackson-extension` is a standalone Jackson extension that covers Java time formatting, null-value serialization, sensitive-data masking and scalar type conversion. It is a plain library: no Spring dependency, no auto-configuration — you register its pieces on your own `ObjectMapper` (or via your framework's Jackson customization).

What it is:

- `JavaTimeModule` — fixed `yyyy-MM-dd HH:mm:ss` / `yyyy-MM-dd` / `HH:mm:ss` formats for `LocalDateTime` / `LocalDate` / `LocalTime` on both serialization and deserialization.
- Null handling — a `BeanSerializerModifier` that emits `""` for null strings, `[]` for null arrays/collections, and (configurably) null-number, null-boolean, null-date and null-object defaults.
- Data masking — a `@Sensitive(strategy = ...)` annotation plus a contextual serializer supporting 12 masking strategies (phone, ID card, email, bank card, Chinese name, ...).
- Scalar conversion — a `ToLongDeserializer` that converts numeric strings to `Long` (via `BigDecimal`) and tolerates blank values.

What it is not:

- Not a Spring Boot starter and not a complete Jackson "all-in-one" bundle — it does not replace `jackson-datatype-jsr310` or `jackson-datatype-jdk8`, which are compile-time dependencies you should keep.

Typical scenarios:

| Scenario | What to use |
| :--- | :--- |
| Uniform `java.time` output in JSON APIs | `JavaTimeModule` |
| `null` fields serialized as `""` / `[]` instead of omitted | `MyBeanSerializerModifier` (+ the `Null*JsonSerializer` singletons) |
| Masking PII fields before returning DTOs | `@Sensitive` + `SensitiveStrategy` |
| Tolerating `"123.45"`-style strings for `Long` fields | `ToLongDeserializer` |

## 2. Features & Status

| Capability | Status | Notes |
| :--- | :--- | :--- |
| `java.time` fixed-pattern module | Implemented | `JavaTimeModule` (ser + deser for `LocalDate` / `LocalDateTime` / `LocalTime`) |
| Null string serialization | Implemented | `NullStringJsonSerializer` → `""` |
| Null array/collection serialization | Implemented | `NullArrayJsonSerializer` → `[]` |
| Null number / boolean / date / object handling | Implemented | `NullNumberJsonSerializer`, `NullBooleanJsonSerializer`, `NullDateJsonSerializer`, `NullObjectJsonSerializer` (toggleable via `MyBeanSerializerModifier` constructor flags) |
| Sensitive data masking | Implemented | `@Sensitive` + `SensitiveJsonSerializer` + 12 strategies in `SensitiveStrategy` |
| Scalar conversion | Implemented | `ToLongDeserializer` (string → `Long`) |
| Tests | Present | JUnit 5 suite covering masking strategies, output format and concurrency isolation |

## 3. Requirements & Compatibility

| Item | Requirement |
| :--- | :--- |
| JDK | 17+ |
| Maven | 3.0+ (Maven Wrapper `mvnw` included) |
| Jackson | 2.17.2 (databind, datatype-jdk8, datatype-jsr310, module-parameter-names) |
| Other deps | commons-lang3 3.20.0, slf4j-api 2.0.18, lombok (provided); JUnit 5.11.4 (test) |

Version lines:

| Branch | JDK | Version pattern |
| :--- | :---: | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. Architecture & Modules

```text
                        your ObjectMapper
                               |
     +-------------------------+--------------------------+
     |                         |                          |
 JavaTimeModule         MyBeanSerializerModifier    @Sensitive /
 (java.time formats)    (null -> "" | [] | 0 ...)   SensitiveJsonSerializer
                                                          |
     +----------------------------------------------------+
     |
     v
POJO  -->  Jackson (databind 2.17.x)  -->  JSON
```

Single-module jar. Package layout under `io.github.easy4j.jackson`:

| Package | Contents |
| :--- | :--- |
| `io.github.easy4j.jackson` | `JavaTimeModule` |
| `io.github.easy4j.jackson.annotation` | `@Sensitive`, `SensitiveStrategy` (12 masking strategies) |
| `io.github.easy4j.jackson.ser` | `SensitiveJsonSerializer`, `Null*JsonSerializer` set, `MyBeanSerializerModifier` |
| `io.github.easy4j.jackson.deser` | `ToLongDeserializer` |

## 5. Installation

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>jackson-extension</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:jackson-extension:2.0.x.x.20260630-SNAPSHOT'
```

The snapshot is served from the project's private repository (see `distributionManagement` in the pom). No Maven Central release is available yet.

## 6. Quick Start

Register the Java time module and the null-handling modifier:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import io.github.easy4j.jackson.JavaTimeModule;
import io.github.easy4j.jackson.ser.MyBeanSerializerModifier;

ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());

mapper.setSerializerFactory(
        BeanSerializerFactory.instance.withSerializerModifier(new MyBeanSerializerModifier()));
```

With this configuration, a `LocalDateTime` field named `createdAt` and a null `String` field named `remark` serialize as:

```json
{"createdAt":"2026-08-05 10:30:00","remark":""}
```

instead of `"2026-08-05T10:30:00"` and an omitted (or `null`) remark. Mask a field with the `@Sensitive` annotation:

```java
import io.github.easy4j.jackson.annotation.Sensitive;
import io.github.easy4j.jackson.annotation.SensitiveStrategy;

public class Payload {
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    public String phone;
}
```

Serializing `phone = "13812345678"` yields `"138****5678"` while the original object stays untouched.

## 7. Configuration

This is a plain library — there are no configuration properties or prefixes. All behavior is configured in code on your `ObjectMapper`:

- `new JavaTimeModule()` for time formats;
- `new MyBeanSerializerModifier()` for null defaults — the full constructor takes six booleans (array / number / string / date / boolean / object) so you can enable only the defaults you need:
  `new MyBeanSerializerModifier(true, false, true, true, false, true)` is the default;
- `@Sensitive(strategy = ...)` per field for masking.

## 8. Core Usage / API

### 8.1 Masking strategies

`SensitiveStrategy.mask(String)` applies a strategy directly (real outputs from the test suite):

```java
SensitiveStrategy.PHONE.mask("13812345678");           // "138****5678"
SensitiveStrategy.CHINESE_NAME.mask("张三丰");           // "张**"
SensitiveStrategy.ID_CARD.mask("130722199102323232");  // "130***********3232"
SensitiveStrategy.EMAIL.mask("tester@example.com");    // "t*****@example.com"
SensitiveStrategy.BANK_CARD.mask("6222021234567890");  // "6222********7890"
SensitiveStrategy.NONE.mask("raw");                    // "raw"
```

### 8.2 String → Long deserialization

```java
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.easy4j.jackson.deser.ToLongDeserializer;

public class Order {
    @JsonDeserialize(using = ToLongDeserializer.class)
    public Long total;   // "123.9" -> 123L, "" or null -> null
}
```

## 9. Testing & Build

```bash
./mvnw clean verify
```

The build is configured with:

- JUnit 5 (`junit-jupiter` 5.11.4) and Maven Surefire;
- JaCoCo coverage reporting plus a line-coverage check rule with a 90% minimum target (`haltOnFailure=false`);
- Source and Javadoc jars attached at package time;
- a `release` profile (GPG signing + Central publishing) reserved for official releases.

## 10. Versioning & Branches

Three parallel version lines, each bound to a JDK baseline:

| Branch | JDK | Version pattern | Maintenance |
| :--- | :---: | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | Current development line |
| `feature/2.0.x` | 17 | `2.0.x.*` | Maintained in parallel |
| `feature/3.0.x` | 21 | `3.0.x.*` | Maintained in parallel |

Each line is compiled and tested with its own JDK baseline; dependency versions (including Jackson) may differ per line. Snapshots on this branch are versioned `2.0.x.x.20260630-SNAPSHOT`.

## 11. Contributing & License

Contributions are welcome — open an issue or pull request on GitHub. All source files are licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).
