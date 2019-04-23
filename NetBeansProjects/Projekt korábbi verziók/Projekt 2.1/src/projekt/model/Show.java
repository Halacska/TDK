package projekt.model;

/*
    Az el?adást/vetítést reprezentáló osztály.
    A néz?teret boolean tömbként tárolja (foglalt: true, szabad: false).
*/


public class Show {
    public String name;
    public boolean[][] room;

    public Show(String name, int row, int column) {
        this.name = name;
        this.room = new boolean[row][column];
        
        //alapméretezetten minden hely szabad
        for (boolean[] r : room) {
            for (boolean c : r)
                c = false;
        }   
    }
    
    //visszaadja, hogy foglalt-e egy hely
    public boolean isBooked(Seat s)
    {
        return room[s.row][s.column];
    }

    public boolean[][] getRoom() {
        return room;
    }

    //kirajzolja a néz?teret standard kimenetre (foglalt: + szabad: 0) (nem tudom, mihez kellhet, de legyen)
    public String printRoom() {
        StringBuilder sb = new StringBuilder();
        for (boolean[] r : room) {
            for (boolean c : r) {
                if (c == false)
                    sb.append("0");
                else
                    sb.append("+");
            }
            sb.append("\n");                
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {        
        return name + "\n" + printRoom();
    }

}
