package com.intelligt.modbus.jlibmodbus.test;

import java.sql.*;
import java.text.SimpleDateFormat;

public class mysql_test_h {

//    public static void main(String[] args) {
//        DBConnection dbConn = new DBConnection();
//        dbConn.connect();
//        dbConn.insertRealData(80.0, 60.0, 8.0, 51.0, 20.0, 50.0, 6.0, 80.0, 80);
//        }
//    }
//
//    class DBConnection {
        Connection connection = null;// 데이터 베이스와 연결을 위한 객체
        PreparedStatement psmt = null; // SQL 문을 데이터베이스에 보내기위한 객체
        ResultSet rs = null; // SQL 질의에 의해 생성된 테이블을 저장하는 객체

        //시간구하기
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //시간형식 맞출 객체 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public void connect() {
        String url = "jdbc:mysql://project-db-stu.ddns.net:3307/wish";
        String user = "wish";
        String password = "1234";
        String driverName = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driverName);

            // ② 연결
            connection = DriverManager.getConnection(url, user, password);


        } catch (ClassNotFoundException e) {
            // `com.mysql.cj.jdbc.Driver` 라는 클래스가 라이브러리로 추가되지 않았다면 오류발생
            System.out.println("[로드 오류]\n" + e.getStackTrace());
        } catch (SQLException e) {
            // DB접속정보가 틀렸다면 오류발생
            System.out.println("[연결 오류]\n" + e.getStackTrace());
        }
    }

    //DB에서 현재시간에 따른 값 불러오기
    public void findValues(int aod){

        //현재 시간 변수
        String cur_time = sdf.format(timestamp);

        //sql문에 들어갈 현재시간 변수에 '%' 합치기
        String substring_date = cur_time.substring(0,18)+"%";

        try {
            connect();
            //String sql = "select * from temp_weather Limit 3";
            String sql = "SELECT * FROM temp_weather WHERE date_time LIKE '" + substring_date + "'";

            psmt = connection.prepareStatement(sql);
            // psmt.setString(1,substring_date);

            // 4. SQL 문장을 실행하고 결과를 리턴
            // psmt.excuteQuery(SQL) : select
            // psmt.excuteUpdate(SQL) : insert, update, delete ..

            rs = psmt.executeQuery(sql);

            if (rs.next()) {
                String date_time = rs.getString("date_time");
                Double temperature = rs.getDouble("temperature");
                Double precipitation = rs.getDouble("precipitation");
                Double wind_speed = rs.getDouble("wind_speed");
                Double humidity = rs.getDouble("humidity");
                Double radiation = rs.getDouble("radiation");
                Double total_cloudiness = rs.getDouble("total_cloudiness");
                Double visibility = rs.getDouble("visibility");
                Double ground_temp = rs.getDouble("ground_temp");

                insertRealData(temperature,precipitation,wind_speed,humidity,radiation,total_cloudiness,visibility,ground_temp, aod);



                System.out.println("DB date_time 출력: " + date_time);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //발전량과 날씨에 따른 값 insert
    public void insertRealData(Double r_temperature,Double r_precipitation,Double r_wind,Double r_humidity,Double r_radiation,Double r_total_cloudiness,Double r_visibility, Double r_ground_temp, int r_aod){
        try {
            connect();
            String sql = "insert into s_real_data values(null,now(),?,?,?,?,?,?,?,?,?,'wish')";

            psmt = connection.prepareStatement(sql);
            psmt.setDouble(1, r_temperature);
            psmt.setDouble(2,r_precipitation);
            psmt.setDouble(3,r_wind);
            psmt.setDouble(4,r_humidity);
            psmt.setDouble(5,r_radiation);
            psmt.setDouble(6,r_total_cloudiness);
            psmt.setDouble(7,r_visibility);
            psmt.setDouble(8,r_ground_temp);
            psmt.setInt(9,r_aod);



            int result = psmt.executeUpdate();

            if(result >= 1){
                System.out.println("DB INSERT SUCCESS!!");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
