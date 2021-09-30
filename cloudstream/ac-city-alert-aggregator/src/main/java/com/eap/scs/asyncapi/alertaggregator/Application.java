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
import reactor.core.publisher.Mono;

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
			Flux<Message<AggregateAlert>> alertFlux = flux.log().window(Duration.ofSeconds(WINDOW_DURATION_SEC))
														.flatMap(this::calculateAverage)
														.log();
			return alertFlux;
		};
	}
					
	private Mono<Message<AggregateAlert>> calculateAverage(Flux<OperationalAlert> flux) {
		return flux
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
					
					// Construct the topic name with alert type and priority as per the Topic hierarchy design
					//		SmartTown/Operations/AggregateAlert/created/v1/{city}/AlertPriority}/{AlertType}
					String topic = String.format("SmartTown/Operations/AggregateAlert/created/v1/%s/%s/%s",
													alert.getCity(), alert.getSeverity(), alert.getAlertType());
							
					Message<AggregateAlert> message = MessageBuilder
						.withPayload(alert)
						.setHeader(BinderHeaders.TARGET_DESTINATION, topic)
						.build();
					return message;
				}).log();
	}
}

	
//	@Bean
//	public Function<Flux<OperationalAlert>, Flux<AggregateAlert>> aggregateTemperature() {
//		return flux -> flux.log().window(Duration.ofSeconds(WINDOW_DURATION_SEC)).flatMap(this::calculateAverage).log();
//	}
//
//	private Mono<AggregateAlert> calculateAverage(Flux<OperationalAlert> flux) {
//		// Aggregate the events in those windows
//		return flux
//				.reduce(new AggregateAlert(0, 0, 0),
//						(a, tempUpdate) -> {
//							System.out.println("UPDATE:" + tempUpdate.toString());
//							return new AggregateAlert(a.getRideCount() + (1 +(int)(Math.random() * 100)),
//										a.getTotalMeter() + (1 +(int)(Math.random() * 100)),
//										a.getTotalPassengers() + (1 +(int)(Math.random() * 100)));
//						});
//
//				// Calculate the window average in RideAveragePayload objects
////				.map(accumulator -> {
////					if (accumulator.getRideCount() == 0) { 
////						// Window was empty, return empty RideAveragePayload
////						AggregateAlert alert = new AggregateAlert();
////						alert.setDurationSec(WINDOW_DURATION_SEC);
////						alert.setTemperature(accumulator.getTotalMeter().add(new BigDecimal(1000)));
////						alert.setCount(20);
////						alert.setTimeStamp(sdf.format(new Date()));
////						return alert;
////					} else { 
////						// Calculate averages based on window
////						AggregateAlert alert = new AggregateAlert();
////						alert.setDurationSec(WINDOW_DURATION_SEC);
////						alert.setTemperature(accumulator.getTotalMeter().add(new BigDecimal(3000)));
////						alert.setCount(20);
////						alert.setTimeStamp(sdf.format(new Date()));
////						return alert;
////					}
////				}).log();
//	}
//	
//	static class AggregateAlert {
//		public int getRideCount() {
//			return rideCount;
//		}
//		public void setRideCount(int rideCount) {
//			this.rideCount = rideCount;
//		}
//		public int getTotalMeter() {
//			return totalMeter;
//		}
//		public void setTotalMeter(int totalMeter) {
//			this.totalMeter = totalMeter;
//		}
//		public int getTotalPassengers() {
//			return totalPassengers;
//		}
//		public void setTotalPassengers(int totalPassengers) {
//			this.totalPassengers = totalPassengers;
//		}
//		@Override
//		public String toString() {
//			return "AggregateAlert [rideCount=" + rideCount + ", totalMeter=" + totalMeter + ", totalPassengers="
//					+ totalPassengers + "]";
//		}
//		public AggregateAlert(int rideCount, int totalMeter, int totalPassengers) {
//			super();
//			this.rideCount = rideCount;
//			this.totalMeter = totalMeter;
//			this.totalPassengers = totalPassengers;
//		}
//		private int rideCount;
//		private int totalMeter;
//		private int totalPassengers;
//	}
//	
//}