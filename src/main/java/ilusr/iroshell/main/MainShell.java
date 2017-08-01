package ilusr.iroshell.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;

import ilusr.core.ioc.ServiceManager;
import ilusr.core.javafx.splashscreen.SplashScreen;
import ilusr.core.javafx.splashscreen.SplashScreenModel;
import ilusr.iroshell.core.CoreRegistration;
import ilusr.iroshell.dockarea.DockInitializer;
import ilusr.iroshell.documentinterfaces.DocumentRegistration;
import ilusr.iroshell.features.DefaultErrorDialogProvider;
import ilusr.iroshell.features.ErrorDialogProvider;
import ilusr.iroshell.features.ExceptionDisplay;
import ilusr.iroshell.features.ExceptionHandler;
import ilusr.iroshell.features.IPreApplicationScreen;
import ilusr.iroshell.menus.MenuRegistration;
import ilusr.iroshell.services.DialogService;
import ilusr.iroshell.services.IApplicationClosingManager;
import ilusr.iroshell.services.IDialogService;
import ilusr.iroshell.services.ILayoutService;
import ilusr.iroshell.services.IOverlayProvider;
import ilusr.iroshell.services.LayoutService;
import ilusr.iroshell.services.PersistableToolBarService;
import ilusr.iroshell.services.PersistenceService;
import ilusr.iroshell.services.ServiceRegistration;
import ilusr.iroshell.statusbar.StatusBarRegistration;
import ilusr.logrunner.LogRunner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//TODO: Clean this up a lot this is feeling like bad code.
/**
 * 
 * @author Jeff Riggle
 *
 */
public class MainShell extends Application {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public IApplicationSettings applicationSettings;
	
	public MainShell() {
		applicationSettings = new ApplicationSettings();
	}
	
	@Override
    public void start(Stage stage) throws Exception {
		//TODO: Clean this up a bit.
		LogRunner.setApplicationName(applicationSettings.applicationName());
		LogRunner.setLoggingLevel(Level.ALL);
		LogRunner.logger().log(Level.INFO, String.format("Starting application %s", applicationSettings.applicationName()));
		runPreApplicationScreens();
    }

	private void runPreApplicationScreens() {
		LogRunner.logger().log(Level.INFO, "Running pre-application screens.");
		
		if (applicationSettings.preApplicationScreens().size() == 0) {
			runApplication(null);
			return;
		}
		
		runPreApplicationScreen(applicationSettings.preApplicationScreens().get(0));
	}
	
	private void runPreApplicationScreen(final IPreApplicationScreen screen) {
		LogRunner.logger().log(Level.INFO, "Running pre-application screen");
		
		final Stage preApplicationStage = new Stage();
		screen.setOnCompleted((e) -> {
			
			if (e.shouldTerminate()) {
				LogRunner.logger().log(Level.INFO, "Closing application due to terminate pre-application screen.");
				preApplicationStage.close();
				return;
			}
			
			boolean useNext = false;
			IPreApplicationScreen nextScreen = null;
			for (IPreApplicationScreen pas : applicationSettings.preApplicationScreens()) {
				if (pas == screen) {
					useNext = true;
					continue;
				}
				
				if (useNext) {
					nextScreen = pas;
					runPreApplicationScreen(pas);
					preApplicationStage.close();
					break;
				}
			}
			
			screen.setOnCompleted(null);
			
			if (useNext && nextScreen == null) {
				LogRunner.logger().log(Level.INFO, "No more pre-application screens to run. Running main app.");
				runApplication(preApplicationStage);
			}
		});
		
		screen.run(preApplicationStage);
		preApplicationStage.show();
	}
	
	private void runApplication(Stage preApplicationStage) {
		SplashScreenModel model = createSplashModel();
		Stage splashStage = createSplashStage(model);
		
		try {
			scheduler.schedule(() -> {
				registerServices(model, splashStage, preApplicationStage);
			}, 0, TimeUnit.NANOSECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private SplashScreenModel createSplashModel() {
		SplashScreenModel retVal = null;
		
		if (!hasFeature(ApplicationFeatures.SPLASH_SCREEN)) {
			return retVal;
		}
		
		LogRunner.logger().log(Level.INFO, "Creating splash screen.");
		retVal = new SplashScreenModel();
		
		retVal.applicationName().setValue(applicationSettings.applicationName());
		retVal.setCurrentState("Creating Model");
		retVal.version().setValue(applicationSettings.version());
		retVal.fontFamily().setValue(applicationSettings.splashOptions().fontFamily());
		
		if (applicationSettings.splashOptions().backgroundColor() != null) {
			retVal.setBackgroundColor(applicationSettings.splashOptions().backgroundColor());
		}
		if (applicationSettings.splashOptions().backgroundImage() != null) {
			retVal.setBackgroundImage(applicationSettings.splashOptions().backgroundImage());
		}
		return retVal;
	}
	
	private Stage createSplashStage(SplashScreenModel model) {
		Stage splashStage = null;
		if (!hasFeature(ApplicationFeatures.SPLASH_SCREEN)) {
			return splashStage;
		}
		
		splashStage = new Stage(StageStyle.UNDECORATED);
		splashStage.setOnCloseRequest((we) -> {
			LogRunner.logger().log(Level.INFO, "Closing splash screen");
		});
		
		Scene splashRoot;
		
		Parent customSplashScreen = applicationSettings.splashOptions().customSplashScreen();
		if (customSplashScreen != null) {
			splashRoot = new Scene(customSplashScreen);
		}
		else {
			SplashScreen splash = new SplashScreen(model);
			splashRoot = new Scene(splash, 300, 300);
		}
		
		splashStage.setScene(splashRoot);
		splashStage.show();
		
		return splashStage;
	}
	
	private void registerServices(SplashScreenModel model, Stage stage, Stage preApplicationStage) {
		try {
			setupRegistrations(model);
			ServiceManager.getInstance().<ILayoutService>get("LayoutService").documentType().set(applicationSettings.getDocumentType());
			
			Platform.runLater(() -> {
				try {
					setupUIAndShow(model);
					intializeComponents(new LoadData(ServiceManager.getInstance(), model));
					if (hasFeature(ApplicationFeatures.PERSISTENCE_MANAGEMENT)) {

						PersistenceService manager = ServiceManager.getInstance().<PersistenceService>get("IPersistenceService");
						manager.loadPersistence();
					}
					
					if (preApplicationStage != null) {
						preApplicationStage.close();
					}
					
					if (stage != null) {
						stage.close();					
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setupApplicationFromSettings(Stage stage) {
		if (applicationSettings.applicationIcon() != null) {
    		stage.getIcons().add(applicationSettings.applicationIcon());
        }
		
        stage.titleProperty().set(applicationSettings.applicationName());
	}
	
	private void setupRegistrations(SplashScreenModel model) throws IOException, URISyntaxException {
		if (model != null) {
			model.setCurrentState("Registering services.");
		}

		LogRunner.logger().log(Level.INFO, "Registering services");
		ServiceManager manager = ServiceManager.getInstance();
		CoreRegistration.register(manager);
		MenuRegistration.register(manager);
		DocumentRegistration.register(manager);
		ServiceRegistration.register(manager);
        DockInitializer.Initialize();
        StatusBarRegistration.register(manager);
	}
	
	private void setupUIAndShow(SplashScreenModel model) throws IOException {
		LogRunner.logger().log(Level.INFO, "Loading UI");
        
		if (model != null) {
        	model.setCurrentState("Loading UI.");
        }
        
		Parent root = new ApplicationShell();
		
		Stage mainStage = new Stage(StageStyle.DECORATED);
        Scene scene = new Scene(root);

        mainStage.setScene(scene);
        scene.getStylesheets().add(MainShell.class.getResource("mainshell.css").toExternalForm());

        //TODO: This seems like an odd place to do this.
        registerDialogService(mainStage, (IOverlayProvider)root);
        
        if (hasFeature(ApplicationFeatures.EXCEPTION_HANDLING)) {
        	setupExceptionHandler();
		}
        
        setupApplicationFromSettings(mainStage);

		if (hasFeature(ApplicationFeatures.PERSISTENCE_MANAGEMENT)) {
        	LogRunner.logger().log(Level.INFO, "Setting up peristenceManagement");
			try {
				registerPersistenceService(mainStage, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	} else {
			registerPersistenceService(mainStage, false);
    	}
		
        mainStage.setOnCloseRequest((we) -> {
        	LogRunner.logger().log(Level.INFO, "Application closing");
        	ServiceManager.getInstance().<IApplicationClosingManager>get("IApplicationClosingManager").onClose();
        	
        	//TODO: Should I have to do this?
        	System.exit(0);
        });
        
        mainStage.show();
        
		if (hasFeature(ApplicationFeatures.FULL_SCREEN_START)) {
			mainStage.setMaximized(true);
		}
	}
	
	private void registerPersistenceService(Stage mainStage, boolean usingPersistence) {
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
        cav.addGenericArgumentValue(mainStage);
        cav.addGenericArgumentValue(getPersistenceLocation(usingPersistence));
        cav.addGenericArgumentValue(ServiceManager.getInstance().<IApplicationClosingManager>get("IApplicationClosingManager"));
        cav.addGenericArgumentValue(ServiceManager.getInstance().<PersistableToolBarService>get("PersistableToolBarService"));
        cav.addGenericArgumentValue(ServiceManager.getInstance().<LayoutService>get("LayoutService"));
        RootBeanDefinition persistenceService = new RootBeanDefinition(PersistenceService.class, cav, null);
        
        ServiceManager.getInstance().registerBean("IPersistenceService", persistenceService);
	}
	
	private void registerDialogService(Stage mainStage, IOverlayProvider overlayProvider) {
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
        cav.addGenericArgumentValue(mainStage.getOwner());
        cav.addGenericArgumentValue(overlayProvider);
        cav.addGenericArgumentValue(applicationSettings.applicationIcon());
        RootBeanDefinition dialogService = new RootBeanDefinition(DialogService.class, cav, null);
        
        ServiceManager.getInstance().registerBean("IDialogService", dialogService);
	}
	
	private String getPersistenceLocation(boolean usingPersistence) {
		String retVal = new String();
		
		if (!usingPersistence) {
			return retVal;
		}
		
		if (applicationSettings.persistenceOptions().getSaveLocation().isEmpty()) {
			retVal = getSanitizedPath();
		} else {
			retVal = applicationSettings.persistenceOptions().getSaveLocation();
		}
		
		return retVal;
	}
	
	private boolean hasFeature(int feature) {
        if ((applicationSettings.applicationFeatures() & feature) == feature) {
        	return true;
		}
        
        return false;
	}
	
	private String getSanitizedPath() {
		String retVal = System.getProperty("user.home");
		String appNamePart = applicationSettings.applicationName().replaceAll("[^a-zA-Z0-9.-]", "");
		return retVal + "/" + appNamePart + "/settings.xml";
	}
	
	private void setupExceptionHandler() {
		ErrorDialogProvider provider = null;
		
		if (applicationSettings.exceptionOptions().displayType() == ExceptionDisplay.Custom) {
			provider = applicationSettings.exceptionOptions().errorProvider();
		} else if (applicationSettings.exceptionOptions().displayType() == ExceptionDisplay.Default) {
			provider = new DefaultErrorDialogProvider();
		}
		
		IDialogService service = ServiceManager.getInstance().<IDialogService>get("IDialogService");
    	Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(service, provider, applicationSettings.exceptionOptions().logException()));
	}
	
	private void intializeComponents(LoadData data) {
		LogRunner.logger().log(Level.INFO, "Loading components");
		for (IComponent comp : applicationSettings.components()) {
			comp.load(data);
		}
	}
}
