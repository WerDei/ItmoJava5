package net.werdei.talechars.server;
import net.werdei.talechars.server.collections.Character;

import java.sql.*;
public class DBManager {

        //Данные значения для моей локальной PostgreSQL БД, их надо заменить на закоменченные при заливе на helios.
        private String url = "jdbc:postgresql://localhost:5432/studs"; //"jdbc:postgresql://pg:5432/studs"
        private String login = "postgres";  // Helios login
        private String password = "postgres"; // Helios password

        // Вставляет персонажа в БД.
        public void insertCharacter(Character character) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection(url, login, password);
                try {
                    PreparedStatement stmt = con.prepareStatement("SELECT * FROM Lab7Characters WHERE name = ? AND description = ? AND power = ? AND location = ? AND owner = ?");
                    stmt.setString(1, character.getName());
                    stmt.setString(2, character.getDescription());
                    stmt.setInt(3, character.getPower());
                    stmt.setString(4, character.getLocation());
                    stmt.setString(5, character.getOwner());
                    ResultSet result = stmt.executeQuery();
                    if (result.next()) System.out.println("Объект уже существует");
                    else {
                        stmt = con.prepareStatement("INSERT INTO Lab7Characters (name, description, power, location, owner) VALUES (?, ?, ?, ?, ?)");
                        stmt.setString(1, character.getName());
                        stmt.setString(2, character.getDescription());
                        stmt.setInt(3, character.getPower());
                        stmt.setString(4, character.getLocation());
                        stmt.setString(5, character.getOwner());
                        stmt.executeUpdate();
                    }
                    stmt.close();
                } finally {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    // Удаляет персонажа из БД.
    public void deleteCharacter(Character character) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, login, password);
            try {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM Lab7Characters WHERE name = ? AND description = ? AND power = ? AND location = ? AND owner = ?");
                stmt.setString(1, character.getName());
                stmt.setString(2, character.getDescription());
                stmt.setInt(3, character.getPower());
                stmt.setString(4, character.getLocation());
                stmt.setString(5, character.getOwner());
                stmt.executeUpdate();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Вставляет пользователя с паролем в БД.
    public void insertUser(String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, login, this.password);
            try {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet result = stmt.executeQuery();
                if (result.next()) System.out.println("Пользователь уже существует");
                else {
                    stmt = con.prepareStatement("INSERT INTO Users (username, password) VALUES (?, ?)");
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.executeUpdate();
                    stmt.close();
                }
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Проверяет, есть ли в БД такие username и password.
    public boolean checkSignIn(String username, String password) {
            boolean check = false;
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, login, this.password);
            try {
                Statement stmt = con.createStatement();
                ResultSet result = stmt.executeQuery("SELECT * FROM Users WHERE username = '" + username + "' AND password = '" + password +"'");
                check = result.next();
                result.close();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    //Проверяет, есть ли в БД такой юзер.
    public boolean checkIfUserExists(String username) {
        boolean check = false;
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, login, password);
            try {
                Statement stmt = con.createStatement();
                ResultSet result = stmt.executeQuery("SELECT * FROM Users WHERE username = '" + username + "'");
                check = result.next();
                result.close();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }


}


