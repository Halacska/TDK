package projekt.view;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import projekt.model.Model;
import projekt.model.Seat;
import projekt.model.Show;
import projekt.model.User;

public class FXMLProjektController implements Initializable {
    
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    GridPane gp = new GridPane();
    @FXML
    Button endgame;
    @FXML
    Button captm;
    @FXML
    Button pets;
    @FXML
    Label username_label;
    @FXML
    Label message_label;
    @FXML
    Label seats_label;    
    @FXML
    Button logout_btn;        
    
    Model model;
    User actual_user = null; 
    Show actual_show = null;    
    HashSet<Seat> actual_seats = new HashSet<>(); //azért szükséges, hogy egyszerre több helyet lehessen foglalni
    
    
    //ha kész a DB, itt majd ki lehet olvasni az adatokat a Modelbe
    public void startController(){
        logout_btn.setVisible(false); //egyel?re csak a logout gombot rejti el, amíg nincs bejelentkezett felhasználó
    }
    
    public void setModel(Model model) {
        this.model = model;
        //csak a példa kedvéért 3 random film, majd a DB-s résznél erre úgyis kitalálunk valami jobbat (gondolom)
        model.getShows().add(new Show("Endgame", 10, 10));
        model.getShows().add(new Show("Captain Marvel", 10, 8));
        model.getShows().add(new Show("Pet Sematary", 8, 8));        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
    }    
    
    //regisztráció  
    @FXML
    public void Registration()
    {
        String uname = username.getText();
        if (uname.isEmpty()) { //ha nem írt be nevet            
            alert("Please enter a username!");
            return;
        }
        String pw = password.getText();
        if (pw.isEmpty()) { //ha nem írt be jelszót            
            alert("Please enter a password!");
            return;
        }
        
        if (model.getUser(uname) != null) { //ha már van ilyen nev? felhasználó            
            alert("Username is already exist.");
            username.clear(); //textfieldek ürítése
            password.clear();
            return;
        }      
        
        User tmp = new User(uname, pw); //user létrehozása...
        model.getUsers().add(tmp); //...és hozzáadása a modelben lév? listához        
        message_label.setText("Succesful registration.");        
        username.clear();
        password.clear();
    }
    
    //bejelentkezés
    @FXML
    public void Login()
    {   
        String uname = username.getText();
        if (uname.isEmpty()) { //ha nem írt be nevet            
            alert("Please enter a username!");
            return;
        }
        String pw = password.getText();
        if (pw.isEmpty()) { //ha nem ír be jelszót            
            alert("Please enter a password!");
            return;
        }
        
        if (actual_user != null) { //ha az el?z? felhazsnáló még nem jelentkezett ki            
            alert("The previous user has to log out!");
            username.clear(); //textfield ürítése
            password.clear();
            return;
        }
            
        User tmp = model.getUser(username.getText()); //beírt név alapján megpróbálja megkeresni a usert a Modelben lév? listában
        if (tmp == null) { //ha nincs a listában (tehát nincs ilyen nev? regisztrált felhasználó)
            alert("User does not exist.");
            username.clear();
            password.clear();
            return;
        }
        if (!tmp.isCorrectPassword(password.getText())) { //helytelen jelszó
            alert("Incorrect password.");
            username.clear();
            password.clear();
            return;
        }
        actual_user = tmp; //ha helyes a jelszó, bejelentkezik
        System.out.printf("The actual user is %s\n", actual_user.name);
        gp.getChildren().clear(); //"kitakarítja" a néz?teret az el?z? felhasználó után
        username.clear(); //meg a textfieldeket is
        password.clear();
        username_label.setText(uname);
        message_label.setText("Succesful login.");
        logout_btn.setVisible(true);
    }
    
    //kijelentkezés
    @FXML
    public void Logout() {
        actual_user = null;        
        message_label.setText("Succesful logout.");
        actual_seats.clear(); //set ürítése
        message_label.setText("");
        username_label.setText("");
        seats_label.setText("");
        logout_btn.setVisible(false);
        gp.getChildren().clear();
    }
     
   //a Show tabon lév? 3 film közötti választás
   @FXML
   public void button0() {
       actual_show = (Show) model.getShows().get(0); //film lekérése a modelben lév? listából
       loadMovie(); //a néz?tér (és a helyek állapotának) betöltése
   }
   
   @FXML
   public void button1() {
       actual_show = (Show) model.getShows().get(1);
       loadMovie();
   }
   
   @FXML
   public void button2() {
       actual_show = (Show) model.getShows().get(2);
       loadMovie();
   }
   
    //innent?l kezdve minden az aktuális bejelentkezett felhasználóra és a kiválasztott filmre vonatkozik
   
   public void loadMovie(){
        gp.getChildren().clear(); //néz?tér nullázása az el?z? betöltött film után        
        for (int i = 1; i < actual_show.room.length; i++)
            gp.add(new Label(Integer.toString(i)), 0, i); //sorszámozás
        for (int i = 1; i < actual_show.room[0].length; i++)
            gp.add(new Label(Integer.toString(i)), i, 0); //oszlopszámozás
        
        for (int i = 1; i < actual_show.room.length; i++){            
            for (int j = 1; j < actual_show.room[i].length; j++){
                Seat s = new Seat(i-1, j-1); //adott sor- és oszlopszámú hely (-1 az indexelés miatt)
                Button b = new Button(); 
                if (actual_user.hasSeat(actual_show, s))
                    b.setText("O"); //ha az akutális felhasználóé a hely
                else if (actual_show.isBooked(s))
                    b.setText("X"); //ha más felhasználóé a hely
                else
                    b.setText(" "); //ha üres
                b.setId(Integer.toString(i-1) + "-" + Integer.toString(j-1)); //button id beállítása ("sor-oszlop" formában)
                b.setOnAction((ActionEvent event) -> { //gombra kattintáskor lefutó rész
                    actual_seats.add(new Seat(b.getId())); //hely hozzáadása a kijelölt helyekhez
                    b.setText("o"); //szimbólum átállítása
                });
                gp.add(b, j, i); //button hozzáadása a gridpane-hez
            }
        }        
    }
    
    
    //foglalás gomb megnyomása (lehet felesleges volt külön metódusba szétszedni a foglalástól, szerintem így átláthatóbb)
    public void bookButtonPushed(){
        if (actual_user == null) { //ha nincs bejelentkezett felhasználó
            alert("Please log in!");
            return;
        }
        if (actual_show == null) { //ha nincs kiválasztott film
            alert("Please choose a movie!");
            return;
        }
        if (actual_seats.isEmpty()) { //ha nincs kiválasztott hely
            alert("Please choose a seat!");
            return;
        } 
        //foglalás metódus meghívása
        Booking(actual_seats);
    }
    
    public void deleteButtonPushed(){ //hely törlése
        String str;
        if (actual_user == null) {
            alert("Please log in!");
            return;
        }
        if (actual_seats.isEmpty()) { //ha nincs kiválasztott hely
            alert("Please choose a seat!");
            return;
        } 
        //film már ígyis-úgyis van kiválasztva 
        
        //törés metódus meghívása
        deleteBooking(actual_seats);
    }
    
    
    //foglalás - ha egy helyet nem is sikerül, a többit lefoglalja
    public void Booking(HashSet<Seat> seats) {
        int success = actual_seats.size(); //számolja a sikeresen lefoglalt helyeket
        String str;
        for (Seat s : seats) {
            if  (actual_show.isBooked(s)) { //vizsgálja, hogy szabad-e még a hely
                success--;
                str = String.format("%s seat is already reserved.\n", s.toString());
                alert(str);
                continue;            
            }
           actual_show.room[s.row][s.column] = true; //ha igen, most már nem
           actual_user.addBooking(actual_show, s); //hozzáaadja a felhasználó listájához            
        }              
        
        str = String.format("%d seat(s) %s succesfully booked.\n", success, (success > 1) ? "are" : "is");
        message_label.setText(str);
        seats_label.setText(actual_user.printBooking());
        loadMovie(); //a néz?tér frissítése
        actual_seats.clear(); //set ürítése
    }
    
    
    //törlés - a foglalás mintájára
    public void deleteBooking(HashSet<Seat> seats) {
        int success = actual_seats.size();
        String str;
        for (Seat s : seats) {
            if (!actual_show.isBooked(s)){ //foglalt-e egyáltalán a hely
                str = String.format("%s seat is empty.\n", s.toString());
                alert(str);
                success--;
                continue;                
            }
            if (!actual_user.hasSeat(actual_show, s)){ //a felhasználóé-e a foglalás, amit törölni akar
                str = String.format("%s seat is not yours.\n", s.toString());
                alert(str);
                success--;
                continue;
            }
            actual_user.deleteBooking(actual_show, s); //törli a foglalást a felhasználó listájáról
            actual_show.room[s.row][s.column] = false; //a hely újra szabad            
        } 
        
        str = String.format("%d seat(s) %s succesfully deleted.\n", success, (success > 1) ? "are" : "is");
        message_label.setText(str);
        seats_label.setText(actual_user.printBooking());
        loadMovie();
        actual_seats.clear();        
    }     
    
    //a felugró ablakokat kezel? függvény
    public void alert(String s){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("");
        alert.setContentText(s);
        alert.showAndWait();
    }
}