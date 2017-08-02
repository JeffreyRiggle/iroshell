# iroshell
This project is a universally usable JavaFX based application shell that has no business specific logic in it. This application shell provides the following features.

* **MDI (Multiple Document Interface)** - Provides a MDI interface that allows for the creation of tabs. These tabs will be hosted in a dock area and can be dragged and dropped to provide more complex placement of views.
* **SDI (Single Document Interface)** - Provides a SDI interface that allows for a single selector and a document pane that changes based off of selector implementation.
* **Error handling** - This application shell can handle unhandled errors and display an exception dialog to the user. There are also feature toggles that allow you to create your own special error handling logic.
* **Splash Screens** - This application has a built in splash screen that can be turned on or off. This splash screen can also be completely replaced with a any custom JavaFx implementation of a splash screen.
* **Pre-Application Screens** - This shell can have any number of pre-configured application screens that show and evaluate before the application is run. A common use case for this might be a patching or login view. These screens can cancel the application from loading.
* **Persistence** - This shell allows for the persistence of MDI layouts.
* **Menu Bars** - This shell provides a basic Menu Bar and allows for the insertion of any menu/menu item into the menu bar.
* **Status Bar** - This shell provides a basic Status Bar area and allows for the insertion of any JavaFx node into the Status Bar.
* **Tool Bar** - This shell provides an interface to create any number of toolbars. These tool bars can be draggable and can be persisted.
* **Dynamic Styling** - This shell provides an interface to allow for any style to be applied to any existing visual element.
* **Notifications** - This shell provides an interface that allows for notifications to be used. Allowing things like toasts to be displayed.
* **Dialog Services** - This shell provides an interface that allows for dialogs to be shown with the main application shell as the parent. This allows for modal, modeless or embedded dialogs to be shown.

## Examples
Examples of this shell in use can be found at https://github.com/JeffreyRiggle/iroshell-examples

## Getting Started
### Prerequisites
Java 8 SDK should be installed on your local machine.
Maven should be installed on your local machine.

## Building
In order to build this simply run `mvn build` on the root folder.

## Testing
In order to test this simply run `mvn test` on the root folder.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details.
