# Usage: ./run_client.sh [<server_hostname> [<server_rmiobject>]]

java -cp ../Server/RMIInterface.jar:. Client.Client $1 $2
