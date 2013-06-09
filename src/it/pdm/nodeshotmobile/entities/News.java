package it.pdm.nodeshotmobile.entities;

import java.util.ArrayList;

public class News extends POI {
	
	private String title;
    private String content;
    private String link;
    private String category;
    private String date;
    
	static public ArrayList<News> news; //lista cdelle news prese tramite Jsonparser
	
    public News(){
    	super();
    	title="";
    	content="";
    	link="";
    	category="";
    	date="";     	
	}
    
    public Integer getId() {
        return super.getId();
	}

    public void setId(Integer id) {
    	super.setId(id);
	}
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        super.setName(title);
	}
    
    public String getContent() {
            return content;
    }

    public void setContent(String content) {
            this.content = content;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
            return date;
    }

    public void setDate(String date) {
            this.date = date;
    }
    
    static public void setListNews(ArrayList<News> list) {
        news = (ArrayList<News>)list.clone();
    }

    @Override
    public String toString() {
    	//getCategory() + " - "+    
    	return getContent() + "\n";
    }
}
