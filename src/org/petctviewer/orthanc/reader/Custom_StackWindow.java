package org.petctviewer.orthanc.reader;

import java.awt.event.WindowEvent;

import ij.ImagePlus;
import ij.gui.StackWindow;

public class Custom_StackWindow extends StackWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Custom_StackWindow(ImagePlus imp) {
		super(imp);

	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		close();
		super.windowClosing(e);
		
	}
	

}
