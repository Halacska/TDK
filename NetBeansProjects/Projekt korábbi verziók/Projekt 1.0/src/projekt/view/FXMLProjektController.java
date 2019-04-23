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
        //csak a p�lda kedv��rt 3 random film, majd a DB-s r�szn�l erre �gyis kital�lunk valami jobbat (gondolom)
        model.getShows().add(new Show("Endgame", 15, 10));
        model.getShows().add(new Show("Captain Marvel", 10, 10));
        model.getShows().add(new Show("Pet Sematary", 10, 8));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
    }    
    
    //regisztr�ci�, bejeltnkez�s, kijelentkez�s    
    
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
        User tmp = model.getUser(username.getText()); //be�rt n�v alapj�n megpr�b�lja megkeresni a usert a Modelben l�v? list�ban
        if (tmp == null) {
            System.out.println("User does not exist.");
            return;
        }
        if (!tmp.isCorrectPassword(password.getText())) {
            System.out.println("Incorrect password.");
            return;
        }
        actual_user = tmp; //ha helyes a jelsz�, bejelentkezik
        System.out.printf("Succesful login. The actual user is %s\n", actual_user.name);
    }
    
    @FXML
    public void Logout() {
        actual_user = null;
        System.out.println("Succesful logout.");
    }
            
   
   //a Show tabon l�v? 3 film k�z�tti v�laszt�s
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
    
    //innent?l kezdve minden az aktu�lis bejelentkezett felhazsn�l�ra �s a kiv�lasztott filmre vonatkozik
    
    
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
        //foglal�s met�dus megh�v�sa
        Booking(Integer.parseInt(row.getText())-1, Integer.parseInt(column.getText())-1); //(-1 az indexel�s miatt)   
    }
    
    public void changeButtonPushed(){ //hely v�ltoztat�sa, minden ugyanaz, mint a foglal�sn�l
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
    
    public void deleteButtonPushed(){ //hely t�rl�se
        if (actual_user == null) {
            System.out.println("Please log in!");
            return;
        }
        //film m�r �gyis-�gyis van kiv�lasztva
        deleteBooking(Integer.parseInt(row.getText())-1, Integer.parseInt(column.getText())-1);
    }
    
    
    //foglal�s
    public void Booking(int row, int column) {         
        if  (actual_show.isBooked(row, column)) { //vizsg�lja, hogy szabad-e m�g a hely
            System.out.println("This seat is already reserved.");
            return;
        }          
        actual_show.room[row][column] = true; //ha igen, most m�r nem
        actual_user.addBooking(actual_show.name, row, column); //hozz�aadja a felhaszn�l� list�j�hoz
        System.out.println("Succesful booking.");
        System.out.printf("%s's seats:\n", actual_user.name); //felhaszn�l� foglal�sainak kilist�z�sa
        System.out.println(actual_user.printBooking());
        txta.setText(actual_show.printRoom()); //a n�z?t�r friss�t�se
    }
    
    
    //t�rl�s - a change met�dus miatt visszaad egy boolean-t
    public boolean deleteBooking(int row, int column) {
        if (!actual_show.isBooked(row, column)){ //foglalt-e egy�ltal�n a hely
            System.out.println("This seat is empty.");
            return false;
        }    
        
        if (!actual_user.hasSeat(actual_show.name, row, column)){ //a felhaszn�l��-e a foglal�s, amit t�r�lni akar
            System.out.println("This seat is not yours.");
            return false;
        }                
        actual_user.deleteBooking(actual_show.name, row, column); //t�rli a foglal�st a felhaszn�l� list�j�r�l
        actual_show.room[row][column] = false; //a hely �jra szabad
        System.out.printf("%s's seats:\n", actual_user.name); //marad�k foglal�sok kilist�z�sa
        System.out.println(actual_user.printBooking());
        txta.setText(actual_show.printRoom()); //a n�z?t�r friss�t�se
        return true;
    }
    
    //foglal�s m�dos�t�sa (t�rl�s, ha sikeres, �j foglal�s)
    public void changeBooking(int prev_row, int prev_column, int new_row, int new_column) {        
        if (!deleteBooking(prev_row, prev_column))
            return;
        Booking(new_row, new_column);        
    }
           
}
