package ukf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/hotelDetailServlet")
public class hotelDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;
    private String error = "";

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

    public void destroy() {
        try { if (con != null) con.close(); } catch (Exception e) {}
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        createHeader(out, request);
        createBody(out, request);
        createFooter(out, request);
    }

    private void createHeader(PrintWriter out, HttpServletRequest request) {
        HttpSession ses = request.getSession(false);
        String meno = ses != null ? (String) ses.getAttribute("first_name") : null;
        String priezvisko = ses != null ? (String) ses.getAttribute("last_name") : null;
        String email = ses != null ? (String) ses.getAttribute("login") : null;

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>Eventy</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("</head>");

        out.println("<nav style='background-color: #1a1a1a; color: white; padding: 15px; display: flex; align-items: center; justify-content: space-between;'>");

        out.println("<div style='display: flex; align-items: center;'>");
        out.println("<a class='navbar-brand mb-0 h1 me-3' href='mainServlet' style='text-decoration: none; color: white;'>Eventy</a>");
        out.println("<form action='mainServlet' method='get'>");
        out.println("<input type='text' name='query' placeholder='Hľadať eventy' style='padding: 5px; border-radius: 5px; border: none;'>");
        out.println("</form></div>");

        out.println("<a href='hotelServlet' style='color: white; text-decoration: none; font-size: 16px;'>Zobraziť hotely</a>");

        out.println("<div style='position: relative; display: inline-block;'>");

        if (ses != null && ses.getAttribute("ID") != null) {
            String inic = meno.substring(0, 1).toUpperCase() + priezvisko.substring(0, 1).toUpperCase();

            out.println("<button onclick='toggleMenu()' style='background: none; border: none; color: white; font-size: 16px; cursor: pointer;'>");
            out.println("<div style='display: flex; align-items: center; gap: 10px;'>");
            out.println("<div style='width: 40px; height: 40px; background-color: #555; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold;'>");
            out.println(inic);
            out.println("</div></div></button>");

            out.println("<div id='profileMenu' style='display: none; position: absolute; right: 0; background: white; color: black; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2); border-radius: 5px; padding: 10px; min-width: 200px;'>");
            out.println("<p style='font-weight: bold;'>" + meno + " " + priezvisko + "</p>");
            out.println("<p style='color: gray; font-size: 14px;'>" + email + "</p>");

            if (RoleHelper.hasAccess(ses, "admin", "staff")) {
                out.println("<a href='myEventServlet' style='display: block; padding: 5px 0;'>Moje eventy</a>");
                out.println("<a href='pridatEventServlet' style='display: block; padding: 5px 0;'>Pridať Event</a>");
            }
            if (RoleHelper.hasAccess(ses, "admin")) {
                out.println("<a href='zmenitRoleServlet' style='display: block; padding: 5px 0;'>Zmeniť role</a>");
            }
            out.println("<a href='upravProfilServlet' style='display: block; padding: 5px 0;'>Upraviť profil</a>");
            out.println("<a href='mojeRezervacieServlet' style='display: block; padding: 5px 0;'>Moje rezervácie</a>");
            out.println("<form action='mainServlet' method='post'>");
            out.println("<input type='hidden' name='operacia' value='logout'>");
            out.println("<button type='submit' style='background: none; border: none; color: red; padding: 5px 0;'>Odhlásiť sa</button>");
            out.println("</form>");
            out.println("</div>");
        } else {
            out.println("<a href='loginServlet' style='color: white; text-decoration: none; padding: 8px 16px; border: 1px solid white; border-radius: 5px;'>Prihlásiť sa</a>");
        }

        out.println("</div></nav>");
        out.println("<script>");
        out.println("function toggleMenu() {");
        out.println("  var menu = document.getElementById('profileMenu');");
        out.println("  menu.style.display = menu.style.display === 'block' ? 'none' : 'block';");
        out.println("}");
        out.println("</script>");
    }

    private void createBody(PrintWriter out, HttpServletRequest request) {
        String idStr = request.getParameter("hotelId");
        String success = request.getParameter("success");
        if ("1".equals(success)) {
            out.println("<div class='container mt-4'>");
            out.println("<div class='alert alert-success'>Ďakujeme za hodnotenie hotela!</div>");
            out.println("</div>");
        }
        HttpSession session = request.getSession(false);

        if (idStr != null) {
            try {
                int hotelId = Integer.parseInt(idStr);
                String sql = "SELECT * FROM hotel WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, hotelId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    out.println("<div class='container mt-5'>");
                    out.println("<h2>" + rs.getString("name") + "</h2>");
                    out.println("<p><strong>Adresa:</strong> " + rs.getString("address") + "</p>");
                    out.println("<p><strong>Hodnotenie:</strong> " + rs.getInt("rating") + " ⭐</p>");
                    out.println("<img src='" + rs.getString("image") + "' alt='Obrázok hotela' class='img-fluid rounded shadow' style='max-height: 400px;'>");
                    String reviewsSql = "SELECT hr.rating, hr.note, hr.date, u.first_name, u.last_name " +
                                        "FROM hotel_reviews hr " +
                                        "JOIN user u ON hr.user_id = u.id " +
                                        "WHERE hr.hotel_id = ? " +
                                        "ORDER BY hr.created_at DESC";

                    PreparedStatement reviewsStmt = con.prepareStatement(reviewsSql);
                    reviewsStmt.setInt(1, hotelId);
                    ResultSet reviewsRs = reviewsStmt.executeQuery();

                    out.println("<div class='container mt-5'>");
                    out.println("<h4>Hodnotenia hotela</h4>");

                    boolean hasReviews = false;
                    while (reviewsRs.next()) {
                        hasReviews = true;
                        String meno = reviewsRs.getString("first_name");
                        String priezvisko = reviewsRs.getString("last_name");
                        int rating = reviewsRs.getInt("rating");
                        String note = reviewsRs.getString("note");
                        Date date = reviewsRs.getDate("date");

                        out.println("<div class='card mb-3 shadow-sm'>");
                        out.println("<div class='card-body'>");
                        out.println("<h6 class='card-title'>" + meno + " " + priezvisko + " – " + rating + "/5 ⭐</h6>");
                        out.println("<p class='card-text'>" + note + "</p>");
                        out.println("<small class='text-muted'>Hodnotené: " + date.toString() + "</small>");
                        out.println("</div>");
                        out.println("</div>");
                        
                    }
                    

                    if (!hasReviews) {
                        out.println("<p class='text-muted'>Tento hotel zatiaľ nemá žiadne hodnotenia.</p>");
                    }
                    out.println("</div>");

                    reviewsRs.close();
                    reviewsStmt.close();
                    if (session != null && session.getAttribute("ID") != null) {
                        out.println("<div class='container mt-5'>");
                        out.println("<h4>Pridať hodnotenie</h4>");
                        out.println("<form action='pridajHodnotenieServlet' method='post'>");

                        out.println("<div class='mb-3'>");
                        out.println("<label for='note' class='form-label'>Poznámka</label>");
                        out.println("<textarea class='form-control' name='note' id='note' rows='3' required></textarea>");
                        out.println("</div>");

                        out.println("<div class='mb-3'>");
                        out.println("<label for='rating' class='form-label'>Hodnotenie (0-5)</label>");
                        out.println("<input type='number' class='form-control' name='rating' id='rating' min='0' max='5' required>");
                        out.println("</div>");

                        out.println("<input type='hidden' name='hotel_id' value='" + hotelId + "'>");
                        out.println("<button type='submit' class='btn btn-primary'>Odoslať hodnotenie</button>");

                        out.println("</form>");
                        out.println("</div>");
                    }
                    out.println("</div>");
                } else {
                    out.println("<div class='container mt-5'><p class='text-danger'>Hotel sa nenašiel.</p></div>");
                }

                rs.close();
                ps.close();
            } catch (Exception e) {
                out.println("<div class='container mt-5'><p class='text-danger'>Chyba: " + e.getMessage() + "</p></div>");
            }
        } else {
            out.println("<div class='container mt-5'><p class='text-warning'>Nešpecifikovaný hotelId.</p></div>");
        }
    }

    private void createFooter(PrintWriter out, HttpServletRequest request) {
        out.println("<footer class='footer py-3 bg-dark text-white text-center' style='position: fixed; bottom: 0; width: 100%;'>");
        out.println("<p class='mb-0'>© 2024 Event Management</p>");
        out.println("</footer>");

        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");

        out.println("<style>");
        out.println("html, body { height: 100%; margin: 0; padding: 0; display: flex; flex-direction: column; }");
        out.println(".container { flex: 1; padding-bottom: 60px; }");
        out.println("footer { height: 60px; }");
        out.println("</style>");
    }
}
