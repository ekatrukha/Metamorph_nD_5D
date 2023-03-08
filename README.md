# Metamorph_nD_5D

[FIJI](https://fiji.sc/) plugin for fast virtual hyperstack loading of [Metamorph](https://www.moleculardevices.com/products/cellular-imaging-systems/acquisition-and-analysis-software/metamorph-microscopy) format "Multi-Dimensional acquisition" nd files (TIF series).  
Works with multi-wavelength/time/z-slices/positions 5D images.

To install, copy jar files from the latest [release](https://github.com/ekatrukha/Metamorph_nD_5D/releases) to your FIJI plugins folder.

At the current release the plugin assumes that all tiffs are 16-bit.

### Usage
At the initial dialog, select Metamorph's nd file.   
In a dialog choose a position to load.
**"Max cache/memory size"** parameter specifies, how much maximim memory (RAM) the virtual hyperstack will occupy. It is measured in the size of individual XY frames.    
In case of **"Adjust image dimenstions?"** hyperstack voxel size/time interval and units will be set with provided values.

----------

Developed in <a href='http://cellbiology.science.uu.nl/'>Cell Biology group</a> of Utrecht University.  
<a href="mailto:katpyxa@gmail.com">E-mail</a> me for any questions.

