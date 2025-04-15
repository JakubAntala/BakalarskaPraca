package ukf;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/zrusRezervaciuServlet")
public class zrusRezervaciuServlet extends HttpServlet {
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ID") == null) {
            response.sendRedirect("loginServlet");
            return;
        }

        int userId = (Integer) session.getAttribute("ID");
        int eventId = Integer.parseInt(request.getParameter("event_id"));

        try {
            String sql = "DELETE FROM event_reservation WHERE user_id = ? AND event_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.executeUpdate();
            ps.close();

            response.sendRedirect("eventDetailServlet?eventId=" + eventId + "&zrusenie=ok");
        } catch (SQLException e) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("Chyba pri zrušení rezervácie: " + e.getMessage());
        }
    }

    public void destroy() {
        try { if (con != null) con.close(); } catch (SQLException e) {}
    }
}
