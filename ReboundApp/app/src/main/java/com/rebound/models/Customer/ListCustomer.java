package com.rebound.models.Customer;

import java.io.Serializable;
import java.util.ArrayList;

public class ListCustomer implements Serializable {
    private ArrayList<Customer> customers;

    public ListCustomer() {
        customers = new ArrayList<>();
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    public void addSampleCustomers() {
        customers.add(new Customer(
                "john_doe", "john.doe@email.com", "pass123",
                "John Doe", "https://i.pravatar.cc/150?img=1", "male", "0123456788"
        ));

        customers.add(new Customer(
                "diem_thuy", "thuynnd22411c@st.uel.edu.vn", "pass456",
                "Diem Thuy", "https://i.pravatar.cc/150?img=2", "female", "0123456789"
        ));

        customers.add(new Customer(
                "mike_wilson", "mike.wilson@email.com", "pass789",
                "Mike Wilson", "https://i.pravatar.cc/150?img=3", "male", "0123456780"
        ));

        customers.add(new Customer(
                "sarah_jones", "sarah.jones@email.com", "pass321",
                "Sarah Jones", "https://i.pravatar.cc/150?img=4", "female", "0123456787"
        ));

        customers.add(new Customer(
                "alex_brown", "alex.brown@email.com", "pass654",
                "Alex Brown", "https://vi.wikipedia.org/wiki/Alex_Rodrigo_Dias_da_Costa#/media/T%E1%BA%ADp_tin:Alex_Rodrigo_Dias_da_Costa.jpg", "male","0123456786"
        ));
    }
}
