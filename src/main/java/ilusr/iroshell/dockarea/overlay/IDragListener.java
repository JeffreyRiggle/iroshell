package ilusr.iroshell.dockarea.overlay;

//TODO: Document.
public interface IDragListener extends IDropListener{
	void dragEntered(DockPosition pos);
	void dragExited(DockPosition pos);
}
