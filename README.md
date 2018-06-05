# Introduction

Prior to JDK10, Java configuration in Docker containers was a bit tricky because 
[JVM Ergonomics ](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/ergonomics.html) set some values 
(like CPU cores or Heap memory limit) based on Docker daemon configuration. This behaviour forced to set JVM limits 
explicitly (with -Xmx for example) or via command line options (like -XX:+UseCGroupMemoryLimitForHeap). Now, with JDK10
, the JVM sets theses values based on Docker container's configuration instead of Docker daemon. 

We'll compare these different behaviours running a demo application in JDK8 and JDK10. The application visualize the 
number of CPU cores and the memory limit in the logs: 

```
    public static void main(String[] args) {
        long memory = Runtime.getRuntime().maxMemory();
        int cpuCores = Runtime.getRuntime().availableProcessors();

        LOGGER.info("Max Java Memory (MB): {}", memory / (1_024 * 1_024));
        LOGGER.info("Max Java CPU Cores: {}", cpuCores);
    }
``` 

We'll see that the values are different depending on the JDK. 

# Requirements

This demo only requires Docker installed in your computer.

# Set up

Clone this repo:

```
    git clone https://github.com/wearearima/docker-java-cpu-memory-limit.git
```

Move to the downloaded repo, `cd docker-java-cpu-memory-limit`, and build same image with different JDK versions:

 - JDK8 image:

```  
    docker build -t eu.arima/docker-java-cpu-memory-limit:java8 -f Dockerfile_jdk8 .
```

 - JDK10 image:

```
    docker build -t eu.arima/docker-java-cpu-memory-limit:java10 -f Dockerfile_jdk10 .
```

We can check the new images invoking `docker image ls`:

```
REPOSITORY                                     TAG                 IMAGE ID            CREATED             SIZE
eu.arima/docker-java-cpu-memory-limit          java8               7e26f907aed2        29 hours ago        631MB
eu.arima/docker-java-cpu-memory-limit          java10              c4eabbac6bd6        29 hours ago        870MB
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

 - JDK8 container:

```  
    docker run --memory=1GB --cpus=1 eu.arima/docker-java-cpu-memory-limit:java8
```

 - JDK10 container:

```
    docker run --memory=1GB --cpus=1 eu.arima/docker-java-cpu-memory-limit:java10 
```

Memory is limited to 1 GB with `--memory=1GB` option and CPU to one core with `--cpus=1` option.

# Results

The result is:

| Feature                                     | Java 8     | Java 10    |
| --------------------------------------------| ---------- | ---------- |
| Max Java Memory (MB)                        | 1324       | 247        |
| Max Java CPU Cores                          | 2          | 1          |

With JDK10 java ergonomics has calculated the memory and cpu limits based on the configuration of the container 
(1GB and 1 cpu). However, the container with JDK8 has calculated these features based on the Docker daemon 
configuration (5.818GiB and 2 cpus).  

# Resources

Useful links:

 - [Java SE Support for Docker](https://blogs.oracle.com/java-platform-group/java-se-support-for-docker-cpu-and-memory-limits)
 - [Java inside docker](https://developers.redhat.com/blog/2017/03/14/java-inside-docker/)
 - [Limit a Docker container's resources](https://docs.docker.com/config/containers/resource_constraints/)

# Credits

By https://www.arima.eu

![ARIMA Software Design](https://arima.eu/arima-claim.png)
