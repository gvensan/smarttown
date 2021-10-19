# Smart Town Workshop - Building EventAPI Products using Spring Cloud Stream, Event Portal and AsyncAPI

You have everything here to complete the workshop with the help of codelab [Creating Event API Products using Spring Cloud Stream, Event Portal and AsyncAPI](https://codelabs.solace.dev/create-api-products-using-scs-and-ep).

At the end of this exercise, you would have built microservices that demonstrates a smart town function of collecting IoT sensor data, monitoring device temperatures, generating alerts and aggregate alerts to be plotted on a google map.

### Microservices

a) __IoT Simulator__ - A microservice to simulate IoT sensors, publishing temperature reading every second      
b) __Alert Aggregator__ - A controller that subscribes to alerts and aggregate the temperature readings to generate aggregate alerts with appropriate priority      
c) __Alert Generator__ - A controller that subscribes to temperature readings and generate alerts with appropriate priority         
	_NOTE:_ You will be using AsyncAPI code generator to generate spting code using spring cloud stream binder and modify the code to generate alerts of different severity based on the recorded tempoerature.

### Web Application

a) __Alerts Dashboard__ - A web application that subscribes to alerts and plot the aggregated temperature on a google map

