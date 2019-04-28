package projekt.model;

import java.util.ArrayList;

public class Model {
    private ArrayList<User> users; //a regisztrált felhazsnálók tárolása
    private ArrayList<Show> shows; //az el?adások tárolása

    public Model() {
        this.users = new ArrayList<>();
        this.shows = new ArrayList<>();
    }

    public ArrayList getUsers() {
        return users;
    }

    public ArrayList getShows() {
        return shows;
    }
    
    //megkeres és visszaad egy adott el?adást a neve alapján
    public Show getShow(String name) {        
        for (Show sh : shows)
        {
            if (name.equals(sh.name))
                return sh;
        }
        return null;
    }
    
    //megkeres és visszaad egy adott felhasználót a neve alapján
    public User getUser(String name) {        
        for (User us : users)
        {
            if (name.equals(us.name))
                return us;
        }
        return null;
    }
    
}
