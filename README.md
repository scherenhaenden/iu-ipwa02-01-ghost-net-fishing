# Ghost Net Fishing

Dieses Projekt ist eine Fallstudie für den Kurs IPWA02-01 – Programmierung von industriellen Informationssystemen mit Java EE an der IU. Es handelt sich um eine Anwendung zum Melden und Bergen von Geisternetzen, die als Beispiel für ein System im Bereich Umweltschutz und Fischerei dient.

## Verwendete Technologien
- Spring Boot
- Spring Data JPA
- Thymeleaf
- Maven
- H2/MySQL

## Projekt starten
1. Führen Sie `mvn clean install` aus, um das Projekt zu bauen.
2. Starten Sie die Anwendung mit `mvn spring-boot:run`.
3. Die Anwendung ist unter `http://localhost:8080` erreichbar.
4. Die H2-Datenbank-Konsole ist unter `http://localhost:8080/h2-console` verfügbar. Verwenden Sie die folgenden Anmeldeinformationen:
    - **JDBC URL**: `jdbc:h2:mem:ghostnetdb`
    - **User Name**: `sa`
    - **Password**: `password`

Dieses Projekt wurde speziell für die Fallstudie an der IU erstellt und ist nicht für den Produktionseinsatz vorgesehen.
