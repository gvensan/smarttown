/*
 This file is used to store the connection credentials, and to initiate an MQTT connection
*/

  // eclipse test server
  var deets = {
    host: 'mrm28q29kuoha.messaging.solace.cloud',
    port: 8443,
    ssl: true,
    username: 'solace-cloud-client',
    password: 'fv1e2ijrun4r7ol5irkk9i6s4g',
  }
  
  var topicName = 'SmartTown/Operations/AggregateAlert/created/v1/+/+/+';
  var windowSizeSecs = 10000;

  // this is for MQTT, it should return a connected or connecting valid Paho client
  function getClientConnection(uniqueID,onMessageArrived,onConnectionLost,onConnect) {
    var client = new Paho.MQTT.Client(deets['host'], Number(deets['port']), uniqueID); // AWS SGP Nano
    // set the callback handlers
    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;
    // define connection options
    var connectOptions = {};
    if (deets['ssl'] == true) {
      connectOptions["useSSL"] = true;
    } else {
      connectOptions["useSSL"] = false;
    }
    //connectOptions["reconnect"] = true;
    connectOptions["userName"] = deets['username'];
    connectOptions["password"] = deets['password'];  // AWS SGP Nano
    connectOptions["onSuccess"] = onConnect;
    // try to connect!
    client.connect(connectOptions);
    return client;
  }
