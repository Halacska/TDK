package projekt.model;

/*
    Egy adott helyet reprezentáló osztály.
    Mivel a controller úgyis csak egy adott el?adással dolgozik egyszerre,
    csak a hely sor- és oszlopszám mez?je van.  
*/


public class Seat {
        public int row;
        public int column;

        public Seat(int row, int column) { //konstruktor inttel megadott formához
            this.row = row;
            this.column = column;
        }
        
        public Seat(String s) { //konstruktor a button id-k szerinti formához
            String[] tmp = s.split("-");
            this.row = Integer.parseInt(tmp[0]);
            this.column = Integer.parseInt(tmp[1]);
        }
        
        
        @Override
        public String toString() {
            return (row+1) + "-" + (column+1); //+1 a nullától való indexelés miatt
        }

    @Override
    public boolean equals(Object obj) {       
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Seat other = (Seat) obj;
        if (this.row != other.row) {
            return false;
        }
        if (this.column != other.column) {
            return false;
        }
        return true;
    }  
    }
