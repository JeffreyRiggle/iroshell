package ilusr.iroshell.dockarea;

import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ilusr.core.ioc.ServiceManager;
import ilusr.iroshell.dockarea.overlay.OverlayStyleNames;
import ilusr.iroshell.services.IStyleContainerService;
import ilusr.iroshell.services.RegistrationType;

public class DockInitializer {

	private static boolean initialized;
	
	public static void Initialize() throws URISyntaxException {
		if (initialized) {
			return;
		}
		
		IStyleContainerService styleService = ServiceManager.getInstance().<IStyleContainerService>get("IStyleContainerService");
		styleService.register(OverlayStyleNames.DEFAULT_ARROW, "-fx-fill: #00ced1", RegistrationType.Override);
		styleService.register(OverlayStyleNames.HOVER_ARROW, "-fx-fill: #0000ff", RegistrationType.Override);
		styleService.register(OverlayStyleNames.PREVIEW_DROP, "-fx-background-color:rgba(0, 224, 179, .3);", RegistrationType.Override);
		
		styleService.register(DockStyleNames.PANEL_DRAG, "-fx-background-color:rgba(0, 224, 179, .3);", RegistrationType.Override);
		
		initialized = true;
		
		Resource resource = new ClassPathResource("dockarearegistrations.xml", DockInitializer.class);
		ServiceManager.getInstance().registerServicesFromResource(resource);
	}
}
