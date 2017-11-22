import zeiss.PALM.Zeiss_PALM_Elements;
import zeiss.PALM.Zeiss_PALM_Image_Infos;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;

/**
 * This plugin implements functionalities to import/export Zeiss PALM text files to/from the ImageJ RoiManager
 * @author Fabrice P. Cordelières, fabrice.cordelieres@gmail.com
 *
 */
public class Zeiss_PALM_Roi_IO implements PlugIn {
	/** Path to the Zeiss PALM Element file **/
	String path=null;
	
	/** Zeiss PALM Element filename **/
	String fileName=null;
	
	@Override
	public void run(String arg) {
		if(arg.equals("import")) importElements();
		if(arg.equals("export")) exportElements();
		if(arg.equals("pull")) pullElements();
	}
	
	/**
	 * Handles import of a Zeiss PALM Element text file to the ImageJ RoiManager
	 */
	public void importElements(){
		OpenDialog od = new OpenDialog("Open: Point to the Zeiss PALM Roi to open");
		path=od.getDirectory();
		fileName=od.getFileName();
		
		if(path!=null && fileName!=null){
			Zeiss_PALM_Image_Infos zpii=new Zeiss_PALM_Image_Infos();
			if(zpii.read()){
				Zeiss_PALM_Elements zpe=new Zeiss_PALM_Elements();
				zpe.readText(path+fileName);
				zpe.sendToManager(zpii);
			}
		}
	}
	
	/**
	 * Handles the export of the ImageJ RoiManager as a Zeiss PALM Element text file
	 */
	public void exportElements(){
		SaveDialog sd = new SaveDialog("Save: Where should the Zeiss Element file be saved ?", "element.txt", ".txt");
		path=sd.getDirectory();
		fileName=sd.getFileName();
		
		if(path!=null && fileName!=null){
			Zeiss_PALM_Image_Infos zpii=new Zeiss_PALM_Image_Infos();
			if(zpii.read()){
				Zeiss_PALM_Elements zpe=new Zeiss_PALM_Elements();
				zpe.getRoisFromManager(zpii);
				zpe.exportElementAsText(path+fileName);
			}
		}
	}
	
	/**
	 * Implements pulling together data from two Zeiss PALM Element text files
	 */
	public void pullElements(){
		OpenDialog od = new OpenDialog("First_file: Point to the first Zeiss PALM Roi to open");
		path=od.getDirectory();
		fileName=od.getFileName();
		
		if(path!=null && fileName!=null){
			Zeiss_PALM_Elements zpe1=new Zeiss_PALM_Elements();
			zpe1.readText(path+fileName);
			
			od = new OpenDialog("Second_file: Point to the second Zeiss PALM Roi to open");
			path=od.getDirectory();
			fileName=od.getFileName();
			
			if(path!=null && fileName!=null){
			
				Zeiss_PALM_Elements zpe2=new Zeiss_PALM_Elements();
				zpe2.readText(path+fileName);
				for(int i=0; i<zpe2.elements.size(); i++) zpe1.elements.add(zpe2.elements.get(i));
				
				SaveDialog sd = new SaveDialog("Save: Where should the Zeiss Element file be saved ?", "element.txt", ".txt");
				path=sd.getDirectory();
				fileName=sd.getFileName();
				
				if(path!=null && fileName!=null) zpe1.exportElementAsText(path+fileName);
			}
		}
	}
}
