./run_rmi.sh > /dev/null

echo "Usage: ./run_middleware.sh <Flights Server hostname> <Cars Server hostname> <Rooms Server hostname>"
echo "e.g. ./run_middleware.sh tr-open-08 tr-open-09 tr-open-10"
echo '  $1 - hostname of Flights'
echo '  $2 - hostname of Cars'
echo '  $3 - hostname of Rooms'

java -Djava.rmi.server.codebase=file:$(pwd)/ Server.RMI.RMIMiddleware $1 $2 $3
