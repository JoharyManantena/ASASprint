package prom16.fonction;

import jakarta.servlet.http.HttpSession;

public class CustomSession {
    private HttpSession session;
    
    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public void add(String key, Object value) {
        this.getSession().setAttribute(key, value);
    }

    public void remove(String key) {
        this.getSession().removeAttribute(key);
    }

    public void update(String key, Object value) {
        this.getSession().setAttribute(key, value);
    }

    public void destroy() {
        this.getSession().invalidate();
        this.setSession(null);
    }
}
