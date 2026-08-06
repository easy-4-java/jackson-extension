[English](./README.md) | [简体中文](./README.zh-CN.md)

# jackson-extension

[项目概述](#1-项目概述) | [功能与状态](#2-功能与状态) | [环境要求](#3-环境要求与兼容性) | [架构与模块](#4-架构与模块) | [安装](#5-安装) | [快速开始](#6-快速开始) | [配置](#7-配置) | [核心用法](#8-核心用法--api) | [测试与构建](#9-测试与构建) | [版本管理](#10-版本与分支) | [许可协议](#11-贡献与许可)

> **项目状态**：`feature/3.0.x` 版本线维护中（JDK 8）。制品尚未发布到 Maven Central，通过项目私服与 GitHub Releases 分发。

## 1. 项目概述

`jackson-extension` 是独立的 Jackson 扩展组件，提供 Java 时间类型格式化、空值序列化、数据脱敏与标量类型转换能力。它是一个纯库：不依赖 Spring、无自动配置——你需要在自己创建的 `ObjectMapper`（或通过框架的 Jackson 定制点）上注册它的各部件。

是什么：

- `JavaTimeModule`——`LocalDateTime` / `LocalDate` / `LocalTime` 序列化与反序列化统一使用 `yyyy-MM-dd HH:mm:ss` / `yyyy-MM-dd` / `HH:mm:ss` 格式；
- 空值处理——`BeanSerializerModifier` 将 null 字符串输出为 `""`、null 数组/集合输出为 `[]`，并可按需开启 null 数值、布尔、日期、对象默认值；
- 数据脱敏——`@Sensitive(strategy = ...)` 注解 + 上下文感知序列化器，内置 12 种脱敏策略（手机号、身份证、邮箱、银行卡、中文姓名等）；
- 标量转换——`ToLongDeserializer` 将数字字符串经 `BigDecimal` 转为 `Long`，空串容忍。

不是什么：

- 不是 Spring Boot Starter，也不是"一站式"完整 Jackson 套件——它不替代 `jackson-datatype-jsr310` 与 `jackson-datatype-jdk8`，这两个是应保留的编译期依赖。

典型场景：

| 场景 | 使用 |
| :--- | :--- |
| JSON API 中统一的 `java.time` 输出 | `JavaTimeModule` |
| null 字段输出为 `""` / `[]` 而不是缺失 | `MyBeanSerializerModifier`（+ `Null*JsonSerializer` 单例） |
| 返回 DTO 前对敏感字段脱敏 | `@Sensitive` + `SensitiveStrategy` |
| `Long` 字段容忍 `"123.45"` 这类字符串 | `ToLongDeserializer` |

## 2. 功能与状态

| 能力 | 状态 | 说明 |
| :--- | :--- | :--- |
| `java.time` 固定格式模块 | 已实现 | `JavaTimeModule`（`LocalDate` / `LocalDateTime` / `LocalTime` 序列化 + 反序列化） |
| null 字符串序列化 | 已实现 | `NullStringJsonSerializer` → `""` |
| null 数组/集合序列化 | 已实现 | `NullArrayJsonSerializer` → `[]` |
| null 数值/布尔/日期/对象处理 | 已实现 | `NullNumberJsonSerializer`、`NullBooleanJsonSerializer`、`NullDateJsonSerializer`、`NullObjectJsonSerializer`（可通过 `MyBeanSerializerModifier` 构造参数开关） |
| 敏感数据脱敏 | 已实现 | `@Sensitive` + `SensitiveJsonSerializer` + `SensitiveStrategy` 12 种策略 |
| 标量转换 | 已实现 | `ToLongDeserializer`（字符串 → `Long`） |
| 测试 | 已有 | JUnit 5 套件，覆盖脱敏策略、输出格式与并发隔离 |

## 3. 环境要求与兼容性

| 项目 | 要求 |
| :--- | :--- |
| JDK | 21+ |
| Maven | 3.0+（内置 Maven Wrapper `mvnw`） |
| Jackson | 2.17.2（databind、datatype-jdk8、datatype-jsr310、module-parameter-names） |
| 其他依赖 | commons-lang3 3.20.0、slf4j-api 2.0.18、lombok（provided）；JUnit 5.11.4（测试） |

版本线：

| 分支 | JDK | 版本模式 |
| :--- | :---: | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. 架构与模块

```text
                        你的 ObjectMapper
                               |
     +-------------------------+--------------------------+
     |                         |                          |
 JavaTimeModule         MyBeanSerializerModifier    @Sensitive /
 (java.time 格式)        (null -> "" | [] | 0 ...)  SensitiveJsonSerializer
                                                          |
     +----------------------------------------------------+
     |
     v
POJO  -->  Jackson (databind 2.17.x)  -->  JSON
```

单模块 jar。`io.github.easy4j.jackson` 下的包结构：

| 包 | 内容 |
| :--- | :--- |
| `io.github.easy4j.jackson` | `JavaTimeModule` |
| `io.github.easy4j.jackson.annotation` | `@Sensitive`、`SensitiveStrategy`（12 种脱敏策略） |
| `io.github.easy4j.jackson.ser` | `SensitiveJsonSerializer`、`Null*JsonSerializer` 系列、`MyBeanSerializerModifier` |
| `io.github.easy4j.jackson.deser` | `ToLongDeserializer` |

## 5. 安装

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>jackson-extension</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:jackson-extension:3.0.x.x.20260630-SNAPSHOT'
```

快照版本由项目私服提供（见 pom 中 `distributionManagement`）。尚未发布 Maven Central 正式版。

## 6. 快速开始

注册时间模块与空值处理 modifier：

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

按此配置，名为 `createdAt` 的 `LocalDateTime` 字段与值为 null 的 `String` 字段 `remark` 将序列化为：

```json
{"createdAt":"2026-08-05 10:30:00","remark":""}
```

而不是 `"2026-08-05T10:30:00"` 与缺失（或 null）的 `remark`。使用 `@Sensitive` 注解脱敏字段：

```java
import io.github.easy4j.jackson.annotation.Sensitive;
import io.github.easy4j.jackson.annotation.SensitiveStrategy;

public class Payload {
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    public String phone;
}
```

当 `phone = "13812345678"` 时，序列化输出为 `"138****5678"`，且原对象不被修改。

## 7. 配置

纯库组件，没有任何配置项或配置前缀。所有行为都在代码中通过 `ObjectMapper` 定制：

- `new JavaTimeModule()` 控制时间格式；
- `new MyBeanSerializerModifier()` 控制 null 默认值——全参构造函数接收六个布尔值（数组 / 数值 / 字符串 / 日期 / 布尔 / 对象），只开启你需要的默认值：
  默认等价于 `new MyBeanSerializerModifier(true, false, true, true, false, true)`；
- 字段级脱敏使用 `@Sensitive(strategy = ...)`。

## 8. 核心用法 / API

### 8.1 脱敏策略

`SensitiveStrategy.mask(String)` 可直接应用策略（以下为测试套件的真实输出）：

```java
SensitiveStrategy.PHONE.mask("13812345678");           // "138****5678"
SensitiveStrategy.CHINESE_NAME.mask("张三丰");           // "张**"
SensitiveStrategy.ID_CARD.mask("130722199102323232");  // "130***********3232"
SensitiveStrategy.EMAIL.mask("tester@example.com");    // "t*****@example.com"
SensitiveStrategy.BANK_CARD.mask("6222021234567890");  // "6222********7890"
SensitiveStrategy.NONE.mask("raw");                    // "raw"
```

### 8.2 字符串 → Long 反序列化

```java
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.easy4j.jackson.deser.ToLongDeserializer;

public class Order {
    @JsonDeserialize(using = ToLongDeserializer.class)
    public Long total;   // "123.9" -> 123L，"" 或 null -> null
}
```

## 9. 测试与构建

```bash
./mvnw clean verify
```

构建配置：

- JUnit 5（`junit-jupiter` 5.11.4）+ Maven Surefire；
- JaCoCo 覆盖率报告 + 行覆盖率检查规则，最低目标 90%（`haltOnFailure=false`）；
- package 阶段附加源码包与 Javadoc 包；
- 提供 `release` profile（GPG 签名 + Central 发布插件），仅用于正式发布。

## 10. 版本与分支

三条并行版本线，各自绑定一个 JDK 基线：

| 分支 | JDK | 版本模式 | 维护状态 |
| :--- | :---: | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | 当前开发线 |
| `feature/2.0.x` | 17 | `2.0.x.*` | 并行维护 |
| `feature/3.0.x` | 21 | `3.0.x.*` | 并行维护 |

各版本线使用对应 JDK 编译与测试，依赖版本（含 Jackson）随版本线可能不同。本分支快照版本为 `3.0.x.x.20260630-SNAPSHOT`。

## 11. 贡献与许可

欢迎通过 GitHub Issue 或 Pull Request 参与贡献。所有源码基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) 许可。
