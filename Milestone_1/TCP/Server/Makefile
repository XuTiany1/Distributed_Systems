all: compile-server-tcp

TCPInterface.jar: Server/Interface/IResourceManager.java
	@echo "Compiling TCP server interface"
	javac Server/Interface/IResourceManager.java
	jar cvf RMIInterface.jar Server/Interface/IResourceManager.class

compile-server-tcp: Server/TCPHandlers/*.java Server/Interface/IResourceManager.java Server/Common/*.java
	javac Server/TCPHandlers/*.java Server/Interface/IResourceManager.java Server/Common/*.java

clean:
	rm -f Server/Interface/*.class Server/Common/*.class Server/RMI/*.class
	rm -f RMIInterface.jar
