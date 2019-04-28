package projekt.model;
import java.util.ArrayList;
import java.util.HashMap;

/*
    A felhasználót reprezentáló osztály.
    A foglalt helyek listáját az el?adások nevei szerint tárolja.
*/

public class User {
    public final String name;
    private final String password;
    private HashMap<String, ArrayList<Seat>> booking;
    
    //visszaadja, hogy az adott felhasználónak van-e foglalása az adott helyre    
    public boolean hasSeat(Show show, Seat s){
        String sname = show.name; //az el?adások név szerinti tárolása miatt
        if (booking.containsKey(sname)){
            if (booking.get(sname).contains(s))
                return true;
            }
        return false;
    }
    
    //foglalás hozzáadása
    public void addBooking(Show show, Seat s){
        String sname = show.name; //az el?adások név szerinti tárolása miatt
        if (!booking.containsKey(sname))
            booking.put(sname, new ArrayList<Seat>()); //ha nincs még adott el?adás, új lista
        booking.get(sname).add(s); //adott hely hozzáadása a listához
    }
    
    //foglalás törlése
    public void deleteBooking(Show show, Seat s){
        String sname = show.name;
        booking.get(sname).remove(s); //hely törlése a listáról
        if (booking.get(sname).isEmpty())
            booking.remove(sname); //ha a lista üressé vált, a lista törlése
    }

    //a bejelentkezéskor beírt jelszó helyes-e
    public boolean isCorrectPassword(String other) {
        return password.equals(other);
    }
       
    //kiírja a felhasználó foglalásait
    public String printBooking() {
        StringBuilder sb = new StringBuilder();
        if (booking.isEmpty()) //ha nincs foglalás
            sb.append("There is no booking.");
        booking.entrySet().forEach((item) -> { //map entry-k bejárása
            sb.append(item.getKey()).append(": "); //el?adás neve
            for(Seat s : item.getValue()) //lista bejárása
                sb.append(s).append(" "); //hely száma
            sb.append("\n");
        });
        return sb.toString();
    }
    
    
    //konstruktor, tostring, getterek
    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.booking = new HashMap<>();
    }

    @Override
    public String toString() {        
        return "Username: " + name + ", password: " + password + "\n" + printBooking();
    }
    
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
    
    public HashMap<String, ArrayList<Seat>> getBooking() {
        return booking;
    }
}
