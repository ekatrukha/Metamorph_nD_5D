# Metamorph_nD_5D

[FIJI](https://fiji.sc/) plugin for fast virtual hyperstack loading of [Metamorph](https://www.moleculardevices.com/products/cellular-imaging-systems/acquisition-and-analysis-software/metamorph-microscopy) format "Multi-Dimensional acquisition" nd files (TIF series).  
Works with multi-wavelength/time/z-slices/positions 5D images.

To install, copy *Metamorph_nD_5D-X.X.X.jar* file from the latest [release](https://github.com/ekatrukha/Metamorph_nD_5D/releases) to your FIJI *plugins* folder.

**NB:** Currently the plugin assumes that all tiffs are 16-bit.

### Usage
At the initial file chooser dialog, select Metamorph's file ending with **nd**.  
The following dialog should appear:  

![Loading options](https://katpyxa.info/software/MMReader/MMReader_v.0.0.2.png)
  
- First option is the position to be loaded.  
- **"Max cache/memory size"** parameter specifies, how much maximim memory (RAM) the virtual hyperstack will occupy. It is measured in the size of individual XY frames. So this number of "last visited" frames will be kept in memory, the rest will be readed "on-the-fly" from the disk.    
- In case of **"Adjust image dimenstions?"** hyperstack voxel size/time interval and units will be set with provided values.
- **"Do not open image, just show info in Results"** will do just that, show Results Table with dataset parameters.  

The plugin is scriptable using ImageJ macro (use Recorder to see parameters), for example:  
*run("Metamorph 5D reader X.X.X", "open=/home/eugene/testing.nd load=[Position 1] max=10");*

### Motivation 

The reason for this plugin is that a "standard" [BioFormats](https://imagej.net/formats/bio-formats) reader, before loading files to Virtual stack, checks all underlying tiff files for consistensy. This process can take a while in case of large datasets containing multi-position/channel/z-slices.   This plugins assumes all tiff's configurations are the same. It reads the first TIFF and loads the rest of them with its settings.
You can read a bit more about it [here](https://forum.image.sc/t/speeding-up-metamorph-file-reading-with-bioformats/42532/4).  

The plugin is based on examples shown at these [one](https://forum.image.sc/t/open-part-of-tif-stack/3166), [two](https://forum.image.sc/t/wrap-imageplus-virtualstack-into-imglib2/4154) discussion topics at image.sc forum.

### Updates history
#### 0.0.4
Fixed error for TIF/STK shift for different wavelengths. It caused horizontal image shift in some wavelength/z-stacks.
#### 0.0.3
Fixed opening error if files are stored as .STK
#### 0.0.2
Added an option to show info only in Results, without loading images.
#### 0.0.1
First release

----------

Developed in <a href='http://cellbiology.science.uu.nl/'>Cell Biology group</a> of Utrecht University.  
<a href="mailto:katpyxa@gmail.com">E-mail</a> me for any questions.

