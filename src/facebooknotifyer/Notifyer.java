/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facebooknotifyer;

/**
 *
 * @author Filip
 */
public interface Notifyer {
    public void onEvent(String title, String message);
    public void onCheck(String time);
    public void onStatusChange(StatusType status);
}
enum StatusType{
    RUNNING,STOPPED,HALTED;

    @Override
    public String toString() {
        switch(this){
            case RUNNING:
                return "Running";
            case STOPPED:
                return "Stopped";
            case HALTED:
                return "Halted";
            default:
                return "";
        }
    }
}