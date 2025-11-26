package org.example.retoconjuntojavafxhibernate.utils;

public interface SessionService<T> {
    void login(T u);
    boolean isLoggedIn();
    void logout();
    T getActive();

    void setObject( String key, Object o );
    Object getObject(String key);
}
