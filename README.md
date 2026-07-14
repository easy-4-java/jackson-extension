# jackson-extension

独立的 Jackson 扩展组件，提供 Java 时间类型、空值、数据脱敏以及标量类型转换支持。

## 版本线

| 分支 | Java | Jackson | 组件版本 |
| --- | --- | --- | --- |
| `feature/1.0.x` | 8 | 2.18.x | `1.0.x.20260630-SNAPSHOT` |
| `feature/2.0.x` | 17 | 2.22.x | `2.0.x.20260630-SNAPSHOT` |
| `feature/3.0.x` | 21 | 3.2.x | `3.0.x.20260630-SNAPSHOT` |

## Maven

```xml
<dependency>
    <groupId>io.github.hiwepy</groupId>
    <artifactId>jackson-extension</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

不同版本线分别使用对应 JDK 进行编译和测试。Jackson 3 使用 `tools.jackson.*` 包，源码与 Jackson 2 版本线独立维护。
