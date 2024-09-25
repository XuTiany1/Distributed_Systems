package Server.Middleware;

public abstract class Middleware extends ResourceManager {
    
    // Instance of
	IResourceManager flight_resourceManager = null;
	IResourceManager car_resourceManager = null;
	IResourceManager room_resourceManager = null;

    public Middleware(String name) {
        super(name);
    }
}
