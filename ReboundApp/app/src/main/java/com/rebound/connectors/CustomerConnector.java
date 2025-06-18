package com.rebound.connectors;

import android.content.Context;

import com.rebound.models.Customer.Customer;
import com.rebound.models.Customer.ListCustomer;
import com.rebound.utils.SharedPrefManager;


import java.util.ArrayList;

public class CustomerConnector {
    private ListCustomer listCustomer;

    public CustomerConnector(Context context) {
        // ✅ Lấy từ SharedPreferences
        listCustomer = SharedPrefManager.getCustomerList(context);

        if (listCustomer == null) {
            listCustomer = new ListCustomer(); // lần đầu chưa có, tạo mẫu
            SharedPrefManager.saveCustomerList(context, listCustomer); // lưu vào
        }
    }

    public ArrayList<Customer> get_all_customers() {
        return listCustomer.getCustomers();
    }

    public Customer login(String email, String pwd) {
        for (Customer c : listCustomer.getCustomers()) {
            if (c.getEmail().equalsIgnoreCase(email) && c.getPassword().equals(pwd)) {
                return c;
            }
        }
        return null;
    }
    public Customer login(String email, String pwd, StringBuilder errorMsg) {
        for (Customer c : listCustomer.getCustomers()) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                if (c.getPassword().equals(pwd)) {
                    return c; // ✅ Thành công
                } else {
                    errorMsg.append("Incorrect password."); // ❌ Sai mật khẩu
                    return null;
                }
            }
        }

        errorMsg.append("Account does not exist."); // ❌ Không tìm thấy email
        return null;
    }
}
