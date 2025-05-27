package com.rebound.models.Customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class ListCustomer implements Serializable {
    public ArrayList<Customer> customers;

    public ListCustomer() {
        customers = new ArrayList<>();
        addSampleCustomers();
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

    private void addSampleCustomers() {
        customers.add(new Customer("john_doe", "john.doe@email.com", "pass123"));
        customers.add(new Customer("jane_smith", "jane.smith@email.com", "pass456"));
        customers.add(new Customer("mike_wilson", "mike.wilson@email.com", "pass789"));
        customers.add(new Customer("sarah_jones", "sarah.jones@email.com", "pass321"));
        customers.add(new Customer("alex_brown", "alex.brown@email.com", "pass654"));
    }

}
