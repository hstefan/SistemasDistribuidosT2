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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HostEntry other = (HostEntry) obj;
        if (this.adress != other.adress && (this.adress == null || !this.adress.equals(other.adress))) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        if ((this.reg_name == null) ? (other.reg_name != null) : !this.reg_name.equals(other.reg_name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.adress != null ? this.adress.hashCode() : 0);
        hash = 67 * hash + this.port;
        hash = 67 * hash + (this.reg_name != null ? this.reg_name.hashCode() : 0);
        return hash;
    }
}
