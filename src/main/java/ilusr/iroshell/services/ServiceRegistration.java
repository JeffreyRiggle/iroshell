package ilusr.iroshell.services;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ilusr.core.ioc.ServiceManager;


public class ServiceRegistration {
	
	public static void register(ServiceManager manager) throws IOException, URISyntaxException {
		Resource resource = new ClassPathResource("serviceregistrations.xml", ServiceRegistration.class);
		manager.registerServicesFromResource(resource);
	}
}
