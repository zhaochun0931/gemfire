sender:
node1 10.0.0.4

receiver:
node2 10.0.0.5


# it is better to run the script on the node2 since it the receiver side.
gfsh run --file gemfire2.gfsh

Cluster-2 gfsh>



# node1
gfsh run --file gemfire1.gfsh





# you should restart the gateway if you setup the sender gateway first on the gemfire1

stop gateway-sender --id=gwxxx
start gateway-sender --id=gwxxx



