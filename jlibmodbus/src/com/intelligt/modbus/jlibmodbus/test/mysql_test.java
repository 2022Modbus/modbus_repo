package com.intelligt.modbus.jlibmodbus.test;

import java.sql.*;

public class mysql_test {

    public static void main(String[] args) {
        DBConnection dbConn = new DBConnection();
        dbConn.connect();
        }
    }

    class DBConnection {
        Connection connection = null;// 데이터 베이스와 연결을 위한 객체
        Statement stmt = null; // SQL 문을 데이터베이스에 보내기위한 객체
        ResultSet rs = null; // SQL 질의에 의해 생성된 테이블을 저장하는 객체


    public void connect() {
        String url = "jdbc:mysql://project-db-stu.ddns.net:3307/wish";
        String user = "wish";
        String password = "1234";
        String driverName = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driverName);

            // ② 연결
            connection = DriverManager.getConnection(url, user, password);

            stmt = connection.createStatement();

            String SQL = "SELECT * FROM temp_weather";

            // 4. SQL 문장을 실행하고 결과를 리턴
            // stmt.excuteQuery(SQL) : select
            // stmt.excuteUpdate(SQL) : insert, update, delete ..
            rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                Float humidity = rs.getFloat("humidity");
                System.out.println("humidity = " + humidity);
            }


        } catch (ClassNotFoundException e) {
            // `com.mysql.cj.jdbc.Driver` 라는 클래스가 라이브러리로 추가되지 않았다면 오류발생
            System.out.println("[로드 오류]\n" + e.getStackTrace());
        } catch (SQLException e) {
            // DB접속정보가 틀렸다면 오류발생
            System.out.println("[연결 오류]\n" + e.getStackTrace());
        }
    }



}
