# Introduction

Prior to Java 8u191, Java configuration in Docker containers was a bit tricky because 
[JVM Ergonomics ](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/ergonomics.html) set some values 
(like CPU cores or Heap memory limit) based on Docker daemon configuration. This behaviour forced to set JVM limits 
explicitly (with -Xmx for example) or via command line options (like -XX:+UseCGroupMemoryLimitForHeap). Now, with Java 
8u191 or Java 10, the JVM sets theses values based on Docker container's configuration instead of Docker daemon. 

We'll compare these different behaviours running a demo application in Java 8u121 and Java 8u191. Unlike the former, the 
latter is a container aware Java version. 

The application visualizes the number of CPU cores and the memory limit in the logs: 

```
    public static void main(String[] args) {
        long memory = Runtime.getRuntime().maxMemory();
        int cpuCores = Runtime.getRuntime().availableProcessors();

        LOGGER.info("Max Java Memory (MB): {}", memory / (1_024 * 1_024));
        LOGGER.info("Max Java CPU Cores: {}", cpuCores);
    }
``` 

We'll see that the values are different depending on the Java version. 

# Requirements

This demo only requires Docker installed in your computer.

# Set up

Clone this repo:

```
    git clone https://github.com/wearearima/docker-java-cpu-memory-limit.git
```

Move to the downloaded repo, `cd docker-java-cpu-memory-limit`, and build same image with different Java versions:

 - Java 8u121 image:

```  
    docker build -t eu.arima/docker-java-cpu-memory-limit:java8u121 -f Dockerfile_java8u121 .
```

 - Java 8u191 image:

```
    docker build -t eu.arima/docker-java-cpu-memory-limit:java8u191 -f Dockerfile_java8u191 .
```

We can check the new images invoking `docker image ls`:

```
REPOSITORY                                     TAG                 IMAGE ID            CREATED             SIZE
eu.arima/docker-java-cpu-memory-limit          java8u121           7e26f907aed2        29 hours ago        92.2MB
eu.arima/docker-java-cpu-memory-limit          java8u191           c4eabbac6bd6        29 hours ago        88.6MB
```

# Docker daemon configuration

Before comparing how JVM ergonomics behaves in each version, let's find out how much memory and how many cpu cores are 
configured in Docker daemon. To do so execute:

```  
    docker info
```
 
This command returns all information about Docker daemon's configuration. For this demo we only pay attention to `CPUs` 
and `Total memory` parameters. In my case these are the values:

```
CPUs: 2
Total Memory: 5.818GiB
``` 

Therefore, I've got configured 2 CPU cores and almost 6GB memory for all my containers. 

# Run 

For demonstration purposes, containers' memory and cpu will be limited running theses commands:

 - Java 8u121 container:

```  
    docker run --memory=1GB --cpus=1 eu.arima/docker-java-cpu-memory-limit:java8u121
```

 - Java 8u191 container:

```
    docker run --memory=1GB --cpus=1 eu.arima/docker-java-cpu-memory-limit:java8u191 
```

Memory is limited to 1 GB with `--memory=1GB` option and CPU to one core with `--cpus=1` option.

# Results

The result is:

| Feature                                     | Java 8u121 | Java 8u191 |
| --------------------------------------------| ---------- | ---------- |
| Max Java Memory (MB)                        | 1324       | 247        |
| Max Java CPU Cores                          | 2          | 1          |

With Java 8u191 java ergonomics has calculated the memory and cpu limits based on the configuration of the container 
(1GB and 1 cpu). However, the container with Java 8u121 has calculated these features based on the Docker daemon 
configuration (5.818GiB and 2 cpus).  

# Resources

Useful links:

 - [Java SE Support for Docker](https://blogs.oracle.com/java-platform-group/java-se-support-for-docker-cpu-and-memory-limits)
 - [Java inside docker](https://developers.redhat.com/blog/2017/03/14/java-inside-docker/)
 - [Limit a Docker container's resources](https://docs.docker.com/config/containers/resource_constraints/)

# Credits

By https://www.arima.eu

![ARIMA Software Design](https://arima.eu/arima-claim.png)
