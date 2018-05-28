package eu.arima.dockercpumemorylimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		long memory = Runtime.getRuntime().maxMemory();
		int cpuCores = Runtime.getRuntime().availableProcessors();

		LOGGER.info("Max Java Memory (MB): {}", memory / (1_024 * 1_024));
		LOGGER.info("Max Java CPU Cores: {}", cpuCores);
	}

}
