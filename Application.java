import java.util.*;
import java.sql.*;

public class Application{

  public String client_email = "";
  public String client_password = "";


  private String m_userName;
  private String m_password;
  private String m_url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
  private String m_driverName = "oracle.jdbc.driver.OracleDriver";

  public static void main(String args[]) {
    Application app = new Application();
    Scanner in = new Scanner(System.in);

    app.m_userName = args[0];
    app.m_password = args[1];

    System.out.println("Pick an option:");
    System.out.println("1 - Registered User");
    System.out.println("2 - Not a Registered User");
    System.out.println("q - Exit");
    String result = in.nextLine();

    if (result.equals("1")){

      while(app.Login(app) == false){}
      System.out.println("Valid Credentials!");
      app.Menu(app);
    }
    else if(result.equals("2")){
	    app.createUser(app);
	    System.out.println("Successfully created account");
    }
    else if(result.equals("q")){

    }

  }

  //Display main menu
  public void Menu(Application app)
  {
    Scanner in = new Scanner(System.in);
    System.out.println("What would you like to do? Choose an option.");
    System.out.println("1 - Search for flight");
    System.out.println("2 - View existing bookings");
    if (1 == 2)
    {
      System.out.println("3 - Record Departure");
      System.out.println("4 - Record Arrival");
    }
    System.out.println("0 - Logout");

    int choice = in.nextInt();
    if (choice == 1)
    {
      //search for flight
    }
    else if (choice == 2)
    {
      //View bookings
    }
    else if (choice == 3)
    {
      //log out
    }
  }

  public void viewBookings(Application app)
  {
    String findBookings;
    //Find booking information related to client email
    findBookings = "select b.tno, dep_date, fare, name " +
                  "from bookings b, tickets t " +
                  "where b.tno = t.tno " +
                  "and t.email = '" + app.client_email +"'";
    Statement stmt;
    Connection con;
    //Statement stmt2; might need later
    try
    {
      con = DriverManager.getConnection(app.m_url, app.m_userName, app.m_password);
      stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
      //stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSTITIVE, ResultSet.CONCUR_UPDATABLE);

      ResultSet rs = stmt.executeQuery(findBookings);

      displayResultSet(rs);
      stmt.close();
      con.close();
    } catch(SQLException ex)
    {
      System.err.println("SQLException: " + ex.getMessage());
    }
  }

	//function to create a new user
  public void createUser(Application app)
  {
    //Get user information
    Scanner in = new Scanner(System.in);
    System.out.print("Please enter an email address: ");
    app.client_email = in.next();
    System.out.print("Enter a password: ");
    app.client_password = in.next();

    Connection m_con;
    String updateTable;
    //Add user email and password to table Users. Not sure what to initialize the date to
    updateTable = "insert into users values('" + app.client_email + "', '" + app.client_password + "', null)";
    Statement stmt;

    try
    {
      m_con = DriverManager.getConnection(app.m_url, app.m_userName, app.m_password);

      stmt = m_con.createStatement();
      stmt.executeUpdate(updateTable);
      stmt.close();
      m_con.close();
    } catch(SQLException ex) {
      System.err.println("SQLException: " +
      ex.getMessage());
    }
  }

  public boolean Login(Application app){
    Scanner in = new Scanner(System.in);
    boolean valid = false;

    System.out.print("Please enter your email: ");
    app.client_email = in.next();
    System.out.print("Please enter your password: ");
    app.client_password = in.next();

    Connection m_con;
    String findUsers;
    findUsers = "SELECT email, pass FROM users";
    Statement stmt;

    try
    {
      Class drvClass = Class.forName(m_driverName);
      DriverManager.registerDriver((Driver)
      drvClass.newInstance());
    } catch(Exception e)
    {
      System.err.print("ClassNotFoundException: ");
      System.err.println(e.getMessage());
    }

    try
    {
      m_con = DriverManager.getConnection(app.m_url, app.m_userName, app.m_password);

      stmt = m_con.createStatement();
      ResultSet rst = stmt.executeQuery(findUsers);
      while(rst.next()){
        System.out.println(rst.getString(1) + " " + rst.getString(2));
        if (app.client_email.equals(rst.getString(1).replaceAll("\\s+","")) && app.client_password.equals(rst.getString(2).replaceAll("\\s+",""))){
          valid = true;
          System.out.print("Found valid.");
          break;
        }
      }
      rst.close();
      stmt.close();
      m_con.close();
    } catch(SQLException ex) {
      System.err.println("SQLException: " +
      ex.getMessage());
    }
      return valid;
  }

  //Function for displaying a result set, with column names
  public void displayResultSet(ResultSet rs)
  {
    String value = null;
    Object o = null;

    try
    {
      ResultSetMetaData rsM = rs.getMetaData();

      int columnCount = rsM.getColumnCount();

      for (int column = 1; column <= columnCount; column++)
      {
        value = rsM.getColumnLabel(column);
        System.out.print(value + "\t");
      }
      System.out.println();

      while(rs.next())
      {
        for (int i = 1; i <= columnCount; i++)
        {
          o = rs.getObject(i);
          if (o != null)
          {
            value = o.toString();
          }
          else
          {
            value = "null";
          }
          System.out.print(value + "\t");
        }
      System.out.println();
      }
    } catch(Exception io)
    {
      System.out.println(io.getMessage());
    }
  }


}
