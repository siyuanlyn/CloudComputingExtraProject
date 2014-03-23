package databeans;

import org.genericdao.PrimaryKey;

@PrimaryKey("placeId")
public class PlaceBean {
	private String placeId;
	private String coordinates;
	private String boundingType;
	private String country;
	private String countryCode;
	private String fullName;
	private String name;
	private String placeType;
	private String url;
	
	public String getPlaceId()	{ return placeId; }
	public String getCoordinates() { return coordinates; }
	public String getBoundingType() { return boundingType; }
	public String getCountry() {return country; }
	public String getCountryCode() { return countryCode; }
	public String getFullName() { return fullName; }
	public String getName() { return name; }
	public String getPlaceType() { return placeType; }
	public String getUrl() { return url; }
	
	public void setPlaceId(String placeId) { this.placeId = placeId; }
	public void setCoordinates(String coordinates) { this.coordinates = coordinates; }
	public void setBoundingType(String boundingType) { this.boundingType = boundingType; }
	public void setCountry(String country) { this.country = country; }
	public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
	public void setFullName(String fullName) { this.fullName = fullName; }
	public void setName(String name) { this.name = name; }
	public void setPlaceType(String placeType) { this.placeType = placeType; }
	public void setUrl(String url) {this.url = url; }
}
