public class Item {

    // Variables that will not be changed later are assigned as Final.
    private final String ID;
    private final String name;
    private final String type;

    private String borrowedDate;
    private String borrowedBy;

    // The constructor used by the partners in each class connected to the Item class.
    protected Item(String ID, String name, String type) {
        this.ID = ID;
        this.name = name;
        this.type = type;
    }

    // Setter functions related to borrow.
    public void setBorrowed_date(String borrowed_date) {this.borrowedDate = borrowed_date;}
    public void setBorrowed_by(String borrowed_by) {this.borrowedBy = borrowed_by;}

    public String getID() { return ID; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBorrowedDate() { return borrowedDate; }
    public String getBorrowedBy() { return borrowedBy; }

}

class Book extends Item {

    private final String author;
    private final String genre;

    public Book(String ID, String name, String author, String genre, String type) {
        super(ID,name,type);
        this.author = author;
        this.genre = genre;
    }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }

}

class Magazine extends Item {

    private final String publisher;
    private final String category;

    public Magazine(String ID, String name, String publisher, String category, String type) {
        super(ID,name,type);
        this.publisher = publisher;
        this.category = category;
    }
    public String getPublisher() { return publisher; }
    public String getCategory() { return category; }

}

class DVD extends Item {

    private final String directory;
    private final String category;
    private final String runtime;

    public DVD(String ID, String name, String directory, String category, String runtime, String type) {
        super(ID,name,type);
        this.directory = directory;
        this.category = category;
        this.runtime = runtime;
    }
    public String getDirectory() { return directory; }
    public String getCategory() { return category; }
    public String getRuntime() { return runtime; }

}