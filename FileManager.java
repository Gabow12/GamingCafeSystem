/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
import java.io.*;
import java.util.LinkedList;

public class FileManager {

    public void saveCustomers(
            LinkedList<Customer> customers) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt"))) {

            for (Customer c : customers) {

                writer.write(
                        c.getCustomerId()
                        + ","
                        + c.getName()
                        + ","
                        + c.getContactNumber()
                        + ","
                        + c.getMembershipType());

                writer.newLine();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public LinkedList<Customer> loadCustomers() {

        LinkedList<Customer> customers = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                customers.add(new Customer(
                                data[0],
                                data[1],
                                data[2],
                                data[3]));
            }

        } catch (Exception e) {

            System.out.println("No customer file found.");
        }

        return customers;
    }

    public void saveUsers(
        LinkedList<User> users) {

    try (BufferedWriter writer =
            new BufferedWriter(
                    new FileWriter("users.txt"))) {

        for (User user : users) {

            writer.write(
                    user.getUsername()
                    + ","
                    + user.getPassword()
                    + ","
                    + user.getRole()
                    + ","
                    + user.getSecurityQuestion()
                    + ","
                    + user.getSecurityAnswer()
                    + ","
                    + user.isBlocked()
                    + ","
                    + user.getLoginAttempts()
            );

            writer.newLine();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

   public LinkedList<User> loadUsers() {

    LinkedList<User> users = new LinkedList<>();

    try (BufferedReader reader =
            new BufferedReader(
                    new FileReader("users.txt"))) {

        String line;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(",");

            if (data.length >= 7) {

                users.add(new User(
                        data[0], 
                        data[1], 
                        data[2], 
                        data[3], 
                        data[4], 
                        Boolean.parseBoolean(data[5]),
                        Integer.parseInt(data[6]) 
                ));

            } else {

                
                users.add(new User(
                        data[0],
                        data[1],
                        data[2],
                        "",
                        "",
                        false,
                        0
                ));
            }
        }

    } catch (Exception e) {
        System.out.println("No user file found.");
    }

    return users;
}

    public void savePCs(
            LinkedList<PC> pcs) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("pcs.txt"))) {

            for (PC pc : pcs) {

                writer.write(
                        pc.getPcId()
                        + ","
                        + pc.getPcName()
                        + ","
                        + pc.getHourlyRate()
                        + ","
                        + pc.getStatus());

                writer.newLine();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public LinkedList<PC> loadPCs() {

        LinkedList<PC> pcs = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("pcs.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                pcs.add(new PC(
                                data[0],
                                data[1],
                                Double.parseDouble(
                                        data[2]),
                                data[3]));
            }

        } catch (Exception e) {

            System.out.println(
                    "No PC file found.");
        }

        return pcs;
    }

    public void saveReservations(
            LinkedList<Reservation> reservations) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reservations.txt"))) {

            for (Reservation r : reservations) {

                writer.write(
                        r.getReservationId()
                        + ","
                        + r.getCustomerId()
                        + ","
                        + r.getPcId()
                        + ","
                        + r.getDate()
                        + ","
                        + r.getStartTime()
                        + ","
                        + r.getEndTime()
                        + ","
                        + r.getStatus()
                        + ","
                        + r.getTotal());

                writer.newLine();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public LinkedList<Reservation>
            loadReservations() {

        LinkedList<Reservation> reservations = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                Reservation reservation = new Reservation(
                                data[0],
                                data[1],
                                data[2],
                                data[3],
                                data[4],
                                data[5]);

                reservation.setStatus(data[6]);

                reservation.setTotal(Double.parseDouble(data[7]));

                reservations.add(reservation);
            }

        } catch (Exception e) {

            System.out.println("No reservation file found.");
        }

        return reservations;
    }

    public void saveProducts(
            LinkedList<Product> products) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"))) {

            for (Product product : products) {

                writer.write(
                        product.getProductId()
                        + ","
                        + product.getProductName()
                        + ","
                        + product.getCategory()
                        + ","
                        + product.getPrice()
                        + ","
                        + product.getStock());

                writer.newLine();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public LinkedList<Product> loadProducts() {

        LinkedList<Product> products = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                products.add(new Product(
                                data[0],
                                data[1],
                                data[2],
                                Double.parseDouble(
                                        data[3]),
                                Integer.parseInt(
                                        data[4])));
            }

        } catch (Exception e) {

            System.out.println("No product file found.");
        }

        return products;
    }

    public void saveOrders(
            LinkedList<Order> orders) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders.txt"))) {

            for (Order order : orders) {

                writer.write(
                        order.getOrderId()
                        + ","
                        + order.getCustomerId()
                        + ","
                        + order.getProductId()
                        + ","
                        + order.getQuantity()
                        + ","
                        + order.getDate()
                        + ","
                        + order.getSubtotal()
                        + ","
                        + order.getDiscount()
                        + ","
                        + order.getTotal());

                writer.newLine();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public LinkedList<Order> loadOrders() {

        LinkedList<Order> orders = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("orders.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                Order order = new Order(
                                data[0],
                                data[1],
                                data[2],
                                Integer.parseInt(
                                        data[3]),
                                data[4]);

                order.setSubtotal(Double.parseDouble(data[5]));

                order.setDiscount(Double.parseDouble(data[6]));

                order.setTotal(Double.parseDouble(data[7]));

                orders.add(order);
            }

        } catch (Exception e) {

            System.out.println("No order file found.");
        }

        return orders;
    }
    
    public void saveSessions(
        LinkedList<Session> sessions) {

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("sessions.txt"))) {

        for (Session session : sessions) {

            writer.write(
                    session.getSessionId()
                    + ","
                    + session.getCustomerId()
                    + ","
                    + session.getPcId()
                    + ","
                    + session.getHours()
                    + ","
                    + session.getDate()
                    + ","
                    + session.getSubtotal()
                    + ","
                    + session.getDiscount()
                    + ","
                    + session.getTotal());

            writer.newLine();
        }

    } catch (IOException e) {

        e.printStackTrace();
    }
}
    
    public LinkedList<Session> loadSessions() {

    LinkedList<Session> sessions = new LinkedList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader("sessions.txt"))) {

        String line;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(",");

            Session session = new Session(
                            data[0],
                            data[1],
                            data[2],
                            Double.parseDouble(data[3]),
                            data[4]);

            session.setSubtotal(Double.parseDouble(data[5]));

            session.setDiscount(Double.parseDouble(data[6]));

            session.setTotal(Double.parseDouble(data[7]));

            sessions.add(session);
        }

    } catch (Exception e) {

        System.out.println("No session file found.");
    }

    return sessions;
}
}
