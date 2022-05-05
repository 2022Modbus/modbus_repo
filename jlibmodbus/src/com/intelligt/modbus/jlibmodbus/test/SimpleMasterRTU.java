package com.intelligt.modbus.jlibmodbus.test;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.serial.*;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import jssc.SerialPortList;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/*
 * Copyright (C) 2016 "Invertor" Factory", JSC
 * All rights reserved
 *
 * This file is part of JLibModbus.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Authors: Vladislav Y. Kochedykov, software engineer.
 * email: vladislav.kochedykov@gmail.com
 */
public class SimpleMasterRTU {

    static public void main(String[] arg) {
        SerialParameters sp = new SerialParameters();
        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);

        try {
            // you can use just string to get connection with remote slave,
            // but you can also get a list of all serial ports available at your system.
            String[] dev_list = SerialPortList.getPortNames();
            // if there is at least one serial port at your system
            if (dev_list.length > 0) {
                // you can choose the one of those you need
                sp.setDevice(dev_list[1]);
                //System.out.println("dev_list[1] = " + dev_list[1]);
                System.out.println("Connected dev_list[2] = " + dev_list[2]);
                // these parameters are set by default
                sp.setBaudRate(SerialPort.BaudRate.BAUD_RATE_9600);
                sp.setDataBits(8);
                sp.setParity(SerialPort.Parity.NONE);
                sp.setStopBits(1);
                //you can choose the library to use.
                //the library uses jssc by default.
                //
                //first, you should set the factory that will be used by library to create an instance of SerialPort.
                SerialUtils.setSerialPortFactory(new SerialPortFactoryRXTX());
                //  JSSC is Java Simple Serial Connector
                SerialUtils.setSerialPortFactory(new SerialPortFactoryJSSC());
                //  PJC is PureJavaComm.
                SerialUtils.setSerialPortFactory(new SerialPortFactoryPJC());
                //  JavaComm is the Java Communications API (also known as javax.comm)
                //SerialUtils.setSerialPortFactory(new SerialPortFactoryJavaComm());
                //in case of using serial-to-wifi adapter
                //String ip = "59.0.236.34";//for instance
                //int port  = 8090;
                //SerialUtils.setSerialPortFactory(new SerialPortFactoryTcp(new TcpParameters(InetAddress.getByName(ip), port, true)));
                // you should use another method:
                //next you just create your master and use it.


                //===================================================================================================
                //===================================================================================================
                while(true) {
                    //DB 메서드 호출 객체 생성
                    mysql_test_h data = new mysql_test_h();

                    //시간구하기
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    //시간형식 맞출 객체 생성
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                    ModbusMaster m = ModbusMasterFactory.createModbusMasterRTU(sp);
                    m.connect();

                    int slaveId = 1;
                    int offset = 0;
                    int quantity = 32;
                    //you can invoke #connect method manually, otherwise it'll be invoked automatically
                    try {
                        // at next string we receive ten registers from a slave with id of 1 at offset of 0.

                        int[] registerValues = m.readHoldingRegisters(slaveId, offset, quantity);
                        // print values
                        //System.out.println(registerValues.);

                        //10초후 출력
                        Thread.sleep(10000);

                        //현재 시간 변수
                        String current_time = sdf.format(timestamp);

                        //발전량 변수
                        int aod_value = registerValues[0];

                        //현재 시간 출력
                        System.out.println("현재 시간 = " + current_time);

                        //현재시간과 동일한 DB에 data_time컬럼 호출 메서드
                        data.findValues(aod_value);
                        //현재시간과 발전량 출력
                        System.out.println("발전량: " + aod_value);

                        //for (int value : registerValues) {
                        //    System.out.println("Address: " + offset++ + ", Value: " + value);
                        //}


                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            m.disconnect();
                        } catch (ModbusIOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }//end
                //===================================================================================================
                //===================================================================================================
                //===================================================================================================
                //===================================================================================================

            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
