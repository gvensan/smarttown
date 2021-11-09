package com.eap.scs.asyncapi.alertaggregator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

//import com.eap.scs.asyncapi.alertgenerator.OperationalAlert;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Application {

	public static final int WINDOW_DURATION_SEC = 10;
	public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	final SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public Function<Flux<OperationalAlert>, Flux<Message<AggregateAlert>>> aggregateTemperature() {
		return flux -> {
			Flux<Message<AggregateAlert>> alertFlux = flux.window(Duration.ofSeconds(WINDOW_DURATION_SEC))
														.map(a -> a)
								    					.flatMap(this::calculateAverage);
//														.log();
			return alertFlux;
		};
		
	}
					
	private Flux<Message<AggregateAlert>> calculateAverage(Flux<OperationalAlert> flux) {
		return flux
				.groupBy(po -> po.getCity())
			    .flatMap(group -> 
			    	group
						.reduce(new AggregateAlert(),
								(a, update) -> {
									AggregateAlert aa = new AggregateAlert(
										a.getCount() + 1,
										WINDOW_DURATION_SEC,
										update.getSeverity(),
										update.getAlertType(),
										update.getCity(),
										sdf.format(new Date()),
										a.getTemperature().add(update.getTemperature()),
										update.getLat(),
										update.get_long());
									return aa;
							})				
						.map(accumulator -> {
							double averageTemperature = accumulator.getTemperature().doubleValue() / accumulator.getCount();
							// Based on the defined bounds for Low, Medium and High temperature,
							// check the incoming temperature reading and set the alert priority appropriately
							String alertPriority = "High";
							if (averageTemperature > 60 && averageTemperature <= 70)
								alertPriority = "Low";
							else if (averageTemperature > 70 && averageTemperature <= 80)
								alertPriority = "Medium";
												
							AggregateAlert alert = new AggregateAlert(
														accumulator.getCount(),
														WINDOW_DURATION_SEC,
														alertPriority,
														accumulator.getAlertType(),
														accumulator.getCity(),
														sdf.format(new Date()),
														new BigDecimal(averageTemperature),
														accumulator.getLat(),
														accumulator.get_long());
							
							System.out.println("Aggregate for city: " + group.key() + "\n" + alert.toString());
							// Construct the topic name with alert type and priority as per the Topic hierarchy design
							//		SmartTown/Analytics/AggregateAlert/created/v1/{city}/{AlertPriority}/{AlertType}
							String topic = String.format("SmartTown/Operations/AggregateAlert/created/v1/%s/%s/%s",
															alert.getCity(), alert.getSeverity(), alert.getAlertType());
									
							Message<AggregateAlert> message = MessageBuilder
								.withPayload(alert)
								.setHeader(BinderHeaders.TARGET_DESTINATION, topic)
								.build();
							return message;
						}));
	}
}