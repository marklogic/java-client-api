package com.marklogic.client;

abstract public class ResourceManager {
	private ResourceServices services;
	protected ResourceManager() {
		super();
	}
	protected void init(ResourceServices services) {
		this.services = services;
	}
	protected ResourceServices getServices() {
		return services;
	}
	public String getName() {
		return (services != null) ? services.getResourceName() : null;
	}
    public void startLogging(RequestLogger logger) {
    	if (services != null)
    		services.startLogging(logger);
    }
    public void stopLogging() {
    	if (services != null)
    		services.stopLogging();
    }
}
