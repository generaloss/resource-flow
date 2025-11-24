# [Resource-Flow](https://github.com/generaloss/resource-flow)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.generaloss/resource-flow.svg)](https://mvnrepository.com/artifact/io.github.generaloss/resource-flow)

A lightweight and unified resource access library for Java applications. Provides consistent APIs for working with files, classpath resources, URLs, ZIP archives, and more.

## Features

* Unified resource abstraction - single API for multiple resource types
* Resource sources:
  * File system resources
  * Classpath/internal resources
  * URL resources
  * ZIP/JAR archive entries
  * Temporary files
* Streaming utilities - Binary and text streaming with convenience methods
* Resource management - Handle-based resource lifecycle management
* Flexible I/O - Support for various data types and formats

---

## Installation

Add the dependency from Maven Central:

``` xml
<dependency>
    <groupId>io.github.generaloss</groupId>
    <artifactId>resource-flow</artifactId>
    <version>25.11.2</version>
</dependency>
```

Requirements:
* Java 11 or higher
* External dependencies are **optional** (spatial-math, raw-list)

---

## Examples

### Basic Usage

``` java
// Different resource types
Resource fileRes = Resource.file("config/settings.json");
Resource internalRes = Resource.internal("defaults.properties");
Resource urlRes = Resource.url("https://example.com/data.txt");

// Read content
String content = fileRes.readString();
byte[] data = classpathRes.readBytes();
String[] lines = urlRes.readLines();
```

### Working with Files

``` java
// File operations
FileResource config = Resource.file("app/config.yaml");
config.createWithParents(); // Create file and parent directories

// Write data
config.writeString("key: value");
config.appendString("\nnew: data");

// List 'app/' directory contents
FileResource[] files = config.parent().listResources();
```

### FastReader

``` java
FastReader reader = resource.reader();
while (reader.hasNext()) {
    String line = reader.nextLine();
    int number = reader.nextInt();
    String word = reader.nextWord();
}
```

### Binary streams reading/writing

``` java
BinaryInputStream binInput = resource.inStreamBin();
// Read complex data structures
String name = binInput.readUTFString();
int[] ids = binInput.readIntArray();
UUID entityId = binInput.readUUID();
// (If has installed spatial-math library)
Vec3f position = binInput.readVec3f();
Quaternion rotation = binInput.readQuaternion();

BinaryOutputStream binOutput = fileRes.outStreamBin();
// Write in the same way
binOutput.writeInt(42);
binOutput.writeFloatArray(1.0f, 2.0f, 3.0f);
```

### Advanced Resource Types

``` java
// ZIP archive resources
ZipFile zip = new ZipFile("archive.zip");
ZipResource[] entries = Resource.zip(zip);

// Classpath resource scanning
ClasspathResource pkg = Resource.classpath("com/example/models");
Class<?>[] classes = pkg.listClassesRecursive();

// Temporary files
TempFileResource temp = Resource.temp("prefix-", ".tmp");
temp.writeBytes(data);
```