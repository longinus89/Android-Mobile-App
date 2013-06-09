package com.msi.androidrss;

public class RSSItem 
{
	private String _title = null;
	private String _description = null;
	private String _link = null;
	private String _category = null;
	private String _pubdate = null;

	
	RSSItem(){
	}
	void setTitle(String title){
		_title = title;
	}
	void setDescription(String description){
		_description = description;
	}
	void setLink(String link){
		_link = link;
	}
	void setCategory(String category){
		_category = category;
	}
	void setPubDate(String pubdate){
		_pubdate = pubdate;
	}
	public String getTitle(){
		return _title;
	}
	public String getDescription(){
		return _description;
	}
	public String getLink(){
		return _link;
	}
	public String getCategory(){
		return _category;
	}
	public String getPubDate(){
		return _pubdate;
	}
	public String toString(){
		// limit how much text we display
		if (_title.length() > 42)	{
			return _title.substring(0, 42) + "...";
		}
		return _title;
	}
}
