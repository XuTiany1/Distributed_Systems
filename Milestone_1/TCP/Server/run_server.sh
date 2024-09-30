#Usage: ./run_server.sh [<tcp server host>]

./run_rmi.sh > /dev/null 2>&1
java Server.ResourceManager.TCPResourceManager $1
