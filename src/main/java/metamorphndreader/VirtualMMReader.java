package metamorphndreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.TiffDecoder;
import ij.process.ImageProcessor;

public class VirtualMMReader {
	String sFileNameFull_nD;
	public String sFileNameShort;
	public String sExtension;
	String sPath;
	public boolean bInit;
	public int nWidth = 0;
	public int nHeight = 0;
	public int nStagePosN = 0;
	public int nWaveN = 1;
	public int nZStepsN = 1;
	public int nTimePointsN = 0;
	ArrayList <String> sWaveName;
	//FileInfo [] fi_in = null;
	ArrayList<FileInfo []> tifFileFIwL = null;
	public int nSelectedPosition = 0;
	
	public VirtualMMReader(String sPath_, String sFullFilename)
	{
		sWaveName = new ArrayList<String>();
		sPath = new String(sPath_);
		sFileNameFull_nD = new String(sFullFilename);
		sFileNameShort = sFullFilename.substring(0,sFullFilename.length()-3);
		bInit = analyzeNDFile();
	}
	
	public boolean analyzeNDFile()
	{
		
		
		//analyze nd file
		try {
			BufferedReader br = new BufferedReader(new FileReader(sPath+sFileNameFull_nD));
			IJ.log("Analyzing nd file: " + sFileNameShort);
			String[] line_array;
			String line = "";
			while (line != null) 
			{
				  line = br.readLine();
				  if(line == null)
				  {
					  break;
				  }
				   // process the line.
				  line_array = line.split(",");
				  switch (line_array[0]){
				  case "\"NWavelengths\"":
					  nWaveN = Integer.parseInt(line_array[1].trim());
					  IJ.log("# of wavelengths: "+ Integer.toString(nWaveN));
					  String sDisplayWNames = "";
					  //read wavelength suffixes
					  for(int nWave = 0; nWave<nWaveN; nWave++)
					  {
						  line = br.readLine();
						  line_array = line.split(",");
						  String sWaveSuffix = line_array[1].trim();
						  //remove quotes
						  sWaveSuffix  = sWaveSuffix.substring(1,sWaveSuffix.length()-1);
						  sWaveSuffix = sWaveSuffix.replace("_", "-");
						  sWaveName.add(sWaveSuffix);
						  sDisplayWNames  = sDisplayWNames +sWaveSuffix + " ";
						  line = br.readLine();
						  
					  }
					  IJ.log(sDisplayWNames);
					  break;
				  case "\"NStagePositions\"":
					  nStagePosN = Integer.parseInt(line_array[1].trim());
					  IJ.log("Stage positions: "+ Integer.toString(nStagePosN));
					  break;
				  case "\"NTimePoints\"":
					  nTimePointsN = Integer.parseInt(line_array[1].trim());
					  IJ.log("Time points: "+ Integer.toString(nTimePointsN));
					  break;
				  case "\"NZSteps\"":
					  nZStepsN = Integer.parseInt(line_array[1].trim());
					  IJ.log("Z-slices: "+ Integer.toString(nZStepsN));
					  break;
				  }
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			IJ.log(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			IJ.log(e.getMessage());
		}

		return true;
	}
	public boolean initReader(final int nPosOpen)
	{
		FileInfo[] fi_out = null;
		tifFileFIwL = new ArrayList<FileInfo []>();
		for(int i=0;i<sWaveName.size();i++)
		{
			fi_out = initWavelength(nPosOpen,i);
			if(fi_out==null)
			{
				return false;
			}
			else
			{
				tifFileFIwL.add(fi_out);
			}
			
		}
		return true;
	}
	
	FileInfo [] initWavelength(final int nPosOpen, final int nWavelength)
	//public boolean initReader(final int nPosOpen)
	{
		
		FileInfo[] fi_out = null;
		// analyze first file
		
		IJ.log("Analyzing TIF files...");
		//let's get extension
		
		String sBeginning = sFileNameShort+"_w"+Integer.toString(nWavelength+1)+sWaveName.get(nWavelength)+"_s"+Integer.toString(nPosOpen)+"_t1.";
		File dir = new File(sPath);
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(sBeginning);
		    }
		});
		
		if (files.length!=1)
		{
			IJ.log("Error reading TIF files, cannot find "+sBeginning );
			return null;
		}
		else
		{
			String fullFirstFilename = files[0].getPath();
			//IJ.log(fullFirstFilename);
			sExtension = fullFirstFilename.substring(fullFirstFilename.length()-3,fullFirstFilename.length());
			IJ.log("TIF extension is "+sExtension);
		}
	
		
		String oneFile =sFileNameShort+"_w"+Integer.toString(nWavelength+1)+sWaveName.get(nWavelength)+"_s"+Integer.toString(nPosOpen)+"_t1."+sExtension;
		
		FileInfo[] info;
		TiffDecoder td = new TiffDecoder(sPath, oneFile);
		try {
			info = td.getTiffInfo();
		} catch (IOException e) {
			String msg = e.getMessage();
			if (msg==null||msg.equals("")) msg = ""+e;
			IJ.error("TiffDecoder", msg);
			return null;
		}
		FileInfo fi = info[0];
		int n = fi.nImages;
		if (info.length==1 && n>1) {
			fi_out = new FileInfo[n];
			long size = fi.width*fi.height*fi.getBytesPerPixel();
			for (int i=0; i<n; i++) {
				fi_out[i] = (FileInfo)fi.clone();
				fi_out[i].nImages = 1;
				fi_out[i].longOffset = fi.getOffset() + i*(size + fi.gapBetweenImages);
			}
		}
		else
		{
			fi_out = info;
		}
		nWidth = fi_out[0].width;
		nHeight = fi_out[0].height;
		nSelectedPosition = nPosOpen;
		IJ.log("...done");
		return fi_out;
	}
	public ImageProcessor getOneProcessor(int nZslice, int nTimeP, int nWave)
	{
		return getOneProcessor(nZslice, nTimeP, nWave, nSelectedPosition);
	}
	
	public ImageProcessor getOneProcessor(int nZslice, int nTimeP, int nWave, int nPos)
	{
		String filename = sFileNameShort+"_w"+Integer.toString(nWave+1)+sWaveName.get(nWave)+"_s"+Integer.toString(nPos)+"_t"+Integer.toString(nTimeP+1)+"."+sExtension;
		FileInfo fi = (FileInfo)tifFileFIwL.get(nWave)[nZslice].clone();
		
		fi.fileName =filename;
		//long size = fi.width*fi.height*fi.getBytesPerPixel();
		//fi.longOffset = fi.getOffset() + nZslice*(size+fi.gapBetweenImages);
		//fi.offset = 0;
		fi.nImages = 1;
		FileOpener fo = new FileOpener(fi);
		//ImagePlus ip = fo.open(false);
		//ip.show();
		ImagePlus imp = null;
		imp = fo.openImage();
		if (imp!=null)
			return imp.getProcessor();
		else
			return null;
		//return fo.open(false).getProcessor(); 
	}
	public long [] getDimensions()
	{
		return new long[] {
				nWidth,
				nHeight,
				nZStepsN,
				nTimePointsN,
				nWaveN
		};
	}
}
