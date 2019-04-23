package projekt.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import projekt.model.Model;
import projekt.model.Show;
import projekt.model.User;

public class FXMLProjektController implements Initializable {
    
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    TextField row;
    @FXML
    TextField column;
    @FXML
    TextField nrow;
    @FXML
    TextField ncolumn;
    @FXML
    TextField prow;
    @FXML
    TextField pcolumn;
    @FXML
    TextArea txta;
            
    
    Model model;
    User actual_user = null; 
    Show actual_show = null;
    

    public void setModel(Model model) {
        this.model = model;
        //csak a példa kedvéért 3 random film, majd a DB-s résznél erre úgyis kitalálunk valami jobbat (gondolom)
        model.getShows().add(new Show("Endgame", 15, 10));
        model.getShows().add(new Show("Captain Marvel", 10, 10));
        model.getShows().add(new Show("Pet Sematary", 10, 8));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
    }    
    
    //regisztráció, bejeltnkezés, kijelentkezés    
    
    @FXML
    public void Registration()
    {
        if (model.getUser(username.getText()) != null) {
            System.out.println("Username is already exist.");
            return;
        }      
        
        User tmp = new User(username.getText(), password.getText());
        model.getUsers().add(tmp);
        actual_user = tmp;
        System.out.println("Succesful registration.");        
    }
    
    
    @FXML
    public void Login()
    {
        User tmp = model.getUser(username.getText()); //beírt név alapján megpróbálja megkeresni a usert a Modelben lév? listában
        if (tmp == null) {
            System.out.println("User does not exist.");
            return;
        }
        if (!tmp.isCorrectPassword(password.getText())) {
            System.out.println("Incorrect password.");
            return;
        }
        actual_user = tmp; //ha helyes a jelszó, bejelentkezik
        System.out.printf("Succesful login. The actual user is %s\n", actual_user.name);
    }
    
    @FXML
    public void Logout() {
        actual_user = null;
        System.out.println("Succesful logout.");
    }
            
   
   //a Show tabon lév? 3 film közötti választás
   public void button0Pushed(){
        actual_show = (Show) model.getShows().get(0);
        txta.setText(actual_show.printRoom());        
    }
    
    public void button1Pushed(){
        actual_show = (Show) model.getShows().get(1);
        txta.setText(actual_show.printRoom());        
    }
    
    public void button2Pushed(){
        actual_show = (Show) model.getShows().get(2);
        txta.setText(actual_show.printRoom());        
    } 
    
    //innent?l kezdve minden az aktuális bejelentkezett felhazsnálóra és a kiválasztott filmre vonatkozik
    
    
    //foglalás gomb megnyomása (lehet felesleges volt külön metódusba szétszedni a foglalástól, szerintem így átláthatóbb)
    public void bookButtonPushed(){
        if (actual_user == null) { //ha nincs bejelentkezett felhasználó
            System.out.println("Please log in!");
            return;
        }
        if (actual_show == null) { //ha nincs kiválasztott film
            System.out.println("Please choose a movie!");
            return;
        }            
        //foglalás metódus meghívása
        Booking(Integer.parseInt(row.getText())-1, Integer.parseInt(column.getText())-1); //(-1 az indexelés miatt)   
    }
    
    public void changeButtonPushed(){ //hely változtatása, minden ugyanaz, mint a foglalásnál
        if (actual_user == null) {
            System.out.println("Please log in!");
            return;
        }
        if (actual_show == null) {
            System.out.println("Please choose a movie!");
            return;
        }            
        changeBooking(Integer.parseInt(prow.getText())-1, Integer.parseInt(pcolumn.getText())-1, Integer.parseInt(nrow.getText())-1, Integer.parseInt(ncolumn.getText())-1);        
    }
    
    public void deleteButtonPushed(){ //hely törlése
        if (actual_user == null) {
            System.out.println("Please log in!");
            return;
        }
        //film már ígyis-úgyis van kiválasztva
        deleteBooking(Integer.parseInt(row.getText())-1, Integer.parseInt(column.getText())-1);
    }
    
    
    //foglalás
    public void Booking(int row, int column) {         
        if  (actual_show.isBooked(row, column)) { //vizsgálja, hogy szabad-e még a hely
            System.out.println("This seat is already reserved.");
            return;
        }          
        actual_show.room[row][column] = true; //ha igen, most már nem
        actual_user.addBooking(actual_show.name, row, column); //hozzáaadja a felhasználó listájához
        System.out.println("Succesful booking.");
        System.out.printf("%s's seats:\n", actual_user.name); //felhasználó foglalásainak kilistázása
        System.out.println(actual_user.printBooking());
        txta.setText(actual_show.printRoom()); //a néz?tér frissítése
    }
    
    
    //törlés - a change metódus miatt visszaad egy boolean-t
    public boolean deleteBooking(int row, int column) {
        if (!actual_show.isBooked(row, column)){ //foglalt-e egyáltalán a hely
            System.out.println("This seat is empty.");
            return false;
        }    
        
        if (!actual_user.hasSeat(actual_show.name, row, column)){ //a felhasználóé-e a foglalás, amit törölni akar
            System.out.println("This seat is not yours.");
            return false;
        }                
        actual_user.deleteBooking(actual_show.name, row, column); //törli a foglalást a felhasználó listájáról
        actual_show.room[row][column] = false; //a hely újra szabad
        System.out.printf("%s's seats:\n", actual_user.name); //maradék foglalások kilistázása
        System.out.println(actual_user.printBooking());
        txta.setText(actual_show.printRoom()); //a néz?tér frissítése
        return true;
    }
    
    //foglalás módosítása (törlés, ha sikeres, új foglalás)
    public void changeBooking(int prev_row, int prev_column, int new_row, int new_column) {        
        if (!deleteBooking(prev_row, prev_column))
            return;
        Booking(new_row, new_column);        
    }
           
}
