import java.util.*;

class User {
    String username,password,emailId;
    
     User(String username, String password) {
        this.username = username;
        this.password = password;
		this.emailId = "";
    }
}
 
//-------------------------------------------------------
class UserDatabase {
	static User[] users = new User[100]; 
    static int userCount = 0; 
    static Passenger[] passengers = new Passenger[100];
    static int passengerCount = 0; 
}

//------------------------------------------------------- 
class Passenger extends User {
    String ticketNumber,fromStation,toStation;
    double fare;
	int age,month, day, year = 2025;
    boolean isValidDate;
    
    Passenger(String username, String password,int age) {
        super(username, password);
		this.age = age;
    }
    
	String getValidDate() {
        Scanner sc = new Scanner(System.in);
		do {
            System.out.print("Enter Month (1-12): ");
            month = sc.nextInt();
            System.out.print("Enter Day: ");
            day = sc.nextInt();
            isValidDate = false;

            if (month >= 1 && month <= 12) {
                int maxDays = (month == 2) ? 28 : (month == 4 || month == 6 || month == 9 || month == 11) ? 30 : 31;
                if (day >= 1 && day <= maxDays) {
                    isValidDate = true;
                }
            }

            if (!isValidDate) {
                System.out.println("Invalid date! Please enter a correct date.");
            }

        } while (!isValidDate);

        return day + "/" + month + "/" + year;
    }

    // Set ticket details...............................................
	void bookTicket(Scanner sc) {
	System.out.println("Available Bus Routes:");
    BusStation.showBusRoutes();
    System.out.print("Enter Bus Number (e.g., 1D, 12U, 16U, 4D): ");
    String busNumber = sc.next();

 // Get the route (list of stations) for the given bus number................................
    String[] route = BusStation.getRouteByBusNumber(busNumber);
    if (route == null) {
        System.out.println("Invalid bus number entered!");
        return;
	}
        
    System.out.println("Selected Bus Route (Stops): ");
       for (int i = 0; i < route.length; i++) {
			System.out.print(route[i]);
			if (i < route.length - 1) {
				System.out.print(" -> ");
			}
		}
		System.out.println();

    System.out.print("Enter FROM station: ");
	sc.nextLine();
    String from = sc.nextLine().trim();
    
    System.out.print("Enter TO station: ");
   String to = sc.nextLine().trim();
	sc.nextLine();

 // Validate that both stations are in the selected route.....................................
        int fromIndex = BusStation.getStationIndex(route, from);
        int toIndex = BusStation.getStationIndex(route, to);

		if (fromIndex == -1 || toIndex == -1) {
            System.out.println("Invalid station selection. The station(s) entered are not in the selected bus route.");
            return;
        }
        if (fromIndex >= toIndex) {
            System.out.println("You entered"+from+"To"+to );
        }
        
        System.out.println("Travel Date: " + getValidDate());
        int segments = Math.abs(toIndex - fromIndex);  
        this.fare = FareCalculator.calculateFare(this.age, segments);
        this.ticketNumber = String.format("%07d", new Random().nextInt(9999999));
        this.fromStation = from;
        this.toStation = to;
        
        System.out.println("\nTicket generate successfully for " + username + "!");
        showPassengerInfo();
    }

	
	// Update an existing ticket.
    void updateTicket(Scanner sc) {
        if (ticketNumber == null) {
            System.out.println("No ticket booked to update.");
        } else {
			bookTicket(sc);
			System.out.println("Updated Travel Date: " + getValidDate());
            System.out.println("Ticket updated successfully for " + username + "!");
        }
    }
	
	 // Cancel an existing ticket.
	void cancelTicket() {
        if (ticketNumber == null) {
            System.out.println("No ticket booked to cancel.");
        } else {
            ticketNumber = null;
            System.out.println("Ticket canceled successfully for " + username + "!");
        }
    }
    
    // Display passenger information
	void showPassengerInfo() {
        System.out.println("Passenger Information:");
        System.out.println("Username: " + username);
        if(ticketNumber != null) {
            System.out.println("Ticket Number: " + ticketNumber);
			System.out.println("Date: " + day + "/" + month + "/" + year);
            System.out.println("From: " + fromStation);
            System.out.println("To: " + toStation);
            System.out.println("Total Fare: Rs." + fare);
          //  System.out.println("Email Id" + emailId);
        } else {
			System.out.println("Email Id" + emailId);
            System.out.println("No ticket booked yet.");
        }
      }
}
	
	
	
class BusStation {
    // Each bus route array: first element is bus number; subsequent elements are station names in order.
    static final String[][] busRoutes = {
		{"1D", "Maninagar", "Rambaug", "Danilimda char rasta", "Anjali", "Nehrunagar", "Isro", "Iskon", "Swagat bunglow", "Bopal gam", "Ghumagam"},
        {"12U", "RTO circle", "Ranip", "Shastrinagar", "Jaimangal", "Jhansi Ki Rani", "Nehrunagar", "Anjali", "Danilimda charrasta", "Chandola lake", "Narol", "Ghodasar", "CTM", "Rameshwarark"},
        {"16U", "Nehrunagar", "Shivranjani", "Starbazar", "Ramdevnagar", "Iskon", "Karnavati club", "Prahladnagar", "Makarba", "Sanand circle"},
        {"4D", "LD Engineering", "Panjrapole Char Rasta", "Jhansi Ki Rani", "Andhjan Mandal", "Sola Cross Road", "Akhbarnagar", "Sabarmati Power House", "Motera Cross Road", "Chandkheda Gam", "DCIS Circle"}

};

    
     // Display all bus routes and their stops.
    static void showBusRoutes() {
        for (String[] route : busRoutes) {
            System.out.print(route[0] + " - ");
            for (int i = 1; i < route.length; i++) {
                System.out.print(route[i]);
                if (i < route.length - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println("\n");
        }
    }
    
    // Get the full route (array of stations) by bus number.
    static String[] getRouteByBusNumber(String busNumber) {
        for (String[] route : busRoutes) {
            if (route[0].equalsIgnoreCase(busNumber)) {
                return route;
            }
        }
        return null;
    }
    
    // Return the index of the station in the route; returns -1 if not found.
     static int getStationIndex(String[] route, String station) {
        for (int i = 1; i < route.length; i++) { // start from index 1 because index 0 is bus number.
            if (route[i].equalsIgnoreCase(station)) {
                return i;
            }
        }
        return -1;
    }
}



class FareCalculator {
    // Calculate fare based on passenger age and the number of segments (each segment costs 7 rupees).
   static double calculateFare(int age, int segments) {
        double baseFare = segments * 7.0;
        double discount = 0;
        
        if (age >= 10 && age <= 22) {
            discount = 0.4;
            System.out.println("Student Card Applied: 40% Discount");
        } else if (age >= 65 && age <= 100) {
            discount = 0.6;
            System.out.println("Senior Citizen Card Applied: 60% Discount");
        } else {
            System.out.println("No Discount Applied");
        }
        
        return baseFare * (1 - discount);
    }
}

	
class Registration {
    void registerUser(Scanner sc) {
		if (UserDatabase.passengerCount >= UserDatabase.passengers.length) {
            System.out.println("User database is full! Cannot register more users.");
            return;
        }
		
        System.out.print("Enter username: ");
        String username = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();
		 System.out.print("Enter your age: ");
        int age = sc.nextInt();
        
         // Check if username already exists
        for (int i = 0; i < UserDatabase.passengerCount; i++) {
            if (UserDatabase.passengers[i].username.equals(username)) {
                System.out.println("Username already exists! Please try a different username.");
                return;
            }
        }
        
         // Add new passenger
        UserDatabase.passengers[UserDatabase.passengerCount] = new Passenger(username, password, age);
        UserDatabase.passengerCount++; // Increase count
        System.out.println("Registration successful!");
    }
}



class Login {
    void loginUser(Scanner sc) {
       if (UserDatabase.passengerCount == 0) {
            System.out.println("No registered users found. Please register first.");
            return;
        }

        
        System.out.print("Enter username: ");
        String username = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();
		System.out.print("Enter e-mailId: ");
        String emailId = sc.next();
        
        // Search for the user with matching credentials.
        for (int i = 0; i < UserDatabase.passengerCount; i++) {
			Passenger p = UserDatabase.passengers[i];
            if (p.username.equals(username) && p.password.equals(password)) {
                System.out.println("Login successful! Welcome " + username + "!");
                    
                   // while (true) {
                        System.out.println("\n--- Passenger Menu --- \n1. View Bus Station Details \n2. View Passenger Information \n3. Book Ticket \n4. Cancel Ticket \n5. Update Ticket \n6. Update Account (Change Password) \n7. Logout \nEnter your choice:");
						String input = sc.next();  // Read input as a string

						 // Check if input contains only digits
						boolean isValid = true;
						for (int j = 0; j < input.length(); j++) {
							if (input.charAt(j) < '0' || input.charAt(j) > '9') {
								isValid = false;
								break;
							}
						}

						if (isValid) {
							int choice = Integer.parseInt(input);  // Convert string to integer safely

							if (choice >= 1 && choice <= 7) {  // Validate range
                            switch (choice) {
								case 1:
									 BusStation.showBusRoutes();
									break;
                                case 2:
                                    p.showPassengerInfo();
                                    break;
                                case 3:
									p.bookTicket(sc);
                                    break;
                                case 4:
                                    p.cancelTicket();
                                    break;
                                case 5:
                                    p.updateTicket(sc);
                                    break;
                                case 6:
									boolean s=false;
									while(!s){
                                    System.out.print("Enter old password: ");
									String pass = sc.next();
									if(pass.equals(password)){
										s=true;
									}else{
										System.out.println("Old password is incorrect. Try again.");
									}
									}
                                    System.out.print("Enter new password: ");
                                    String newPass = sc.next();
                                    p.password = newPass;  // Directly update the inherited field.
                                    System.out.println("Password updated successfully for " + p.username + "!");
                                    break;
                                case 7:
                                    System.out.println("Logging out...");
                                    return; // Exit the nested menu and return to the main menu.
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
						} else {
                            System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                    }	
                } else {
                    System.out.println("User functionalities not available for this user type.");
                    return;
                }
            //}
        }
	}
        System.out.println("Invalid username or password!");
    }
}



class TextualContent{
		void show(){
		String aboutUs= "\nRegister Office:- Sixth Floor, Dr. Jayeshbhai Chavda Bhawan, AMC East Zone Office,\n Ahmedabad Municipal Corporation,  Road, Bhagvanpura, Ahmedabad Pincode: 3\n\n"
						+" Ahmedabad Janmarg Ltd is registered under Companies Act, 1956 and is 100% subsidiary of Ahmedabad Municipal Corporation.\n In order to provide faster, reliable, eco friendly and advanced Public Transportation\n\n"
						+" 1. Shri M. Thennarasan IAS , Municipal Commissioner - Chairman AJL \n 2. Shri Mayank Chavda, IPS, Joint Commissioner of Police (Traffic), Ahmedabad \n 3. Shri Amrutesh Kalidas Aurangabadkar, IAS, Executive Director\n\n"
						+" Ahmedabad Janmarg Ltd has won many accolades for the implementation of sustainable BRTS Operation.\n\n"
						+" 1.Received National Award for “Best Mass Transit Rapid System Project - 2009”under JnNURM scheme \n on 3rd December 2009 by Honourable Prime Minister, Government of India\n"
						+" 2.International Award from UITP for Public Transport Strategy Award at Milan at June, 2015.\n"
						+" 3.Best Digital Payments Innovator  5th Feb. 2018 Smart City Digital Payment Awards 2018 by Smart City Mission. SCADL.\n"
						+" 4.Digital City award (for CCPS-Janmitra card) 24th May. 2019 Award by \n Secretary Parameswaran Iyer (DWS, Ministry of Drinking Water and Sanitation), \n GOI with support of Smart city Mission and Govt. of India. SCADL.\n\n"
						+" Ahmedabad Janmarg Ltd has 160 km operational route with 380 buses and about 2.20 lakhs  \n average passengers per day, AJL will try to provide last point connectivity to BRTS \n users through trunk & feeder operation and committed \n to provide safe and clean, comfortable and pollution free public transportation to citizens";
			 System.out.println(aboutUs);				
		}
		void display(){
			String contactUs="\nM. Thennarasan, IAS\nChairman Ahmedabad Janmarg Limited\nSardar Gandhi Bhavan, Danapith, Ahmedabad\nEmail:mc@ahmedabadcity.gov.in\n\n"
							+"Shri Amrutesh Kalidas Aurangabadkar, IAS\nExecutive Director Ahmedabad Janmarg Limited\nUsmanpura,Ahmedabad\nEmail:Amrutesh@Ahmedabadcity.gov.in\n\n"
							+"Prof. H.M. Shivanand Swamy\nDirector Center of Excellence in Urban Transport, CEPT University\nRangpura,Ahmedabad\nEmail:hmshivanandswamy@cept.ac.in\n\n"
							+"Toll Free No.:1800 233 2030\n\n"
							+"ICICI Customer care :1800 2100 104";
			System.out.println(contactUs);				
		}
		
} 



class Run {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        int choice1;


		System.out.println("***-----Welcome To Brts Portal-----*** \nHere Some Advantages of Our Services.... \n \nTotal No. of Electric Buses Oerated :150 \nFuel saving per day (In Liters):13349.50 \nGHG emission reduction(CO2e per day in Kg):15503.40 \n");
        //while (true) {
			int count = 0;
			for (int i = 0; i < UserDatabase.users.length; i++) {
				if (UserDatabase.users[i] != null) {
					count++;
				}
			}
            System.out.println("\n1.Menu \n2.Login \n3.Registration \n4.Exit \nEnter your choice (0 to 4): ");
            if (sc.hasNextInt()) {
            choice1 = sc.nextInt();
				if (choice1 == 4) {
                    System.out.println("Exiting... Thank you for using BRTS Portal!");
                    //break; 
                }

            switch (choice1) {
                    case 1: int choice2;
							while (true) {
                            System.out.println("\n1.About us \n2.Time Table \n3.Contact Us \n4.Give Riview or complain \n5.Back to Main manu \nEnter your choice (1 to 5):");
                            if (sc.hasNextInt()){
                            choice2 = sc.nextInt();
							if (choice2 == 5) {
                        break; 
                            }
							switch (choice2) {
                                    case 1:
                                        new TextualContent().show();
                                        break;
                                    case 2:
                                        System.out.println("---Time Table---");
                                        System.out.println("--> 1D: 8 am--9:15 am--10 am--12:30 am--1:25 pm--2 pm--3:45 pm--5:45 pm--7:25 pm--8:30 pm");
                                        System.out.println("--> 12U: 7 am--7:45 am--8:30 am--9:15 am--10 am--10:45 am--11:30 am--1:20 pm--2 pm--3:30 pm--5:35 pm--7:45 pm--8:15 pm");
                                        System.out.println("--> 16U: 7:45 am--8:30 am--9 am--10 am--12:30 am--1:45 pm--2 pm--3:20 pm--5:45 pm--6:20 pm--7:35 pm--9:30 pm");
                                        System.out.println("--> 4D: 9 am--10 am--11:35 am--1:30 pm--2 pm--3:35 pm--5:40 pm--6:30 pm--8:20 pm--8:45 pm");
										
                                        break;
                                    case 3:
										new TextualContent().display();
                                        break;
                                    case 4:
										System.out.print("Enter your review/complaint: ");
                                        sc.nextLine(); // clear newline
                                        String review = sc.nextLine();
                                        System.out.println("Thank you for your feedback!");
                                        break;
                                    default:
                                        System.out.println("Invalid choice! Please enter a number between 1 and 6.");
                                }
                            } else {
                                System.out.println("Invalid input! Please enter a number.");
                                sc.nextLine();
                            }
                        }
                        break;

                    case 2:
						new Login().loginUser(sc);
                        break;

                    case 3:
                        new Registration().registerUser(sc);
                        break;
                    default:
                        System.out.println("Invalid choice! Please enter 1, 2, 3, or 4.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine();
            }
        //}
    }
}
