package zeiss.PALM;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class implements methods to read/write Zeiss PALM Element text files
 * @author Fabrice P. Cordelières, fabrice.cordelieres@gmail.com
 *
 */
public class Zeiss_PALM_Elements {
	/**Default software's name**/
	String SOFTWARE="PALMRobo Elements";
	
	/**Software name, as found within the file**/
	String softwareName;
	
	/**Software version, as found within the file**/
	String softwareVersion;
	
	/**Date**/
	String date;
	
	/**Time**/
	String time;
	
	/**Unit**/
	String unit;
	
	/** List of all the Zeiss_PALM_Roi **/
	public ArrayList<Zeiss_PALM_Roi> elements=new ArrayList<Zeiss_PALM_Roi>();
	
	
	/**
	 * Reads a Zeiss PALM Element file exported as a text file 
	 * @param filePath the path to the Zeiss PALM Element file exported as a text file
	 */
	public void readText(String filePath){
		try {
			BufferedReader br=new BufferedReader(new FileReader(filePath));
			
			String line=br.readLine();
			
			if(line.equals(SOFTWARE)){
				//Read header
				softwareName=line;
				softwareVersion=br.readLine().split("\t")[1];
				line=br.readLine();
				date=line.split("\t")[1];
				time=line.split("\t")[2];
				
				br.readLine(); //Return
				unit=br.readLine();
				br.readLine(); //"Elements:"
				br.readLine(); //Return
				br.readLine(); //Columns header
				br.readLine(); //Return
				
				line=br.readLine();
				while(line!=null){
					String roiLine=line;
					while(!line.equals("")){
						line=br.readLine();
						roiLine+="\n"+line;
					}
					if(!roiLine.equals("")) elements.add(new Zeiss_PALM_Roi(roiLine));
					line=br.readLine();
				}
				br.close();
			}else{
				br.close();
				throw new IllegalArgumentException("This is not a Zeiss PALM Element file exported as text");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes the Zeiss PALM Element file as a text file 
	 * @param filePath the path where the Zeiss PALM Element file should be saved as a text file
	 */
	public void exportElementAsText(String filePath){
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(filePath));
			bw.write(toString());
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts all the Zeiss_PALM_Roi contained in the Element file into an array of the ImageJ Rois
	 * @param infos images infos required to locate Roi
	 */
	public Roi[] getElementsAsImageJRoi(Zeiss_PALM_Image_Infos infos){
		Roi[] out= new Roi[elements.size()];
		for(int i=0; i<elements.size(); i++) out[i]=elements.get(i).getImageJRoi(infos);
		return out;
	}
	
	/**
	 * Sends all Zeiss_PALM_Roi to ImageJ's RoiManager
	 * @param infos images infos required to locate Roi
	 */
	public void sendToManager(Zeiss_PALM_Image_Infos infos){
		RoiManager rm=RoiManager.getInstance();
		if(rm==null) rm=new RoiManager();
		
		Roi[] rois=getElementsAsImageJRoi(infos);
		for(int i=0; i<rois.length; i++) rm.addRoi(rois[i]);
	}
	
	/**
	 * Converts the ImageJ Rois stored in the RoiManager into Zeiss PALM Rois, stored int othe current Element object
	 * @param infos images infos required to locate Roi
	 */
	public void getRoisFromManager(Zeiss_PALM_Image_Infos infos){
		RoiManager rm=RoiManager.getInstance();
		
		if(rm!=null){
			softwareName=SOFTWARE;
			softwareVersion="V 4.6.0.4";
			setDateTime();
			unit="MICROMETER";
			
			for(int i=0; i<rm.getCount(); i++) elements.add(new Zeiss_PALM_Roi(rm.getRoi(i), infos));
		}
	}
	
	/**
	 * Move all the Zeiss_PALM_Roi to a new location, based on a translation vector
	 * @param translation the translation to be applied
	 */
	public void move(Zeiss_PALM_Point translation){
		for(int i=0; i<elements.size(); i++) elements.get(i).move(translation);
	}
	
	/**
	 * Retrieves and formats current date and time
	 */
	void setDateTime(){
		SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy");
	    Date now=new Date();
	    date=sdf.format(now);
	    
	    sdf= new SimpleDateFormat("HH:mm:ss");
	    time=sdf.format(now);
	}
	
	@Override
	public String toString(){
		String out=softwareName+"\n"
				+"Version:\t"+softwareVersion+"\n"
				+"Date, Time :\t"+date+"\t"+time+"\n"
				+"\n"
				+unit+"\n"
				+"Elements :\n"
				+"\n"
				+"Type\tColor\tThickness\tNo\tCutShot\tArea\tComment\tCoordinates\t\n"
				+"\n\n\n";
		
		for(int i=0; i<elements.size(); i++) out+=elements.get(i).toString()+"\n\n";
		
		return out;
	}
}
