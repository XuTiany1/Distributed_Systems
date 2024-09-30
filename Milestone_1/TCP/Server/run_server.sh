#Usage: ./run_server.sh [<tcp server host>]

./run_rmi.sh > /dev/null 2>&1
java -Djava.tcp.server.codebase=file:$(pwd)/ Server.ResourceManager.TCPResourceManager $1 
