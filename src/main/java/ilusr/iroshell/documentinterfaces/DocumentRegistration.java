package ilusr.iroshell.documentinterfaces;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ilusr.core.ioc.ServiceManager;


public class DocumentRegistration {
	public static void register(ServiceManager manager) throws IOException, URISyntaxException {
		Resource resource = new ClassPathResource("documentregistrations.xml", DocumentRegistration.class);
		manager.registerServicesFromResource(resource);
	}
}
