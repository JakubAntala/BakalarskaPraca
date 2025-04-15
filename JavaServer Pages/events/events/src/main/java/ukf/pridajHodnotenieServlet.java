package ukf;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/pridajHodnotenieServlet")
public class pridajHodnotenieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            throw new ServletException("Nepodarilo sa pripojiť k databáze", e);
        }
    }

    public void destroy() {
        try { if (con != null) con.close(); } catch (Exception e) {}
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ID") == null) {
            response.sendRedirect("loginServlet");
            return;
        }

        int userId = (Integer) session.getAttribute("ID");
        int hotelId = Integer.parseInt(request.getParameter("hotel_id"));
        String note = request.getParameter("note");
        int rating = Integer.parseInt(request.getParameter("rating"));

        try {
            String sql = "INSERT INTO hotel_reviews (hotel_id, user_id, rating, note, date, created_at, updated_at) " +
                         "VALUES (?, ?, ?, ?, CURDATE(), ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            ps.setInt(1, hotelId);
            ps.setInt(2, userId);
            ps.setInt(3, rating);
            ps.setString(4, note);
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now); 

            ps.executeUpdate();
            ps.close();

            response.sendRedirect("hotelDetailServlet?hotelId=" + hotelId + "&success=1");
        } catch (Exception e) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("Chyba pri ukladaní hodnotenia: " + e.getMessage());
        }
    }
}
