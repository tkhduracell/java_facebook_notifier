package facebooknotifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import java.sql.*;

/**
 * @author Filip
 */
public class FacebookChecker {

    private final ArrayList<FacebookAccount> accounts;
    private final ArrayList<Notifyer> notfiers;
    private static final int DELAY = 8000;
    private final Timer timer;

    public FacebookChecker(ArrayList<FacebookAccount> accounts, Notifyer notfier) {
        this(accounts);
        this.notfiers.add(notfier);
    }

    public FacebookChecker(ArrayList<FacebookAccount> accounts) {
        this.accounts = accounts;
        this.notfiers = new ArrayList<Notifyer>(1);

        this.timer = new Timer();
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (FacebookAccount a : getAccounts()) {
                    long bef = System.currentTimeMillis();
                    String response = FacebookChecker.checkAccount(a);
                    long after = System.currentTimeMillis();
                    String date = new Date().toString() + " it took " + (after - bef) + " ms";
                    Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, date);
                    for (Notifyer n : notfiers) {
                        if (!response.isEmpty()) {
                            n.onEvent(a.getTitle(), response);
                        }
                        n.onCheck(date);
                    }
                }
            }
        };
        this.timer.schedule(task, DELAY);
        for (Notifyer n : notfiers) {
            n.onStatusChange(StatusType.RUNNING);
        }
    }

    protected ArrayList<FacebookAccount> getAccounts() {
        return accounts;
    }

    public void addNotifyer(Notifyer notifier) {
        notfiers.add(notifier);
    }

    public void stop() {
        timer.cancel();
        for(Notifyer n : notfiers){
            n.onStatusChange(StatusType.HALTED);
        }
    }

    /**
     * Gets the latest notification from Facebook
     * @param fa A FacebookAccount with the id and key
     * @return Empty string if no new notificaton has arrived, else the notification text
     */
    private static String checkAccount(FacebookAccount fa) {
        String url =
                String.format("http://www.facebook.com/feeds/notifications.php?"
                + "id=%s"
                + "&"
                + "viewer=%s"
                + "&"
                + "key=%s"
                + "&"
                + "format=json",
                fa.getId(),
                fa.getId(),
                fa.getKey());
        try {
            Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Fetching JSON...");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    new URL(url).openStream()));

            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
            Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Fetching JSON 100%!");

            Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Serializing...");
            JSON jso = JSONSerializer.toJSON(sb.toString());
            JSONObject json = (JSONObject) jso;

            Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Serializing...");
            JSON jso = JSONSerializer.toJSON(sb.toString());
            JSONObject json = (JSONObject) jso;
            Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Serializing...100%!");

            if (fa.getTitle() == null || fa.getTitle().isEmpty()) {
                JSONObject title = json.getJSONObject("title");
                fa.setTitle(title.getString("__html"));
            }
            String update = json.getString("updated");
            if (update.equals(fa.getLastUpdated())) { //No update
                return "";
            } else {                                  //New update
                JSONArray entites = json.getJSONArray("entries");
                JSONObject last = (JSONObject) entites.get(0);
                fa.setLastUpdated(json.getString("updated"));
                String title = last.getString("title");
                return title;
            }
        } catch (MalformedURLException mex) {
            Logger.getLogger(FacebookChecker.class.getName()).log(Level.SEVERE, null, mex);
        } catch (IOException iex) {
            Logger.getLogger(FacebookChecker.class.getName()).log(Level.SEVERE, "Couldn't connect to server", iex);
        } catch (Exception ex) {
            Logger.getLogger(FacebookChecker.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        throw new RuntimeException();
    }
}