package metamorphndreader;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import net.imglib2.cache.img.CellLoader;
import net.imglib2.cache.img.ReadOnlyCachedCellImgFactory;
import net.imglib2.cache.img.ReadOnlyCachedCellImgOptions;
import net.imglib2.cache.img.SingleCellArrayImg;
import net.imglib2.cache.img.optional.CacheOptions.CacheType;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

public class Metamorph_nD_Reader implements PlugIn {

	String sFileNameFull_;
	
	String sPath_;

	int nActivePosition=0;
	@Override
	public void run(String arg) {
		
		
		if(arg.equals(""))
		{
			
			OpenDialog openDial = new OpenDialog("Open Metamorph nd file...","", "*.nd");
			
	        sPath_ = openDial.getDirectory();
	        if (sPath_==null)
	        	return;
	        sFileNameFull_ = openDial.getFileName();			
		}
		VirtualMMReader vReader = new VirtualMMReader(sPath_, sFileNameFull_); 
		
		//error during initialization
		if(!vReader.bInit)
			return;
		
		DialogOptions dial = new DialogOptions();
		
		if(!dial.showDialog(vReader.nStagePosN))
			return;
		//just sho parsing results
		if(dial.bJustRestuls)
		{
			ResultsTable SummaryRT = new ResultsTable();
			SummaryRT.incrementCounter();
    		SummaryRT.addValue("Positions", vReader.nStagePosN);
    		SummaryRT.addValue("Wavelengths", vReader.nWaveN);
    		SummaryRT.addValue("TimePoints", vReader.nTimePointsN);
    		SummaryRT.addValue("Z-slices", vReader.nZStepsN);
    		SummaryRT.show("Results");
			return;
		}
		
		vReader.nSelectedPosition = dial.nSelectedPosition;
		IJ.log("Opened position #"+Integer.toString(vReader.nSelectedPosition));
		final long[] dimensions = vReader.getDimensions();

		// set up cell size such that one cell is one plane
		final int[] cellDimensions = new int[] {
				(int) dimensions[0],
				(int) dimensions[1],
				1
		};
		
		// make a CellLoader that copies one plane of data from the virtual stack
		final CellLoader< UnsignedShortType > loader = new CellLoader< UnsignedShortType >()
		{
			@Override
			public void load( final SingleCellArrayImg< UnsignedShortType, ? > cell ) throws Exception
			{
				final int z = ( int ) cell.min( 2 );
				final int t = ( int ) cell.min( 3 );
				final int c = ( int ) cell.min( 4 );
				final short[] impdata = ( short[] ) vReader.getOneProcessor(z, t, c).getPixels();
				final short[] celldata = ( short[] ) cell.getStorageArray();
				System.arraycopy( impdata, 0, celldata, 0, celldata.length );
			}
		};

		// create a CellImg with that CellLoader
		final Img< UnsignedShortType > img = new ReadOnlyCachedCellImgFactory().create(
				dimensions,
				new UnsignedShortType(),
				loader,
				ReadOnlyCachedCellImgOptions.options().cellDimensions( cellDimensions ).cacheType(CacheType.BOUNDED).maxCacheSize(dial.nCacheSize) );
	
		ImagePlus imgPlus = ImageJFunctions.show(Views.permute( Views.permute(img, 4, 2),3,4) , vReader.sFileNameShort+"_position_"+Integer.toString(vReader.nSelectedPosition));
		if (dial.bAdjustDim)
		{
			Calibration cal = imgPlus.getCalibration();//new Calibration();
			cal.pixelWidth = dial.dXYpixelSize;
			cal.pixelHeight = dial.dXYpixelSize;
			cal.pixelDepth = dial.dZpixelSize;
			cal.frameInterval = dial.dTSize;
			cal.setTimeUnit(dial.sTimeUnits);
			cal.setXUnit(dial.sPixelUnits);
			cal.setYUnit(dial.sPixelUnits);
			cal.setZUnit(dial.sPixelUnits);
			imgPlus.setCalibration(cal);
		}
		//ImageJFunctions.show( img );
		/**/
		
	}
	
	public static void main( String... args) throws Exception
	{
		
		new ImageJ();
		Metamorph_nD_Reader testI = new Metamorph_nD_Reader(); 
		
		//testI.run("/home/eugene/Desktop/BigTrace_data/ExM_MT_8bit.tif");
		testI.run("");
		
		
	}

}
