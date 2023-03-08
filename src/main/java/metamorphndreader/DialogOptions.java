package metamorphndreader;

import ij.Prefs;
import ij.gui.GenericDialog;

public class DialogOptions {

	public int nCacheSize = 100;
	public int nSelectedPosition;
	public double dXYpixelSize;
	public double dZpixelSize;
	public double dTSize;
	public boolean bAdjustDim = false;
	public String sPixelUnits = "";
	public String sTimeUnits = "";
	
	public boolean showDialog(final int nPosN)
	{
		GenericDialog gDial = new GenericDialog("Loading options");
		String [] saPositions = new String [nPosN];
		
		for(int i=0;i<nPosN;i++)
		{
			saPositions[i]= "Position "+Integer.toString(i+1);
		}
		gDial.addChoice("Load: ",saPositions, saPositions[0]);
		//gDial.addMessage("~~~~~~~~");
		gDial.addNumericField("Max cache/memory size:", Prefs.get("MMVirtualReader.nCacheSize", 10), 0, 5,"frames");
		//gDial.addMessage("~~~~~~~~");
		gDial.addCheckbox("Adjust image dimenstions?", Prefs.get("MMVirtualReader.bAdjustDim", false));
		gDial.addNumericField("XY pixel size: ", Prefs.get("MMVirtualReader.dXYpixelSize", 1.0), 2, 6," ");
		gDial.addNumericField("Z-step size: ", Prefs.get("MMVirtualReader.dZpixelSize", 1.0), 2, 6," ");
		gDial.addStringField("Voxel units:",Prefs.get("MMVirtualReader.sPixelUnits", "pixels"));
		gDial.addNumericField("Time step: ", Prefs.get("MMVirtualReader.dTSize", 1.0), 2, 6," ");
		gDial.addStringField("Time units:",Prefs.get("MMVirtualReader.sTimeUnits", "frames"));
		gDial.setResizable(false);
		gDial.showDialog();
		if (gDial.wasCanceled())
            return false;
		
		nSelectedPosition = gDial.getNextChoiceIndex()+1;
		
		nCacheSize =  (int) gDial.getNextNumber();
		Prefs.set("MMVirtualReader.nCacheSize", nCacheSize);
		
		bAdjustDim = gDial.getNextBoolean();
		Prefs.set("MMVirtualReader.bAdjustDim", bAdjustDim);
		if(bAdjustDim)
		{
			dXYpixelSize =  gDial.getNextNumber();
			Prefs.set("MMVirtualReader.dXYpixelSize", dXYpixelSize);
			dZpixelSize =  gDial.getNextNumber();
			Prefs.set("MMVirtualReader.dZpixelSize", dZpixelSize);
			sPixelUnits = gDial.getNextString();
			Prefs.set("MMVirtualReader.sPixelUnits", sPixelUnits);
			dTSize =  gDial.getNextNumber();
			Prefs.set("MMVirtualReader.dTSize", dTSize);
			sTimeUnits = gDial.getNextString();
			Prefs.set("MMVirtualReader.sTimeUnits", sTimeUnits);
		}
		return true;
		
	}
	
}
