package zeiss.PALM;

/**
 * This class is designed to handle single points coordinates from Zeiss PALM Element text files
 * @author Fabrice P. Cordelières, fabrice.cordelieres@gmail.com
 *
 */
public class Zeiss_PALM_Point {
	/**X coordinate**/
	double x;
	
	/**Y coordinate**/
	double y;
	
	/**
	 * Creates a new Zeiss_PALM_Point by parsing a single String where coordinates are expected to be stored as X,Y
	 * both coordinates being of double type
	 * @param infos the String where coordinates are expected to be stored as X,Y
	 */
	public Zeiss_PALM_Point(String infos){
		decode(infos);
	}
	
	/**
	 * Creates a new Zeiss_PALM_Point based on a set of coordinates
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Zeiss_PALM_Point(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	/**
	 * Parses a single String where coordinates are expected to be stored as X,Y into two doubles
	 * @param infos the String where coordinates are expected to be stored as X,Y
	 */
	public void decode(String infos){
		String[] coord=infos.split(",");
		x=Double.parseDouble(coord[0]);
		y=Double.parseDouble(coord[1]);
	}
	
	/**
	 * Converts a Zeiss_PALM_Point as a local point which coordinates are relative to the image
	 * @param infos images infos required to locate Roi
	 * @return the converted Zeiss_PALM_Point
	 */
	public Zeiss_PALM_Point convertFromZeissToImage(Zeiss_PALM_Image_Infos infos){
		return new Zeiss_PALM_Point(infos.imageDimensions.x/2-(x-infos.position.x)/infos.calibration.x+infos.zeroPosition.x, infos.imageDimensions.y/2-(y-infos.position.y)/infos.calibration.y+infos.zeroPosition.y);
	}
	
	/**
	 * Converts a local point which coordinates are relative to the image to a Zeiss_PALM_Point
	 * @param infos images infos required to locate Roi
	 * @return the converted Zeiss_PALM_Point
	 */
	public Zeiss_PALM_Point convertFromImageToZeiss(Zeiss_PALM_Image_Infos infos){
		return new Zeiss_PALM_Point(infos.position.x-(x-infos.imageDimensions.x/2-infos.zeroPosition.x)*infos.calibration.x, infos.position.y-(y-infos.imageDimensions.y/2-infos.zeroPosition.y)*infos.calibration.y);
	}
	
	/**
	 * Move a point to a new location, based on a translation vector
	 * @param translation the translation to be applied
	 */
	public void move(Zeiss_PALM_Point translation){
		x+=translation.x;
		y+=translation.y;
	}
	
	/**
	 * Checks if both coordinates are NaN
	 * @return true if both coordinates are NaN
	 */
	public boolean isNaN(){
		return Double.isNaN(x) && Double.isNaN(y);
	}
	
	/**
	 * Rounds the input value to a single decimal
	 * @param val input value
	 * @return a rounded value to a single decimal
	 */
	private String round(double val){
		return ""+(Math.floor(val*10)/10);
	}
	
	@Override
	public String toString(){
		return round(x)+","+round(y);
	}
}
