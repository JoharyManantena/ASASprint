//
package prom16.fonction;

public class VERBmethod {
    private String url;
    private String method;

    public VERBmethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean matches(String requestUrl, String requestMethod) {
        return this.url.equals(requestUrl) && this.method.equals(requestMethod);
    }
}
