package projekt.view;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    
    Model model;
    User actual_user = null; 
    Show actual_show = null;    
    HashSet<Seat> actual_seats = new HashSet<>(); //az�rt sz�ks�ges, hogy egyszerre t�bb helyet lehessen foglalni
    
    public void setModel(Model model) {
        this.model = model;
        //csak a p�lda kedv��rt 3 random film, majd a DB-s r�szn�l erre �gyis kital�lunk valami jobbat (gondolom)
        model.getShows().add(new Show("Endgame", 10, 10));
        model.getShows().add(new Show("Captain Marvel", 10, 8));
        model.getShows().add(new Show("Pet Sematary", 8, 8));        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
    }    
    
    //regisztr�ci�  
    @FXML
    public void Registration()
    {
        String uname = username.getText();
        if (uname.isEmpty()) { //ha nem �rt be nevet
            System.out.println("Please enter a username!");
            return;
        }
        String pw = password.getText();
        if (pw.isEmpty()) { //ha nem �rt be jelsz�t
            System.out.println("Please enter a password!");
            return;
        }
        
        if (model.getUser(uname) != null) { //ha m�r van ilyen nev? felhaszn�l�
            System.out.println("Username is already exist.");
            username.clear(); //textfieldek �r�t�se
            password.clear();
            return;
        }      
        
        User tmp = new User(uname, pw); //user l�trehoz�sa...
        model.getUsers().add(tmp); //...�s hozz�ad�sa a modelben l�v? list�hoz
        System.out.println("Succesful registration.");
        username.clear();
        password.clear();
    }
    
    //bejelentkez�s
    @FXML
    public void Login()
    {   
        String uname = username.getText();
        if (uname.isEmpty()) { //ha nem �rt be nevet
            System.out.println("Please enter a username!");
            return;
        }
        String pw = password.getText();
        if (pw.isEmpty()) { //ha nem �r be jelsz�t
            System.out.println("Please enter a password!");
            return;
        }
        
        if (actual_user != null) { //ha az el?z? felhazsn�l� m�g nem jelentkezett ki
            System.out.println("The previous user has to log out!\n");
            username.clear(); //textfield �r�t�se
            password.clear();
            return;
        }
            
        User tmp = model.getUser(username.getText()); //be�rt n�v alapj�n megpr�b�lja megkeresni a usert a Modelben l�v? list�ban
        if (tmp == null) { //ha nincs a list�ban (teh�t nincs ilyen nev? regisztr�lt felhaszn�l�)
            System.out.println("User does not exist.");
            username.clear();
            password.clear();
            return;
        }
        if (!tmp.isCorrectPassword(password.getText())) { //helytelen jelsz�
            System.out.println("Incorrect password.");
            username.clear();
            password.clear();
            return;
        }
        actual_user = tmp; //ha helyes a jelsz�, bejelentkezik
        System.out.printf("Succesful login. The actual user is %s\n", actual_user.name);
        gp.getChildren().clear(); //"kitakar�tja" a n�z?teret az el?z? felhaszn�l� ut�n
        username.clear(); //meg a textfieldeket is
        password.clear();
    }
    
    //kijelentkez�s
    @FXML
    public void Logout() {
        actual_user = null;
        System.out.println("Succesful logout.");        
    }
     
   //a Show tabon l�v? 3 film k�z�tti v�laszt�s
   @FXML
   public void button0() {
       actual_show = (Show) model.getShows().get(0); //film lek�r�se a modelben l�v? list�b�l
       loadMovie(); //a n�z?t�r (�s a helyek �llapot�nak) bet�lt�se
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
   
    //innent?l kezdve minden az aktu�lis bejelentkezett felhaszn�l�ra �s a kiv�lasztott filmre vonatkozik
   
   public void loadMovie(){
        gp.getChildren().clear(); //n�z?t�r null�z�sa az el?z? bet�lt�tt film ut�n        
        for (int i = 1; i < actual_show.room.length; i++)
            gp.add(new Label(Integer.toString(i)), 0, i); //sorsz�moz�s
        for (int i = 1; i < actual_show.room[0].length; i++)
            gp.add(new Label(Integer.toString(i)), i, 0); //oszlopsz�moz�s
        
        for (int i = 1; i < actual_show.room.length; i++){            
            for (int j = 1; j < actual_show.room[i].length; j++){
                Seat s = new Seat(i-1, j-1); //adott sor- �s oszlopsz�m� hely (-1 az indexel�s miatt)
                Button b = new Button(); 
                if (actual_user.hasSeat(actual_show, s))
                    b.setText("O"); //ha az akut�lis felhaszn�l�� a hely
                else if (actual_show.isBooked(s))
                    b.setText("X"); //ha m�s felhazsn�l�� a hely
                else
                    b.setText(" "); //ha �res
                b.setId(Integer.toString(i-1) + "-" + Integer.toString(j-1)); //button id be�ll�t�sa ("sor-oszlop" form�ban)
                b.setOnAction((ActionEvent event) -> { //gombra kattint�skor lefut� r�sz
                    actual_seats.add(new Seat(b.getId())); //hely hozz�ad�sa a kijel�lt helyekhez
                    b.setText("o"); //szimb�lum �t�ll�t�sa
                });
                gp.add(b, j, i); //button hozz�ad�sa a gridpane-hez
            }
        }        
    }
    
    
    //foglal�s gomb megnyom�sa (lehet felesleges volt k�l�n met�dusba sz�tszedni a foglal�st�l, szerintem �gy �tl�that�bb)
    public void bookButtonPushed(){
        if (actual_user == null) { //ha nincs bejelentkezett felhaszn�l�
            System.out.println("Please log in!");
            return;
        }
        if (actual_show == null) { //ha nincs kiv�lasztott film
            System.out.println("Please choose a movie!");
            return;
        }
        if (actual_seats.isEmpty()) { //ha nincs kiv�lasztott hely
            System.out.println("Please choose a seat!");
            return;
        } 
        //foglal�s met�dus megh�v�sa
        Booking(actual_seats);
    }
    
    public void deleteButtonPushed(){ //hely t�rl�se
        if (actual_user == null) {
            System.out.println("Please log in!");
            return;
        }
        if (actual_seats.isEmpty()) { //ha nincs kiv�lasztott hely
            System.out.println("Please choose a seat!");
            return;
        } 
        //film m�r �gyis-�gyis van kiv�lasztva 
        
        //t�r�s met�dus megh�v�sa
        deleteBooking(actual_seats);
    }
    
    
    //foglal�s - ha egy helyet nem is siker�l, a t�bbit lefoglalja
    public void Booking(HashSet<Seat> seats) {
        int success = actual_seats.size(); //sz�molja a sikeresen lefoglalt helyeket
        for (Seat s : seats) {
            if  (actual_show.isBooked(s)) { //vizsg�lja, hogy szabad-e m�g a hely
                System.out.printf("%s seat is already reserved.\n", s.toString());
                success--;
                continue;            
            }
           actual_show.room[s.row][s.column] = true; //ha igen, most m�r nem
           actual_user.addBooking(actual_show, s); //hozz�aadja a felhaszn�l� list�j�hoz            
        }              
        
        System.out.printf("%d seat(s) %s succesfully booked.\n", success, (success > 1) ? "are" : "is");
        System.out.printf("%s's seats:\n", actual_user.name); //felhaszn�l� foglal�sainak kilist�z�sa
        System.out.println(actual_user.printBooking());
        loadMovie(); //a n�z?t�r friss�t�se
        actual_seats.clear(); //set �r�t�se
    }
    
    
    //t�rl�s - a foglal�s mint�j�ra
    public void deleteBooking(HashSet<Seat> seats) {
        int success = actual_seats.size();
        for (Seat s : seats) {
            if (!actual_show.isBooked(s)){ //foglalt-e egy�ltal�n a hely
                System.out.printf("%s seat is empty.\n", s.toString());
                success--;
                continue;                
            }
            if (!actual_user.hasSeat(actual_show, s)){ //a felhaszn�l��-e a foglal�s, amit t�r�lni akar
                System.out.printf("%s seat is not yours.\n", s.toString());
                success--;
                continue;
            }
            actual_user.deleteBooking(actual_show, s); //t�rli a foglal�st a felhaszn�l� list�j�r�l
            actual_show.room[s.row][s.column] = false; //a hely �jra szabad            
        } 
        
        System.out.printf("%d seat(s) %s succesfully deleted.\n", success, (success > 1) ? "are" : "is");
        System.out.printf("%s's seats:\n", actual_user.name); //marad�k foglal�sok kilist�z�sa
        System.out.println(actual_user.printBooking());
        loadMovie();
        actual_seats.clear();        
    }           
}