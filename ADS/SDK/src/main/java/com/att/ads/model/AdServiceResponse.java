package com.att.ads.model;

/**
 * Class encapsulates all of the data from Ad service response.
 * 
 */
public class AdServiceResponse {

	private String clickUrl = null;
	private ImageUrl imageUrl= null;
	private String type = null;
	private String text = null;
	
	public class ImageUrl {
		private String image = null;
		
		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}		
	}
	public String getClickUrl() {
		return clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}
	public ImageUrl getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(ImageUrl imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
