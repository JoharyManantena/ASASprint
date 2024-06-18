package prom16.fonction;

import java.util.HashMap;

public class ModelView {
    private String url;
    private HashMap<String , Object> data = new HashMap<String , Object>();

    public ModelView(String url) {
        this.setUrl(url);
    }

    public ModelView() {}

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addObject(String lien,Object obj){
        this.getData().put(lien, obj);
    }
}
