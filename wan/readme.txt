gemfire1 10.0.0.4

gemfire2 10.0.0.5


it's better to run the script on the gemfire2 since it the receiver side.

gfsh run --file gemfire2.gfsh


gfsh run --file gemfire1.gfsh





you should restart the gateway if you setup the sender gateway first on the gemfire1

stop gateway-sender --id=xxxgw

start gateway-sender --id=xxxgw
