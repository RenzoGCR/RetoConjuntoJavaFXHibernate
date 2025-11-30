package org.example.retoconjuntojavafxhibernate.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Proveedor de la SessionFactory de Hibernate para toda la aplicación.
 * Implementa el patrón Singleton para asegurar que solo exista una instancia
 * de SessionFactory, lo cual es una práctica recomendada por Hibernate.
 */
public class DataProvider {

    private static SessionFactory sessionFactory = null;

    /**
     * Constructor privado para prevenir la instanciación directa.
     */
    private DataProvider() {}

    /**
     * Obtiene la instancia única de la SessionFactory.
     * Si la SessionFactory no ha sido creada todavía, la inicializa.
     * <p>
     * La configuración se carga desde el archivo hibernate.cfg.xml por defecto.
     * Además, sobrescribe las propiedades de usuario y contraseña de la base de datos
     * con las variables de entorno {@code DB_USER} y {@code DB_PASSWORD}.
     *
     * @return La instancia única de {@link SessionFactory}.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Carga la configuración desde hibernate.cfg.xml
                var configuration = new Configuration().configure();

                // Sobrescribe las credenciales con variables de entorno para mayor seguridad
                configuration.setProperty("hibernate.connection.username", System.getenv("DB_USER"));
                configuration.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));

                // Construye la SessionFactory
                sessionFactory = configuration.buildSessionFactory();
            } catch (Throwable ex) {
                // Captura cualquier excepción durante la inicialización
                System.err.println("Error al inicializar la SessionFactory de Hibernate: " + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }
}
