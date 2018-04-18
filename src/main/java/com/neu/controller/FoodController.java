package com.neu.controller;

import com.neu.dao.FoodDAO;
import com.neu.dao.OrderDAO;
import com.neu.dao.UserDAO;
import com.neu.pojo.Food;
import com.neu.pojo.Orders;
import com.neu.pojo.Orderdetail;
import com.neu.pojo.Users;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Controller
public class FoodController {

    @RequestMapping(value = "/cart.htm", method = RequestMethod.GET)
    public String addOrderPage(HttpServletRequest request) {
//        String id = request.getParameter("items");
//        System.out.println(items);
        return "cart";
    }

    @RequestMapping(value = "/cart.htm", method = RequestMethod.POST)
    public String addOrder(HttpServletRequest request) {
        HttpSession session = request.getSession();
//        String id = request.getParameter("items");
//        System.out.println(id);
        return "cart";
    }

    @RequestMapping(value = "/addToCart.htm", method = RequestMethod.POST)
    public String addToCart(HttpServletRequest request, FoodDAO foodDAO, HttpSession session) {
        String id = request.getParameter("add");
        String name = request.getParameter("w3ls_item");
        String price = request.getParameter("amount");
//        String username = (String) session.getAttribute("user");
        HashMap<Food, Integer> foodInCart = null;
        if (session.getAttribute("foodInCart") == null) {
            foodInCart = new HashMap<Food, Integer>();
        } else {
            foodInCart = (HashMap<Food, Integer>) session.getAttribute("foodInCart");
        }
        Food food = foodDAO.findFood(Integer.parseInt(id));
        if (foodInCart.get(food) != null) {
            foodInCart.put(food, foodInCart.get(food) + 1);
        } else {
            foodInCart.put(food, 1);
        }
        session.setAttribute("foodInCart", foodInCart);
        return "cart";
    }

    @RequestMapping(value = "/delete.htm", method = RequestMethod.GET)
    public String delete(HttpServletRequest request, FoodDAO foodDAO) {
        int id = Integer.parseInt(request.getParameter("food"));
        HttpSession session = request.getSession();
        HashMap<Food, Integer> map = (HashMap<Food, Integer>) session.getAttribute("foodInCart");
        Food f = foodDAO.findFood(id);
        map.remove(f);
        return "cart";
    }

    @RequestMapping(value = "/updateQuantity.htm", method = RequestMethod.POST)
    public String updateQuantity(HttpServletRequest request, FoodDAO foodDAO) {
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        int id = Integer.parseInt(request.getParameter("food"));
        HttpSession session = request.getSession();
        HashMap<Food, Integer> map = (HashMap<Food, Integer>) session.getAttribute("foodInCart");
        Food f = foodDAO.findFood(id);
        map.put(f, quantity);
        return "cart";
    }

    @RequestMapping(value = "/checkout.htm", method = RequestMethod.POST)
    public String checkout(HttpServletRequest request, OrderDAO orderDAO, UserDAO userDAO) throws Exception {
        HttpSession session = request.getSession();
        HashMap<Food, Integer> map = (HashMap<Food, Integer>) session.getAttribute("foodInCart");
        request.setAttribute("order", map);
        session.setAttribute("foodInCart", null);
        Orders order = new Orders();
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date());

//        order.setOrderTime(time);
        order.setAddress(request.getParameter("address"));
        String username = (String)session.getAttribute("user");
        Users user = userDAO.get(username);
        order.setUsers(user);
        orderDAO.addOrder(order);
        double totalprice = 0;
        for (Food food : map.keySet()) {
            Orderdetail orderdetail = new Orderdetail();
            orderdetail.setNums(map.get(food));
            orderdetail.setPrice(food.getPrice() * map.get(food));
            orderDAO.addOrderdetail(orderdetail);
            totalprice += food.getPrice() * map.get(food);
        }
        request.setAttribute("totalprice", totalprice);
        return "checkout";
    }

    @RequestMapping(value = "/viewMyOrder.htm",method = RequestMethod.GET)
    public String viewMyOrder(){
        return "viewmyorder";
    }
}