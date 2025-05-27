package com.rebound.connectors;

import com.rebound.models.Customer.Customer;
import com.rebound.models.Customer.ListCustomer;

import java.util.ArrayList;

public class CustomerConnector {
    private ListCustomer listCustomer;

    public CustomerConnector() {
        listCustomer = new ListCustomer();
    }

    public ArrayList<Customer> get_all_customers() {
        if (listCustomer == null) {  // Remove semicolon
            listCustomer = new ListCustomer();

        }
        return listCustomer.getCustomers();
    }

    public Customer login(String email, String pwd)
    {
        ListCustomer lc = new ListCustomer();

        for (Customer c : lc.getCustomers())
        {
            if (c.getEmail().equalsIgnoreCase(email) && c.getPassword().equals(pwd))
            {
                return c;
            }

        }
        return null;
    }

}
