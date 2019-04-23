package projekt.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    public final String name;
    private final String password;
    private HashMap<String, ArrayList<Seat>> booking; //a foglalt helyeket t�rolja az el?ad�sok nevei szerint
    
    //k�l�n oszt�ly a helyeknek (szerintem k�nnyebben kezelhet?, mint map-in-mapezni)
    class Seat {
        int row;
        int column;

        public Seat(int row, int column) {
            this.row = row;
            this.column = column;
        }
        
        @Override
        public String toString() {
            return (row+1) + "-" + (column+1); //+1 a null�t�l val� indexel�s miatt
        }
    }
    
    //visszaadja, hogy az adott felhaszn�l�nak van-e foglal�sa az adott helyre    
    public boolean hasSeat(String show, int row, int column){
        if (booking.containsKey(show))
            for (Seat s : booking.get(show)) {
                if (s.row == row && s.column == column)
                    return true;  
            }
        return false;
    }
    
    //foglal�s hozz�ad�sa
    public void addBooking(String show, int row, int column){
        if (!booking.containsKey(show))
            booking.put(show, new ArrayList<Seat>());
        booking.get(show).add(new Seat(row, column));        
    }
    
    //foglal�s t�rl�se
    public void deleteBooking(String show, int row, int column){
        int i = 0;
        for (Seat s : booking.get(show)) {            
            if (s.row == row && s.column == column)
                break;
            i++;
        }
        booking.get(show).remove(i);
        if (booking.get(show).isEmpty())
            booking.remove(show);
    }

    //a bejelntkez�skor be�rt jelsz� helyes-e
    public boolean isCorrectPassword(String other) {
        if (password.equals(other))
            return true;
        return false;
    }
       
    //ki�rja a felhaszn�l� foglal�sait
    public String printBooking() {
        StringBuilder sb = new StringBuilder();
        if (booking.isEmpty())
            sb.append("There is no booking yet.");
        booking.entrySet().forEach((item) -> {
            sb.append(item.getKey()).append(": ");
            for(Seat s : item.getValue())
                sb.append(s).append(" ");
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
