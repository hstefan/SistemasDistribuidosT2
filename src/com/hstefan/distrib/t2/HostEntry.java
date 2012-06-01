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
    public String reg_name;

    public HostEntry(InetAddress adress, String reg_name) {
        this.adress = adress;
        this.reg_name = reg_name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.adress != null ? this.adress.hashCode() : 0);
        hash = 31 * hash + (this.reg_name != null ? this.reg_name.hashCode() : 0);
        return hash;
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
        if ((this.reg_name == null) ? (other.reg_name != null) : !this.reg_name.equals(other.reg_name)) {
            return false;
        }
        return true;
    }
}
