/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hstefan.distrib.t2;

import java.net.InetAddress;

/**
 *
 * @author hstefan
 */
class HostEntry {
    public InetAddress adress;
    public int port;
    public String reg_name;

    public HostEntry(InetAddress adress, int port, String reg_name) {
        this.adress = adress;
        this.port = port;
        this.reg_name = reg_name;
    }
}
